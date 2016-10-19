package kms2.EagleEye;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.view.Menu;
import android.widget.TextView;

/**
 *　イーグルアイutiltyメソッド格納クラス
 * 
 * @version 1.0, 4 Jan, 2010
 * @author kurisu
 */
public class EagleEyeUtil {
	/**
	 * 現在位置緯度経度取得メソッド
	 * 
	 * 端末のGPSから現在の緯度と経度を取得する。
	 * 
	 * @param LocationManager
	 * @return　Location
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static Location getMyLocation(LocationManager mgr){
		Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		return location;			
	}



	/**
	 * 住所取得メソッド
	 * 
	 * 緯度と経度から住所を取得する。
	 * 取得できなかった場合、空文字をreturn
	 * 
	 * @param Location, Context
	 * @return　String
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static String point2address(Location location, Context context) throws IOException{
		/** 住所取得 */
		Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
		List<Address> list_address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);

		/** 取得成功チェック　*/
		if(!list_address.isEmpty()){
			Address address = list_address.get(0);
			StringBuffer strbuf = new StringBuffer();

			String buf;
			for (int i = 0; (buf = address.getAddressLine(i)) != null; i++){
				strbuf.append(buf);
			}
			return strbuf.toString();
		}
		return MainForm.BLANK;
	}



	/**
	 * 緯度、経度取得メソッド
	 * 
	 * 住所から緯度と経度を取得する。
	 * 取得できなかった場合、nullをreturn
	 * 
	 * @param Location, Context
	 * @return　String
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static Location address2point(String address, Context context, Resources rs) throws IOException{
		Location result = new Location("");

		/** 郵便番号処理 */
		if (address == null || !address.matches("^[0-9]")) {
			address = rs.getString(R.string.zipCode) + address;
		}

		Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
		List<Address> list_address = geocoder.getFromLocationName(address, 1);

		/** 取得成功チェック */
		if (!list_address.isEmpty()){
			Address resultAddress = list_address.get(0);

			result.setLatitude(resultAddress.getLatitude());
			result.setLongitude(resultAddress.getLongitude());
		}else{
			result = null;
		}
		
		return result;
	}



	/**
	 * 緯度、経度整合性チェックメソッド
	 * 
	 * 緯度、経度としての整合性のチェックを行う
	 * 正常:true
	 * 異常:false
	 * 
	 * @param String, int
	 * @return　boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean numCheck(String num, int max){
		double dblNum;

		/** double型に変換チェック*/
		try {
			dblNum = Double.parseDouble(num);
		} catch(NumberFormatException e) {
			return false;
		}

		/** 範囲チェック */
		if(dblNum < (max * -1) || max < dblNum){
			return false;
		}

		return true;
	}



	/**
	 * Gmailアカウント名整合性チェックメソッド
	 * 
	 * Gmailのアカウント名としての整合性のチェックを行う
	 * 正常:true
	 * 異常:false
	 * 
	 * @param String, int, int
	 * @return　boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean checkAccountName(String str, int min, int max){
		if (!str.matches("[0-9a-z.]+")) {
			return false;
		}

		/** 範囲チェック */
		if(str.length() < min || max < str.length()){
			return false;
		}

		return true;
	}



	/**
	 * メイン画面メニュー項目作成メソッド
	 * 
	 * メイン画面メニューに項目を追加して作成する
	 * 
	 * @param Menu
	 * @return　Menu
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static Menu makeMainMenu(Menu menu) {        
		menu.add(Menu.NONE, MainForm.MENU_ID_DATA_LIST, Menu.NONE, R.string.readDataList)
		.setIcon(android.R.drawable.ic_menu_save);
		menu.add(Menu.NONE, MainForm.MENU_ID_SELECT_GMAIL, Menu.NONE, R.string.readMailList)
		.setIcon(android.R.drawable.ic_menu_send);
		menu.add(Menu.NONE, MainForm.MENU_ID_SETTING, Menu.NONE, R.string.setting)
		.setIcon(android.R.drawable.ic_menu_preferences);

		return menu;
	}



	/**
	 * 画像生成メソッド
	 * 
	 * 渡された画像を回転させ、生成する
	 * 
	 * @param Bitmap, degree
	 * @return　Bitmap
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean rotateBitmap(Bitmap bmp, float degree) {
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		Bitmap result = Bitmap.createBitmap(h, w, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint();
		paint.setColor(Color.argb(255, 255, 255, 255));
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(new Rect(0, 0, h, w), paint);
		canvas.rotate(degree, w/2, h/2);
		canvas.drawBitmap(bmp,(w-h)/2, (w-h)/2, null);

		ResultValue.setsDirectionArrow(new BitmapDrawable(result));

		return true;
	}



	/**
	 * 方向計算メソッド
	 * 
	 * 現在地から目的地の方向を計算する
	 * 
	 * @param Location, Location
	 * @return　boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean getDirection(Location myLocation, Location location, Resources rs) {
		double dDffLtt = (location.getLatitude() - myLocation.getLatitude()) * (Double.valueOf(rs.getString(R.string.earthAround)) / 360) * 1000;
		double dDffLng;
		
		/** ルート修正 */
		if((location.getLongitude() - myLocation.getLongitude()) < -180.0){
			dDffLng = ((location.getLongitude() - myLocation.getLongitude()) + 360.0) * Double.valueOf(rs.getString(R.string.earthAround)) / 360 * 1000;
		}else if((location.getLongitude() - myLocation.getLongitude()) > 180.0){
			dDffLng = ((location.getLongitude() - myLocation.getLongitude()) - 360.0) * Double.valueOf(rs.getString(R.string.earthAround)) / 360 * 1000;
		}else{
			dDffLng = (location.getLongitude() - myLocation.getLongitude()) * (Double.valueOf(rs.getString(R.string.earthAround)) / 360) * 1000;
		}

		double dDirection  = 90 - Math.atan2(dDffLtt, dDffLng) * 180 / Math.PI;
		if(dDirection < 0){
			dDirection += 360;
		}
		ResultValue.setsDirection(dDirection);

		return true;
	}



	/**
	 * 距離計算メソッド
	 * 
	 * 現在地から目的地の距離を計算する
	 * 
	 * @param Location, double, double
	 * @return　float
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static double getDst(Location myLocation, double ltt, double lng) {
		float[] results = new float[1];
		Location.distanceBetween(myLocation.getLatitude(), myLocation.getLongitude(), ltt, lng, results);

		double result = (double) results[0];
		
		return result;
	}



	/**
	 * 方向文字計算メソッド
	 * 
	 * 画面に表示する方向文字を計算する
	 * 
	 * @param BigDecimal, float
	 * @return　boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean getStrDrc(BigDecimal bdDrc, float fDrc, Resources rs) {
		String resultStr;

		if(11.25 < fDrc && fDrc <= 191.25){
			if(fDrc <= 101.25){
				if(fDrc <= 56.25){
					if(fDrc <= 33.75){
						resultStr = rs.getString(R.string.nne) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.ne) + bdDrc + rs.getString(R.string.sign);
					}
				}else{
					if(fDrc <= 78.25){
						resultStr = rs.getString(R.string.ene) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.e) + bdDrc + rs.getString(R.string.sign);
					}
				}
			}else{
				if(fDrc <= 146.25){
					if(fDrc <= 123.25){
						resultStr = rs.getString(R.string.ese) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.se) + bdDrc + rs.getString(R.string.sign);
					}
				}else{
					if(fDrc <= 168.75){
						resultStr = rs.getString(R.string.sse) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.s) + bdDrc + rs.getString(R.string.sign);
					}
				}
			}
		}else{
			if(11.25 < fDrc && fDrc <= 281.25){
				if(fDrc <= 236.25){
					if(fDrc <= 213.75){
						resultStr = rs.getString(R.string.ssw) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.sw) + bdDrc + rs.getString(R.string.sign);
					}
				}else{
					if(fDrc <= 258.75){
						resultStr = rs.getString(R.string.wsw) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.w) + bdDrc + rs.getString(R.string.sign);
					}
				}
			}else{
				if(11.25 < fDrc && fDrc <= 326.25){
					if(fDrc <= 303.75){
						resultStr = rs.getString(R.string.wnw) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.nw) + bdDrc + rs.getString(R.string.sign);
					}
				}else{
					if(11.25 < fDrc && fDrc <= 348.75){
						resultStr = rs.getString(R.string.nnw) + bdDrc + rs.getString(R.string.sign);
					}else{
						resultStr = rs.getString(R.string.n) + bdDrc + rs.getString(R.string.sign);
					}
				}
			}
		}

		ResultValue.setsPrintDirection(resultStr);
		return true;
	}



	/**
	 * 到着処理メソッド
	 * 
	 * 到着処理を行う
	 * 正常：true
	 * 異常:false
	 * 
	 * @param BitmapDrawable
	 * @return　boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean getArrivalResult(BitmapDrawable bmd, Resources rs) {
		BigDecimal bddrc = new BigDecimal(0.000);
		ResultValue.setsPrintDirection(rs.getString(R.string.n) + bddrc + rs.getString(R.string.sign));
		ResultValue.setsDirectionArrow(bmd);

		return true;
	}



	/**
	 * 四捨五入メソッド
	 * 
	 * 指定桁の四捨五入を行う
	 * 
	 * @param float, int
	 * @return　BigDecimal
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static BigDecimal roundF2Bd(float num, int i){
		BigDecimal result = new BigDecimal(num);
		result = result.setScale(i, RoundingMode.HALF_UP);

		return result;
	}



	/**
	 * Intent設定メソッド
	 * 
	 * メイン画面の現在位置or目的地をIntentにセットする
	 * 
	 * @param Intent, String, Location
	 * @return　Intent
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public static Intent setLocation2Intent(Intent intent, String address, String latitude, String longitude) {
		intent.putExtra(MainForm.INTENT_ADDRESS, address);
		intent.putExtra(MainForm.INTENT_LATITUDE, latitude);
		intent.putExtra(MainForm.INTENT_LONGITUDE, longitude);

		return intent;
	}



	/**
	 * Intent設定メソッド(設定)
	 * 
	 * 設定をIntentにセットする
	 * 
	 * @param Intent, int, int, int
	 * @return　Intent
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public static Intent setSetting(Intent intent, String gmail, int display, int full, int alarm, int buffer1, int buffer2, int buffer3) {
		intent.putExtra(MainForm.INTENT_GMAIL, gmail);
		intent.putExtra(MainForm.INTENT_DISPLAY, display);
		intent.putExtra(MainForm.INTENT_FULL, full);
		intent.putExtra(MainForm.INTENT_ALARM, alarm);
		intent.putExtra(MainForm.INTENT_BUFFER1, buffer1);
		intent.putExtra(MainForm.INTENT_BUFFER2, buffer2);
		intent.putExtra(MainForm.INTENT_BUFFER3, buffer3);

		return intent;
	}



	/**
	 * Gmailタイトル作成メソッド
	 * 
	 * 位置データをGmail送信用に変更する
	 * 
	 * @param String[]
	 * @return　String
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public static String makeTitle(String[] data, String head, String dlm) {
		String result = head + dlm +data[4] + dlm + data[5] + dlm + data[6];

		return result;
	}



	/**
	 * 検索条件作成メソッド
	 * 
	 * 検索条件文字列を作成する
	 * 
	 * @param String, String
	 * @return　String
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public static String makeSearchWhere(String like1, String like2) {
		String selection = Locations.Location.ADDRESS + MainForm.SPACE + 
		MainForm.LIKE + MainForm.SPACE + MainForm.ENC + MainForm.WILD_CARD + 
		like1 + MainForm.WILD_CARD + MainForm.ENC +
		MainForm.SPACE + MainForm.OR + MainForm.SPACE +
		Locations.Location.NAME + MainForm.SPACE + 
		MainForm.LIKE + MainForm.SPACE + MainForm.ENC + MainForm.WILD_CARD + 
		like2 + MainForm.WILD_CARD + MainForm.ENC;

		return selection;
	}



	/**
	 * タイトルバー作成メソッド
	 * 
	 * 専用のタイトルバーを作成する
	 * 正常:true
	 * 
	 * @param TextView, TextView, PackageManager, Resources
	 * @return　boolean
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public static boolean getTitle(TextView leftText, TextView rightText, PackageManager pm, Resources rs){
		PackageInfo info = null;
		try {
			info = pm.getPackageInfo(rs.getString(R.string.packageName), 0);
			leftText.setText(rs.getString(R.string.app_name));
			rightText.setText(rs.getString(R.string.titleVersion) + info.versionName);
		} catch (NameNotFoundException e) {
			return false;
		} catch (NotFoundException e) {
			return false;
		}
		return true;
	}



	/**
	 * Location->GeoPoint変換
	 * 
	 *　LocationをGeoPointに変換する
	 * 
	 * @param Location
	 * @return　GeoPoint
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static GeoPoint cnvLocation2Geopoint(Location location) {
		int latitudeE6 = (int)(location.getLatitude() * 1E6);
		int longitudeE6 = (int)(location.getLongitude() * 1E6);
		GeoPoint result = new GeoPoint(latitudeE6, longitudeE6);

		return result;
	}



	/**
	 * GeoPoint->Location変換
	 * 
	 *　GeoPointをLocationに変換する
	 * 
	 * @param GeoPoint
	 * @return　Location
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static Location cnvGeopoint2Location(GeoPoint geoPoint) {
		Location result = new Location("");
		result.setLatitude(geoPoint.getLatitudeE6() / 1E6);
		result.setLongitude(geoPoint.getLongitudeE6() / 1E6);

		return result;
	}
}
