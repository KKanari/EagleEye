/**
 * Copyright (c) 2007 KMS2 Co.,Ltd.All Rights Reserved.
 */

package kms2.EagleEye;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

public class MainForm extends Activity implements LocationListener, SensorEventListener{
	public static final String BLANK = "";
	public static final String SPACE = " ";
	public static final String EQUAL = "=";
	public static final String NEWLINE = "\n";
	public static final String OR = "OR";
	public static final String LIKE = "LIKE";
	public static final String WILD_CARD = "%";
	public static final String ENC = "'";
	public static final String DENC = "\"";
	public static final String SLASH = "/";
	public static final String PRN_FRONT = "<";
	public static final String PRN_BACK = ">";
	public static final String COMMA = ",";
	public static final int FLG_DEFAULT = -1; 
	public static final int FLG_ON = 1; 
	public static final int FLG_OFF = 0;
	public static final int MSGTIME = Toast.LENGTH_LONG;
	public static final int MAIN_ID = 0;
	public static final String MODE_ID = "MODE_ID";
	public static final int MENU_ID_DATA_LIST = (Menu.FIRST + 1);
	public static final int MENU_ID_SELECT_GMAIL = (Menu.FIRST + 2); 
	public static final int MENU_ID_SETTING = (Menu.FIRST + 3); 
	public static final int MAP = (Menu.FIRST + 4); 
	public static final int FULL_SCREEN = (Menu.FIRST + 5); 
	public static final String INTENT_ADDRESS = "address";
	public static final String INTENT_LATITUDE = "latitude";
	public static final String INTENT_LONGITUDE = "longitude";
	public static final String INTENT_READ_FLG = "intentreadflg";
	public static final int SETTING_FLG = 3;
	public static final String INTENT_LIST_FLG = "listflg";
	public static final String INTENT_MYLOCATION_FLG = "mylocationflg";
	public static final String INTENT_GMAIL_LIST_FLG = "gmaillistflg";
	public static final String INTENT_ID = "id";
	public static final String INTENT_DATANAME = "dataname";
	public static final String INTENT_GMAIL = "gmail";
	public static final String INTENT_DISPLAY = "display";
	public static final String INTENT_FULL = "full";
	public static final String INTENT_ALARM = "alarm";
	public static final String INTENT_BUFFER1 = "buffer1";
	public static final String INTENT_BUFFER2 = "buffer2";
	public static final String INTENT_BUFFER3 = "buffer3";

	private LocationManager sLocMgr;
	private SensorManager sSnsMgr;
	private Location sMyLocation = new Location("");
	private String sMyAddress = BLANK;
	private Location sLocation = new Location("");
	private String sAddress = BLANK;
	private boolean naviFlg = false;
	private String sSettingGmail = ""; 
	private int sSettingDisplay = FLG_ON; 
	private int sSettingFull = FLG_OFF; 
	private int sSettingAlarm = FLG_ON; 
	private int sSettingBuffer1; 
	private int sSettingBuffer2; 
	private int sSettingBuffer3; 
	private boolean firstDisplayFlg = true;

	private final int LATITUDE_MAX = 90;
	private final int LONGTUDE_MAX = 180;
	private final int LAYOUT_RFYMAP1_WIDTH = 65;
	private final int LAYOUT_RFYMAP1_HEIGHT = 75;
	private final int LAYOUT_ARROW1_MARGIN = 30;
	private final int LAYOUT_ARROW1 = 100;
	private final int LAYOUT_RFYMAP2_WIDTH = 100;
	private final int LAYOUT_RFYMAP2_HEIGHT = 50;
	private final int LAYOUT_ARROW2 = 150;
	private final int LAYOUT_ARROW2_MARGIN = 0;



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
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		/** instance */
		TextView titleLeftText = (TextView) findViewById(R.id.left_text);
		TextView titleRightText = (TextView) findViewById(R.id.right_text);
		EditText editAddress = (EditText) findViewById(R.id.Address);
		HorizontalScrollView myAddressScrollView = (HorizontalScrollView) findViewById(R.id.MyAddressScrollView);
		Button strtBtn = (Button) findViewById(R.id.Start);
		LinearLayout rfymapLayout = (LinearLayout) findViewById(R.id.RfrMapLayout);
		Button myLocationTitleBtn = (Button) findViewById(R.id.MylocationTitle);
		Button destinationTitleBtn = (Button) findViewById(R.id.DestinationTitle);

		/** タイトルバー作成 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MSGTIME).show();
		}

		/** センサー初期化 */
		sLocMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
		sSnsMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

		/** 設定ファイル読み込み */
		if(AccessSettingFile.existSettingFile(getString(R.string.filePath), getString(R.string.fileName))){
			String[] settings = AccessSettingFile.readSettingFile(getString(R.string.filePath), getString(R.string.fileName));
			if(settings == null){
				Toast.makeText(getApplicationContext(), 
						getString(R.string.readSettingFileErrorMsg), 
						MSGTIME).show();
			}else{
				setSettings(settings);

				/** 緯度、経度非表示処理 */
				if(sSettingDisplay == FLG_OFF){
					NonDisplayLttLng();
				}
			}
		}

		/** 現在地取得 */	
		Location location = EagleEyeUtil.getMyLocation(sLocMgr);
		if(location == null){
			Toast.makeText(getApplicationContext(), 
					getString(R.string.getLctErrorMsg), 
					MSGTIME).show();
		}else{
			sMyLocation = location;
			try {
				sMyAddress = EagleEyeUtil.point2address(sMyLocation, this);
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), 
						getString(R.string.p2aErrorMsg), 
						MSGTIME).show();
			}
			if(sMyAddress.equals(BLANK)){
				Toast.makeText(getApplicationContext(), 
						getString(R.string.p2aErrorMsg), 
						MSGTIME).show();
			}
		}

		/** 現在地表示 */
		setLttLng();
		setAdd();

		/** 目的地住所入力欄イベントリスナー */
		editAddress.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					/** ソフトキーボード非表示 */
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

					/** 入力欄の住所から緯度、経度を取得 */
					EditText addressEdit = (EditText) findViewById(R.id.Address);
					Location location = null;
					try {
						location = EagleEyeUtil.address2point(addressEdit.getText().toString(), MainForm.this, getResources());
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), 
								getString(R.string.a2pErrorMsg), 
								MSGTIME).show();
					}
					if(location == null){
						Toast.makeText(getApplicationContext(), 
								getString(R.string.a2pErrorMsg), 
								MSGTIME).show();
					}else{
						sLocation = location;
						setEditLttLng();
					}
					/** フォーカス移動 */
					Button startBotton = (Button) findViewById(R.id.Start);
					startBotton.requestFocus();
				}
				return false;
			}
		});

		/** 地図参照ボタン イベントリスナー */
		rfymapLayout.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				/** 地図参照画面に遷移 */
				Intent intent = new Intent(MainForm.this, MapForm.class);
				intent.putExtra(MainForm.INTENT_LATITUDE, String.valueOf(sMyLocation.getLatitude()));
				intent.putExtra(MainForm.INTENT_LONGITUDE, String.valueOf(sMyLocation.getLongitude()));

				resetMyAddress();
				startActivityForResult(intent, MAP);
			}
		});

		/** 距離方向取得ボタン イベントリスナー */
		strtBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				/** ソフトキーボード非表示 */
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

				/** 住所再取得 */
				try {
					sMyAddress = EagleEyeUtil.point2address(sMyLocation, MainForm.this);
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), 
							getString(R.string.p2aErrorMsg), 
							MSGTIME).show();
				}
				if(sMyAddress.equals(BLANK)){
					Toast.makeText(getApplicationContext(), 
							getString(R.string.p2aErrorMsg), 
							MSGTIME).show();
				}

				// 画面住所更新
				setAdd();

				/** 入力項目欄住所ありor住所なしの処理分岐 */
				EditText addressEdit = (EditText) findViewById(R.id.Address);
				if(!String.valueOf(addressEdit.getText()).equals(BLANK)){
					/**　住所が正常なら緯度経度取得後、距離方向表示メソッド呼び出し */
					Location location = null;
					try {
						location = EagleEyeUtil.address2point(addressEdit.getText().toString(), MainForm.this, getResources());
					} catch (IOException e) {
						Toast.makeText(getApplicationContext(), 
								getString(R.string.a2pErrorMsg), 
								MSGTIME).show();
					}
					if(location == null){
						Toast.makeText(getApplicationContext(), 
								getString(R.string.a2pErrorMsg), 
								MSGTIME).show();
					}else{
						/** 画面反映 */
						sLocation = location;
						setEditLttLng();

						/**　距離、方向表示 */
						getEdit();
						getResult(sLocation.getLatitude(), sLocation.getLongitude());

						/** 設定の全画面表示ONの場合 */
						if(sSettingFull == FLG_ON){
							Intent intent = new Intent(MainForm.this, FullScreenForm.class);
							intent = EagleEyeUtil.setLocation2Intent(intent, BLANK, String.valueOf(sLocation.getLatitude()), String.valueOf(sLocation.getLongitude()));

							resetMyAddress();
							startActivityForResult(intent, FULL_SCREEN);
						}else{
							setResult();
						}
					}
				}else{
					/** instance */
					EditText latitude = (EditText) findViewById(R.id.Latitude);
					EditText longitude = (EditText) findViewById(R.id.Longitude);

					/** 入力欄緯度、経度整合性チェック */
					if(!EagleEyeUtil.numCheck(latitude.getText().toString(), LATITUDE_MAX) || !EagleEyeUtil.numCheck(longitude.getText().toString(), LONGTUDE_MAX)){
						Toast.makeText(getApplicationContext(), 
								getString(R.string.dstDataErrorMsg), 
								MSGTIME).show(); 
					}else{
						/**　距離、方向表示 */
						getEdit();
						getResult(sLocation.getLatitude(), sLocation.getLongitude());

						/** 設定の全画面表示ONの場合 */
						if(sSettingFull == FLG_ON){
							Intent intent = new Intent(MainForm.this, FullScreenForm.class);
							intent = EagleEyeUtil.setLocation2Intent(intent, BLANK, String.valueOf(sLocation.getLatitude()), String.valueOf(sLocation.getLongitude()));

							resetMyAddress();
							startActivityForResult(intent, FULL_SCREEN);
						}else{
							setResult();
						}
					}
				}
			}
		});

		/** 現在位置タイトルボタン イベントリスナー */
		myLocationTitleBtn.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				/** instance */
				Button myLocationTitleBtn = (Button) findViewById(R.id.MylocationTitle);

				if(hasFocus == true){
					myLocationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_sp));
				}else{
					myLocationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_d));
				}
			}
		});

		/** 現在位置タイトルボタン イベントリスナー */
		myLocationTitleBtn.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/** instance */
				Button myLocationTitleBtn = (Button) findViewById(R.id.MylocationTitle);

				if(event.getAction() == MotionEvent.ACTION_UP 
						|| event.getX() < v.getPaddingLeft() || (v.getPaddingLeft() + v.getWidth()) < event.getX()
						|| event.getY() < v.getPaddingTop() || (v.getPaddingTop() + v.getHeight()) < event.getY()){
					myLocationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_d));
				}else{
					myLocationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_tp));
				}
				return false;
			}
		});

		/** 現在位置タイトルボタン イベントリスナー */
		myLocationTitleBtn.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				/** 住所再取得 */
				try {
					sMyAddress = EagleEyeUtil.point2address(sMyLocation, MainForm.this);
				} catch (IOException e) {
					Toast.makeText(getApplicationContext(), 
							getString(R.string.p2aErrorMsg), 
							MSGTIME).show();
				}
				if(sMyAddress.equals(BLANK)){
					Toast.makeText(getApplicationContext(), 
							getString(R.string.p2aErrorMsg), 
							MSGTIME).show();
				}

				// 画面住所更新
				setAdd();

				Intent intent = new Intent(MainForm.this, EditDataNameForm.class);

				intent = EagleEyeUtil.setLocation2Intent(intent, sMyAddress, String.valueOf(sMyLocation.getLatitude()), String.valueOf(sMyLocation.getLongitude()));
				intent.putExtra(MainForm.INTENT_MYLOCATION_FLG, MainForm.FLG_ON);

				resetMyAddress();
				startActivityForResult(intent, MAIN_ID);

				return false;
			}
		});

		/** 目的地タイトルボタン イベントリスナー */
		destinationTitleBtn.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				/** instance */
				Button locationTitleBtn = (Button) findViewById(R.id.DestinationTitle);

				if(hasFocus == true){
					locationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_sp));
				}else{
					locationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_d));
				}
			}
		});

		/** 目的地タイトルボタン イベントリスナー */
		destinationTitleBtn.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/** instance */
				Button myLocationTitleBtn = (Button) findViewById(R.id.DestinationTitle);

				if(event.getAction() == MotionEvent.ACTION_UP 
						|| event.getX() < v.getPaddingLeft() || (v.getPaddingLeft() + v.getWidth()) < event.getX()
						|| event.getY() < v.getPaddingTop() || (v.getPaddingTop() + v.getHeight()) < event.getY()){
					myLocationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_d));
				}else{
					myLocationTitleBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.save_button_tp));
				}
				return false;
			}
		});

		/** 目的地タイトルボタン イベントリスナー */
		destinationTitleBtn.setOnLongClickListener(new View.OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				/** instance */
				EditText editAddress = (EditText) findViewById(R.id.Address);
				EditText editLatitude = (EditText) findViewById(R.id.Latitude);
				EditText editLongitude = (EditText) findViewById(R.id.Longitude);

				/** 入力欄緯度、経度整合性チェック */
				if(String.valueOf(editAddress.getText()).equals(BLANK)
						&& (editLatitude.getText().toString().equals(BLANK)
								|| editLongitude.getText().toString().equals(BLANK))){
					Toast.makeText(getApplicationContext(), 
							getString(R.string.dstDataErrorMsg), 
							MSGTIME).show(); 
				}else if((!editLatitude.getText().toString().equals(BLANK) && !EagleEyeUtil.numCheck(editLatitude.getText().toString(), LATITUDE_MAX))
						|| (!editLongitude.getText().toString().equals(BLANK)) && !EagleEyeUtil.numCheck(editLongitude.getText().toString(), LONGTUDE_MAX)){
					Toast.makeText(getApplicationContext(), 
							getString(R.string.dstDataErrorMsg), 
							MSGTIME).show(); 
				}else{
					Intent intent = new Intent(MainForm.this, EditDataNameForm.class);

					intent = EagleEyeUtil.setLocation2Intent(intent, editAddress.getText().toString(), editLatitude.getText().toString(), editLongitude.getText().toString());
					intent.putExtra(MainForm.INTENT_MYLOCATION_FLG, MainForm.FLG_OFF);

					resetMyAddress();
					startActivityForResult(intent, MAIN_ID);
				}
				return false;
			}
		});

		/** 現在位置住所欄 イベントリスナー */
		myAddressScrollView.setOnFocusChangeListener(new View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus){
				/** instance */
				HorizontalScrollView myAddressScrollView = (HorizontalScrollView) findViewById(R.id.MyAddressScrollView);
				if(firstDisplayFlg == false){
					if(hasFocus == true){
						myAddressScrollView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_selector_background_focus_9));
					}else{
						myAddressScrollView.setBackgroundColor(getResources().getColor(R.color.white));
					}
				}else{
					firstDisplayFlg = false;
				}
			}
		});

		/** 現在位置住所欄 イベントリスナー */
		myAddressScrollView.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				/** instance */
				HorizontalScrollView myAddressScrollView = (HorizontalScrollView) findViewById(R.id.MyAddressScrollView);

				if(event.getAction() == MotionEvent.ACTION_UP){
					myAddressScrollView.setBackgroundColor(getResources().getColor(R.color.white));
				}else{
					myAddressScrollView.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_selector_background_pressed_9));
				}
				return false;
			}
		});
	}



	/**
	 * メニューボタン初回起動時イベント
	 * 
	 * アプリケーション起動後、
	 * メイン画面でのメニュー初押下時に呼び出し
	 * 
	 * @param Menu
	 * @return　boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Menu resultMenu = EagleEyeUtil.makeMainMenu(menu);
		return super.onCreateOptionsMenu(resultMenu);
	}



	/**
	 * メニュー項目選択イベント
	 * 
	 * メニュー内の項目が選択された時に呼び出し
	 * 
	 * @param MenuItem
	 * @return　boolean
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// メニュー画面遷移メソッド実行
		transition(item.getItemId());

		return true;
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
		if(naviFlg == true){
			/** instance */
			LinearLayout drcView = (LinearLayout) findViewById(R.id.DirectionArrow);

			Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);

			/** 矢印方向計算 */
			float fDirection = (float) (ResultValue.getsDirection() - event.values[0]);
			if(fDirection < 0.0){
				fDirection = fDirection + 360;
			}

			// 画像生成 
			EagleEyeUtil.rotateBitmap(bitmapOrg, fDirection);

			drcView.setBackgroundDrawable(ResultValue.getsDirectionArrow());
		}
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
					MSGTIME).show();
		}

		// 画面反映
		setLttLng();

		/** 目的地までの距離、方向が表示の際、更新 */
		if(naviFlg == true){
			getResult(sLocation.getLatitude(), sLocation.getLongitude());
			setResult();
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



	/**
	 * 遷移イベント
	 * 
	 * 他画面から遷移した際、呼び出される
	 * 
	 * @param int, int, intent
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode != RESULT_OK) {
			if(requestCode == SETTING_FLG){
				/** 設定ファイル読み込み */
				if(AccessSettingFile.existSettingFile(getString(R.string.filePath), getString(R.string.fileName))){
					String[] settings = AccessSettingFile.readSettingFile(getString(R.string.filePath), getString(R.string.fileName));
					if(settings == null){
						Toast.makeText(getApplicationContext(), 
								getString(R.string.readSettingFileErrorMsg), 
								MSGTIME).show();
					}else{
						setSettings(settings);

						/** 緯度、経度非表示処理 */
						if(sSettingDisplay == FLG_OFF){
							NonDisplayLttLng();
						}else{
							displayLttLng();
						}
						HorizontalScrollView myAddressScrollView = (HorizontalScrollView) findViewById(R.id.MyAddressScrollView);
						myAddressScrollView.setBackgroundColor(getResources().getColor(R.color.white));
					}
				}
			}
			/** 入力欄更新フラグがONの場合、Editerデータ更新 */
		}else if (intent.getBooleanExtra(INTENT_READ_FLG, false)){
			sAddress = intent.getStringExtra(INTENT_ADDRESS);
			if(!intent.getStringExtra(INTENT_LATITUDE).equals(BLANK)){
				sLocation.setLatitude(Double.valueOf(intent.getStringExtra(INTENT_LATITUDE)));

				/** instance */
				TextView latitude = (TextView) findViewById(R.id.Latitude);

				/** 表示 */
				latitude.setText(intent.getStringExtra(INTENT_LATITUDE));
			}
			if(!intent.getStringExtra(INTENT_LONGITUDE).equals(BLANK)){
				sLocation.setLongitude(Double.valueOf(intent.getStringExtra(INTENT_LONGITUDE)));

				/** instance */
				TextView longitude = (TextView) findViewById(R.id.Longitude);

				/** 表示 */
				longitude.setText(intent.getStringExtra(INTENT_LONGITUDE));
			}
			setEditAdd();
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

			if(sSettingFull == FLG_OFF){
				LinearLayout drcView = (LinearLayout) findViewById(R.id.DirectionArrow);
				drcView.setBackgroundDrawable(bmd);
			}

			/** アラーム起動 */
			if(sSettingAlarm == FLG_ON){
				MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
				mp.start();
			}

			// ナビ終了
			naviFlg = false;

			//			// 方向センサー終了
			//			if(sSnsMgr != null){
			//				sSnsMgr.unregisterListener(this);
			//			}
		}else{
			// 方向計算
			EagleEyeUtil.getDirection(sMyLocation, sLocation, getResources());

			/** 方向文字作成 */ 
			BigDecimal bdDrc = EagleEyeUtil.roundF2Bd((float) ResultValue.getsDirection(), 0);
			EagleEyeUtil.getStrDrc(bdDrc, (float) ResultValue.getsDirection(), getResources());

			if(naviFlg == false){
				//				/** SensorManager */
				//				List<Sensor> sensors = sSnsMgr.getSensorList(Sensor.TYPE_ORIENTATION);
				//				if (sensors.size() > 0) {
				//					Sensor sensor = sensors.get(0);
				//					sSnsMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
				//				}
				naviFlg = true;
			}
		}
	}



	/**
	 * 画面遷移メソッド
	 * 
	 * 各画面に必要な引数を設定し遷移する
	 * 
	 * @param なし
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void transition(int item){
		/** 押されたメニューに応じて遷移 */
		switch(item){
		case MENU_ID_DATA_LIST:
			Intent intent2 = new Intent(MainForm.this, DataListForm.class);
			intent2.putExtra(INTENT_GMAIL_LIST_FLG, FLG_OFF);

			resetMyAddress();
			startActivityForResult(intent2, item);

			break;

		case MENU_ID_SELECT_GMAIL:
			Intent intent4 = new Intent(MainForm.this, SelectGmailForm.class);
			intent4.putExtra(MainForm.INTENT_GMAIL, sSettingGmail);

			resetMyAddress();
			startActivityForResult(intent4, item);

			break;

		case MENU_ID_SETTING:
			Intent intent5 = new Intent(MainForm.this, SettingForm.class);

			intent5 = EagleEyeUtil.setSetting(intent5, sSettingGmail, sSettingDisplay, sSettingFull, sSettingAlarm, sSettingBuffer1, sSettingBuffer2, sSettingBuffer3);

			resetMyAddress();
			startActivityForResult(intent5, SETTING_FLG);

			break;

		}
	}



	/**
	 * 現在位置緯度、経度表示メソッド
	 * 
	 * メイン画面の現在位置欄の緯度、経度に値を表示する
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setLttLng() {
		/** instance */
		TextView latitude = (TextView) findViewById(R.id.MyLatitude);
		TextView longitude = (TextView) findViewById(R.id.MyLongitude);

		/** 表示 */
		latitude.setText(String.valueOf(sMyLocation.getLatitude()));
		longitude.setText(String.valueOf(sMyLocation.getLongitude()));
	}



	/**
	 * 現在位置住所表示メソッド
	 * 
	 * メイン画面の現在位置欄の住所に値を表示する
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setAdd() {
		// instance
		TextView address = (TextView) findViewById(R.id.MyAddress);

		// 表示
		address.setText(sMyAddress);

		/** スクロールビューサイズ調整 */
		address.setLayoutParams(new HorizontalScrollView.LayoutParams
				(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		HorizontalScrollView addscr = (HorizontalScrollView) findViewById(R.id.MyAddressScrollView);
		addscr.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 23));
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
		TextView dst = (TextView) findViewById(R.id.Distance);
		TextView drc = (TextView) findViewById(R.id.Direction);

		/** 距離と方向表示 */
		dst.setText(ResultValue.getsPrintDistance());
		drc.setText(ResultValue.getsPrintDirection());
	}



	/**
	 * 目的地住所入力メソッド
	 * 
	 * メイン画面の目的地欄の住所に値を入力した状態にする
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setEditAdd() {
		// instance
		EditText address = (EditText) findViewById(R.id.Address);

		// EditTextに入力
		address.setText(sAddress);
	}



	/**
	 * 目的地緯度、経度入力メソッド
	 * 
	 * メイン画面の目的地欄の緯度、経度に値を入力した状態にする
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setEditLttLng() {
		/** instance */
		EditText latitude = (EditText) findViewById(R.id.Latitude);
		EditText longitude = (EditText) findViewById(R.id.Longitude);

		/** EditTextに入力 */
		latitude.setText(String.valueOf(sLocation.getLatitude()));
		longitude.setText(String.valueOf(sLocation.getLongitude()));
	}



	/**
	 * 入力欄取得メソッド
	 * 
	 * 入力欄に入力されている値を取得する
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void getEdit() {
		Location location = new Location("");
		String address = "";

		/** instance */
		EditText editAddress = (EditText) findViewById(R.id.Address);
		EditText editLatitude = (EditText) findViewById(R.id.Latitude);
		EditText editLongitude = (EditText) findViewById(R.id.Longitude);

		if(editAddress.getText().toString().length() != 0){
			address = editAddress.getText().toString();
		}
		if(editLatitude.getText().toString().length() != 0){
			location.setLatitude(Double.valueOf(editLatitude.getText().toString()));
		}
		if(editLongitude.getText().toString().length() != 0){
			location.setLongitude(Double.valueOf(editLongitude.getText().toString()));
		}

		sAddress = address;
		sLocation = location;
	}



	/**
	 * 緯度経度表示メソッド
	 * 
	 * メイン画面の緯度と経度の項目を表示にする
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void displayLttLng() {
		/** instance */
		LinearLayout myLttLayout = (LinearLayout) findViewById(R.id.MyLatitudeLayout);
		LinearLayout myLngLayout = (LinearLayout) findViewById(R.id.MyLongitudeLayout);
		LinearLayout lttLayout = (LinearLayout) findViewById(R.id.LatitudeLayout);
		LinearLayout lngLayout = (LinearLayout) findViewById(R.id.LongitudeLayout);
		LinearLayout insertLayout = (LinearLayout) findViewById(R.id.InsertLayout);
		LinearLayout rfymapLayout = (LinearLayout) findViewById(R.id.RfrMapLayout);
		TextView rfymapStr = (TextView) findViewById(R.id.RfrMapStr);
		LinearLayout arrowMargin = (LinearLayout) findViewById(R.id.ArrowMargin);
		LinearLayout directionArrow = (LinearLayout) findViewById(R.id.DirectionArrow);

		/** 非表示処理 */
		myLttLayout.setVisibility(View.VISIBLE);
		myLngLayout.setVisibility(View.VISIBLE);
		lttLayout.setVisibility(View.VISIBLE);
		lngLayout.setVisibility(View.VISIBLE);

		myLttLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		myLngLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		lttLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		lngLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		/** 地図参照ボタンレイアウト変更 */
		rfymapLayout.setLayoutParams(new LinearLayout.LayoutParams(LAYOUT_RFYMAP1_WIDTH, LAYOUT_RFYMAP1_HEIGHT));
		rfymapStr.setText(getString(R.string.rfrMap1));
		insertLayout.setGravity(Gravity.LEFT);

		/** 矢印レイアウト変更 */
		directionArrow.setLayoutParams(new LinearLayout.LayoutParams(LAYOUT_ARROW1, LAYOUT_ARROW1));
		arrowMargin.setLayoutParams(new LinearLayout.LayoutParams(LAYOUT_ARROW1_MARGIN, LayoutParams.WRAP_CONTENT));
	}



	/**
	 * 緯度経度非表示メソッド
	 * 
	 * メイン画面の緯度と経度の項目を非表示にする
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void NonDisplayLttLng() {
		/** instance */
		LinearLayout myLttLayout = (LinearLayout) findViewById(R.id.MyLatitudeLayout);
		LinearLayout myLngLayout = (LinearLayout) findViewById(R.id.MyLongitudeLayout);
		LinearLayout lttLayout = (LinearLayout) findViewById(R.id.LatitudeLayout);
		LinearLayout lngLayout = (LinearLayout) findViewById(R.id.LongitudeLayout);
		LinearLayout insertLayout = (LinearLayout) findViewById(R.id.InsertLayout);
		LinearLayout rfymapLayout = (LinearLayout) findViewById(R.id.RfrMapLayout);
		TextView rfymapStr = (TextView) findViewById(R.id.RfrMapStr);
		LinearLayout arrowMargin = (LinearLayout) findViewById(R.id.ArrowMargin);
		LinearLayout directionArrow = (LinearLayout) findViewById(R.id.DirectionArrow);

		/** 非表示処理 */
		myLttLayout.setVisibility(View.INVISIBLE);
		myLngLayout.setVisibility(View.INVISIBLE);
		lttLayout.setVisibility(View.INVISIBLE);
		lngLayout.setVisibility(View.INVISIBLE);

		myLttLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
		myLngLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
		lttLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
		lngLayout.setLayoutParams(new LinearLayout.LayoutParams(0, 0));

		/** 地図参照ボタンレイアウト変更 */
		rfymapLayout.setLayoutParams(new LinearLayout.LayoutParams(LAYOUT_RFYMAP2_WIDTH, LAYOUT_RFYMAP2_HEIGHT));
		rfymapStr.setText(getString(R.string.rfrMap2));
		insertLayout.setGravity(Gravity.RIGHT);

		/** 矢印レイアウト変更 */
		directionArrow.setLayoutParams(new LinearLayout.LayoutParams(LAYOUT_ARROW2, LAYOUT_ARROW2));
		arrowMargin.setLayoutParams(new LinearLayout.LayoutParams(LAYOUT_ARROW2_MARGIN, LayoutParams.WRAP_CONTENT));
	}



	/**
	 * 設定値格納メソッド
	 * 
	 * 読み込んだ設定値を変数に格納する。
	 * 
	 * @param String[]
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void setSettings(String[] settings) {
		sSettingGmail = settings[0];
		sSettingDisplay = Integer.valueOf(settings[1]); 
		sSettingFull = Integer.valueOf(settings[2]); 
		sSettingAlarm = Integer.valueOf(settings[3]); 
		sSettingBuffer1 = Integer.valueOf(settings[4]); 
		sSettingBuffer2 = Integer.valueOf(settings[5]); 
		sSettingBuffer3 = Integer.valueOf(settings[6]); 
	}



	/**
	 * 現在位置住所欄リセットメソッド
	 * 
	 * 現在位置部の住所欄を初期表示の状態に戻す。
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void resetMyAddress() {
		/** instance */
		HorizontalScrollView myAddressScrollView = (HorizontalScrollView) findViewById(R.id.MyAddressScrollView);

		myAddressScrollView.setBackgroundColor(getResources().getColor(R.color.white));
		firstDisplayFlg = false;
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