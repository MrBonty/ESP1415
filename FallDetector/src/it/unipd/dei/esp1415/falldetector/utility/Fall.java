package it.unipd.dei.esp1415.falldetector.utility;

import java.util.Calendar;

public class Fall {
	private long id;
	private long timeStampFallEvent;
	private Calendar dateEvent;
	private boolean isNotified;
	private long sessionId;
	private String xmlFile;
	
	private static final int TRUE = 1;
	private static final int FALSE = 0;
	
	public Fall(long timeStamp, long sessionId){
		timeStampFallEvent = timeStamp;
		this.sessionId = sessionId;
		isNotified = false;
		dateEvent = Calendar.getInstance();
	}
	
	/**
	 * [m]
	 * Method to set the id of the fall event
	 * 
	 * @param id of the db
	 */
	public void setId(long id){
		this.id= id;
	}
	
	/**
	 * [m]
	 * Method to get the id of the fall event
	 * 
	 * @return the id
	 */
	public long getId(){
		return id;
	}
	
	public void setNotification(int value){
		if(value == FALSE){
			isNotified = false;
		}else {
			isNotified = true;
		}
	}
	
	public void setNotification(boolean value){
		isNotified = value;
	}
	
	public boolean isNotified(){
		return isNotified;
	}
	
	public int isNotifiedAsInteger(){
		if(isNotified){
			return TRUE;
		}
		return FALSE;
	}
	
	public void setTimeStampFallEvent(long timestamp){
		timeStampFallEvent = timestamp;
	}
	
	public long getTimeStampFallEvent(){
		return timeStampFallEvent;
	}
	
	public String[] dateTimeStampFallEven(){
		String date = dateEvent.get(Calendar.DAY_OF_MONTH) + "/"
				+ (dateEvent.get(Calendar.MONTH) + 1) + "/"
				+ dateEvent.get(Calendar.YEAR);
		
		String time = dateEvent.get(Calendar.HOUR)
				+ ":" + dateEvent.get(Calendar.MINUTE)
				+ ":" + dateEvent.get(Calendar.SECOND);
		
		String[] tmp =  {date, time};
		return tmp;
	}
	
	public void setSessionId(long sessionId){
		this.sessionId = sessionId;
	}
	
	public long getSessionId(){
		return sessionId;
	}
	
	/**
	 * [m]
	 * Method to get the name of the file xml
	 * 
	 * @return the name of the xml file
	 */
	public String getXmlFileName(){
		return xmlFile;
	}
	
	/**
	 * [m]
	 * Method to get the name of the file xml from existing on stored on db
	 * 
	 * @param xml the name get from db
	 */
	public void setXmlFileName(String xml){
		xmlFile = xml;
	}
	/**
	 * [m]
	 * Method to set name of file xml as hhmmssmmmddmmaaaa
	 */
	public void generateXmlName(){
		xmlFile = "" + dateEvent.get(Calendar.HOUR)
				+ dateEvent.get(Calendar.MINUTE)
				+ dateEvent.get(Calendar.SECOND)
				+ dateEvent.get(Calendar.MILLISECOND)
				+ dateEvent.get(Calendar.DAY_OF_MONTH)
				+ (dateEvent.get(Calendar.MONTH) + 1)
				+ dateEvent.get(Calendar.YEAR);
	}
	
}

