package it.unipd.dei.esp1415.falldetector.database;

import android.database.sqlite.SQLiteDatabase;

/** 
 * Helper class for define database tables, columns, .... 
 */
public class DatabaseTable {
	
	public static final String SESSION_TABLE = "Session";
	public static final String FALL_EVENTS_TABLE = "Fall_Events";
	public static final String ACCEL_TABLE = "Accel_Data";
	public static final String MAIL_TABLE = "Mail_Address";
	public static final String TMP_ACC_TABLE = "Temp_Acc_Data";
			
	//Common names of columns
	public static final String COLUMN_PK_ID = "_id";
	
	//Session columns
	public static final String COLUMN_SS_NAME = "name"; //save as Text
	public static final String COLUMN_SS_START_DATE = "start_date"; //save as integer timestamp
	public static final String COLUMN_SS_DURATION = "duration"; //save as integer
	public static final String COLUMN_SS_COLOR_THUMBNAIL = "color_thumbnail"; //save as integer that define a color ARGB
	public static final String COLUMN_SS_FALLS_NUMBER = "falls_number"; //save as integer
	public static final String COLUMN_SS_IS_ACTIVE = "is_active"; //save as integer default 0
	public static final String COLUMN_SS_XML = "file"; //save as text that define the name of file 
	public static final String COLUMN_SS_IS_PAUSE = "is_pause";
	public static final String COLUMN_SS_CHRONO_TMP = "chrono_tmp";	// used for calculate duration
	
	public static final String[] ALL_COLUMNS_SESSION = {COLUMN_PK_ID, COLUMN_SS_NAME,
													   COLUMN_SS_START_DATE, COLUMN_SS_DURATION, 
													   COLUMN_SS_COLOR_THUMBNAIL, COLUMN_SS_FALLS_NUMBER,
													   COLUMN_SS_IS_ACTIVE, COLUMN_SS_XML,
													   COLUMN_SS_IS_PAUSE, COLUMN_SS_CHRONO_TMP};
	
	//Fall Events columns
	public static final String COLUMN_FE_DATE = "event_date"; //save as integer timestamp
	public static final String COLUMN_FE_IS_NOTIFIED = "is_notified"; //save as integer default 0
	public static final String COLUMN_FE_XML = "file"; //save as text that define the name of file 
	public static final String COLUMN_FE_LATITUDE = "latitude";
	public static final String COLUMN_FE_LONGITUDE = "longitude";
	public static final String COLUMN_FE_FK_SESSION = "session"; //integer not null that define the foreign key 
	
	public static final String[] ALL_COLUMNS_FALL_EVENTS = {COLUMN_PK_ID, COLUMN_FE_DATE,
															COLUMN_FE_IS_NOTIFIED, COLUMN_FE_XML,
															COLUMN_FE_LATITUDE, COLUMN_FE_LONGITUDE,
															COLUMN_FE_FK_SESSION};
	
	//Accel Data Columns
	public static final String COLUMN_AC_TS = "accel_timestamp"; //save as integer timestamp
	public static final String COLUMN_AC_X = "x"; //data on x
	public static final String COLUMN_AC_Y = "y"; //data on y
	public static final String COLUMN_AC_Z = "z"; //data on z
	public static final String COLUMN_AC_FK_FALLS = "fall"; //integer not null that define the foreign key
	
	public static final String[] ALL_COLUMNS_ACCEL = {COLUMN_PK_ID, COLUMN_AC_TS,
													  COLUMN_AC_X, COLUMN_AC_Y,
													  COLUMN_AC_Z, COLUMN_AC_FK_FALLS};
	
	//Accel temporary data
	public static final String COLUMN_TMP_AC_TS = "accel_timestamp"; //save as integer timestamp
	public static final String COLUMN_TMP_AC_X = "x"; //data on x
	public static final String COLUMN_TMP_AC_Y = "y"; //data on y
	public static final String COLUMN_TMP_AC_Z = "z"; //data on z
	
	//Mail columns
	public static final String COLUMN_ML_NAME = "name"; //save as Text
	public static final String COLUMN_ML_SURNAME = "surname"; //save as Text
	public static final String COLUMN_ML_ADDRESS = "address"; //save as Text
	
	public static final String[] ALL_COLUMNS_MAIL = {COLUMN_PK_ID, COLUMN_ML_NAME,
												     COLUMN_ML_SURNAME, COLUMN_ML_ADDRESS};
	
	//create table
	private static final String CREATE_SESSION = "CREATE TABLE " + SESSION_TABLE + " ("
			+ COLUMN_PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_SS_NAME + " TEXT, "
			+ COLUMN_SS_START_DATE + " INTEGER, "
			+ COLUMN_SS_DURATION + " INTEGER, "
			+ COLUMN_SS_COLOR_THUMBNAIL + " INTEGER, "
			+ COLUMN_SS_FALLS_NUMBER + " INTEGER, "
			+ COLUMN_SS_IS_ACTIVE + " INTEGER DEFAULT 0, " //set to default to 0 ->false
			+ COLUMN_SS_XML + " TEXT, "
			+ COLUMN_SS_IS_PAUSE + " INTEGER DEFAULT 0, "
			+ COLUMN_SS_CHRONO_TMP + " INTEGER" + ");";

	private static final String CREATE_FALL_EVENTS = "CREATE TABLE " + FALL_EVENTS_TABLE + " ("
			+ COLUMN_PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_FE_DATE + " INTEGER, "
			+ COLUMN_FE_IS_NOTIFIED +  " INTEGER DEFAULT 0, " //set to default to 0 ->false
			+ COLUMN_FE_XML + " TEXT, "
			+ COLUMN_FE_LATITUDE + " REAL, " 
			+ COLUMN_FE_LONGITUDE + " REAL, "
			+ COLUMN_FE_FK_SESSION + " INTEGER, "
			+ "FOREIGN KEY(" + COLUMN_FE_FK_SESSION + ") REFERENCES " 
			+ SESSION_TABLE + "(" + COLUMN_PK_ID + ") " 
			+ "ON DELETE RESTRICT ON UPDATE CASCADE" + ");" ;
	
	private static final String CREATE_ACCEL_DATA = "CREATE TABLE " + ACCEL_TABLE + " ("
			+ COLUMN_PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_AC_TS + " INTEGER, "
			+ COLUMN_AC_X + " REAL, "
			+ COLUMN_AC_Y + " REAL, "
			+ COLUMN_AC_Z + " REAL, "
			+ COLUMN_AC_FK_FALLS + " INTEGER, "
			+ "FOREIGN KEY(" + COLUMN_AC_FK_FALLS + ") REFERENCES " 
			+ FALL_EVENTS_TABLE + "(" + COLUMN_PK_ID + ") " 
			+ "ON DELETE RESTRICT ON UPDATE CASCADE" + ");" ;
	
	private static final String CREATE_MAIL = "CREATE TABLE " + MAIL_TABLE + " ("
			+ COLUMN_PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_ML_NAME + " TEXT, "
			+ COLUMN_ML_SURNAME + " TEXT, "
			+ COLUMN_ML_ADDRESS + " TEXT );" ;
	
	//Temporary accelerometer data
	public static final String CREATE_TMP_ACC = "CREATE TABLE " + TMP_ACC_TABLE + " ("
			+ COLUMN_PK_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_TMP_AC_TS + " INTEGER, "
			+ COLUMN_TMP_AC_X + " REAL, "
			+ COLUMN_TMP_AC_Y + " REAL, "
			+ COLUMN_TMP_AC_Z + " REAL); ";
	
	private static final String ALTER_TABLE_FALL_EVENTS_1 = ""
			+ "ALTER TABLE " + FALL_EVENTS_TABLE + " ADD COLUMN " + COLUMN_FE_LATITUDE + " REAL;";

	private static final String ALTER_TABLE_FALL_EVENTS_2 = ""
			+ "ALTER TABLE " + FALL_EVENTS_TABLE + " ADD COLUMN " + COLUMN_FE_LONGITUDE + " REAL;";
	
	private static final String ALTER_TABLE_SESSION_1 = ""
			+ "ALTER TABLE " + SESSION_TABLE + " ADD COLUMN " + COLUMN_SS_IS_PAUSE + " INTEGER DEFAULT 0;";
	
	private static final String ALTER_TABLE_SESSION_2 = ""
			+ "ALTER TABLE " + SESSION_TABLE + " ADD COLUMN " + COLUMN_SS_CHRONO_TMP+ " INTEGER;";
	
	//Utility string for db
    public static final String SET_FK_ON = "PRAGMA foreign_keys = ON;";
    public static final String SET_FK_OFF = "PRAGMA foreign_keys = OFF;";
    
    public static final String SEE_FK_STATUS = "PRAGMA foreign_keys;";
    
	
	/**
	 * [m]
	 * This method set the tables of database
	 * 
	 * @param db the database to set
	 */
	public static void onCreate(SQLiteDatabase db){	
		db.execSQL(SET_FK_ON);	
		db.execSQL(CREATE_SESSION);
		db.execSQL(CREATE_FALL_EVENTS);
		db.execSQL(CREATE_MAIL);
		db.execSQL(CREATE_ACCEL_DATA);
		db.execSQL(CREATE_TMP_ACC);
	}//[m] onCreate()
    
	/**
	 * [m]
	 * upgrade the database
	 * 
	 * @param db 
	 * @param oldVersion the version of the old database
	 * @param newVersion the version of the new database
	 */
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, 
			int newVersion){
		
		//switch case that start from oldVersion and after execute
		//on cascade all modification for the database
		switch (oldVersion){
		case 1:
			db.execSQL(CREATE_MAIL);
		case 2:
			db.execSQL(CREATE_ACCEL_DATA);
		case 3:
			db.execSQL(ALTER_TABLE_FALL_EVENTS_1);
			db.execSQL(ALTER_TABLE_FALL_EVENTS_2);
		case 4:
			db.execSQL(CREATE_TMP_ACC);
		case 5:
			db.execSQL(ALTER_TABLE_SESSION_1);
			db.execSQL(ALTER_TABLE_SESSION_2);
		default:
			break;
		}
	}// [m] onUpgrade()
 }
