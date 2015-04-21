package it.unipd.dei.esp1415.falldetector.utility;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;

public class Moderator {
	
	public static final int START_FRAG_POS = -1;
	
	private static Activity mainAct;
	private static Context mContext;
	private static int mCurrPosSession = START_FRAG_POS;
	private static ArrayList<Session> mSessions;
	private static boolean mIsCalledFromBack;
	
	public Moderator(Context context, Activity main){ //called from mainActivity
		Moderator.mainAct = main;
		Moderator.mContext = context;
	}
	
	public Moderator(){}
	
	public void setDataSession(ArrayList<Session> session){
		mSessions = session;
	}
	public ArrayList<Session> getDataSession(){
		return mSessions;
	}
	public Context getContext(){
		return mContext;
	}
	public Activity getMain(){
		return mainAct;
	}
	
	public void setCurretnPosSession(int pos){
		mCurrPosSession = pos;
	}
	public int getCurretnPosSession(){
		return mCurrPosSession;
	}
	public boolean isCalledFromBack(){
		return mIsCalledFromBack;
	}
	public void resetIsCalledFromBack(){
		mIsCalledFromBack = false;
	}
	public void resetCurretnPosSessionFromBack(){
		mCurrPosSession = START_FRAG_POS;
		mIsCalledFromBack = true;
		
	}
	
	
	
}
