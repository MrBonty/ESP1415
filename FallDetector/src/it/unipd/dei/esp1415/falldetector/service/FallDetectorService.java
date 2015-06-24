package it.unipd.dei.esp1415.falldetector.service;

import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class FallDetectorService extends Service {
	
	public static final String X_AXIS_NEW_DATA = "X_AXIS_NEW_DATA";
	public static final String Y_AXIS_NEW_DATA = "Y_AXIS_NEW_DATA";
	public static final String Z_AXIS_NEW_DATA = "Z_AXIS_NEW_DATA";
	
	public static final String XYZ_DATA = "XYZ_DATA";

	private Mediator mMed;
	
	private SensorManager sensorManager;
	
	private LocalBroadcastManager broadcaster;
	
	private Intent broadcastDataIntent;

	public void onCreate() {
		super.onCreate();
		
		// Initialize the sensor manager
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		// Get default accelerometer sensor
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Attach sensorListener to our accelerometer sensor/ every 100000 microseconds = 100milliseconds
		sensorManager.registerListener(sensorEventListener, accelerometer, 100000);
		
		broadcaster = LocalBroadcastManager.getInstance(this);
		broadcastDataIntent = new Intent(FallDetectorService.XYZ_DATA);
		
		mMed = new Mediator();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		sensorManager.unregisterListener(sensorEventListener);
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// No bind needed
		return null;
	}
	
	private final SensorEventListener sensorEventListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		// Store the data from the accelerometer
		public void onSensorChanged(SensorEvent event) {

			// Ensure mutually exclusive access to the sensor.
			synchronized (this) {
				Log.i("event.values[0]: ", event.values[0] + "");
				Log.i("event.values[1]: ", event.values[1] + "");
				Log.i("event.values[2]: ", event.values[2] + "");
				Log.i("size: ", mMed.getSize() + "");
				
				mMed.setX_data(event.values[0]);
				mMed.setY_data(event.values[1]);
				mMed.setZ_data(event.values[2]);
				mMed.incrementSize();
				
				broadcastDataIntent.putExtra(FallDetectorService.X_AXIS_NEW_DATA, event.values[0]);
				broadcastDataIntent.putExtra(FallDetectorService.Y_AXIS_NEW_DATA, event.values[1]);
				broadcastDataIntent.putExtra(FallDetectorService.Z_AXIS_NEW_DATA, event.values[2]);
				broadcaster.sendBroadcast(broadcastDataIntent);
			}
		}
	};
}