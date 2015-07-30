/**
 * 
 */
package it.unipd.dei.esp1415.falldetector.service;

import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import java.util.Calendar;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AlarmServiceHelper extends BroadcastReceiver{


	private static int mNotHour = 0; //notification hour
	private static int mNotMinutes = 0; //notification minutes
	//private static long mNotMill = 0; //notification time in milliseconds
	
	private static boolean mIsActive = false;
	
	private static AlarmManager mAlarm;
	
	private static SharedPreferences preferences;
	private static SharedPreferences localPref;
	
	private static Calendar mNextNotificationDate;
	private static Calendar mCurrentNotificationDate;

	private static final String PREF_FILE_NAME = "AlarmServicePref";
	private static final String IS_STARTED = "isStarted";
	private static final String NEXT_NOTIFICATION_DATE = "nextNotificationDate";
	private static final String CURRENT_NOTIFICATION_DATE = "currentNotificationDate";
	
	private static final long NOT_NOTIFICATION_VALUE = -1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		setAlarm(context);
	}
	
	@SuppressLint("NewApi")
	public static void setAlarm(Context context){
		if(localPref == null){
			localPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		}
		if(preferences == null){
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
		
		Editor editor = localPref.edit();
		mIsActive = preferences.getBoolean(SettingsMainFragment.SAVE_ADVISE_CHK, false);
		mCurrentNotificationDate = Calendar.getInstance();
		mNextNotificationDate = Calendar.getInstance();
		
		if(mIsActive){
			mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			
			splitTime(preferences.getString(SettingsMainFragment.SAVE_ADVISE_TIME, SettingsMainFragment.STANDARD_TIME));

			calculateNotificationDate();
			
			boolean isStart = localPref.getBoolean(IS_STARTED, false);
			if(isStart){
				PendingIntent intent = createPendingIntent(context, mCurrentNotificationDate.getTimeInMillis());
				deleteAlarm(context, intent);
			}
			
			mCurrentNotificationDate.setTimeInMillis(mNextNotificationDate.getTimeInMillis());
			mNextNotificationDate.add(Calendar.DAY_OF_YEAR, 1);
			
			
			if(hasExpired(mCurrentNotificationDate)){
				context.startService(new Intent(context, AlarmService.class));
				
				mCurrentNotificationDate.setTimeInMillis(mNextNotificationDate.getTimeInMillis());
				mNextNotificationDate.add(Calendar.DAY_OF_YEAR, 1);
			}
			
			System.out.println(mCurrentNotificationDate.toString());
			
			PendingIntent intent = createPendingIntent(context, mCurrentNotificationDate.getTimeInMillis());
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				mAlarm.setExact(AlarmManager.RTC_WAKEUP, mCurrentNotificationDate.getTimeInMillis(), intent);
			} else {
				mAlarm.set(AlarmManager.RTC_WAKEUP, mCurrentNotificationDate.getTimeInMillis(), intent);
			}
			
			editor.putBoolean(IS_STARTED,true);
			editor.putLong(NEXT_NOTIFICATION_DATE, mNextNotificationDate.getTimeInMillis());
			editor.putLong(CURRENT_NOTIFICATION_DATE, mCurrentNotificationDate.getTimeInMillis());
			
		}else{
			
			editor.putBoolean(IS_STARTED,false);
			editor.putLong(NEXT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			editor.putLong(CURRENT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			
		}
		
		editor.commit();
	}
	
	/**[m]
	 * @param notificationDate
	 * @return 
	 */
	private static boolean hasExpired(Calendar notificationDate) {
		Calendar nowDate = Calendar.getInstance();
		
		long not = notificationDate.getTimeInMillis();
		long now = nowDate.getTimeInMillis();
		
		
		return (not - now) < 0;	
	}

	public static void deleteAlarm(Context context) {
		
		
		if(localPref == null){
			localPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		}
		
		long time = localPref.getLong(CURRENT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
		
		if(time != NOT_NOTIFICATION_VALUE){
			PendingIntent intent = createPendingIntent(context, time);
			deleteAlarm(context, intent);
		}
		
		Editor editor = localPref.edit();
		editor.putBoolean(IS_STARTED,false);
		editor.putLong(NEXT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
		editor.putLong(CURRENT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);

		editor.commit();
	}
	
	private static void deleteAlarm(Context context, PendingIntent intent){
		mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		mAlarm.cancel(intent);
	}
 
	private static PendingIntent createPendingIntent(Context context, long time){
		Intent intent = new Intent(context, AlarmService.class);
		
		return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	
	/**
	 * [m]
	 * Method to split time in minute and hour from hh:mm
	 * 
	 * @param time to split
	 */
	private static void splitTime(String time){
		String[] tmp = time.split(":");
		
		mNotHour = Integer.parseInt(tmp[0]);
		mNotMinutes = Integer.parseInt(tmp[1]);
		//mNotMill = timeToMill(mNotHour, mNotMinutes, 0);
	}//[m] splitTime()
	
	/*
	private static long timeToMill(int hour, int minute, int seconds){
		return ((long)((hour* 3600) + (minute * 60) + seconds)) * 1000;
	}*/
	
	private static void calculateNotificationDate(){
		
		long currentNotificationDate = localPref.getLong(CURRENT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
		long timeSaved = localPref.getLong(NEXT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
		
		if(timeSaved != NOT_NOTIFICATION_VALUE && currentNotificationDate != NOT_NOTIFICATION_VALUE){
			mNextNotificationDate.setTimeInMillis(timeSaved);
			mCurrentNotificationDate.setTimeInMillis(currentNotificationDate);
		}

		int hour = mNextNotificationDate.get(Calendar.HOUR_OF_DAY);
		int minute = mNextNotificationDate.get(Calendar.MINUTE);
		int second = mNextNotificationDate.get(Calendar.SECOND);
		int milli = mNextNotificationDate.get(Calendar.MILLISECOND);

		if(timeSaved != NOT_NOTIFICATION_VALUE && hour == mNotHour && minute == mNotMinutes){
			if(mCurrentNotificationDate.getTimeInMillis() > System.currentTimeMillis()){
				mNextNotificationDate.add(Calendar.DAY_OF_YEAR, -1);
			}
		}else{
			
			mNextNotificationDate = Calendar.getInstance();
			
			hour = mNextNotificationDate.get(Calendar.HOUR_OF_DAY);
			minute = mNextNotificationDate.get(Calendar.MINUTE);
			second = mNextNotificationDate.get(Calendar.SECOND);
			milli = mNextNotificationDate.get(Calendar.MILLISECOND);
			
			System.out.println(mNextNotificationDate.toString());
			
			int time = (hour * 3600) + (minute * 60) + second;
			int notTime = (mNotHour * 3600) + (mNotMinutes *60);

			if(time > notTime){
				mNextNotificationDate.add(Calendar.DATE, 1); //add a day
			}

			mNextNotificationDate.add(Calendar.HOUR_OF_DAY, (mNotHour - hour));
			mNextNotificationDate.add(Calendar.MINUTE, (mNotMinutes - minute));
			mNextNotificationDate.add(Calendar.SECOND, - second);
			mNextNotificationDate.add(Calendar.MILLISECOND, - milli);
		}
	}
}
