package kms2.EagleEye;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DataListForm extends Activity {
	private ArrayList<String[]> mDataList = new ArrayList<String[]>();
	private String[] mData;
	private String mSelection = null;
	
	private final int DATA_INDEX_ID = 0;
	private final int DATA_INDEX_CREATED = 1;
	private final int DATA_INDEX_OBJECT = 2;
	private final int DATA_INDEX_NAME = 3;
	private final int DATA_INDEX_ADDRESS = 4;
	private final int DATA_INDEX_LATITUDE = 5;
	private final int DATA_INDEX_LONGTUDE = 6;
	private final String MAILTO_URI = "mailto:";


	/*****************************************************
	 * イベントメソッド
	 * 
	 * 端末の動作によって呼び出される処理
	 *****************************************************/

	/**
	 * 位置データ一覧画面作成イベント
	 * 
	 * 位置データ一覧画面が作成される際に呼び出し
	 * 
	 * @param Bundle
	 * @return　void
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.data_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		/** instance */
		TextView titleLeftText = (TextView) findViewById(R.id.left_text);
		TextView titleRightText = (TextView) findViewById(R.id.right_text);
		final EditText schEdit = (EditText) findViewById(R.id.SearchEdit);
		Button schBtn = (Button) findViewById(R.id.SearchButton);
		final ListView list = (ListView)findViewById(R.id.DataList);

		/** タイトルバー作成 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		try{
			// 初期リスト作成
			mDataList = getList(mSelection);
			
			// List表示
			setList(list);
		}catch (Exception e) {
			Toast.makeText(getApplicationContext(), getString(R.string.readDBErrorMsg), MainForm.MSGTIME).show();
		}
		
		/** 検索文字入力欄イベントリスナー */
		schEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					/** ソフトキーボード非表示 */
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

					/** 検索条件リスト作成 */
					mSelection = EagleEyeUtil.makeSearchWhere(schEdit.getText().toString(), schEdit.getText().toString());
					mDataList = getList(mSelection);

					/** List表示*/
					setList(list);
				}
				return false;
			}
		});


		/** 検索ボタンイベントリスナー */
		schBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				/** ソフトキーボード非表示 */
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

				/** 検索条件リスト作成 */
				mSelection = EagleEyeUtil.makeSearchWhere(schEdit.getText().toString(), schEdit.getText().toString());
				mDataList = getList(mSelection);

				/** List表示*/
				setList(list);
			}
		});

		/** リストイベントリスナー */
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 選択アイテム取得
				mData = mDataList.get(position);
				/** 処理内容選択ダイアログ表示 */
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DataListForm.this);
				alertDialogBuilder.setTitle(getString(R.string.prcTitle));
				CharSequence[] processing = { getString(R.string.prcReference), getString(R.string.prcSend), 
						getString(R.string.prcEdit), getString(R.string.prcDelete)};

				alertDialogBuilder.setItems(processing, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0:
							/** メイン画面に反映処理 */
							Intent intent1 = new Intent(DataListForm.this, MainForm.class);
							intent1.putExtra(MainForm.INTENT_ADDRESS, mData[DATA_INDEX_ADDRESS]);
							intent1.putExtra(MainForm.INTENT_LATITUDE, mData[DATA_INDEX_LATITUDE]);
							intent1.putExtra(MainForm.INTENT_LONGITUDE, mData[DATA_INDEX_LONGTUDE]);
							intent1.putExtra(MainForm.INTENT_READ_FLG, true);
							setResult(RESULT_OK, intent1);

							dialog.dismiss();
							finish();

							break;

						case 1:
							// メール起動処理
							startMail();

							dialog.dismiss();

							break;

						case 2:
							/** 編集処理 */
							Intent intent2 = new Intent(DataListForm.this, EditDataNameForm.class);
							intent2.putExtra(MainForm.INTENT_ID, mData[DATA_INDEX_ID]);
							intent2.putExtra(MainForm.INTENT_MYLOCATION_FLG, Integer.valueOf(mData[DATA_INDEX_OBJECT]));
							intent2.putExtra(MainForm.INTENT_DATANAME, mData[DATA_INDEX_NAME]);
							intent2.putExtra(MainForm.INTENT_ADDRESS, mData[DATA_INDEX_ADDRESS]);
							intent2.putExtra(MainForm.INTENT_LATITUDE, mData[DATA_INDEX_LATITUDE]);
							intent2.putExtra(MainForm.INTENT_LONGITUDE, mData[DATA_INDEX_LONGTUDE]);
							intent2.putExtra(MainForm.INTENT_LIST_FLG, MainForm.FLG_ON);

							startActivityForResult(intent2, 0);

							dialog.dismiss();

							break;
						case 3:
							dialog.dismiss();

							/** 削除処理 */
							AlertDialog.Builder alertDeleteDialogBuilder = new AlertDialog.Builder(DataListForm.this);

							// タイトルを設定
							alertDeleteDialogBuilder.setTitle(getString(R.string.dltTitle));
							alertDeleteDialogBuilder.setMessage(getDeleteData());
							alertDeleteDialogBuilder.setPositiveButton(getString(R.string.dltOk), new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = getIntent();
									intent.setData(Locations.Location.CONTENT_URI);
									String where = Locations.Location.ID + MainForm.EQUAL + mData[DATA_INDEX_ID];
									getContentResolver().delete(intent.getData(), where, null);

									/** リスト再取得 */
									mDataList = getList(mSelection);
									setList(list);

									dialog.dismiss();
								}
							});
							alertDeleteDialogBuilder.setNeutralButton(getString(R.string.dltCancel), new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							alertDeleteDialogBuilder.setCancelable(false);
							alertDeleteDialogBuilder.create().show();

							break;
						}
					}
				});
				alertDialogBuilder.setCancelable(true);
				alertDialogBuilder.create().show();
			}
		});
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
		/** instance */
		final ListView list = (ListView)findViewById(R.id.DataList);

		/** 初期リスト更新 */
		mDataList = getList(mSelection);
		setList(list);
	}

	
	
	/**
	 * 初回起動時ソフトウェアキーボード非表示イベント
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
		final EditText schEdit = (EditText) findViewById(R.id.SearchEdit);

		Handler mHandler = new Handler();
		
		if(hasFocus && schEdit == getCurrentFocus()) { 
			Message m = Message.obtain(mHandler, new Runnable() { 
				@Override 
				public void run() { 
					/** ソフトキーボード非表示 */
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager.hideSoftInputFromWindow(schEdit.getWindowToken(), 0);
				} 
			});
			mHandler.sendMessage(m); 
		} 
	} 
	
	

	
	
	
	/*****************************************************
	 * 計算処理メソッド
	 * 
	 * メイン画面の動作イベントから呼び出されるメソッド
	 *****************************************************/

	/**
	 * 対象位置選択リスト作成処理
	 * 
	 * 対象位置選択リスト作成し、表示する。
	 * 
	 * @param ListView
	 * @return　void
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public void setList(ListView list){
		ArrayList<PositionDataStatus> arrayList = new ArrayList<PositionDataStatus>();

		for(int i = 0; i < mDataList.size(); i++){
			final String[] line = mDataList.get(i);

			PositionDataStatus item = new PositionDataStatus();   
			item.setColorFlg(Integer.valueOf(line[DATA_INDEX_OBJECT]));
			item.setRow1(line[DATA_INDEX_CREATED]);   
			item.setRow2(line[DATA_INDEX_NAME]);
			item.setRow3(line[DATA_INDEX_ADDRESS]);
			if(Integer.valueOf(line[DATA_INDEX_OBJECT]) == MainForm.FLG_ON){
				item.setColor(getResources().getColor(R.color.skyblue));
			}else{
				item.setColor(getResources().getColor(R.color.orange));
			}

			arrayList.add(item);   
		}

		PositionDataAdapter adapter = new PositionDataAdapter(this, R.layout.list_position_data, arrayList);   
		list.setAdapter(adapter);
	}



	/**
	 * 位置データ取得メソッド
	 * 
	 * DBから位置データを取得し、返却する。
	 * 
	 * @param String
	 * @return　ArrayList<String[]>
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public ArrayList<String[]> getList(String selection){
		ArrayList<String[]> list = new ArrayList<String[]>();

		Intent intent = getIntent();
		intent.setData(Locations.Location.CONTENT_URI);

		Cursor cursor = managedQuery(intent.getData(), null, selection, null, null);

		/** カラム番号取得 */
		int mColumnIndexId = cursor.getColumnIndex(Locations.Location.ID);
		int mColumnIndexCreated = cursor.getColumnIndex(Locations.Location.CREATE_DATE);
		int mColumnIndexObject = cursor.getColumnIndex(Locations.Location.OBJECT);
		int mColumnIndexName = cursor.getColumnIndex(Locations.Location.NAME);
		int mColumnIndexAddress = cursor.getColumnIndex(Locations.Location.ADDRESS);
		int mColumnIndexLatitude = cursor.getColumnIndex(Locations.Location.LATITUDE);
		int mColumnIndexLongitude = cursor.getColumnIndex(Locations.Location.LONGITUDE);

		for(int i = 0; cursor.moveToNext(); i++) {
			// DB値保持用
			String[] cLine = {
					cursor.getString(mColumnIndexId),
					cursor.getString(mColumnIndexCreated),
					cursor.getString(mColumnIndexObject),
					cursor.getString(mColumnIndexName),
					cursor.getString(mColumnIndexAddress),
					cursor.getString(mColumnIndexLatitude),
					cursor.getString(mColumnIndexLongitude)};

			list.add(cLine);
		}
		return list;
	}



	/**
	 * Gmail起動メソッド
	 * 
	 * 位置データを入力したGmailを起動する。
	 * 
	 * @param String
	 * @return　ArrayList<String[]>
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public void startMail(){
		String subject = EagleEyeUtil.makeTitle(mData, getString(R.string.gmailWhere), getString(R.string.delimitation));

		Uri uri = Uri.parse(MAILTO_URI);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra(Intent.EXTRA_SUBJECT, subject); 
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 

		startActivity(it);
	}




	/**
	 * 削除データ文字列作成メソッド
	 * 
	 * 削除確認に表示される削除データを作成する
	 * 
	 * @param なし
	 * @return　String
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public String getDeleteData(){
		String result = getString(R.string.dltExp) + 
		MainForm.NEWLINE + MainForm.NEWLINE +
		mData[DATA_INDEX_CREATED] + MainForm.NEWLINE + 
		mData[DATA_INDEX_NAME] + MainForm.NEWLINE + MainForm.NEWLINE + 
		getString(R.string.addressTag) + mData[DATA_INDEX_ADDRESS] + MainForm.NEWLINE +
		getString(R.string.latitudeTag) + mData[DATA_INDEX_LATITUDE] + MainForm.NEWLINE +
		getString(R.string.longitudeTag) + 	mData[DATA_INDEX_LONGTUDE];

		return result;
	}
}

