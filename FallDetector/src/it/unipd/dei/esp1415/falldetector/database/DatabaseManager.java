package it.unipd.dei.esp1415.falldetector.database;

import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	public static final int INSERT_ERROR = -1;
	public static final int ON_OPEN_ERROR = -2;
	public static final int ID_NOT_SET_ERROR = -3;
	
	public DatabaseManager(Context context){
		mDbHelper = new DatabaseHelper(context);
	}
	
	/**
	 * [m]
	 * this method open the database
	 * 
	 * @throws SQLException
	 */
	private void open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
	}
	
	/**
	 * [m]
	 * this method close the database
	 */
	private void close(){
		mDbHelper.close();
	}
	
	public long insertASession(Session session){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_NAME, session.getName());
		values.put(DatabaseTable.COLUMN_SS_START_DATE, session.getStartTimestamp());
		values.put(DatabaseTable.COLUMN_SS_IS_ACTIVE, session.isActive());
		values.put(DatabaseTable.COLUMN_SS_DURATION, session.getDuration());
		values.put(DatabaseTable.COLUMN_SS_COLOR_THUMBNAIL, session.getColorThumbnail());
		values.put(DatabaseTable.COLUMN_SS_FALLS_NUMBER, session.getFallsNum());
		values.put(DatabaseTable.COLUMN_SS_XML, session.getXmlFileName());
				
		return insertInDatabase(DatabaseTable.SESSION_TABLE, values); 
	}//[m] insertASession
	
	public long insertAFall(){ //TODO Fall Obj
		return -1;
	}//[m] insertAFall
	
	public int upgradeASession(long sessionId, ContentValues valuesToupgrade){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + sessionId;
	
		return upgradeRow(DatabaseTable.SESSION_TABLE, valuesToupgrade, whereClause, null);
	}//[m] upgradeASession
	
	public int upgradeASession(Session session){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_NAME, session.getName());
		values.put(DatabaseTable.COLUMN_SS_START_DATE, session.getStartTimestamp());
		values.put(DatabaseTable.COLUMN_SS_IS_ACTIVE, session.isActive());
		values.put(DatabaseTable.COLUMN_SS_DURATION, session.getDuration());
		values.put(DatabaseTable.COLUMN_SS_COLOR_THUMBNAIL, session.getColorThumbnail());
		values.put(DatabaseTable.COLUMN_SS_FALLS_NUMBER, session.getFallsNum());
		values.put(DatabaseTable.COLUMN_SS_XML, session.getXmlFileName());
		
		String whereClause = "";
		long id = session.getId();
		if(id > 0){
			whereClause = DatabaseTable.COLUMN_PK_ID + " = " + id;
		}else{
			return ID_NOT_SET_ERROR;
		}//if... else...
		
		return upgradeRow(DatabaseTable.SESSION_TABLE, values, whereClause, null);
	}//[m] upgradeASession
	
	/**
	 * [m]
	 * private generic method to insert a new row in the database
	 * 
	 * @param tableName the name of the table into add the new row.
	 * @param values this map contains the initial column values for the row. The keys should be the column names and the values the column values
	 * @return the id of the session, or ON_OPEN_ERROR if an error is occurred on open the database,
	 * or INSERT_ERROR if an error is occurred during the insertion.
	 */
	private long insertInDatabase(String tableName, ContentValues values){
		try{
			open();
		} catch (SQLException e){
			e.printStackTrace();
			return ON_OPEN_ERROR;
		}//try.. catch
		
		long id = mDb.insert(tableName, null, values);
		
		close();
		return id;
	}//[m] insertInDatabase
	
	/**
	 * [m]
	 * private generic method to upgrade row for a Table
	 * 
	 * @param table the name of the table into do the upgrade.
	 * @param values values a map from column names to new column values. null is a valid value that will be translated to NULL. 
	 * @param whereClause the optional WHERE clause to apply when updating. Passing null will update all rows.
	 * @param whereArgs You may include it in the where clause, which will be replaced by the values from whereArgs. The values will be bound as Strings
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database.
	 */
	private int upgradeRow(String table, ContentValues values, String whereClause, String[] whereArgs){
		try{
			open();
		} catch (SQLException e){
			e.printStackTrace();
			return ON_OPEN_ERROR;
		}//try.. catch
		
		int tmp = mDb.update(table, values, whereClause, whereArgs);
		
		close();
		
		return tmp;
	}//[m] upgradeRow
	
	public Cursor getAllSession(){
		return null;
	}
}
