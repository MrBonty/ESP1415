package it.unipd.dei.esp1415.falldetector.service;


import java.util.Calendar;

import it.unipd.dei.esp1415.falldetector.MainActivity;
import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.support.v4.app.TaskStackBuilder;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AlarmService extends Service{

	private int notifyID = 1;
	public final static String NEW_TASK = "newSession";
	private boolean hasToNotify = true;
	
	public final static String NOT_CONTROL = "notControl";
	
	private final static int ADV_DELAY = 2;
	
	@Override
	public void onCreate() {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if(intent != null && intent.getBooleanExtra(NOT_CONTROL, false)){
			hasToNotify = true;
		}else{
			hasToNotify = verifyNotification();
		}
		
		if(hasToNotify){
			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setContentTitle(getResources().getString(R.string.alarm_title))
			.setContentText(getResources().getString(R.string.alarm_info))
			.setSmallIcon(R.drawable.ic_notification)
			.setAutoCancel(true)
			.setLights(0xffffffff, 200, 200);


			Intent startAct = new Intent(this, MainActivity.class);	
			startAct.putExtra(NEW_TASK, true);

			TaskStackBuilder stackBuilder =TaskStackBuilder.create(this);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(startAct);

			PendingIntent resultPendingIntent =
					stackBuilder.getPendingIntent(
							0,
							PendingIntent.FLAG_UPDATE_CURRENT
							);
			mBuilder.setContentIntent(resultPendingIntent);
			Notification not = mBuilder.build();
			notificationManager.notify(notifyID, not);

			Intent i = new Intent(AlarmServiceHelper.GET_A_NEW_ALARM);

			LocalBroadcastManager broadcasting = LocalBroadcastManager.getInstance(getApplication());
			AlarmServiceHelper broadcaster = new AlarmServiceHelper();
			broadcasting.registerReceiver(broadcaster, new IntentFilter(AlarmServiceHelper.GET_A_NEW_ALARM));
			broadcasting.sendBroadcast(i);
			broadcasting.unregisterReceiver(broadcaster);

			Log.i("NOTIFY", "Started Notification");
		}
		
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public IBinder onBind(Intent intent) {
		// No bind needed, for avoid error
		return null;
	}
	
	@Override
	public void onDestroy() {
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		
		super.onDestroy();
	}

	private boolean verifyNotification(){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		String[] tmp = pref.getString(SettingsMainFragment.SAVE_ADVISE_TIME, SettingsMainFragment.STANDARD_TIME).split(":");
		int advMinute = (Integer.parseInt(tmp[0])*60) + (Integer.parseInt(tmp[1]));
		
		Calendar instance = Calendar.getInstance();
		int instMinute = instance.get(Calendar.HOUR_OF_DAY)*60 + instance.get(Calendar.MINUTE);
		
		return advMinute <= (instMinute + ADV_DELAY);
	}

}
