package it.unipd.dei.esp1415.falldetector;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FallDetectorService extends Service{

	public void onCreate(){
		super.onCreate();
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// No bind needed
		return null;
	}

}
