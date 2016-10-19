package kms2.EagleEye;

import java.io.IOException;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

public class MapForm extends MapActivity{
	public static MapController mapCtrl;
	private static LinearLayout sSeachLayout;
	private static LinearLayout sConfirmationLayout;
	private static EditText sSeachEdit;
	private static TextView sAddress;
	private static TextView sLatitude;
	private static TextView sLongitude;
	private static Context context;
	private static int mZoomLevel;

	private GeoPoint mGeoPoint;

	private static final int ZOOM_MAX = 20;
	private static final int ZOOM_MIN = 1;
	private static final int BETWEEN_ZOOM_MAX = 30;



	/*****************************************************
	 * �C�x���g���\�b�h
	 * 
	 * �[���̓���ɂ���ČĂяo����鏈��
	 *****************************************************/

	/**
	 * �}�b�v��ʍ쐬�C�x���g
	 * 
	 * �}�b�v��ʂ��쐬�����ۂɌĂяo��
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
		setContentView(R.layout.map);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		context = this;

		/** instance */
		TextView titleLeftText = (TextView) findViewById(R.id.left_text);
		TextView titleRightText = (TextView) findViewById(R.id.right_text);
		sSeachLayout = (LinearLayout) findViewById(R.id.SeachLayout);
		sConfirmationLayout = (LinearLayout) findViewById(R.id.ConfirmationLayout);
		sSeachEdit = (EditText) findViewById(R.id.MapSearchEdit);
		sAddress = (TextView) findViewById(R.id.MapAddress);
		sLatitude = (TextView) findViewById(R.id.MapLatitude);
		sLongitude = (TextView) findViewById(R.id.MapLongitude);
		Button seachBtn= (Button) findViewById(R.id.MapSearchButton);
		Button okBtn= (Button) findViewById(R.id.OkButton);
		MapView mapView = (MapView)findViewById(R.id.MapView);
		ZoomControls zoomControls = (ZoomControls)findViewById(R.id.ZoomControls);

		/** �^�C�g���o�[�쐬 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		/** �}�b�v�R���g���[���쐬 */
		mapCtrl = mapView.getController();
		mZoomLevel = Integer.valueOf(getString(R.string.defaultZoom));
		mapCtrl.setZoom(mZoomLevel);

		/** ���ݒn�擾 */
		Bundle extras = getIntent().getExtras();
		int latitudeE6 = (int)(Double.valueOf(extras.getString(MainForm.INTENT_LATITUDE)) * 1E6);
		int longitudeE6 = (int)(Double.valueOf(extras.getString(MainForm.INTENT_LONGITUDE)) * 1E6);

		mGeoPoint = new GeoPoint(latitudeE6, longitudeE6);
		mapCtrl.setCenter(mGeoPoint);

		/** CustomOverlay */
		Bitmap myplaces = BitmapFactory.decodeResource(getResources(), R.drawable.pin_blue);
		Bitmap point = BitmapFactory.decodeResource(getResources(), R.drawable.pin_orange);
		CustomOverlay overlay = new CustomOverlay(myplaces, point, mGeoPoint);

		List<Overlay> list = mapView.getOverlays();
		list.add(overlay);
		
		/** �������͗��C�x���g���X�i�[ */
		sSeachEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					/** �\�t�g�L�[�{�[�h��\�� */
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

					try {
						Location location = EagleEyeUtil.address2point(sSeachEdit.getText().toString(), MapForm.this, getResources());
						if(location == null){
							Toast.makeText(getApplicationContext(), 
									getString(R.string.a2pErrorMsg), 
									MainForm.MSGTIME).show();
						}else{
							GeoPoint point = EagleEyeUtil.cnvLocation2Geopoint(location);
							CustomOverlay.setPoint(point);

							setCustomZoom(mGeoPoint, point);

							showsConfirmationLayout();
						}
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), 
								getString(R.string.a2pErrorMsg), 
								MainForm.MSGTIME).show();
					}
				}
				return false;
			}
		});

		/** �����{�^�� �C�x���g���X�i�[ */
		seachBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				/** �\�t�g�L�[�{�[�h��\�� */
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

				try {
					Location location = EagleEyeUtil.address2point(sSeachEdit.getText().toString(), MapForm.this, getResources());
					if(location == null){
						Toast.makeText(getApplicationContext(), 
								getString(R.string.a2pErrorMsg), 
								MainForm.MSGTIME).show();
					}else{
						GeoPoint point = EagleEyeUtil.cnvLocation2Geopoint(location);
						CustomOverlay.setPoint(point);

						setCustomZoom(mGeoPoint, point);

						showsConfirmationLayout();
					}
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), 
							getString(R.string.a2pErrorMsg), 
							MainForm.MSGTIME).show();
				}
			}
		});

		/** �Z���m�F�{�^�� �C�x���g���X�i�[ */
		okBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				setResult();
			}
		});

		/** �Y�[���{�^���i+�j �C�x���g���X�i�[ */
		zoomControls.setOnZoomInClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mZoomLevel < ZOOM_MAX){
					mZoomLevel++;
					mapCtrl.setZoom(mZoomLevel);
				}
			}
		});

		/** �Y�[���{�^���i-�j �C�x���g���X�i�[ */
		zoomControls.setOnZoomOutClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mZoomLevel > ZOOM_MIN){
					mZoomLevel--;
					mapCtrl.setZoom(mZoomLevel);
				}
			}
		}); 
	}


	/**
	 * �߂�{�^���C�x���g
	 * 
	 * �[���̖߂�{�^�����������ꂽ�ۂɌĂяo��
	 * 
	 * @param int, KeyEvent
	 * @return�@boolean
	 * 
	 * @version 1.0, 13 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(sConfirmationLayout.getVisibility() == View.VISIBLE){
				CustomOverlay.clearPoint();
				MapForm.mapCtrl.animateTo(mGeoPoint);

				showsSeachLayout();
			}else{
				finish();
			}
		}
		return false;
	}



	/**
	 * ����N�����\�t�g�E�F�A�L�[�{�[�h��\���C�x���g
	 * 
	 * ��ʏ����\�����A
	 * �\�t�g�E�F�A�L�[�{�[�h��\������B
	 * 
	 * @param boolean
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void onWindowFocusChanged(boolean hasFocus) { 
		super.onWindowFocusChanged(hasFocus);

		Handler mHandler = new Handler();
		
		if(hasFocus && sSeachEdit == getCurrentFocus()) { 
			Message m = Message.obtain(mHandler, new Runnable() { 
				@Override 
				public void run() { 
					/** �\�t�g�L�[�{�[�h��\�� */
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager.hideSoftInputFromWindow(sSeachEdit.getWindowToken(), 0);
				} 
			});
			mHandler.sendMessage(m); 
		} 
	} 


	//	/**
	//	 * ���j���[�{�^������N�����C�x���g
	//	 * 
	//	 * �A�v���P�[�V�����N����A
	//	 * ���C����ʂł̃��j���[���������ɌĂяo��
	//	 * 
	//	 * @param Menu
	//	 * @return�@boolean
	//	 * 
	//	 * @version 1.0, 4 Jan, 2010
	//	 * @author kurisu
	//	 */
	//	@Override
	//	public boolean onCreateOptionsMenu(Menu menu) {
	//		Menu resultMenu = EagleEyeUtil.makeMapMenu(menu);
	//		return super.onCreateOptionsMenu(resultMenu);
	//	}



	//	/**
	//	 * ���j���[���ڑI���C�x���g
	//	 * 
	//	 * ���j���[���̍��ڂ��I�����ꂽ���ɌĂяo��
	//	 * 
	//	 * @param MenuItem
	//	 * @return�@boolean
	//	 * 
	//	 * @version 1.0, 4 Jan, 2010
	//	 * @author kurisu
	//	 */
	//	@Override
	//	public boolean onOptionsItemSelected(MenuItem item) {
	//		// ���j���[��ʑJ�ڃ��\�b�h���s
	//		Transition(item.getItemId());
	//
	//		return true;
	//	}






	/*****************************************************
	 * �v�Z�������\�b�h
	 * 
	 * �}�b�v��ʂ̓���C�x���g����Ăяo����郁�\�b�h
	 *****************************************************/

	/**
	 * ���ʑ��M�C�x���g
	 * 
	 * �}�b�v��
	 * 
	 * @param �Ȃ�
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setResult() {
		Intent intent = new Intent(MapForm.this, MainForm.class);

		intent.putExtra(MainForm.INTENT_ADDRESS, sAddress.getText());
		intent.putExtra(MainForm.INTENT_LATITUDE, sLatitude.getText());
		intent.putExtra(MainForm.INTENT_LONGITUDE, sLongitude.getText());
		intent.putExtra(MainForm.INTENT_READ_FLG, true);
		setResult(RESULT_OK, intent);

		finish();
	}



	/**
	 * �����^�u�\������
	 * 
	 * �w��ʒu�̊m�F�^�u��\������
	 * 
	 * @param �Ȃ�
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static void showsSeachLayout() {
		sSeachEdit.setText(MainForm.BLANK);
		sConfirmationLayout.setVisibility(View.INVISIBLE);
		sSeachLayout.setVisibility(View.VISIBLE);
	}



	/**
	 * �m�F�^�u�\������
	 * 
	 * �w��ʒu�̊m�F�^�u��\������
	 * 
	 * @param �Ȃ�
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static void showsConfirmationLayout() {
		sSeachLayout.setVisibility(View.INVISIBLE);
		sConfirmationLayout.setVisibility(View.VISIBLE);
	}



	/**
	 * �w��ʒu�\������
	 * 
	 * �w��ʒu���擾���A�\������
	 * 
	 * @param GeoPoint
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static void showPointData(GeoPoint point) {
		String address = "";
		Location location = new Location("");
		location.setLatitude((double) point.getLatitudeE6() / 1E6);
		location.setLongitude((double) point.getLongitudeE6() / 1E6);
		try {
			address = EagleEyeUtil.point2address(location, context);
		} catch (IOException e) {
			address = "";
		}

		sAddress.setText(address);
		sLatitude.setText(String.valueOf(location.getLatitude()));
		sLongitude.setText(String.valueOf(location.getLongitude()));
	}



	/**
	 * �{���v�Z����
	 * 
	 * ���݈ʒu�ƖړI�n�̋����ɉ����āA�Y�[���𒲐�����
	 * 
	 * @param GeoPoint
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public static void setCustomZoom(GeoPoint myPoint, GeoPoint point) {
		Location myLocation = EagleEyeUtil.cnvGeopoint2Location(myPoint);
		double between = EagleEyeUtil.getDst(myLocation, (point.getLatitudeE6() / 1E6), (point.getLongitudeE6() / 1E6));

		int checkDistance = BETWEEN_ZOOM_MAX;
		int zoom = ZOOM_MAX;
		for(; zoom > ZOOM_MIN; zoom--){
			if(between <= checkDistance){
				break;
			}else{
				checkDistance = checkDistance * 2;
			}
		}

		mZoomLevel = zoom;
		mapCtrl.setZoom(mZoomLevel);
	}



	protected boolean isRouteDisplayed() {
		return false;
	}
}
