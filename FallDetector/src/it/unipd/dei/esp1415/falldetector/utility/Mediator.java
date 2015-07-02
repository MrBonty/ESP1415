package it.unipd.dei.esp1415.falldetector.utility;

import it.unipd.dei.esp1415.falldetector.fragment.DetailSessionFragment;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

public class Mediator {
	
	public static final int START_FRAG_POS = -1;
	
	private static Activity mainAct;
	private static Context mContext;
	private static int mCurrPosSession = START_FRAG_POS;
	private static ArrayList<Session> mSessions;
	private static boolean mIsCalledFromBack;
	private static boolean mIsSessionSet = false;
	private static boolean mIsLand = false;
	private static boolean mIsLarge = false;
	
	private static boolean mIsWifiControlled = false;
	private static boolean mIsMobileControlled = false;
	private static boolean mIsLocationControlled = false;
	
	private static DetailSessionFragment mDetailSessionFragment;
	
	/**
	 * [c]
	 * constructor called only from the main activity to initialize the Mediator
	 * 
	 * @param context context of the application
	 * @param main the main activity
	 */
	public Mediator(Context context, Activity main){ //called from mainActivity
		Mediator.mainAct = main;
		Mediator.mContext = context;
	}
	
	/**
	 * [c]
	 * Void constructor for all activity. FOR MAIN ACTIVITY USE constructor -> Mediator(Context context, Activity main) 
	 */
	public Mediator(){}
	
	/**
	 * [m]
	 * Method to set the Sessions Array
	 * @param session the array
	 */
	public void setDataSession(ArrayList<Session> session){
		mIsSessionSet = true;
		mSessions = session;
	}
	
	/**
	 * [m]
	 * Method to get the Sessions Array
	 * @return the array
	 */
	public ArrayList<Session> getDataSession(){
		return mSessions;
	}
	
	/**
	 * [m]
	 * Method to get the context save from MainActivity
	 * 
	 * @return the context
	 */
	public Context getContext(){
		return mContext;
	}
	
	/**
	 * [m]
	 * Method to get the MainActivity
	 * 
	 * @return the MainActivity
	 */
	public Activity getMain(){
		return mainAct;
	}
	
	/**
	 * [m]
	 * Method to set the current position clicked on list of Session
	 * 
	 * @param pos the position of the Session Clicked
	 */
	public void setCurretnPosSession(int pos){
		mCurrPosSession = pos;
	}
	/**
	 * [m]
	 * Method to get the current position clicked on list of Session
	 * 
	 * @return pos the position of the Session Clicked
	 */
	public int getCurretnPosSession(){
		return mCurrPosSession;
	}
	/**
	 * [m]
	 * Method to view if MainActivity is called from Back Button from Detail Activity. 
	 * After call resetIsCalledFromBack
	 * On Detail Activity USE method 'resetCurretnPosSessionFromBack()' on 'onBackPressed()' method
	 * 
	 * @see resetCurretnPosSessionFromBack
	 * @see resetIsCalledFromBack
	 * 
	 * @return true if MainActivity is called from Back Button from Detail Activity false otherwise
	 */
	public boolean isCalledFromBack(){
		return mIsCalledFromBack;
	}
	public void resetIsCalledFromBack(){
		mIsCalledFromBack = false;
	}
	/**
	 * [m]
	 * Method to set the position of Session to START_FRAG_POS
	 */
	public void resetCurretnPosSessionFromBack(){
		mCurrPosSession = START_FRAG_POS;
		mIsCalledFromBack = true;	
	}
	
	public boolean hasDataSession(){
		return mIsSessionSet;
	}
	
	
	public boolean isLand(){	
		return mIsLand;
	}
	public boolean isLand(boolean isLand){
		mIsLand = isLand;
		return mIsLand;
	}
	
	public boolean isLarge(){	
		return mIsLarge;
	}
	public boolean isLarge(boolean isLarge){
		mIsLarge = isLarge;
		return mIsLarge;
	}
	
	public void setDetailFrag(DetailSessionFragment detailFrag){
		mDetailSessionFragment = detailFrag;
	}
	public DetailSessionFragment getDetatilFrag(){
		return mDetailSessionFragment;
	}
	
	public boolean isLocationControlled(){
		return mIsLocationControlled;
	}
	public void isLocationControlled(boolean isLocationControlled){
		mIsLocationControlled = isLocationControlled;
	}
	
	public void isConnectionControlled(boolean isWifiControlled, boolean isMobileControlled){
		mIsWifiControlled = isWifiControlled;
		mIsMobileControlled = isMobileControlled;
	}
	
	public boolean isConnectionControlled(){
		return mIsMobileControlled && mIsWifiControlled;
	}
}
