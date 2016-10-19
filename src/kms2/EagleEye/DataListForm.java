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
	 * �C�x���g���\�b�h
	 * 
	 * �[���̓���ɂ���ČĂяo����鏈��
	 *****************************************************/

	/**
	 * �ʒu�f�[�^�ꗗ��ʍ쐬�C�x���g
	 * 
	 * �ʒu�f�[�^�ꗗ��ʂ��쐬�����ۂɌĂяo��
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
		setContentView(R.layout.data_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		/** instance */
		TextView titleLeftText = (TextView) findViewById(R.id.left_text);
		TextView titleRightText = (TextView) findViewById(R.id.right_text);
		final EditText schEdit = (EditText) findViewById(R.id.SearchEdit);
		Button schBtn = (Button) findViewById(R.id.SearchButton);
		final ListView list = (ListView)findViewById(R.id.DataList);

		/** �^�C�g���o�[�쐬 */
		if(!EagleEyeUtil.getTitle(titleLeftText, titleRightText, getPackageManager(), getResources())){
			Toast.makeText(getApplicationContext(), getString(R.string.getTitleErrorMsg), MainForm.MSGTIME).show();
		}

		try{
			// �������X�g�쐬
			mDataList = getList(mSelection);
			
			// List�\��
			setList(list);
		}catch (Exception e) {
			Toast.makeText(getApplicationContext(), getString(R.string.readDBErrorMsg), MainForm.MSGTIME).show();
		}
		
		/** �����������͗��C�x���g���X�i�[ */
		schEdit.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					/** �\�t�g�L�[�{�[�h��\�� */
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

					/** �����������X�g�쐬 */
					mSelection = EagleEyeUtil.makeSearchWhere(schEdit.getText().toString(), schEdit.getText().toString());
					mDataList = getList(mSelection);

					/** List�\��*/
					setList(list);
				}
				return false;
			}
		});


		/** �����{�^���C�x���g���X�i�[ */
		schBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				/** �\�t�g�L�[�{�[�h��\�� */
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

				/** �����������X�g�쐬 */
				mSelection = EagleEyeUtil.makeSearchWhere(schEdit.getText().toString(), schEdit.getText().toString());
				mDataList = getList(mSelection);

				/** List�\��*/
				setList(list);
			}
		});

		/** ���X�g�C�x���g���X�i�[ */
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// �I���A�C�e���擾
				mData = mDataList.get(position);
				/** �������e�I���_�C�A���O�\�� */
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DataListForm.this);
				alertDialogBuilder.setTitle(getString(R.string.prcTitle));
				CharSequence[] processing = { getString(R.string.prcReference), getString(R.string.prcSend), 
						getString(R.string.prcEdit), getString(R.string.prcDelete)};

				alertDialogBuilder.setItems(processing, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch(which){
						case 0:
							/** ���C����ʂɔ��f���� */
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
							// ���[���N������
							startMail();

							dialog.dismiss();

							break;

						case 2:
							/** �ҏW���� */
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

							/** �폜���� */
							AlertDialog.Builder alertDeleteDialogBuilder = new AlertDialog.Builder(DataListForm.this);

							// �^�C�g����ݒ�
							alertDeleteDialogBuilder.setTitle(getString(R.string.dltTitle));
							alertDeleteDialogBuilder.setMessage(getDeleteData());
							alertDeleteDialogBuilder.setPositiveButton(getString(R.string.dltOk), new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = getIntent();
									intent.setData(Locations.Location.CONTENT_URI);
									String where = Locations.Location.ID + MainForm.EQUAL + mData[DATA_INDEX_ID];
									getContentResolver().delete(intent.getData(), where, null);

									/** ���X�g�Ď擾 */
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
	 * �J�ڃC�x���g
	 * 
	 * ����ʂ���J�ڂ����ہA�Ăяo�����
	 * 
	 * @param int, int, intent
	 * @return�@void
	 * 
	 * @version 1.0, 4 Jan, 2010
	 * @author kurisu
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		/** instance */
		final ListView list = (ListView)findViewById(R.id.DataList);

		/** �������X�g�X�V */
		mDataList = getList(mSelection);
		setList(list);
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
		/** instance */
		final EditText schEdit = (EditText) findViewById(R.id.SearchEdit);

		Handler mHandler = new Handler();
		
		if(hasFocus && schEdit == getCurrentFocus()) { 
			Message m = Message.obtain(mHandler, new Runnable() { 
				@Override 
				public void run() { 
					/** �\�t�g�L�[�{�[�h��\�� */
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputMethodManager.hideSoftInputFromWindow(schEdit.getWindowToken(), 0);
				} 
			});
			mHandler.sendMessage(m); 
		} 
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
	 * �ʒu�f�[�^�擾���\�b�h
	 * 
	 * DB����ʒu�f�[�^���擾���A�ԋp����B
	 * 
	 * @param String
	 * @return�@ArrayList<String[]>
	 * 
	 * @version 1.0, 12 Jan, 2010
	 * @author kurisu
	 */
	public ArrayList<String[]> getList(String selection){
		ArrayList<String[]> list = new ArrayList<String[]>();

		Intent intent = getIntent();
		intent.setData(Locations.Location.CONTENT_URI);

		Cursor cursor = managedQuery(intent.getData(), null, selection, null, null);

		/** �J�����ԍ��擾 */
		int mColumnIndexId = cursor.getColumnIndex(Locations.Location.ID);
		int mColumnIndexCreated = cursor.getColumnIndex(Locations.Location.CREATE_DATE);
		int mColumnIndexObject = cursor.getColumnIndex(Locations.Location.OBJECT);
		int mColumnIndexName = cursor.getColumnIndex(Locations.Location.NAME);
		int mColumnIndexAddress = cursor.getColumnIndex(Locations.Location.ADDRESS);
		int mColumnIndexLatitude = cursor.getColumnIndex(Locations.Location.LATITUDE);
		int mColumnIndexLongitude = cursor.getColumnIndex(Locations.Location.LONGITUDE);

		for(int i = 0; cursor.moveToNext(); i++) {
			// DB�l�ێ��p
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
	 * Gmail�N�����\�b�h
	 * 
	 * �ʒu�f�[�^����͂���Gmail���N������B
	 * 
	 * @param String
	 * @return�@ArrayList<String[]>
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
	 * �폜�f�[�^������쐬���\�b�h
	 * 
	 * �폜�m�F�ɕ\�������폜�f�[�^���쐬����
	 * 
	 * @param �Ȃ�
	 * @return�@String
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

