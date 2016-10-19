/**
 * Copyright (c) 2007 KMS2 Co.,Ltd.All Rights Reserved.
 */

package kms2.EagleEye;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.widget.*;

public class FullScreenForm extends Activity implements LocationListener, SensorEventListener{
	private static LocationManager sLocMgr;
	private static SensorManager sSnsMgr;
	private static Location sMyLocation = new Location("");
	private static Location sLocation = new Location("");
	private static boolean naviFlg = true;
	private static int sSettingAlarm = MainForm.FLG_ON;



	/*****************************************************
	 * イベントメソッド
	 * 
	 * 端末の動作によって呼び出される処理
	 *****************************************************/

	/**
	 * メイン画面作成イベント
	 * 
	 * メイン画面が作成される際に呼び出し
	 * 
	 * @param Bundle
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.full);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		/** instance */
		TextView titleLeftText = (TextView) findViewById(R.id.left_text);
		TextView titleRightText = (TextView) findViewById(R.id.right_text);

		/** センサー初期化 */
		sLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		sSnsMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		/** タイトルバー作成 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		/** 現在地取得 */
		sMyLocation = EagleEyeUtil.getMyLocation(sLocMgr);
		if(sMyLocation == null){
			Toast.makeText(getApplicationContext(), 
					getString(R.string.getLctErrorMsg), 
					MainForm.MSGTIME).show();

			sMyLocation.setLatitude(Double.valueOf(getString(R.string.defaultLatitude)));
			sMyLocation.setLongitude(Double.valueOf(getString(R.string.defaultLongitude)));
		}
		
		/** 目的地取得 */
		Bundle extras = getIntent().getExtras();
		sLocation.setLatitude(extras.getDouble(MainForm.INTENT_LATITUDE));
		sLocation.setLongitude(extras.getDouble(MainForm.INTENT_LONGITUDE));

		setResult();
	}



	/**
	 * 端末方向変換イベント
	 * 
	 * 端末の向いている方向が変更された時に呼び出し
	 * 
	 * @param SensorEvent
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		/** instance */
		ImageView drcView = (ImageView) findViewById(R.id.FullDirectionArrow);

		Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);

		/** 矢印方向計算 */
		float fDirection = (float) (ResultValue.getsDirection() - event.values[0]);
		if(fDirection < 0.0){
			fDirection = fDirection + 360;
		}
		
		/** 画像生成 */
		EagleEyeUtil.rotateBitmap(bitmapOrg, fDirection);

		drcView.setBackgroundDrawable(ResultValue.getsDirectionArrow());
	}




	/**
	 * 端末現在地変更イベント
	 * 
	 * 端末から取得するGPSの位置の変更時に呼び出し
	 * 
	 * @param Location
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onLocationChanged(Location location) {
		/** 現在位置更新 */
		sMyLocation = EagleEyeUtil.getMyLocation(sLocMgr);
		if(sMyLocation == null){
			Toast.makeText(getApplicationContext(), 
					getString(R.string.getLctErrorMsg), 
					MainForm.MSGTIME).show();
		}

		/** 目的地までの距離、方向が表示の際、更新 */
		if(naviFlg == true){
			getResult(sLocation.getLatitude(), sLocation.getLongitude());
		}
	}



	/**
	 * アプリback ground移動時イベント
	 * 
	 * アプリがback groundに移行した際に呼び出し
	 * 
	 * @param なし
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onPause() {
		super.onPause();

		/** センサー終了 */
		sLocMgr.removeUpdates(this);
		sSnsMgr.unregisterListener(this);
	}



	/**
	 * アプリfore ground移動時イベント
	 * 
	 * アプリがfore groundに移行した際に呼び出し
	 * 
	 * @param なし
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onResume() {
		super.onResume();

		/** センサー開始 */
		sLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		List<Sensor> sensors = sSnsMgr.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			sSnsMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}




	
	/*****************************************************
	 * 計算処理メソッド
	 * 
	 * メイン画面の動作イベントから呼び出されるメソッド
	 *****************************************************/

	/**
	 * 目的地までの距離、方向計算メソッド
	 * 
	 * 目的地までの距離と方向を計算する
	 * 
	 * @param なし
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void getResult(double latitude, double longitude){
		/** 結果変数宣言 */
		String unit = getString(R.string.meter);

		/** 距離取得処理 */
		double distance = EagleEyeUtil.getDst(sMyLocation, latitude, longitude);
		ResultValue.setsDistance(distance);
		BigDecimal resultDst = new BigDecimal(distance);

		/** 距離に対応して表記変更 */
		if(resultDst.compareTo(new BigDecimal(1000.0)) >= 0){
			resultDst = resultDst.divide(new BigDecimal(1000.0));
			unit = getString(R.string.kiloMeter);
		}

		/** 小数点3位未満四捨五入 */
		resultDst = resultDst.setScale(3, RoundingMode.HALF_UP);
		ResultValue.setsPrintDistance(resultDst + unit);

		/** 目的地までの距離が設定距離未満かチェック */
		if(resultDst.compareTo(new BigDecimal(getString(R.string.arrivalDistance))) < 0 && unit == getString(R.string.meter)){
			BitmapDrawable bmd = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.arrival));
			EagleEyeUtil.getArrivalResult(bmd, getResources());

			LinearLayout drcView = (LinearLayout) findViewById(R.id.FullDirectionArrow);
			drcView.setBackgroundDrawable(bmd);

			/** アラーム起動 */
			if(sSettingAlarm == MainForm.FLG_ON){
				MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
				mp.start();
			}

			// ナビ終了
			naviFlg = false;

			// 方向センサー終了
			if(sSnsMgr != null){
				sSnsMgr.unregisterListener(this);
			}
		}else{
			// 方向計算
			EagleEyeUtil.getDirection(sMyLocation, sLocation, getResources());

			/** 方向文字作成 */ 
			BigDecimal bdDrc = EagleEyeUtil.roundF2Bd((float) ResultValue.getsDirection(), 0);
			EagleEyeUtil.getStrDrc(bdDrc, (float) ResultValue.getsDirection(), getResources());

			if(naviFlg == false){
				naviFlg = true;
			}
		}
	}

	
	
	/**
	 * 距離、方向表示メソッド
	 * 
	 * メイン画面の距離と方向欄（矢印抜き）に値を表示する
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setResult() {
		/** instance */
		TextView dst = (TextView) findViewById(R.id.FullDistance);
		TextView drc = (TextView) findViewById(R.id.FullDirection);

		/** 距離と方向表示 */
		dst.setText(ResultValue.getsPrintDistance());
		drc.setText(ResultValue.getsPrintDirection());
	}


	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}



	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}



	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}



	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}
}
