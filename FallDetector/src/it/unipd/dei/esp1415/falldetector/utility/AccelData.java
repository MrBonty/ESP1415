package it.unipd.dei.esp1415.falldetector.utility;

/**
 * Class to describe Accelerator Obj
 *
 */
public class AccelData {
	
	private long id;
	private long timestamp;
	private double x;
	private double y;
	private double z;
	private long fallId;
	
	public AccelData(){}
	
	public AccelData(long timeStamp, long fallId){
		this.timestamp = timeStamp;
		this.fallId = fallId;
	}//[c]
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}//[m] getId()

	/**
	 * Set id from the db
	 * 
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}//[m] setId()

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}//[m] getTimestamp()

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}//[m] setTimestamp()

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}//[m] getX()

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}//[m] setX()

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}//[m] getY()

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}//[m] setY()

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}//[m] getZ()

	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}//[m] setZ()

	/**
	 * @return the fallId
	 */
	public long getFallId() {
		return fallId;
	}//[m] getFallId() 

	/**
	 * @param fallId the fallId to set
	 */
	public void setFallId(long fallId) {
		this.fallId = fallId;
	}//[m] setFallId()
}//{c} AccelData
