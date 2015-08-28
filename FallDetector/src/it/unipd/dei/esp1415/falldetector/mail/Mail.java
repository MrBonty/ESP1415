package it.unipd.dei.esp1415.falldetector.mail;
import java.util.ArrayList;

/**
 * Utility class for define a mail obj
 */
public class Mail {
	
	private String sender;
	
	private String subject;
	private String message;
	
	private ArrayList<String> receivers;
	private boolean hasMore;
	
	public Mail(String sender, String receiver){
		this.sender = sender;
		receivers.add(receiver);
		hasMore = false;
	}//[c] Mail()
	
	public Mail(String sender, ArrayList<String> receivers){
		this.sender = sender;
		this.receivers = receivers;
		hasMore = receivers.size() > 1;
	}//[c] Mail()
	
	/**
	 * [m]
	 * method to set the subject of the mail
	 * 
	 * @param subject of the mail
	 */
	public void setSubject(String subject){
		this.subject= subject;
	}//[m] setSubject()
	
	/**
	 *	[m]
	 * method to set the message of the mail
	 * 
	 * @param message of the mail
	 */
	public void setMessage(String message){
		this.message= message;
	}//[m] setMessage()
	
	/** 
	 * [m]
	 * Method to add a receiver to the arraylist 
	 * 
	 * @param receiver to add
	 */
	public void addReceiver(String receiver){
		receivers.add(receiver);
		if(receivers.size()> 0){
			hasMore = true;
		}
	}//[m] addReceiver()
	
	/**
	 * [m]
	 * @return the mail message
	 */
	public String getMessage(){
		return message;
	}//[m] getMessage()
	
	/**
	 * [m]
	 * @return the mail subject
	 */
	public String getSubject(){
		return subject;
	}//[m] getSubject()
	
	/**
	 * [m]
	 * @return the mail sender
	 */
	public String getSender(){
		return sender;
	}//[m] getSender()
	
	/** 
	 * [m]
	 * @return the first receiver
	 */
	public String getReceiver(){
		return receivers.get(0);
	}//[m] getReceiver()
	
	/**
	 * [m]
	 * @return the receivers
	 */
	public ArrayList<String> getReceivers() {
		return receivers;
	}//[m] getReceivers() 
	
	/**	 
	 * [m]
	 * @param receivers the arraylist of receivers to set
	 */
	public void setReceivers(ArrayList<String> receivers) {
		this.receivers = receivers;
		hasMore = receivers.size() > 1;
	}//[m] setReceivers()
	
	/**
	 * [m]
	 * Method to remove a receiver
	 * 
	 * @param pos
	 * @return
	 */
	public String removeReceivers(int pos){
		if(receivers.size()>1){
			return receivers.remove(pos);
		}
		hasMore = false;
		return null;
	}//[m] removeReceivers()
	
	/**
	 * [m]
	 * @return true if has multiple receivers, false otherwise
	 */
	public boolean hasMore() {
		return hasMore;
	}//[m] hasMore() 
	
	/**
	 * [m]
	 * @return the size of the arraylist of receivers
	 */
	public int getReceiversSize(){
		return receivers.size();
	}//[m] getReceiversSize()
}
