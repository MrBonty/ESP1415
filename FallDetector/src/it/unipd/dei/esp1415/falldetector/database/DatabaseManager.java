package it.unipd.dei.esp1415.falldetector.database;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.utility.AccelData;
import it.unipd.dei.esp1415.falldetector.utility.Fall;
import it.unipd.dei.esp1415.falldetector.utility.MailAddress;
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
	/**
	 * [m]
	 * Method to insert a Session into the database
	 * 
	 * @param session to insert in the database
	 * @return the id of the session, or ON_OPEN_ERROR if an error is occurred on open the database,
	 * or INSERT_ERROR if an error is occurred during the insertion.
	 */
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
	
	/**
	 * [m]
	 * Method to insert a Fall into the database
	 * 
	 * @param fall to insert in the database
	 * @return the id of the fall event, or ON_OPEN_ERROR if an error is occurred on open the database,
	 * or INSERT_ERROR if an error is occurred during the insertion.
	 */
	public long insertAFall(Fall fall){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_FE_DATE, fall.getTimeStampFallEvent());
		values.put(DatabaseTable.COLUMN_FE_IS_NOTIFIED, fall.isNotifiedAsInteger());
		values.put(DatabaseTable.COLUMN_FE_XML, fall.getXmlFileName());
		values.put(DatabaseTable.COLUMN_FE_FK_SESSION, fall.getSessionId());
		
		return insertInDatabase(DatabaseTable.FALL_EVENTS_TABLE, values);
	}//[m] insertAFall
	
	/**
	 * [m]
	 * Method to insert a MailAddress into the database
	 * 
	 * @param address to insert in the database
	 * @return the id of the MailAddress, or ON_OPEN_ERROR if an error is occurred on open the database,
	 * or INSERT_ERROR if an error is occurred during the insertion.
	 */
	public long insertAMailAddress(MailAddress address){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_ML_ADDRESS, address.getAddress());
		values.put(DatabaseTable.COLUMN_ML_NAME, address.getName());
		values.put(DatabaseTable.COLUMN_ML_SURNAME, address.getSurname());
		
		return insertInDatabase(DatabaseTable.MAIL_TABLE, values);
	}//[m] insertAMailAddress
	
	/**
	 * [m]
	 * Method to insert a Accelerometer Data into the database
	 * 
	 * @param data to insert in the database
	 * @return the id of the AccelData, or ON_OPEN_ERROR if an error is occurred on open the database,
	 * or INSERT_ERROR if an error is occurred during the insertion.
	 */
	public long insertAnAccelData(AccelData data){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_AC_TS, data.getTimestamp());
		values.put(DatabaseTable.COLUMN_AC_X, data.getX());
		values.put(DatabaseTable.COLUMN_AC_Y, data.getY());
		values.put(DatabaseTable.COLUMN_AC_Z, data.getZ());
		values.put(DatabaseTable.COLUMN_AC_FK_FALLS, data.getFallId());
		
		return insertInDatabase(DatabaseTable.ACCEL_TABLE, values);
	}//[m] insertAnAccelData
	
	/**
	 * [m]
	 * Method to upgrade a specific part of the Session
	 * 
	 * @param sessionId the id of the session to upgrade
	 * @param valuesToupgrade an ContentValues of specific data to upgrade, if you want to upgrade all session UpgradeASession(Session session)
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database
	 */
	public int upgradeASession(long sessionId, ContentValues valuesToupgrade){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + sessionId;
	
		return upgradeRow(DatabaseTable.SESSION_TABLE, valuesToupgrade, whereClause, null);
	}//[m] upgradeASession
	
	/**
	 * [m]
	 * Method to upgrade all Session values (not ID)
	 * 
	 * @param session the session to upgrade
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database, or ID_NOT_SET_ERROR if the id of the session in not set.
	 */
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
	 * Method to upgrade a specific part of the Fall
	 * 
	 * @param fallId the id of the fall to upgrade
	 * @param valuesToupgrade an ContentValues of specific data to upgrade, if you want to upgrade all session UpgradeAFall(Fall fall)
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database
	 */
	public int upgradeAFall(long fallId, ContentValues valuesToupgrade){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + fallId;
	
		return upgradeRow(DatabaseTable.FALL_EVENTS_TABLE, valuesToupgrade, whereClause, null);
	}//[m] upgradeAFall
	
	/**
	 * [m]
	 * Method to upgrade all Fall values (not ID)
	 * 
	 * @param fall the fall to upgrade
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database, 
	 * or ID_NOT_SET_ERROR if the id of the fall in not set.
	 */
	public int upgradeAFall(Fall fall){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_FE_DATE, fall.getTimeStampFallEvent());
		values.put(DatabaseTable.COLUMN_FE_IS_NOTIFIED, fall.isNotifiedAsInteger());
		values.put(DatabaseTable.COLUMN_FE_XML, fall.getXmlFileName());
		values.put(DatabaseTable.COLUMN_FE_FK_SESSION, fall.getSessionId());

		String whereClause = "";
		long id = fall.getId();
		if(id > 0){
			whereClause = DatabaseTable.COLUMN_PK_ID + " = " + id;
		}else{
			return ID_NOT_SET_ERROR;
		}//if... else...
		
		return upgradeRow(DatabaseTable.FALL_EVENTS_TABLE, values, whereClause, null);
	}//[m] upgradeAFall
	
	/**
	 * [m]
	 * Method to upgrade a specific part of the Mail Address
	 * 
	 * @param addressId the id of the Mail Address
	 * @param valuesToupgrade an ContentValues of specific data to upgrade, if you want to upgrade all session upgradeAMailAddress(MailAddress address)
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database
	 */
	public int upgradeAMailAddress(long addressId, ContentValues valuesToupgrade){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + addressId;
	
		return upgradeRow(DatabaseTable.MAIL_TABLE, valuesToupgrade, whereClause, null);
	}//[m] upgradeAMailAddress
	
	/**
	 * [m]
	 * Method to upgrade all Mail Address values (not ID)
	 * 
	 * @param address the address to upgrade
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database, 
	 * or ID_NOT_SET_ERROR if the id of the fall in not set.
	 */
	public int upgradeAMailAddress(MailAddress address){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_ML_ADDRESS, address.getAddress());
		values.put(DatabaseTable.COLUMN_ML_NAME, address.getName());
		values.put(DatabaseTable.COLUMN_ML_SURNAME, address.getSurname());

		String whereClause = "";
		long id = address.getId();
		if(id > 0){
			whereClause = DatabaseTable.COLUMN_PK_ID + " = " + id;
		}else{
			return ID_NOT_SET_ERROR;
		}//if... else...
		
		return upgradeRow(DatabaseTable.MAIL_TABLE, values, whereClause, null);
	}//[m] upgradeAMailAddress

	/**
	 * [m]
	 * Method to upgrade a specific part of the Accelerometer Data
	 * 
	 * @param addressId the id of the AccelData
	 * @param valuesToupgrade an ContentValues of specific data to upgrade, if you want to upgrade all session upgradeAnAccelData(AccelData data)
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database
	 */
	public int upgradeAnAccelData(long dataId, ContentValues valuesToupgrade){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + dataId;
	
		return upgradeRow(DatabaseTable.ACCEL_TABLE, valuesToupgrade, whereClause, null);
	}//[m] upgradeAnAccelData
	
	/**
	 * [m]
	 * Method to upgrade all Accelerometer Data values (not ID)
	 * 
	 * @param data the data to upgrade
	 * @return the number of rows affected, or ON_OPEN_ERROR if an error occurred on open the database, 
	 * or ID_NOT_SET_ERROR if the id of the fall in not set.
	 */
	public int upgradeAnAccelData(AccelData data){
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_AC_TS, data.getTimestamp());
		values.put(DatabaseTable.COLUMN_AC_X, data.getX());
		values.put(DatabaseTable.COLUMN_AC_Y, data.getY());
		values.put(DatabaseTable.COLUMN_AC_Z, data.getZ());
		values.put(DatabaseTable.COLUMN_AC_FK_FALLS, data.getFallId());

		String whereClause = "";
		long id = data.getId();
		if(id > 0){
			whereClause = DatabaseTable.COLUMN_PK_ID + " = " + id;
		}else{
			return ID_NOT_SET_ERROR;
		}//if... else...
		
		return upgradeRow(DatabaseTable.ACCEL_TABLE, values, whereClause, null);
	}//[m] upgradeAnAccelData
	
	/**
	 * [m]
	 * private generic method to insert a new row in the database
	 * 
	 * @param tableName the name of the table into add the new row.
	 * @param values this map contains the initial column values for the row. The keys should be the column names and the values the column values
	 * @return the id of the item, or ON_OPEN_ERROR if an error is occurred on open the database,
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
	
	/**
	 * [m]
	 * Method to get all Session stored in database as ArrayList
	 * 
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return a void ArrayList if has no element, or null if occurred an error on open the database.
	 */
	public ArrayList<Session> getSessionAsArray(String selection, String orderBy){
		ArrayList<Session> tmp = new ArrayList<Session>();
		
		Cursor c = getSessionAsCursor(selection, orderBy);
		
		if(c == null){
			return null;
		}
		
		if(c.moveToFirst()){
			do{
				Session s = new Session(c.getString(c.getColumnIndex(DatabaseTable.COLUMN_SS_NAME)),
						c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_SS_START_DATE)));
				s.setColorThumbnail(c.getInt(c.getColumnIndex(DatabaseTable.COLUMN_SS_COLOR_THUMBNAIL)));
				s.setDuration(c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_SS_DURATION)));
				s.setId(c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_PK_ID)));
				s.setFallsNum(c.getInt(c.getColumnIndex(DatabaseTable.COLUMN_SS_FALLS_NUMBER)));
				s.setToActive(c.getInt(c.getColumnIndex(DatabaseTable.COLUMN_SS_IS_ACTIVE)));
				s.setXmlFileName(c.getString(c.getColumnIndex(DatabaseTable.COLUMN_SS_XML)));
				
				tmp.add(s);
			
			}while(c.moveToNext());
			
		}
		
		return tmp;
	}//[m] getSessionAsArray
	
	
	/**
	 * [m]
	 * Method to get all Session stored in database as Cursor
	 * 
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
	 * @see Cursor 
	 */
	public Cursor getSessionAsCursor(String selection, String orderBy){
		
		Cursor tmp = queryDb(DatabaseTable.SESSION_TABLE, DatabaseTable.ALL_COLUMNS_SESSION, selection, orderBy);
		return tmp;
	}//[m] getSessionAsCursor
	
	/**
	 * [m]
	 * Method to get all Fall stored in database as ArrayList
	 * 
     * @param sessionId the id of the session associated
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return a void ArrayList if has no element, or null if occurred an error on open the database.
	 */
    public ArrayList<Fall> getFallForSessionAsArray(long sessionId, String orderBy){
    	ArrayList<Fall> tmp = new ArrayList<Fall>();
    	
    	Cursor c = getFallForSessionAsCursor(sessionId, orderBy);
    	if(c == null){
			return null;
		}
		
		if(c.moveToFirst()){
			do{
				Fall f = new Fall(c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_FE_DATE)), c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_FE_FK_SESSION)));
				f.setNotification(c.getInt(c.getColumnIndex(DatabaseTable.COLUMN_FE_IS_NOTIFIED)));
				f.setId(c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_PK_ID)));
				f.setXmlFileName(c.getString(c.getColumnIndex(DatabaseTable.COLUMN_FE_XML)));
				
				tmp.add(f);
				
			}while(c.moveToNext());
			
		}
		
		return tmp;
    }//[m] getFallForSessionAsArray
    
    /**
     * [m]
     * Method to get all Fall Event stored in database as Cursor
     * 
     * @param sessionId the id of the session associated
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
     * @see Cursor 
     */
    public Cursor getFallForSessionAsCursor(long sessionId, String orderBy){
    	
    	String selection = DatabaseTable.COLUMN_FE_FK_SESSION + " = " + sessionId; 
    	
    	Cursor tmp = queryDb(DatabaseTable.FALL_EVENTS_TABLE, DatabaseTable.ALL_COLUMNS_FALL_EVENTS, selection, orderBy);
    	return tmp;
    }//[m] getFallForSessionAsCursor

    /**
     * [m]
     * Method to get all mail Address as Array
     * 
     * @return a void ArrayList if has no element, or null if occurred an error on open the database.
     */
	public ArrayList<MailAddress> getMailAddressAsArray(){
		ArrayList<MailAddress> tmp = new ArrayList<MailAddress>();
		Cursor c = getMailAddressAsCursor();
    	if(c == null){
			return null;
		}
		
		if(c.moveToFirst()){
			do{
				MailAddress toAdd = new MailAddress(c.getString(c.getColumnIndex(DatabaseTable.COLUMN_ML_ADDRESS)));
				toAdd.setName(c.getString(c.getColumnIndex(DatabaseTable.COLUMN_ML_NAME)));
				toAdd.setSurname(c.getString(c.getColumnIndex(DatabaseTable.COLUMN_ML_SURNAME)));
				toAdd.setId(c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_PK_ID)));
				
				tmp.add(toAdd);
			}while(c.moveToNext());
			
		}
		
		return tmp;
	}//[m] getMailAddressAsArray
	
    /**
     * [m]
     * Method to get all mail Address as Cursor
     * 
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
     * @see Cursor
     */
	public Cursor getMailAddressAsCursor(){
    	
    	Cursor tmp = queryDb(DatabaseTable.MAIL_TABLE, DatabaseTable.ALL_COLUMNS_MAIL, null, null);
    	return tmp;
    }//[m] getMailAddressAsCursor
	
	/**
	 * [m]
     * Method to get all Accelerometer Data stored in database as Array List
     * 
     * @param sessionId the id of the fall associated
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return a void ArrayList if has no element, or null if occurred an error on open the database.
	 */
	public ArrayList<AccelData> getAccelDataAsArrayForAFall(long fallId, String orderBy){
		ArrayList<AccelData> tmp = new ArrayList<AccelData>();
		Cursor c = getAccelDataAsCursorForAFall(fallId, orderBy);
    	if(c == null){
			return null;
		}
		
		if(c.moveToFirst()){
			do{			
				AccelData toAdd = new AccelData(c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_AC_TS)),
						c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_AC_FK_FALLS)));
				toAdd.setId(c.getLong(c.getColumnIndex(DatabaseTable.COLUMN_PK_ID)));
				toAdd.setX(c.getDouble(c.getColumnIndex(DatabaseTable.COLUMN_AC_X)));
				toAdd.setY(c.getDouble(c.getColumnIndex(DatabaseTable.COLUMN_AC_Y)));
				toAdd.setZ(c.getDouble(c.getColumnIndex(DatabaseTable.COLUMN_AC_Z)));
				tmp.add(toAdd);
				
			}while(c.moveToNext());
			
		}
		return tmp;
	}
	
	/**
	 * [m]
     * Method to get all Accelerometer Data stored in database as Cursor
     * 
     * @param sessionId the id of the fall associated
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
     * @see Cursor 
	 */
	public Cursor getAccelDataAsCursorForAFall(long fallId, String orderBy){
    	
    	String selection = DatabaseTable.COLUMN_AC_FK_FALLS + " = " + fallId; 
		
    	Cursor tmp = queryDb(DatabaseTable.ACCEL_TABLE, DatabaseTable.ALL_COLUMNS_ACCEL, selection, orderBy);
    	return tmp;
    }//[m] getMailAddressAsCursor
	/**
	 * [m]
	 * Complex method to query the given table, returning a Cursor over the result set.
	 * 
	 * @param distinct true if you want each row to be unique, false otherwise.
	 * @param table The table name to compile the query against.
	 * @param columns A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @param groupBy A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * @param having A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @param limit Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause.
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
	 * @see Cursor 
	 */
	public Cursor queryDb(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit){
		try{
			open();
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}//try.. catch
		
		Cursor tmp = mDb.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		
		close();
		return tmp;
	}
	/**
	 * [m]
	 * Intermediary method to query the given table, returning a Cursor over the result set.
	 * 
	 * @param table The table name to compile the query against.
	 * @param columns A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param selectionArgs You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings.
	 * @param groupBy A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped.
	 * @param having A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used.
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
	 * @see Cursor 
	 */
	public Cursor queryDb(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
		try{
			open();
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}//try.. catch
		
		Cursor tmp = mDb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
		
		close();
		return tmp;
	}
	
	/**
	 * [m]
	 * Simplest method to query the given table, returning a Cursor over the result set.
	 * 
	 * @param table The table name to compile the query against.
	 * @param columns A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
	 * @see Cursor 
	 */
	public Cursor queryDb(String table, String[] columns, String selection){
		try{
			open();
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}//try.. catch
		
		Cursor tmp = mDb.query(table, columns, selection, null, null, null, null);
		
		close();
		return tmp;
	}
	
	/**
	 * [m]
	 * Intermediary method to query the given table, returning a Cursor over the result set.
	 * 
	 * @param table The table name to compile the query against.
	 * @param columns A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used.
	 * @param selection A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table.
	 * @param orderBy How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered.
	 * @return A Cursor object, which is positioned before the first entry, or null if occurred an error on open the database. Note that Cursor are not synchronized, see the documentation for more details.
	 * @see Cursor 
	 */
	public Cursor queryDb(String table, String[] columns, String selection, String orderBy){
		try{
			open();
		} catch (SQLException e){
			e.printStackTrace();
			return null;
		}//try.. catch
		
		Cursor tmp = mDb.query(table, columns, selection, null, null, null, orderBy);
		
		close();
		return tmp;
	}
	
	
	// TODO delete methods documentation
	
	public int deleteASession(long sessionId){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + sessionId;
		return deleteFromDb(DatabaseTable.SESSION_TABLE, whereClause);
	}
	
	public int deleteAFall(long fallId){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + fallId;
		return deleteFromDb(DatabaseTable.FALL_EVENTS_TABLE, whereClause);
	}
	
	public int deleteAMailAddress(long mailId){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + mailId;
		return deleteFromDb(DatabaseTable.MAIL_TABLE, whereClause);
	}
	
	public int deleteAnAccelData(long dataId){
		String whereClause = DatabaseTable.COLUMN_PK_ID + " = " + dataId;
		return deleteFromDb(DatabaseTable.ACCEL_TABLE, whereClause);
	}
	
	/**
	 * [m]
	 * @param tableNam the table to delete from
	 * @param whereClause the optional WHERE clause to apply when deleting. Passing null will delete all rows.
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise, or ON_OPEN_ERROR if db not open. To remove all rows and get a count pass "1" as the whereClause. 
	 */
	private int deleteFromDb(String tableNam, String whereClause){
		try{
			open();
		} catch (SQLException e){
			e.printStackTrace();
			return ON_OPEN_ERROR;
		}//try.. catch

		
		int row = mDb.delete(tableNam, whereClause, null);
	    close();
		return row;
	}
}
