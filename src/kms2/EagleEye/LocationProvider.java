package kms2.EagleEye;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LocationProvider extends ContentProvider{
	public class DatabaseHelper extends SQLiteOpenHelper{
		public static final String DATABASE_NAME = "locations.db";
		public static final int DATABASE_VERSION = 1;
		public static final String LOCATIONS_TABLE_NAME = "locations";

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		
		
		/** �e�[�u���쐬 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " 
					+ LOCATIONS_TABLE_NAME
					+ "(" + Locations.Location.ID + " INTEGER PRIMARY KEY,"
					+ Locations.Location.CREATE_DATE + " DATETIME NOT NULL,"
					+ Locations.Location.OBJECT + " INTEGER NOT NULL,"
					+ Locations.Location.NAME + " TEXT,"
					+ Locations.Location.ADDRESS + " TEXT,"
					+ Locations.Location.LATITUDE + " TEXT,"
					+ Locations.Location.LONGITUDE + " TEXT"
					+ ");");
		}


		
		/** DB���@�[�W�����A�b�v */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE_NAME);
			onCreate(db);
		}
	}	

	
	
	DatabaseHelper dbHelper;
	
	
	
	/** �I�[�v���`�F�b�N */
	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	/** ���R�[�h�擾 */
	@Override
	public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
		/** orderBy�ݒ� */
		String orderBy;
		if(TextUtils.isEmpty(sort)){
			orderBy = Locations.Location.DEFAULT_SORT;
		}else{
			orderBy = sort;
		}
		
		/** ���R�[�h�擾 */
		SQLiteDatabase mDB = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(DatabaseHelper.LOCATIONS_TABLE_NAME);
		Cursor c = qb.query(mDB, projection, selection, selectionArgs, null, null, orderBy);
		
		return c;
	}



	/** ���R�[�h�ǉ� */
	@Override
	public Uri insert(Uri url, ContentValues IntialValues) {
		long rowId;
		
		// �����`�F�b�N
		if(IntialValues != null){
			SQLiteDatabase mDB = dbHelper.getWritableDatabase();
			rowId = mDB.insert(DatabaseHelper.LOCATIONS_TABLE_NAME, null, IntialValues);
			
			// insert success
			if(rowId > 0){
				Uri uri = ContentUris.withAppendedId(Locations.Location.CONTENT_URI, rowId);
				getContext().getContentResolver().notifyChange(uri, null);
				return uri;
			}
		}
		
		return null;
	}



	/** ���R�[�h�폜 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		count = mDB.delete(DatabaseHelper.LOCATIONS_TABLE_NAME, where, whereArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}


	@Override
	public String getType(Uri uri) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		count = mDB.update(DatabaseHelper.LOCATIONS_TABLE_NAME, values, selection, selectionArgs);
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
