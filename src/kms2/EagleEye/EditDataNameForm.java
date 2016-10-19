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
	 * �C�x���g���\�b�h
	 * 
	 * �[���̓���ɂ���ČĂяo����鏈��
	 *****************************************************/

	/**
	 * �ʒu�f�[�^���ҏW��ʍ쐬�C�x���g
	 * 
	 * �ʒu�f�[�^���ҏW��ʂ��쐬�����ۂɌĂяo��
	 * 
	 * @param Bundle
	 * @return�@void
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

		/** �^�C�g���o�[�쐬 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		/** �ҏW�O�\�� */
		sExtras = getIntent().getExtras();
		String dataname = sExtras.getString(MainForm.INTENT_DATANAME);
		if(dataname != null){
			editText.setText(dataname);
		}
		
		/** �ҏW�Ώە����F�ύX */
		if(sExtras.getInt(MainForm.INTENT_MYLOCATION_FLG) == MainForm.FLG_ON){
			objectText.setText(getString(R.string.editObjectMyLocation));
			objectText.setTextColor(getResources().getColor(R.color.skyblue));
		}else{
			objectText.setText(getString(R.string.editObjectLocation));
			objectText.setTextColor(getResources().getColor(R.color.orange));
		}

		/** �ړI�n�Z�����͗��C�x���g���X�i�[ */
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

		/** OK�{�^���C�x���g���X�i�[ */
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

		/** Cancel�{�^���C�x���g���X�i�[ */
		cancelBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	
	
	/**
	 * ����N�����\�t�g�E�F�A�L�[�{�[�h�\���C�x���g
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
	//	 * �߂�{�^���C�x���g
	//	 * 
	//	 * �[���̖߂�{�^�����������ꂽ�ۂɌĂяo��
	//	 * 
	//	 * @param int, KeyEvent
	//	 * @return�@boolean
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
	 * �v�Z�������\�b�h
	 * 
	 * �ʒu�f�[�^���ҏW��ʂ̓���C�x���g����Ăяo����郁�\�b�h
	 *****************************************************/

	/**
	 * DB�A�N�Z�X���\�b�h
	 * 
	 * DB�ɐڑ����A�ǉ����X�V���s��
	 * 
	 * @param int, KeyEvent
	 * @return�@boolean
	 * 
	 * @version 1.0, 13 Jan, 2010
	 * @author kurisu
	 */
	public boolean accessLocationDB() {
		/** instance */
		EditText edittext = (EditText) findViewById(R.id.EditEdit);

		/** DB�@Insert or Update�l�ݒ� */
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
	//	 * �ۑ��Ώۈʒu�I����ʕ\��
	//	 * 
	//	 * �J�ڑO�̉�ʂ��ۑ��Ώۈʒu�I����ʂ̏ꍇ�A��ʂ�\������
	//	 * 
	//	 * @param �Ȃ�
	//	 * @return�@�Ȃ�
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
	 * �ʒu�f�[�^�ꗗ��ʕ\��
	 * 
	 * �ʒu�f�[�^�ꗗ��ʂ�\������
	 * 
	 * @param �Ȃ�
	 * @return�@�Ȃ�
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
	 * �ʒu�f�[�^�����������\�b�h
	 * 
	 * �ʒu�f�[�^�̌����𐔂��A100�����̏ꍇ�A�ŌÃf�[�^���폜����
	 * 
	 * @param �Ȃ�
	 * @return�@boolean
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
