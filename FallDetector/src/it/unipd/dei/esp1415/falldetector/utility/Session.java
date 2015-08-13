package it.unipd.dei.esp1415.falldetector.utility;

import java.util.ArrayList;
import java.util.Calendar;

import android.graphics.Bitmap;

public class Session {
	private String name;
	
	private Calendar startDate;
	
	private long startTimeStamp;
	private long endTimeStamp;
	private long duration = 0;  
	private int colorThumbnail; // as #RRGGBB or #AARRGGBB
	private long id;
	private int falls = 0;
	private boolean isActive;
	private String xmlFile;
	private ArrayList<Fall> fallsEvent; // TODO describe fall event with a
											// class

	private static final int TRUE = 1;
	private static final int FALSE = 0;
	
	public static final int DESC = 0;
	public static final int ASC = 1;
	
	private Bitmap bitmapIcon = null;
	
	
	/**
	 * [c] Create a Session object At the creation of the Session it make a
	 * timestamp
	 * 
	 * @param name
	 *            the session name
	 */
	public Session(String name) {
		this.name = name;
		startDate = Calendar.getInstance();
		startTimeStamp = startDate.getTimeInMillis();
		setDuration(startTimeStamp);
	}//[c] Session()

	/**
	 * [c] Create a Session object
	 * 
	 * @param name
	 *            the session name
	 * @param timeStamp
	 *            the date
	 */
	public Session(String name, long timeStamp) {
		this.name = name;
		if(timeStamp > 0){
			startDate = Calendar.getInstance();
			startDate.setTimeInMillis(timeStamp);
		}
		this.startTimeStamp = timeStamp;
		setDuration(startTimeStamp);
	}//[c] Session()
	
	/**
	 * [m]
	 * Change Name of Session
	 * 
	 * @param name of Session
	 */
	public void setName(String name){
		this.name = name;
	}//[m] setName()
	
	/**
	 * [m]
	 * Method that return the name of the session
	 * 
	 * @return the session name
	 */
	public String getName(){
		return name;
	}//[m] getName()

	/**
	 * [m]
	 * Method that return the date of the session
	 * 
	 * @return the string of the date if startTimeStamp> 0, else ""
	 */
	public String getStartDate() {
		if(startTimeStamp > 0){
			return startDate.get(Calendar.DAY_OF_MONTH) + "/"
				+ (startDate.get(Calendar.MONTH) + 1) + "/"
				+ startDate.get(Calendar.YEAR);
		}
		
		return "";
	}//[m] getDate()
	
	public long getStartTimestamp() {
		return startTimeStamp;
	}//[m] getStartTimestamp()
	
	/**
	 * [m]
	 * method to set the id
	 * 
	 * @param id the id of database for current session
	 */
	public void setId(long id){
		this.id = id;
	}//[m] setId()
	
	/**
	 * [m]
	 * method to get the id
	 * 
	 * @return the id of database for current session
	 */
	public long getId(){
		return id;		
	}//[m] getId()
	
	/**
	 * [m]
	 * method to set the color of current session as AARRGGBB
	 * 
	 * @param color the color for the thumbnail as AARRGGBB
	 */
	public void setColorThumbnail(int color){
		colorThumbnail = color;
	}//[m] setColorThumbnail()
	
	/**
	 * [m]
	 * method to get the color of current session as AARRGGBB
	 * 
	 * @return the color for the thumbnail as AARRGGBB
	 */
	public int getColorThumbnail(){
		return colorThumbnail;
	}//[m] getColorThumbnail()
	
	/**
	 * [m]
	 * method to set the number of falls of current session
	 * 
	 * @param number the number of falls of current session
	 */
	public void setFallsNum(int number){
		falls = number;
	}//[m] setFallsNum()
	
	/**
	 * [m]
	 * method to get the number of falls of current session
	 * 
	 * @return number of falls of current session
	 */
	public int getFallsNum(){
		return falls;
	}// [m] getFallsNum()
	
	/**
	 * [m]
	 * method to set the duration of current session
	 */
	public void setDuration(){
		endTimeStamp = Calendar.getInstance().getTimeInMillis();
		duration = endTimeStamp - startTimeStamp;
	}//[m] setDuration()
	
	/**
	 * [m]
	 * method to set the duration of current session
	 * 
	 * @param timeStamp the new timeStamp -> duration = timeStamp - startTimeStamp
	 */
	public void setDuration(long timeStamp){
		//endTimeStamp = timeStamp;
		duration = timeStamp;
	}//[m] setDuration()
	
	/**
	 * [m]
	 * method to get the duration of current session
	 * 
	 * @return the value of duration 
	 */
	public long getDuration(){
		return duration;
	}//[m] getDuration()
	
	/**
	 * [m]
	 * Method to see if current session is active. Use method setToActive to set this value on start
	 * 
	 * @see setToActive
	 *
	 * @return true if the session is active, false otherwise
	 */
	public boolean isActive(){
		return isActive;
	}//[m] isActive() 
	
	/**
	 * [m]
	 * Method to see if current session is active as integer. Use method setToActive to set this value on start
	 * 
	 * @see setToActive
	 *
	 * @return 1 if the session is active as true, 0 otherwise as false
	 */
	public int isActiveAsInteger(){
		if(isActive){
			return TRUE;
		}
		return FALSE; 
	}// [m] isActiveAsInteger()
	
	/**
	 * [m]
	 * Set if the session is active
	 * 
	 * @param value if is 0 isActive return false, otherwise it return true
	 * 
	 * @see isActive
	 */
	public void setToActive(int value){
		if(value == FALSE) isActive = false;
		else isActive = true;
	}// [m] setToActive()
	
	/**
	 * [m]
	 * Set if the session is active
	 * 
	 * @param value
	 * 
	 * @see isActive
	 */
	public void setToActive(boolean value){
		isActive = value;
	}// [m] setToActive()
	
	/**
	 * [m]
	 * Method to get the name of the file xml
	 * 
	 * @return the name of the xml file
	 */
	public String getXmlFileName(){
		return xmlFile;
	}//[m] getXmlFileName()
	
	/**
	 * [m]
	 * Method to get the name of the file xml from existing on stored on db
	 * 
	 * @param xml the name get from db
	 */
	public void setXmlFileName(String xml){
		xmlFile = xml;
	}// [m] setXmlFileName()
	
	/**
	 * [m]
	 * Method to set name of file xml as hhmmssmmmddmmaaaa
	 */
	public void generateXmlName(){
		xmlFile = "" + startDate.get(Calendar.HOUR)
				+ startDate.get(Calendar.MINUTE)
				+ startDate.get(Calendar.SECOND)
				+ startDate.get(Calendar.MILLISECOND)
				+ startDate.get(Calendar.DAY_OF_MONTH)
				+ (startDate.get(Calendar.MONTH) + 1)
				+ startDate.get(Calendar.YEAR);
	}// [m] generateXmlName()
	
	/**
	 * [m]
	 * Method to save the bitmap for the session 
	 * 
	 * @param bitmapIcon the bitmap to save
	 */
	public void setBitmap(Bitmap bitmapIcon){
		this.bitmapIcon = bitmapIcon;
	}//[m] setBitmap()
	
	/**
	 * [m]
	 * Method to return the saved bitmap for current session
	 * 
	 * @return the saved bitmap
	 */
	public Bitmap getBitmap(){
		return bitmapIcon;
	}//[m] getBitmap()
	
	/**
	 * [m]
	 * Method to get the start time as string
	 * 
	 * @return start time as hh:mm:ss if startTimeStamp > 0, else ""
	 */
	public String getStartTimeToString(){
		if(startTimeStamp > 0){
		String tmp = startDate.get(Calendar.HOUR)
				+ ":" + startDate.get(Calendar.MINUTE)
				+ ":" + startDate.get(Calendar.SECOND);
		return tmp;
		}
		return "";
	}//[m] getStartTimeToString()
	
	public void setFallEvents(ArrayList<Fall> items){
		fallsEvent = items;
		orderFalls(DESC);
		
	}
	public void addFall(Fall object){
		fallsEvent.add(object);
	}
	
	public void addFallEvents(ArrayList<Fall> items){
		for (int i = 0; i< items.size(); i++){
			Fall obj = items.get(i);
			
			int j;
			for (j = 0; j< fallsEvent.size() && obj.getTimeStampFallEvent() > fallsEvent.get(j).getTimeStampFallEvent(); j++);
			
			fallsEvent.add(j, obj);
		}
	}
	public Fall getFall(int index){
		return fallsEvent.get(index);
	}
	public Fall removeFall(int index){
		return fallsEvent.remove(index);
	}
	public ArrayList<Fall> getFallEvents(){
		return fallsEvent;
	}
	
	private void orderFalls(int or){
		//TODO SELECT ORDER ALGORITHM
	}

	/**[m]
	 * Method to get the duration as String HH:MM
	 * 
	 * @return the duration as HH:MM
	 */
	public String getDurationString() {
		String tmp = "00:00";
		if(duration > 0){
			int s = (int) (duration / 1000);
		  
		    int h = (int) (s / 3600);
			
		    int m = (int) ((s % 3600)/60);
		    
		    tmp = h+ ":"+ m;
		}
		return tmp;
	}//[m] getDurationString()
	
	public void setStartDateAndTimestamp(Calendar sessionStartTime){
		startDate = sessionStartTime;
		startTimeStamp = sessionStartTime.getTimeInMillis();
	}
	
	public Calendar getStartDateCalendar(){
		startDate.setTimeInMillis(startTimeStamp);
		return startDate;
	}
	
	
}
