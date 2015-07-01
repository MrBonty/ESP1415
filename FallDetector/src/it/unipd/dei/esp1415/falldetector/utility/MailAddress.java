
package it.unipd.dei.esp1415.falldetector.utility;

public class MailAddress {
	private String name;
	private String surname;
	private String address;
	private long id;
	
	public MailAddress(String address){
		this.address = address;
	}//[c] MailAddress()
	
	/**
	 * [m]
	 * @return the name
	 */
	public String getName() {
		return name;
	}//[m] getName() 
	
	/**
	 * [m]
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}//[m] setName()
	
	/**
	 * [m]
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}//[m] getSurname()
	
	/**
	 * [m]
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}//[m] setSurname()
	
	/**
	 * [m]
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}//[m] getAddress()
	
	/**
	 * [m]
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}//[m] setAddress()

	/**
	 * [m]
	 * @return the id
	 */
	public long getId() {
		return id;
	}//[m] getId()

	/**
	 * [m]
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}//[m] setId()
}
