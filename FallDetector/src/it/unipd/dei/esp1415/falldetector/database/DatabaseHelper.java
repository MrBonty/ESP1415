package it.unipd.dei.esp1415.falldetector.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for Database
 *
 */
public class DatabaseHelper  extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "Fall_app.db";
	private static final int DATABASE_VERSION = 5;
	
	/**[c]
	 * Constructor for the class
	 * 
	 * @param context the application context use to open the db
	 */
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}//[m] DatabaseHelper()
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		DatabaseTable.onCreate(db);	
	}//[m] onCreate()

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		DatabaseTable.onUpgrade(db, oldVersion, newVersion);
	}//[m] onUpgrade()
}
