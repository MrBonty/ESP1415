package it.unipd.dei.esp1415.falldetector.utility;

public class AccelData {
	
	private long id;
	private long timestamp;
	private double x;
	private double y;
	private double z;
	private long fallId;
	
	public AccelData(long timeStamp, long fallId){
		this.timestamp = timeStamp;
		this.fallId = fallId;
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}

	/**
	 * @return the fallId
	 */
	public long getFallId() {
		return fallId;
	}

	/**
	 * @param fallId the fallId to set
	 */
	public void setFallId(long fallId) {
		this.fallId = fallId;
	}
}
