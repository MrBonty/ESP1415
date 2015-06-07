package it.unipd.dei.esp1415.falldetector.mail;
import java.util.ArrayList;


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
	}
	public Mail(String sender, ArrayList<String> receivers){
		this.sender = sender;
		this.receivers = receivers;
		hasMore = receivers.size() > 1;
		
	}
	
	public void setSubject(String subject){
		this.subject= subject;
	}
	public void setMessage(String message){
		this.message= message;
	}
	public void addReceiver(String receiver){
		receivers.add(receiver);
		if(receivers.size()> 0){
			hasMore = true;
		}
	}
	public String getMessage(){
		return message;
	}
	public String getSubject(){
		return subject;
	}
	public String getSender(){
		return sender;
	}
	
	public String getReceiver(){
		return receivers.get(0);
	}
	/**
	 * @return the receivers
	 */
	public ArrayList<String> getReceivers() {
		return receivers;
	}
	/**
	 * @param receivers the receivers to set
	 */
	public void setReceivers(ArrayList<String> receivers) {
		this.receivers = receivers;
		hasMore = true;
	}
	public String removeReceivers(int pos){
		if(receivers.size()>1){
			return receivers.remove(pos);
		}
		hasMore = false;
		return null;
	}
	
	/**
	 * @return the hasMore
	 */
	public boolean hasMore() {
		return hasMore;
	}
}
