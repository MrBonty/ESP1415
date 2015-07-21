package it.unipd.dei.esp1415.falldetector.service;

import java.util.Calendar;

import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import android.app.AlarmManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AlarmService extends Service{
		
	private BroadcastReceiver mReceiver;
	private Context mContext; 
	
	private int mNotHour = 0; //notification hour
	private int mNotMinutes = 0; //notification minutes
	private long mNotMill = 0; //notification time in milliseconds
	
	private boolean mIsActive = false;
	
	private long mStartTime = 0;
	private long mDuration;
	
	private SharedPreferences preferences;
	private SharedPreferences localPref;
	
	private Calendar mNextNotificationDate;
	private Calendar mLastNotificationDate;
	private static final int TIME_DEVIATION = 50; 

	private static final String PREF_FILE_NAME = "AlarmServicePref";
	private static final String IS_STARTED = "isStarted";
	private static final String NEXT_NOTIFICATION_DATE = "nextNotificationDate";
	private static final String LAST_NOTIFICATION_DATE = "lastNotificationDate";
	
	private static final long NOT_NOTIFICATION_VALUE = -1;
	
	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		localPref = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		
		// get an instance of the receiver in your service
		IntentFilter filter = new IntentFilter();
		filter.addAction("action");
		filter.addAction("anotherAction");
		mReceiver = new InnerReceiver();
		registerReceiver(mReceiver, filter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		mIsActive = preferences.getBoolean(SettingsMainFragment.SAVE_ADVISE_CHK, false);
		
		if(mIsActive){
			
			splitTime(preferences.getString(SettingsMainFragment.SAVE_ADVISE_TIME, SettingsMainFragment.STANDARD_TIME));
			
			long lastNotificationDate = localPref.getLong(LAST_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			if(lastNotificationDate != NOT_NOTIFICATION_VALUE){
				mLastNotificationDate = Calendar.getInstance();
				mLastNotificationDate.setTimeInMillis(lastNotificationDate);
			}
			
			calculateNotificationDate();
			
			//TODO

			return Service.START_STICKY;
		}else {
			onDestroy();
		}
		
		return Service.START_NOT_STICKY;
	}


	@Override
	public IBinder onBind(Intent intent) {
		// No bind needed, for avoid error
		return null;
	}
	
	@Override
	public void onDestroy() {
		if(mIsActive){
			SharedPreferences.Editor editor = localPref.edit();
			editor.putBoolean(IS_STARTED,true);
			if(mNextNotificationDate != null){
				editor.putLong(NEXT_NOTIFICATION_DATE, mNextNotificationDate.getTimeInMillis());
			} else{
				editor.putLong(NEXT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			}
			if(mLastNotificationDate != null){
				editor.putLong(LAST_NOTIFICATION_DATE, mLastNotificationDate.getTimeInMillis());
			} else{
				editor.putLong(LAST_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			}
			
			editor.commit();
		}
		super.onDestroy();
	}
	
	/**
	 * [m]
	 * Method to split time in minute and hour from hh:mm
	 * 
	 * @param time to split
	 */
	private void splitTime(String time){
		String[] tmp = time.split(":");
		
		mNotHour = Integer.parseInt(tmp[0]);
		mNotMinutes = Integer.parseInt(tmp[1]);
		mNotMill = timeToMill(mNotHour, mNotMinutes, 0);
	}//[m] splitTime()
	
	private long timeToMill(int hour, int minute, int seconds){
		return ((long)((hour* 3600) + (minute * 60) + seconds)) * 1000;
	}
	
	private void calculateNotificationDate(){
		
		mNextNotificationDate = Calendar.getInstance();
		long timeSaved = localPref.getLong(NEXT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
		
		if(timeSaved != NOT_NOTIFICATION_VALUE){
			mNextNotificationDate.setTimeInMillis(timeSaved);
		}

		int hour = mNextNotificationDate.get(Calendar.HOUR_OF_DAY);
		int minute = mNextNotificationDate.get(Calendar.MINUTE);
		int second = mNextNotificationDate.get(Calendar.SECOND);
		int milli = mNextNotificationDate.get(Calendar.MILLISECOND);

		if(timeSaved != NOT_NOTIFICATION_VALUE && hour == mNotHour && minute == mNotMinutes){
			
		}else{
			int time = (hour * 3600) + (minute * 60) + second;
			int notTime = (mNotHour * 3600) + (mNotMinutes *60);

			if(time < notTime){
				mNextNotificationDate.add(Calendar.DATE, 1); //add a day
			}

			second = 0;
			mNextNotificationDate.add(Calendar.HOUR_OF_DAY, (mNotHour - hour));
			mNextNotificationDate.add(Calendar.MINUTE, (mNotMinutes - minute));
			mNextNotificationDate.add(Calendar.SECOND, - second);
			mNextNotificationDate.add(Calendar.MILLISECOND, - milli);
		}
	}
	
	private class InnerReceiver extends BroadcastReceiver {

		public InnerReceiver(){
			//TODO
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
		}
	}


}
