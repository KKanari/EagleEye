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
 *�@�C�[�O���A�Cutilty���\�b�h�i�[�N���X
 * 
 * @version 1.0, 4 Jan, 2010
 * @author kurisu
 */
public class EagleEyeUtil {
	/**
	 * ���݈ʒu�ܓx�o�x�擾���\�b�h
	 * 
	 * �[����GPS���猻�݂̈ܓx�ƌo�x���擾����B
	 * 
	 * @param LocationManager
	 * @return�@Location
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static Location getMyLocation(LocationManager mgr){
		Location location = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		return location;			
	}



	/**
	 * �Z���擾���\�b�h
	 * 
	 * �ܓx�ƌo�x����Z�����擾����B
	 * �擾�ł��Ȃ������ꍇ�A�󕶎���return
	 * 
	 * @param Location, Context
	 * @return�@String
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static String point2address(Location location, Context context) throws IOException{
		/** �Z���擾 */
		Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
		List<Address> list_address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);

		/** �擾�����`�F�b�N�@*/
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
	 * �ܓx�A�o�x�擾���\�b�h
	 * 
	 * �Z������ܓx�ƌo�x���擾����B
	 * �擾�ł��Ȃ������ꍇ�Anull��return
	 * 
	 * @param Location, Context
	 * @return�@String
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static Location address2point(String address, Context context, Resources rs) throws IOException{
		Location result = new Location("");

		/** �X�֔ԍ����� */
		if (address == null || !address.matches("^[0-9]")) {
			address = rs.getString(R.string.zipCode) + address;
		}

		Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
		List<Address> list_address = geocoder.getFromLocationName(address, 1);

		/** �擾�����`�F�b�N */
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
	 * �ܓx�A�o�x�������`�F�b�N���\�b�h
	 * 
	 * �ܓx�A�o�x�Ƃ��Ă̐������̃`�F�b�N���s��
	 * ����:true
	 * �ُ�:false
	 * 
	 * @param String, int
	 * @return�@boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean numCheck(String num, int max){
		double dblNum;

		/** double�^�ɕϊ��`�F�b�N*/
		try {
			dblNum = Double.parseDouble(num);
		} catch(NumberFormatException e) {
			return false;
		}

		/** �͈̓`�F�b�N */
		if(dblNum < (max * -1) || max < dblNum){
			return false;
		}

		return true;
	}



	/**
	 * Gmail�A�J�E���g���������`�F�b�N���\�b�h
	 * 
	 * Gmail�̃A�J�E���g���Ƃ��Ă̐������̃`�F�b�N���s��
	 * ����:true
	 * �ُ�:false
	 * 
	 * @param String, int, int
	 * @return�@boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean checkAccountName(String str, int min, int max){
		if (!str.matches("[0-9a-z.]+")) {
			return false;
		}

		/** �͈̓`�F�b�N */
		if(str.length() < min || max < str.length()){
			return false;
		}

		return true;
	}



	/**
	 * ���C����ʃ��j���[���ڍ쐬���\�b�h
	 * 
	 * ���C����ʃ��j���[�ɍ��ڂ�ǉ����č쐬����
	 * 
	 * @param Menu
	 * @return�@Menu
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
	 * �摜�������\�b�h
	 * 
	 * �n���ꂽ�摜����]�����A��������
	 * 
	 * @param Bitmap, degree
	 * @return�@Bitmap
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
	 * �����v�Z���\�b�h
	 * 
	 * ���ݒn����ړI�n�̕������v�Z����
	 * 
	 * @param Location, Location
	 * @return�@boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static boolean getDirection(Location myLocation, Location location, Resources rs) {
		double dDffLtt = (location.getLatitude() - myLocation.getLatitude()) * (Double.valueOf(rs.getString(R.string.earthAround)) / 360) * 1000;
		double dDffLng;
		
		/** ���[�g�C�� */
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
	 * �����v�Z���\�b�h
	 * 
	 * ���ݒn����ړI�n�̋������v�Z����
	 * 
	 * @param Location, double, double
	 * @return�@float
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
	 * ���������v�Z���\�b�h
	 * 
	 * ��ʂɕ\����������������v�Z����
	 * 
	 * @param BigDecimal, float
	 * @return�@boolean
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
	 * �����������\�b�h
	 * 
	 * �����������s��
	 * ����Ftrue
	 * �ُ�:false
	 * 
	 * @param BitmapDrawable
	 * @return�@boolean
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
	 * �l�̌ܓ����\�b�h
	 * 
	 * �w�茅�̎l�̌ܓ����s��
	 * 
	 * @param float, int
	 * @return�@BigDecimal
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
	 * Intent�ݒ胁�\�b�h
	 * 
	 * ���C����ʂ̌��݈ʒuor�ړI�n��Intent�ɃZ�b�g����
	 * 
	 * @param Intent, String, Location
	 * @return�@Intent
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
	 * Intent�ݒ胁�\�b�h(�ݒ�)
	 * 
	 * �ݒ��Intent�ɃZ�b�g����
	 * 
	 * @param Intent, int, int, int
	 * @return�@Intent
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
	 * Gmail�^�C�g���쐬���\�b�h
	 * 
	 * �ʒu�f�[�^��Gmail���M�p�ɕύX����
	 * 
	 * @param String[]
	 * @return�@String
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public static String makeTitle(String[] data, String head, String dlm) {
		String result = head + dlm +data[4] + dlm + data[5] + dlm + data[6];

		return result;
	}



	/**
	 * ���������쐬���\�b�h
	 * 
	 * ����������������쐬����
	 * 
	 * @param String, String
	 * @return�@String
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
	 * �^�C�g���o�[�쐬���\�b�h
	 * 
	 * ��p�̃^�C�g���o�[���쐬����
	 * ����:true
	 * 
	 * @param TextView, TextView, PackageManager, Resources
	 * @return�@boolean
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
	 * Location->GeoPoint�ϊ�
	 * 
	 *�@Location��GeoPoint�ɕϊ�����
	 * 
	 * @param Location
	 * @return�@GeoPoint
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
	 * GeoPoint->Location�ϊ�
	 * 
	 *�@GeoPoint��Location�ɕϊ�����
	 * 
	 * @param GeoPoint
	 * @return�@Location
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
