package it.unipd.dei.esp1415.falldetector.service;


import it.unipd.dei.esp1415.falldetector.MainActivity;
import it.unipd.dei.esp1415.falldetector.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.support.v4.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class AlarmService extends Service{

	private int notifyID = 1;
	public final static String NEW_TASK = "newSession";
	
	@Override
	public void onCreate() {

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setContentTitle(getResources().getString(R.string.alarm_title))
			.setContentText(getResources().getString(R.string.alarm_info))
			.setSmallIcon(R.drawable.image_icon);
		
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
		notificationManager.notify(notifyID, mBuilder.build());
		
		AlarmServiceHelper.setAlarm(this);
		
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
		
		notificationManager.cancel(notifyID);
		super.onDestroy();
	}



}
