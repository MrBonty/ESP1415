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

/**
 *  This Class extends a BroadcastReceiver for manage alarm
 *  Receive BOOT_COMPLETED signal by starting a device
 *  Receive GET_A_NEW_ALARM and DELETE_ALARM 
 *
 */
public class AlarmServiceHelper extends BroadcastReceiver{

	private int mNotHour = 0; //notification hour
	private int mNotMinutes = 0; //notification minutes
	//private static long mNotMill = 0; //notification time in milliseconds
	
	private boolean mIsActive = false;
	
	private static AlarmManager mAlarm;
	
	private SharedPreferences preferences;
	private SharedPreferences localPref;
	
	private Calendar mNextNotificationDate;
	private Calendar mCurrentNotificationDate;

	private static final String PREF_FILE_NAME = "AlarmServicePref";
	private static final String IS_STARTED = "isStarted";
	private static final String NEXT_NOTIFICATION_DATE = "nextNotificationDate";
	private static final String CURRENT_NOTIFICATION_DATE = "currentNotificationDate";
	
	private static final long NOT_NOTIFICATION_VALUE = -1;
	
	public static final String GET_A_NEW_ALARM = "android.falldetector.action.GET_A_NEW_ALARM";
	public static final String DELETE_ALARM = "android.falldetector.action.DELETE_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent){
		String action = intent.getAction();
		
		if(action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(GET_A_NEW_ALARM)){
			setAlarm(context);
		}else if(action.equals(DELETE_ALARM)){
			deleteAlarm(context);
		}
	}
	
	@SuppressLint("NewApi")
	private void setAlarm(Context context){
		if(localPref == null){
			localPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
		}
		if(preferences == null){
			preferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
		
		Editor editor = localPref.edit();
		
		//get info from local preferences
		mIsActive = preferences.getBoolean(SettingsMainFragment.SAVE_ADVISE_CHK, false);

		
		if(mIsActive){
			mCurrentNotificationDate = Calendar.getInstance();
			mNextNotificationDate = Calendar.getInstance();
			
			if(mAlarm == null){
				mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			}
			
			splitTime(preferences.getString(SettingsMainFragment.SAVE_ADVISE_TIME, SettingsMainFragment.STANDARD_TIME));

			//get saved info
			long currentNotificationDate = localPref.getLong(CURRENT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			long timeSaved = localPref.getLong(NEXT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			
			if(timeSaved != NOT_NOTIFICATION_VALUE && currentNotificationDate != NOT_NOTIFICATION_VALUE){
				mNextNotificationDate.setTimeInMillis(timeSaved);
				mCurrentNotificationDate.setTimeInMillis(currentNotificationDate);
			}

			int hour = mNextNotificationDate.get(Calendar.HOUR_OF_DAY);
			int minute = mNextNotificationDate.get(Calendar.MINUTE);

			if(timeSaved != NOT_NOTIFICATION_VALUE && hour == mNotHour && minute == mNotMinutes){
				if(mCurrentNotificationDate.getTimeInMillis() > System.currentTimeMillis()){
					mNextNotificationDate.add(Calendar.DAY_OF_YEAR, -1);
				}
			}else{
				calculateNotificationDate();
			}
			
			
			boolean isStart = localPref.getBoolean(IS_STARTED, false);
			
			if(isStart){
				// delete existing  alarm
				PendingIntent intent = createPendingIntent(context, mCurrentNotificationDate.getTimeInMillis());
				deleteAlarm(context, intent);
				
				//Start notification if the device was closed at notification hour
				if(hasExpired(mCurrentNotificationDate)){
					Intent intnt = new Intent(context, AlarmService.class);
					intnt.putExtra(AlarmService.NOT_CONTROL, true);
					
					context.startService(intnt);
					
					calculateNotificationDate();
				}
			}
			
			mCurrentNotificationDate.setTimeInMillis(mNextNotificationDate.getTimeInMillis());
			mNextNotificationDate.add(Calendar.DAY_OF_YEAR, 1);
			
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
			
			boolean isStart = localPref.getBoolean(IS_STARTED, false);
			
			if(isStart){
				// delete existing  alarm
				PendingIntent intent = createPendingIntent(context, mCurrentNotificationDate.getTimeInMillis());
				deleteAlarm(context, intent);
			}
			
			editor.putBoolean(IS_STARTED,false);
			editor.putLong(NEXT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			editor.putLong(CURRENT_NOTIFICATION_DATE, NOT_NOTIFICATION_VALUE);
			
		}
		
		editor.commit();
	}//[m] setAlarm()
	
	/**[m]
	 * Verify if the notification has expire
	 * 
	 * @param notificationDate the date to test
	 * @return true if has expired, false otherwise
	 */
	private boolean hasExpired(Calendar notificationDate) {
		Calendar nowDate = Calendar.getInstance();
		
		long not = notificationDate.getTimeInMillis();
		long now = nowDate.getTimeInMillis();
		
		
		return (not - now) < 0;	
	}//[m] hasExpired()

	/**
	 * [m]
	 * Private method to delete an alarm
	 * 
	 * @param context
	 */
	private void deleteAlarm(Context context) {
		
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
	}//[m] deleteAlarm()
	
	/**
	 * [m]
	 * Private method to delete an alarm
	 * 
	 * @param context
	 * @param intent the intent of the alarm to delete
	 */
	private void deleteAlarm(Context context, PendingIntent intent){
		if(mAlarm == null){
			mAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		}
		
		mAlarm.cancel(intent);
	}//[m] deleteAlarm()
 
	/**
	 * [m]
	 * Method to create the pending intent for the AlarmManager
	 * 
	 * @param context
	 * @param time the timestamp of the new alarm
	 * @return the PendingIntent
	 */
	private static PendingIntent createPendingIntent(Context context, long time){
		Intent intent = new Intent(context, AlarmService.class);
		intent.putExtra(AlarmService.NOT_CONTROL, true);
		
		return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}//[m] createPendingIntent()
	
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
		//mNotMill = timeToMill(mNotHour, mNotMinutes, 0);
	}//[m] splitTime()

	/**
	 * [m]
	 *	Calculate the new notification date
	 */
	private void calculateNotificationDate(){

		mNextNotificationDate = Calendar.getInstance();

		int hour = mNextNotificationDate.get(Calendar.HOUR_OF_DAY);
		int minute = mNextNotificationDate.get(Calendar.MINUTE);
		int second = mNextNotificationDate.get(Calendar.SECOND);
		int milli = mNextNotificationDate.get(Calendar.MILLISECOND);

		int time = (hour * 3600) + (minute * 60) + second;
		int notTime = (mNotHour * 3600) + (mNotMinutes *60);

		if(time > notTime){
			mNextNotificationDate.add(Calendar.DATE, 1); //add a day
		}

		mNextNotificationDate.add(Calendar.HOUR_OF_DAY, (mNotHour - hour));
		mNextNotificationDate.add(Calendar.MINUTE, (mNotMinutes - minute));
		mNextNotificationDate.add(Calendar.SECOND, - second);
		mNextNotificationDate.add(Calendar.MILLISECOND, - milli);
	
	}// [m] calculateNotificationDate()
}//{c} AlarmServiceHelper()
