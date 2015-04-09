package it.unipd.dei.esp1415.falldetector.utility;

import java.util.ArrayList;
import java.util.Calendar;

public class Session {
	private String name;
	private Calendar date;
	private long timeStamp;
	private String duration;
	private String colorThumbnail; // as #RRGGBB or #AARRGGBB
	private long id;
	private int falls;
	private boolean isActive;
	private ArrayList<String> fallsEvent; // TODO describe fall event with a
											// class

	/**
	 * [c] Create a Session object At the creation of the Session it make a
	 * timestamp
	 * 
	 * @param name
	 *            the session name
	 */
	public Session(String name) {
		this.name = name;
		date = Calendar.getInstance();
		timeStamp = date.getTimeInMillis();
	}//[c] Session

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
		date.setTimeInMillis(timeStamp);
		this.timeStamp = timeStamp;
	}//[c] Session
	
	/**
	 * [m]
	 * Change Name of Session
	 * 
	 * @param name of Session
	 */
	public void setName(String name){
		this.name = name;
	}//[m] setName
	
	/**
	 * [m]
	 * Method that return the name of the session
	 * 
	 * @return the session name
	 */
	public String getName(){
		return name;
	}//[m] getName

	/**
	 * [m]
	 * Method that return the date of the session
	 * 
	 * @return the string of the date
	 */
	public String getDate() {
		return date.get(Calendar.DAY_OF_MONTH) + "/"
				+ (date.get(Calendar.MONTH) + 1) + "/"
				+ date.get(Calendar.YEAR);

	}//[m] getDate

	// public void save
}
