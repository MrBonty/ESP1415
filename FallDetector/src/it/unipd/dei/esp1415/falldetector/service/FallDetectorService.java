package it.unipd.dei.esp1415.falldetector.service;

import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.Toast;

public class FallDetectorService extends Service {
	
	public static final String X_AXIS_NEW_DATA = "X_AXIS_NEW_DATA";
	public static final String Y_AXIS_NEW_DATA = "Y_AXIS_NEW_DATA";
	public static final String Z_AXIS_NEW_DATA = "Z_AXIS_NEW_DATA";
	
	public static final String X_AXIS_ARRAY_DATA = "X_AXIS_ARRAY_DATA";
	public static final String Y_AXIS_ARRAY_DATA = "Y_AXIS_ARRAY_DATA";
	public static final String Z_AXIS_ARRAY_DATA = "Z_AXIS_ARRAY_DATA";
	
	public static final String SIZE_DATA = "SIZE_DATA";
	
	public static final String XYZ_DATA = "XYZ_DATA";
	public static final String XYZ_ARRAY = "XYZ_ARRAY";
	
	public static final String ACTIVITY_ACTIVE = "SERVICE_ACTIVE";
	
	private boolean isActivityActive;
	
	// TODO sensor
	private SensorManager sensorManager;
	
	private LocalBroadcastManager broadcaster;
	
	private Intent broadcastDataIntent;
	private Intent broadcastArrayIntent;
	
	private float xyz[];
	
	private float[] x_data;
	private float[] y_data;
	private float[] z_data;
	
	int size;
	
	public FallDetectorService(){
		
		xyz = new float[3];
		
		x_data = new float[1000000];
		y_data = new float[1000000];
		z_data = new float[1000000];
		size = 0;
		
		isActivityActive = false;
	}
	
	public void onCreate() {
		super.onCreate();
		
		// TODO Delete toast message
		Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		isActivityActive = intent.getBooleanExtra(FallDetectorService.ACTIVITY_ACTIVE, false);
		if(isActivityActive)
			start();
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
		isActivityActive = false;
		sensorManager.unregisterListener(sensorEventListener);
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// No bind needed
		return null;
	}
	
	private void start(){
		
		Log.i("Started","I'm in!");
		
		// Initialize the sensor manager
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		// TODO delete Toast message
		Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
		
		// Get default accelerometer sensor
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Attach sensorListener to our accelerometer sensor
		sensorManager.registerListener(sensorEventListener, accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		
		// TODO	Know how this work
		broadcaster = LocalBroadcastManager.getInstance(this);
		
		broadcastArrayIntent = new Intent(FallDetectorService.XYZ_ARRAY);
		broadcastArrayIntent.putExtra(FallDetectorService.X_AXIS_ARRAY_DATA, x_data);
		broadcastArrayIntent.putExtra(FallDetectorService.Y_AXIS_ARRAY_DATA, y_data);
		broadcastArrayIntent.putExtra(FallDetectorService.Z_AXIS_ARRAY_DATA, z_data);
		broadcastArrayIntent.putExtra(FallDetectorService.SIZE_DATA, size);
		
		broadcastDataIntent = new Intent(FallDetectorService.XYZ_DATA);
		
		// UpdateGUI throw thread every 100milliseconds
		Timer updateTimer = new Timer("AccelerometerTimer");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				x_data[size] = xyz[0];
				y_data[size] = xyz[1];
				z_data[size] = xyz[2];
				size++;
				if(isActivityActive){
					Log.w("size: ", size+"");
					Log.e("xyz[0]: ", xyz[0]+"");
					Log.e("xyz[1]: ", xyz[1]+"");
					Log.e("xyz[2]: ", xyz[2]+"");
					
					broadcastDataIntent.putExtra(FallDetectorService.X_AXIS_NEW_DATA, x_data[size-1]);
					broadcastDataIntent.putExtra(FallDetectorService.Y_AXIS_NEW_DATA, y_data[size-1]);
					broadcastDataIntent.putExtra(FallDetectorService.Z_AXIS_NEW_DATA, z_data[size-1]);
					broadcaster.sendBroadcast(broadcastDataIntent);
				}else
				{
					cancel();
				}
					
			}
		}, 0, 100);	//updateTimer
	}	// start
	
	
	/**
	 * Used for gathering accelerometer data
	 */
	private final SensorEventListener sensorEventListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		// Store the data from the accelerometer
		public void onSensorChanged(SensorEvent event) {

			// Ensure mutually exclusive access to the sensor.
			synchronized (this) {
				xyz = event.values;
			}
		}
	};
}