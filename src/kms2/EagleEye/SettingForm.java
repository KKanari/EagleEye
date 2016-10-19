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
	 * �C�x���g���\�b�h
	 * 
	 * �[���̓���ɂ���ČĂяo����鏈��
	 *****************************************************/

	/**
	 * �ݒ��ʍ쐬�C�x���g
	 * 
	 * �ݒ��ʂ��쐬�����ۂɌĂяo��
	 * 
	 * @param Bundle
	 * @return�@void
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

		/** �^�C�g���o�[�쐬 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		/** �ݒ蔽�f */
		sSettingGmail = getIntent().getExtras().getString(MainForm.INTENT_GMAIL);
		sSettingDisplay = getIntent().getExtras().getInt(MainForm.INTENT_DISPLAY);
		sSettingFull = getIntent().getExtras().getInt(MainForm.INTENT_FULL);
		sSettingAlarm = getIntent().getExtras().getInt(MainForm.INTENT_ALARM);
//		sSettingBuffer1 = getIntent().getExtras().getInt(MainForm.INTENT_BUFFER1);
//		sSettingBuffer2 = getIntent().getExtras().getInt(MainForm.INTENT_BUFFER2);
//		sSettingBuffer3 = getIntent().getExtras().getInt(MainForm.INTENT_BUFFER3);

		// List�\��
		setList(list);

		/** ���X�g�C�x���g���X�i�[ */
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
					
					/** �_�C�A���O�\�� */
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
	 * �v�Z�������\�b�h
	 * 
	 * ���C����ʂ̓���C�x���g����Ăяo����郁�\�b�h
	 *****************************************************/

	/**
	 * �Ώۈʒu�I�����X�g�쐬����
	 * 
	 * �Ώۈʒu�I�����X�g�쐬���A�\������B
	 * 
	 * @param ListView
	 * @return�@void
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public void setList(ListView list){
		ArrayList<SettingStatus> arrayList = new ArrayList<SettingStatus>();
		int[] settingData = {sSettingDisplay, sSettingFull, sSettingAlarm, GMAIL_FLG};
		String[] listStr = {getString(R.string.settingDisplay), getString(R.string.settingFull), getString(R.string.settingAlarm), getString(R.string.settingGmail)};

		for(int i = 0; i < AccessSettingFile.SETTING_DATA_NUM - AccessSettingFile.SETTING_BUFFER_NUM; i++){
			/** ��ʐݒu */
			SettingStatus item = new SettingStatus();   
			item.setCheacked(settingData[i]);
			item.setText(listStr[i]);   

			arrayList.add(item);   
		}

		SettingAdapter adapter = new SettingAdapter(this, R.layout.list_setting, arrayList);   
		list.setAdapter(adapter);
	}
}
