package kms2.EagleEye;

import java.sql.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditDataNameForm extends Activity {
	private static Bundle sExtras;

	/*****************************************************
	 * イベントメソッド
	 * 
	 * 端末の動作によって呼び出される処理
	 *****************************************************/

	/**
	 * 位置データ名編集画面作成イベント
	 * 
	 * 位置データ名編集画面が作成される際に呼び出し
	 * 
	 * @param Bundle
	 * @return　void
	 * 
	 * @version 1.0, 13 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.edit_dataname);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		/** instance */
		TextView titleLeftText = (TextView) findViewById(R.id.left_text);
		TextView titleRightText = (TextView) findViewById(R.id.right_text);
		TextView objectText = (TextView) findViewById(R.id.EditObjext);
		EditText editText = (EditText) findViewById(R.id.EditEdit);
		Button okBtn = (Button) findViewById(R.id.EditOkButton);
		Button cancelBtn = (Button) findViewById(R.id.EditCancelButton);

		/** タイトルバー作成 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		/** 編集前表示 */
		sExtras = getIntent().getExtras();
		String dataname = sExtras.getString(MainForm.INTENT_DATANAME);
		if(dataname != null){
			editText.setText(dataname);
		}
		
		/** 編集対象文字色変更 */
		if(sExtras.getInt(MainForm.INTENT_MYLOCATION_FLG) == MainForm.FLG_ON){
			objectText.setText(getString(R.string.editObjectMyLocation));
			objectText.setTextColor(getResources().getColor(R.color.skyblue));
		}else{
			objectText.setText(getString(R.string.editObjectLocation));
			objectText.setTextColor(getResources().getColor(R.color.orange));
		}

		/** 目的地住所入力欄イベントリスナー */
		editText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					accessLocationDB();
					checkMaxNum();
					if(sExtras.getInt(MainForm.INTENT_LIST_FLG) == MainForm.FLG_OFF){
						displayDataList();
					}
					finish();
				}
				return false;
			}
		});

		/** OKボタンイベントリスナー */
		okBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				accessLocationDB();
				checkMaxNum();
				if(sExtras.getInt(MainForm.INTENT_LIST_FLG) == MainForm.FLG_OFF){
					displayDataList();
				}
				finish();
			}
		});

		/** Cancelボタンイベントリスナー */
		cancelBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	
	
	/**
	 * 初回起動時ソフトウェアキーボード表示イベント
	 * 
	 * 画面初期表示時、
	 * ソフトウェアキーボードを表示する。
	 * 
	 * @param boolean
	 * @return　void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	public void onWindowFocusChanged(boolean hasFocus) { 
		super.onWindowFocusChanged(hasFocus);

		/** instance */
		EditText editText = (EditText) findViewById(R.id.EditEdit);

		Handler mHandler = new Handler();
		
		if(hasFocus && editText == getCurrentFocus()) { 
			Message m = Message.obtain(mHandler, new Runnable() { 
				@Override 
				public void run() { 
					/** instance */
					EditText editText = (EditText) findViewById(R.id.EditEdit);

					InputMethodManager manager = (InputMethodManager) 
					getSystemService(INPUT_METHOD_SERVICE); 
					manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT); 
				} 
			});
			mHandler.sendMessage(m); 
		} 
	} 


	//	/**
	//	 * 戻りボタンイベント
	//	 * 
	//	 * 端末の戻るボタンが押下された際に呼び出し
	//	 * 
	//	 * @param int, KeyEvent
	//	 * @return　boolean
	//	 * 
	//	 * @version 1.0, 13 Jan, 2010
	//	 * @author kurisu
	//	 */
	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		displaySelectPreservation();
	//		finish();
	//		return false;
	//	}


	/*****************************************************
	 * 計算処理メソッド
	 * 
	 * 位置データ名編集画面の動作イベントから呼び出されるメソッド
	 *****************************************************/

	/**
	 * DBアクセスメソッド
	 * 
	 * DBに接続し、追加か更新を行う
	 * 
	 * @param int, KeyEvent
	 * @return　boolean
	 * 
	 * @version 1.0, 13 Jan, 2010
	 * @author kurisu
	 */
	public boolean accessLocationDB() {
		/** instance */
		EditText edittext = (EditText) findViewById(R.id.EditEdit);

		/** DB　Insert or Update値設定 */
		ContentValues values = new ContentValues();
		Date now = new Date(System.currentTimeMillis());
		values.put(Locations.Location.CREATE_DATE, Locations.Location.DATE_FORMAT.format(now).toString());
		values.put(Locations.Location.OBJECT, sExtras.getInt(MainForm.INTENT_MYLOCATION_FLG));
		values.put(Locations.Location.NAME, edittext.getText().toString());
		values.put(Locations.Location.ADDRESS, sExtras.getString(MainForm.INTENT_ADDRESS));
		values.put(Locations.Location.LATITUDE, sExtras.getString(MainForm.INTENT_LATITUDE));
		values.put(Locations.Location.LONGITUDE, sExtras.getString(MainForm.INTENT_LONGITUDE));

		Intent intent = getIntent();
		intent.setData(Locations.Location.CONTENT_URI);

		/** DB Insert or Update */
		if(sExtras.getInt(MainForm.INTENT_LIST_FLG) == MainForm.FLG_ON){
			String selection = Locations.Location.ID + MainForm.EQUAL + sExtras.getString(MainForm.INTENT_ID);
			getContentResolver().update(intent.getData(), values, selection, null);
		}else{
			getContentResolver().insert(intent.getData(), values);
		}
		return true;
	}



	//	/**
	//	 * 保存対象位置選択画面表示
	//	 * 
	//	 * 遷移前の画面が保存対象位置選択画面の場合、画面を表示する
	//	 * 
	//	 * @param なし
	//	 * @return　なし
	//	 * 
	//	 * @version 1.0, 13 Jan, 2010
	//	 * @author kurisu
	//	 */
	//	public void displaySelectPreservation() {
	//		if(sExtras.getInt(MainForm.INTENT_LIST_FLG) == MainForm.FLG_OFF){
	//			Intent intent = new Intent(EditDataNameForm.this, SelectPreservationForm.class);
	//			intent.putExtras(sExtras);
	//			startActivity(intent);
	//		}
	//	}
	//
	//

	/**
	 * 位置データ一覧画面表示
	 * 
	 * 位置データ一覧画面を表示する
	 * 
	 * @param なし
	 * @return　なし
	 * 
	 * @version 1.0, 13 Jan, 2010
	 * @author kurisu
	 */
	public void displayDataList() {
		Intent intent = new Intent(EditDataNameForm.this, DataListForm.class);
		intent.putExtra(MainForm.INTENT_GMAIL_LIST_FLG, MainForm.FLG_OFF);

		startActivity(intent);
	}



	/**
	 * 位置データ件数調整メソッド
	 * 
	 * 位置データの件数を数え、100件超の場合、最古データを削除する
	 * 
	 * @param なし
	 * @return　boolean
	 * 
	 * @version 1.0, 1 Feb, 2010
	 * @author kurisu
	 */
	public boolean checkMaxNum() {
		Intent intent = getIntent();
		intent.setData(Locations.Location.CONTENT_URI);

		Cursor cursor = managedQuery(intent.getData(), null, null, null, null);
		if(cursor.getCount() > Integer.valueOf(getString(R.string.editListMax))){
			cursor.moveToLast();
			String id = cursor.getString(cursor.getColumnIndex(Locations.Location.ID));
			String where = Locations.Location.ID + MainForm.EQUAL + id;
			getContentResolver().delete(intent.getData(), where, null);
		}

		return true;
	}
}
