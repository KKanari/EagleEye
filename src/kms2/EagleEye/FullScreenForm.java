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
	 * �C�x���g���\�b�h
	 * 
	 * �[���̓���ɂ���ČĂяo����鏈��
	 *****************************************************/

	/**
	 * ���C����ʍ쐬�C�x���g
	 * 
	 * ���C����ʂ��쐬�����ۂɌĂяo��
	 * 
	 * @param Bundle
	 * @return�@void
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

		/** �Z���T�[������ */
		sLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		sSnsMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		
		/** �^�C�g���o�[�쐬 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		/** ���ݒn�擾 */
		sMyLocation = EagleEyeUtil.getMyLocation(sLocMgr);
		if(sMyLocation == null){
			Toast.makeText(getApplicationContext(), 
					getString(R.string.getLctErrorMsg), 
					MainForm.MSGTIME).show();

			sMyLocation.setLatitude(Double.valueOf(getString(R.string.defaultLatitude)));
			sMyLocation.setLongitude(Double.valueOf(getString(R.string.defaultLongitude)));
		}
		
		/** �ړI�n�擾 */
		Bundle extras = getIntent().getExtras();
		sLocation.setLatitude(extras.getDouble(MainForm.INTENT_LATITUDE));
		sLocation.setLongitude(extras.getDouble(MainForm.INTENT_LONGITUDE));

		setResult();
	}



	/**
	 * �[�������ϊ��C�x���g
	 * 
	 * �[���̌����Ă���������ύX���ꂽ���ɌĂяo��
	 * 
	 * @param SensorEvent
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		/** instance */
		ImageView drcView = (ImageView) findViewById(R.id.FullDirectionArrow);

		Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);

		/** �������v�Z */
		float fDirection = (float) (ResultValue.getsDirection() - event.values[0]);
		if(fDirection < 0.0){
			fDirection = fDirection + 360;
		}
		
		/** �摜���� */
		EagleEyeUtil.rotateBitmap(bitmapOrg, fDirection);

		drcView.setBackgroundDrawable(ResultValue.getsDirectionArrow());
	}




	/**
	 * �[�����ݒn�ύX�C�x���g
	 * 
	 * �[������擾����GPS�̈ʒu�̕ύX���ɌĂяo��
	 * 
	 * @param Location
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onLocationChanged(Location location) {
		/** ���݈ʒu�X�V */
		sMyLocation = EagleEyeUtil.getMyLocation(sLocMgr);
		if(sMyLocation == null){
			Toast.makeText(getApplicationContext(), 
					getString(R.string.getLctErrorMsg), 
					MainForm.MSGTIME).show();
		}

		/** �ړI�n�܂ł̋����A�������\���̍ہA�X�V */
		if(naviFlg == true){
			getResult(sLocation.getLatitude(), sLocation.getLongitude());
		}
	}



	/**
	 * �A�v��back ground�ړ����C�x���g
	 * 
	 * �A�v����back ground�Ɉڍs�����ۂɌĂяo��
	 * 
	 * @param �Ȃ�
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onPause() {
		super.onPause();

		/** �Z���T�[�I�� */
		sLocMgr.removeUpdates(this);
		sSnsMgr.unregisterListener(this);
	}



	/**
	 * �A�v��fore ground�ړ����C�x���g
	 * 
	 * �A�v����fore ground�Ɉڍs�����ۂɌĂяo��
	 * 
	 * @param �Ȃ�
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onResume() {
		super.onResume();

		/** �Z���T�[�J�n */
		sLocMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

		List<Sensor> sensors = sSnsMgr.getSensorList(Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			sSnsMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}




	
	/*****************************************************
	 * �v�Z�������\�b�h
	 * 
	 * ���C����ʂ̓���C�x���g����Ăяo����郁�\�b�h
	 *****************************************************/

	/**
	 * �ړI�n�܂ł̋����A�����v�Z���\�b�h
	 * 
	 * �ړI�n�܂ł̋����ƕ������v�Z����
	 * 
	 * @param �Ȃ�
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void getResult(double latitude, double longitude){
		/** ���ʕϐ��錾 */
		String unit = getString(R.string.meter);

		/** �����擾���� */
		double distance = EagleEyeUtil.getDst(sMyLocation, latitude, longitude);
		ResultValue.setsDistance(distance);
		BigDecimal resultDst = new BigDecimal(distance);

		/** �����ɑΉ����ĕ\�L�ύX */
		if(resultDst.compareTo(new BigDecimal(1000.0)) >= 0){
			resultDst = resultDst.divide(new BigDecimal(1000.0));
			unit = getString(R.string.kiloMeter);
		}

		/** �����_3�ʖ����l�̌ܓ� */
		resultDst = resultDst.setScale(3, RoundingMode.HALF_UP);
		ResultValue.setsPrintDistance(resultDst + unit);

		/** �ړI�n�܂ł̋������ݒ苗���������`�F�b�N */
		if(resultDst.compareTo(new BigDecimal(getString(R.string.arrivalDistance))) < 0 && unit == getString(R.string.meter)){
			BitmapDrawable bmd = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.arrival));
			EagleEyeUtil.getArrivalResult(bmd, getResources());

			LinearLayout drcView = (LinearLayout) findViewById(R.id.FullDirectionArrow);
			drcView.setBackgroundDrawable(bmd);

			/** �A���[���N�� */
			if(sSettingAlarm == MainForm.FLG_ON){
				MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
				mp.start();
			}

			// �i�r�I��
			naviFlg = false;

			// �����Z���T�[�I��
			if(sSnsMgr != null){
				sSnsMgr.unregisterListener(this);
			}
		}else{
			// �����v�Z
			EagleEyeUtil.getDirection(sMyLocation, sLocation, getResources());

			/** ���������쐬 */ 
			BigDecimal bdDrc = EagleEyeUtil.roundF2Bd((float) ResultValue.getsDirection(), 0);
			EagleEyeUtil.getStrDrc(bdDrc, (float) ResultValue.getsDirection(), getResources());

			if(naviFlg == false){
				naviFlg = true;
			}
		}
	}

	
	
	/**
	 * �����A�����\�����\�b�h
	 * 
	 * ���C����ʂ̋����ƕ������i��󔲂��j�ɒl��\������
	 * 
	 * @param �Ȃ�
	 * @return�@�Ȃ�
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setResult() {
		/** instance */
		TextView dst = (TextView) findViewById(R.id.FullDistance);
		TextView drc = (TextView) findViewById(R.id.FullDirection);

		/** �����ƕ����\�� */
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
