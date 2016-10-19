package kms2.EagleEye;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SelectGmailForm extends Activity {
	private final String LIST_SENDER = "list_sender";
	private final String LIST_SUBJECT = "list_subject";
	private final int INDEX_SUBJECT_ADDRESS = 1;
	private final int INDEX_SUBJECT_LATITUDE = 2;
	private final int INDEX_SUBJECT_LONGTIDE = 3;
	private final int INDEX_SUBJECT_NUM = 4;
	private final int INDEX_DATALIST_SENDER = 0;
	private final int INDEX_DATALIST_ADDRESS = 1;
	private final int INDEX_DATALIST_LATITUDE = 2;
	private final int INDEX_DATALIST_LONGTUDE = 3;
	private final int SENDER_NUM = 2;
	private final int SENDER_LINE_NUM = 4;
	private final int INDEX_SENDER_NAME = 2;

	private String sSettingGmail = MainForm.BLANK; 
	private int mIndexSender;
	private int mIndexSubject;
	private int mIndexMsgSender;
	private int mIndexMsgSubject;

	private ArrayList<String[]> mGmailDataList;

	/*****************************************************
	 * イベントメソッド
	 * 
	 * 端末の動作によって呼び出される処理
	 *****************************************************/

	/**
	 * Gmail参照対象選択画面作成イベント
	 * 
	 * Gmail参照対象選択画面が作成される際に呼び出し
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

		/** タイトルバー作成 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		// Gmailアカウント名取得
		sSettingGmail = getIntent().getExtras().getString(MainForm.INTENT_GMAIL);

		/** List表示*/
		ListView list = (ListView)findViewById(R.id.ListList);
		setList(list);

		/** リストイベントリスナー */
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final String[] mData = mGmailDataList.get(position);

				Intent intent = new Intent(SelectGmailForm.this, MainForm.class);
				intent.putExtra(MainForm.INTENT_ADDRESS, mData[INDEX_DATALIST_ADDRESS]);
				intent.putExtra(MainForm.INTENT_LATITUDE, mData[INDEX_DATALIST_LATITUDE]);
				intent.putExtra(MainForm.INTENT_LONGITUDE, mData[INDEX_DATALIST_LONGTUDE]);
				intent.putExtra(MainForm.INTENT_READ_FLG, true);

				setResult(RESULT_OK, intent);
				finish();
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
		ArrayList<HashMap<String, String>> gmailList = new ArrayList<HashMap<String, String>>();

		// Gmailデータ取得
		mGmailDataList = getMail();

		for(int i = 0; i < mGmailDataList.size(); i++){
			final String[] line = mGmailDataList.get(i);
			HashMap<String, String> hashmap = new HashMap<String, String>() {
				private static final long serialVersionUID = 1L;
				{
					put(LIST_SENDER, line[INDEX_DATALIST_SENDER]);
					put(LIST_SUBJECT, line[INDEX_DATALIST_ADDRESS]);} };

					gmailList.add(hashmap);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, gmailList, R.layout.list_gmail_data, 
				new String[]{LIST_SENDER, LIST_SUBJECT},
				new int[]{R.id.GmailDataRow1, R.id.GmailDataRow2});
		list.setAdapter(adapter);
	}



	/**
	 * Gmailデータ取得メソッド
	 * 
	 * Gmailから該当データを取得し、返却する。
	 * 
	 * @param なし
	 * @return　ArrayList<String[]>
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public ArrayList<String[]> getMail(){
		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] msgSelection = {getString(R.string.gmailDate), getString(R.string.gmailSender), getString(R.string.gmailSubject), getString(R.string.gmailConversationId)};
		Uri uri = Uri.parse(getString(R.string.settingGmailDBUri) + sSettingGmail + getString(R.string.settingGmailFixation));
		Cursor messages = managedQuery(uri, msgSelection, null, null, null);
		if(messages.getCount() != 0){
			mIndexSender = messages.getColumnIndex(getString(R.string.gmailSender));
			mIndexSubject = messages.getColumnIndex(getString(R.string.gmailSubject));

			for(int i = 0; messages.moveToNext(); i++) {
				String[] selection = {getString(R.string.gmailMsgDate), getString(R.string.gmailMsgSender), getString(R.string.gmailMsgSubject)};
				String conversationId = messages.getString(messages.getColumnIndex(getString(R.string.gmailConversationId)));

				Uri msgUri = Uri.parse(getString(R.string.settingGmailDBUri) + sSettingGmail + getString(R.string.settingGmailFixation) + MainForm.SLASH + conversationId + getString(R.string.settingGmailMessages));
				Cursor cursor = managedQuery(msgUri, selection, null, null, null);

				if(cursor.getCount() != 0){
					if(i == 0){
						mIndexMsgSender = cursor.getColumnIndex(getString(R.string.gmailMsgSender));
						mIndexMsgSubject = cursor.getColumnIndex(getString(R.string.gmailMsgSubject));
					}

					for(int n = 0; cursor.moveToNext(); n++) {
						String sender = cursor.getString(mIndexMsgSender);
						sender = changeFormatMsgSender(sender);
						String[] subjects = cursor.getString(mIndexMsgSubject).split(getString(R.string.delimitation), -1);
						if(subjects.length == INDEX_SUBJECT_NUM && subjects[0].equals(getString(R.string.gmailWhere))){
							String[] data = {sender, subjects[INDEX_SUBJECT_ADDRESS], subjects[INDEX_SUBJECT_LATITUDE], subjects[INDEX_SUBJECT_LONGTIDE]};
							result.add(data);
						}
					}
				}else{
					String sender = messages.getString(mIndexSender);
					sender = changeFormatConversationsSender(sender);
					String[] subjects = messages.getString(mIndexSubject).split(getString(R.string.delimitation), -1);
					if(subjects.length == INDEX_SUBJECT_NUM && subjects[0].equals(getString(R.string.gmailWhere))){
						String[] data = {sender, subjects[INDEX_SUBJECT_ADDRESS], subjects[INDEX_SUBJECT_LATITUDE], subjects[INDEX_SUBJECT_LONGTIDE]};
						result.add(data);
					}

				}
			}
		}
		return result;
	}


	/**
	 * 送信者フォーマット処理
	 * 
	 * Gmailのmessagesテーブルから取得した送信者文字列を
	 * アプリ用フォーマットの送信者文字列に変更する。
	 * 
	 * @param String
	 * @return　String
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public String changeFormatMsgSender(String str){
		String result = MainForm.BLANK;
		String[] results = str.split(MainForm.DENC + MainForm.SPACE);
		if(results.length == SENDER_NUM){
			results[0] = results[0].replaceFirst(MainForm.DENC, MainForm.BLANK);
			results[1] = results[1].replace(MainForm.PRN_FRONT, MainForm.BLANK);
			results[1] = results[1].replace(MainForm.PRN_BACK, MainForm.BLANK);
			
			if(!results[0].equals(MainForm.BLANK) && !results[1].equals(MainForm.BLANK)){
				result = results[0] + MainForm.COMMA + MainForm.SPACE + results[1];
			}else{
				result = results[0] + results[1];
			}
		}

		return result;
	}



	/**
	 * 送信者フォーマット処理
	 * 
	 * Gmailのconversationsテーブルから取得した送信者文字列を
	 * アプリ用フォーマットの送信者文字列に変更する。
	 * 
	 * @param String
	 * @return　String
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public String changeFormatConversationsSender(String str){
		String[] results = str.split(MainForm.NEWLINE, -1);
		String result = MainForm.BLANK;

		if(results.length == SENDER_LINE_NUM){
			if(!results[INDEX_SENDER_NAME].equals(MainForm.BLANK)){
				result = results[INDEX_SENDER_NAME];
			}else{
				result = getString(R.string.sGmailListMe);
			}
		}
		return result;
	}
}
