package it.unipd.dei.esp1415.falldetector.utility;

public class FallAlgorithmUtility {

	public static double FALL_UPPER_BOUND = 18;
	public static double FALL_LOWER_BOUND = 7;
	public static int ACC_DATA_SIZE = 1000;

	public static double module(AccelData element){
		return Math.sqrt(Math.pow(element.getX(), 2) 
				+ Math.pow(element.getY() , 2) 
				+ Math.pow(element.getZ() , 2));
	}
	
	
	

}
