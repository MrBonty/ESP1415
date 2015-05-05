package it.unipd.dei.esp1415.falldetector.database;

import android.database.sqlite.SQLiteDatabase;

public class DatabaseTable {
	
	public static final String SESSION_TABLE = "Session";
	public static final String FALL_EVENTS_TABLE = "Fall_Events";
			
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
	
	public static final String[] ALL_COLUMNS_SESSION = {COLUMN_PK_ID, COLUMN_SS_NAME,
													   COLUMN_SS_START_DATE, COLUMN_SS_DURATION, 
													   COLUMN_SS_COLOR_THUMBNAIL, COLUMN_SS_FALLS_NUMBER,
													   COLUMN_SS_IS_ACTIVE, COLUMN_SS_XML};
	
	//Fall Events columns
	public static final String COLUMN_FE_DATE = "event_date"; //save as integer timestamp
	public static final String COLUMN_FE_IS_NOTIFIED = "is_notified"; //save as integer default 0
	public static final String COLUMN_FE_XML = "file"; //save as text that define the name of file 
	public static final String COLUMN_FE_FK_SESSION = "session"; //integer not null that define the foreign key 
	
	public static final String[] ALL_COLUMNS_FALL_EVENTS = {COLUMN_PK_ID, COLUMN_FE_DATE,
															COLUMN_FE_IS_NOTIFIED, COLUMN_FE_XML,
															COLUMN_FE_FK_SESSION};
	
	//create table
	private static final String CREATE_SESSION = "CREATE TABLE " + SESSION_TABLE + " ("
			+ COLUMN_PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_SS_NAME + " TEXT, "
			+ COLUMN_SS_START_DATE + " INTEGER, "
			+ COLUMN_SS_DURATION + " INTEGER, "
			+ COLUMN_SS_COLOR_THUMBNAIL + " INTEGER, "
			+ COLUMN_SS_FALLS_NUMBER + " INTEGER, "
			+ COLUMN_SS_IS_ACTIVE + " INTEGER DEFAULT 0, " //set to default to 0 ->false
			+ COLUMN_SS_XML + " TEXT" + ");";

	private static final String CREATE_FALL_EVENTS = "CREATE TABLE " + FALL_EVENTS_TABLE + " ("
			+ COLUMN_PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_FE_DATE + " INTEGER, "
			+ COLUMN_FE_IS_NOTIFIED +  " INTEGER DEFAULT 0, " //set to default to 0 ->false
			+ COLUMN_FE_XML + " TEXT, "
			+ COLUMN_FE_FK_SESSION + " INTEGER, "
			+ "FOREIGN KEY(" + COLUMN_FE_FK_SESSION + ") REFERENCES " 
			+ SESSION_TABLE + "(" + COLUMN_PK_ID + ") " 
			+ "ON DELETE RESTRICT ON UPDATE CASCADE" + ");" ;
	
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
	}
    
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
		
		switch (oldVersion){
		
		}
	}
 }
