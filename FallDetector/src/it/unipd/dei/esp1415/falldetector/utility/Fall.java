package it.unipd.dei.esp1415.falldetector.utility;

import java.util.Calendar;

public class Fall {
	private long id;
	private long timeStampFallEvent;
	private Calendar dateEvent;
	private double latitude;
	private double longitude;
	private boolean isNotified;
	private long sessionId;
	
	private static final int TRUE = 1;
	private static final int FALSE = 0;
	
	public Fall(long timeStamp, long sessionId){
		timeStampFallEvent = timeStamp;
		this.sessionId = sessionId;

		dateEvent = Calendar.getInstance();
		dateEvent.setTimeInMillis(timeStamp);

		isNotified = false;
	}
	
	/**
	 * [m]
	 * Method to set the id of the fall event
	 * 
	 * @param id of the db
	 */
	public void setId(long id){
		this.id= id;
	}//[m] setId()
	
	/**
	 * [m]
	 * Method to get the id of the fall event
	 * 
	 * @return the id
	 */
	public long getId(){
		return id;
	}//[m] getId()
	
	/**
	 * [m]
	 * Method to set if a fall is notified
	 * 
	 * @param value use TRUE, that equals 1, or FALSE, that equals 0, of this class
	 */
	public void setNotification(int value){
		if(value == FALSE){
			isNotified = false;
		}else {
			isNotified = true;
		}
	}//[m] setNotification()
	
	/**
	 * [m]
	 * Method to set if a fall is notified
	 * 
	 * @param value true or false
	 */
	public void setNotification(boolean value){
		isNotified = value;
	}//[m] setNotification()
	
	/**
	 * [m]
	 * @return true if the session is notified, false otherwise 
	 */
	public boolean isNotified(){
		return isNotified;
	}//[m] isNotified()
	
	/**
	 * [m]
	 * method use for store value on db
	 * 
	 * @return TRUE, that equals 1, if the session is notified, FALSE, that equals 0, otherwise 
	 */
	public int isNotifiedAsInteger(){
		if(isNotified){
			return TRUE;
		}
		return FALSE;
	}//[m] isNotifiedAsInteger()
	
	/** 
	 * [m]
	 * Method to set the timestamp of the fall
	 * 
	 * @param timestamp the timestamp of the fall
	 */
	public void setTimeStampFallEvent(long timestamp){
		timeStampFallEvent = timestamp;
	} // [m] setTimeStampFallEvent()
	
	/**
	 * [m]
	 * @return the timestamp of the fall
	 */
	public long getTimeStampFallEvent(){
		return timeStampFallEvent;
	}//[m] getTimeStampFallEvent()
	
	/**
	 * [m]
	 * @return the date of the fall by the timestamp
	 */
	public String[] dateTimeStampFallEven(){
		String date = dateEvent.get(Calendar.DAY_OF_MONTH) + "/"
				+ (dateEvent.get(Calendar.MONTH) + 1) + "/"
				+ dateEvent.get(Calendar.YEAR);
		
		String time = dateEvent.get(Calendar.HOUR)
				+ ":" + dateEvent.get(Calendar.MINUTE)
				+ ":" + dateEvent.get(Calendar.SECOND);
		
		String[] tmp =  {date, time};
		return tmp;
	}// [m] dateTimeStampFallEven()
	
	/**
	 * [m]
	 * Method to set the id of the session for which the fall is associated
	 * 
	 * @param sessionId the id of the session
	 */
	public void setSessionId(long sessionId){
		this.sessionId = sessionId;
	}// [m] setSessionId()
	
	/**
	 * [m]
	 * @return the id of the session for which the fall is associated
	 */
	public long getSessionId(){
		return sessionId;
	}// [m] getSessionId()
	
	public void setLatitude(double lat){
		latitude = lat;
	}
	
	public void setLongitude(double lon){
		longitude = lon;
	}
	
	public double getLatitude(){
		return latitude;
	}
	
	public double getLongitude(){
		return longitude;
	}
	
	@Override
	public String toString(){
		String s;
		if(isNotified)
			s = "Sent";
		else
			s = "Not Sent";
		return dateEvent.getTime().toString() + " " + s;
	}
}

