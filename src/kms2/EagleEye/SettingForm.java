package kms2.EagleEye;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingForm extends Activity {
	private static final int GMAIL_FLG = -1;
	private static final int GMAIL_NAME_MAX = 30;
	private static final int GMAIL_NAME_MIN = 6;

	private String sSettingGmail = MainForm.BLANK; 
	private int sSettingDisplay; 
	private int sSettingFull; 
	private int sSettingAlarm;
//	private int sSettingBuffer1; 
//	private int sSettingBuffer2; 
//	private int sSettingBuffer3; 

	/*****************************************************
	 * イベントメソッド
	 * 
	 * 端末の動作によって呼び出される処理
	 *****************************************************/

	/**
	 * 設定画面作成イベント
	 * 
	 * 設定画面が作成される際に呼び出し
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
		setContentView(R.layout.list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		/** instance */
		TextView titleLeftText = (TextView) findViewById(R.id.left_text);
		TextView titleRightText = (TextView) findViewById(R.id.right_text);
		final ListView list = (ListView)findViewById(R.id.ListList);

		/** タイトルバー作成 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		/** 設定反映 */
		sSettingGmail = getIntent().getExtras().getString(MainForm.INTENT_GMAIL);
		sSettingDisplay = getIntent().getExtras().getInt(MainForm.INTENT_DISPLAY);
		sSettingFull = getIntent().getExtras().getInt(MainForm.INTENT_FULL);
		sSettingAlarm = getIntent().getExtras().getInt(MainForm.INTENT_ALARM);
//		sSettingBuffer1 = getIntent().getExtras().getInt(MainForm.INTENT_BUFFER1);
//		sSettingBuffer2 = getIntent().getExtras().getInt(MainForm.INTENT_BUFFER2);
//		sSettingBuffer3 = getIntent().getExtras().getInt(MainForm.INTENT_BUFFER3);

		// List表示
		setList(list);

		/** リストイベントリスナー */
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position){
				case 0:
					if(sSettingDisplay == MainForm.FLG_ON){
						sSettingDisplay = MainForm.FLG_OFF;
					}else{
						sSettingDisplay = MainForm.FLG_ON;
					}

					if(!AccessSettingFile.makeSettingFile(getString(R.string.filePath), getString(R.string.fileName),
							sSettingGmail, sSettingDisplay, sSettingFull, sSettingAlarm)){
						Toast.makeText(getApplicationContext(), getString(R.string.makeSettingFileErrorMsg), MainForm.MSGTIME).show();
					}

					setList(list);

					break;

				case 1:
					if(sSettingFull == MainForm.FLG_ON){
						sSettingFull = MainForm.FLG_OFF;
					}else{
						sSettingFull = MainForm.FLG_ON;
					}

					if(!AccessSettingFile.makeSettingFile(getString(R.string.filePath), getString(R.string.fileName),
							sSettingGmail, sSettingDisplay, sSettingFull, sSettingAlarm)){
						Toast.makeText(getApplicationContext(), getString(R.string.makeSettingFileErrorMsg), MainForm.MSGTIME).show();
					}

					setList(list);

					break;

				case 2:
					if(sSettingAlarm == MainForm.FLG_ON){
						sSettingAlarm = MainForm.FLG_OFF;
					}else{
						sSettingAlarm = MainForm.FLG_ON;
					}

					if(!AccessSettingFile.makeSettingFile(getString(R.string.filePath), getString(R.string.fileName),
							sSettingGmail, sSettingDisplay, sSettingFull, sSettingAlarm)){
						Toast.makeText(getApplicationContext(), getString(R.string.makeSettingFileErrorMsg), MainForm.MSGTIME).show();
					}

					setList(list);

					break;

				case 3:
					final EditText edtInput = new EditText(SettingForm.this);
					edtInput.setSingleLine(true);
					edtInput.setText(sSettingGmail);
					
					/** ダイアログ表示 */
					new AlertDialog.Builder(SettingForm.this) 
					.setTitle(getString(R.string.settingGmail))
					.setMessage(getString(R.string.settingGmailDialog))
					.setView(edtInput) 
					.setPositiveButton(getString(R.string.settingOk), new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int whichButton) {
							if(EagleEyeUtil.checkAccountName(edtInput.getText().toString(), GMAIL_NAME_MIN, GMAIL_NAME_MAX)){
								sSettingGmail = edtInput.getText().toString();

								if(!AccessSettingFile.makeSettingFile(getString(R.string.filePath), getString(R.string.fileName),
										sSettingGmail, sSettingDisplay, sSettingFull, sSettingAlarm)){
									Toast.makeText(getApplicationContext(), getString(R.string.makeSettingFileErrorMsg), MainForm.MSGTIME).show();
								}
							}else{
								Toast.makeText(getApplicationContext(), getString(R.string.gmailNameErrorMsg), MainForm.MSGTIME).show();
							}
						} 
					}) 
					.setNegativeButton(getString(R.string.settingCancel), new DialogInterface.OnClickListener() { 
						public void onClick(DialogInterface dialog, int whichButton) { 
						} 
					}) 
					.show();
					break;
				}
			}
		});
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
		ArrayList<SettingStatus> arrayList = new ArrayList<SettingStatus>();
		int[] settingData = {sSettingDisplay, sSettingFull, sSettingAlarm, GMAIL_FLG};
		String[] listStr = {getString(R.string.settingDisplay), getString(R.string.settingFull), getString(R.string.settingAlarm), getString(R.string.settingGmail)};

		for(int i = 0; i < AccessSettingFile.SETTING_DATA_NUM - AccessSettingFile.SETTING_BUFFER_NUM; i++){
			/** 画面設置 */
			SettingStatus item = new SettingStatus();   
			item.setCheacked(settingData[i]);
			item.setText(listStr[i]);   

			arrayList.add(item);   
		}

		SettingAdapter adapter = new SettingAdapter(this, R.layout.list_setting, arrayList);   
		list.setAdapter(adapter);
	}
}
