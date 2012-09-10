package pl.iszala.travelernotebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author Robert
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper{
	
	/**
	 * Default constructor- creates database
	 * @param context
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}
	
	/**
	 * Implemented methood from SQLiteOpenHelper- called when
	 * constructor of DatabaseHelper is called
	 * Creates three tables (TRIP, POINT, TRANSFER) with relations
	 * TRAVEL-POINT and POINT TRANSFER
	 * @see SQLiteOpenHelper
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TRIP_TABLE_NAME
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ FROM + " TEXT, "
				+ TO + " TEXT);");
		db.execSQL("CREATE TABLE " + TRANSFER_TABLE_NAME
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ NOTE + " TEXT, "
				+ TIME + " TEXT, "
				+ DATE + " TEXT, "
				+ ADDRESS + " TEXT, "
				+ CITY + " TEXT, "
				+ COUNTRY + " TEXT, "
				+ LATITUDE + " TEXT, "
				+ LOGITUDE + " TEXT, "
				+ TRANFER_TYPE + " TEXT, "
				+ FK_POINT + " INTEGER NOT NULL REFERENCES "+ POINT_TABLE_NAME +"(_id)"
				+ "ON DELETE CASCADE);");
		db.execSQL("CREATE TABLE " + POINT_TABLE_NAME
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ NAME + " TEXT, "
				+ ADDRESS + " TEXT, "
				+ CITY + " TEXT, "
				+ COUNTRY + " TEXT, "
				+ LATITUDE + " TEXT, "
				+ LOGITUDE + " TEXT, "
				+ FK_TRAVEL + " INTEGER NOT NULL REFERENCES "+ TRIP_TABLE_NAME +"(_id)"
				+ "ON DELETE CASCADE);");
	}
	
	/**
	 * Implemented method from SQLiteOpenHelper- we don't use it now
	 * @see SQLiteOpenHelper
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	/**
	 * Private fields, mainly names of columns and tables in database
	 */
	private static final String DATABASE_NAME = "TRAVELER_NOTEBOOK";
	
	//Table names
	private static final String TRIP_TABLE_NAME = "TRIP";
	private static final String POINT_TABLE_NAME = "POINT";
	private static final String TRANSFER_TABLE_NAME = "TRANSFER";
	
	//Columns names
	private static final String FROM = "FROM_PLACE";
	private static final String TO = "TO_PLACE";
	
	private static final String NAME = "NAME";
	private static final String ADDRESS = "ADDRESS";
	private static final String CITY = "CITY";
	private static final String COUNTRY = "COUNTRY";
	private static final String LATITUDE = "LATITUDE";
	private static final String LOGITUDE = "LOGITUDE";
	
	private static final String NOTE = "NOTE";
	private static final String TIME = "TIME";
	private static final String DATE = "DATE";
	private static final String TRANFER_TYPE = "TRANSFER_TYPE";
	
	//Foregin keys name
	private static final String FK_TRAVEL = "FK_TRAVEL_ID";
	private static final String FK_POINT = "FK_POINT_ID";

}
