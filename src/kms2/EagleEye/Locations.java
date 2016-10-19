package kms2.EagleEye;


import java.text.SimpleDateFormat;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Locations {
	public static final String AUTHORITY = "kms2.EagleEye"; 
	private Locations (){};
	
	public static final class Location implements BaseColumns{
		private Location (){};
		public static final Uri CONTENT_URI = 
			Uri.parse("content://" + AUTHORITY + ".Locations");
		public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		public static final String DEFAULT_SORT = "created DESC";
		public static final String ID = "_id";
		public static final String CREATE_DATE = "created";
		public static final String OBJECT = "object";
		public static final String NAME = "name";
		public static final String ADDRESS = "address";
		public static final String LATITUDE = "latitude";
		public static final String LONGITUDE = "longitude";
	}
}
