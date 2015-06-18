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
//import it.unipd.dei.esp1415.falldetector.xmlutil.XmlReader;

public class FallDetectorService extends Service {
	
//	private XmlFile file;
//	private XmlEditor edit;
//	private Element mainNode;
	//private XmlReader read;
	
	public static final String XYZ_DATA = "XYZ_DATA";
	public static final String XYZ_RESULT = "XYZ_RESULT";
	
	// TODO sensor
	private SensorManager sensorManager;
	
	// TODO accelerometer data
	private float[] xyz;
	
	private LocalBroadcastManager broadcaster;
	
	private Intent broadcastIntent;
	
//	private String[] attrKey = {"TimeStamp","X","Y","Z","Read"};
//	private String[] attrVal = {null, null, null, null, null};
	
	public FallDetectorService(){
		xyz = new float[3];
	}
	
	public void onCreate() {
		super.onCreate();
		
		// TODO Delete toast message
		Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
		
		broadcaster = LocalBroadcastManager.getInstance(this);
		
		// Initialize the sensor manager
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		
		// Instantiate xml file, editor, reader 
		// TODO GET NAME OF FILE FROM SESSION....
//		file = new XmlFile("ServiceAccData", this.getApplicationContext());
//		edit = file.edit();
		//read = file.read();

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// TODO delete Toast message
		Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
		
		// Get default accelerometer sensor
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Attach sensorListener to our accelerometer sensor
		sensorManager.registerListener(sensorEventListener, accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		
		// TODO
		broadcastIntent = new Intent(FallDetectorService.XYZ_RESULT);
		
		// UpdateGUI throw thread every 100milliseconds
		Timer updateTimer = new Timer("AccelerometerTimer");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(xyz != null){
					Log.i("xyz_data",xyz[0]+"");
					Log.i("xyz_data",xyz[1]+"");
					Log.i("xyz_data",xyz[2]+"");
					broadcastIntent.putExtra(FallDetectorService.XYZ_DATA, xyz);
					broadcaster.sendBroadcast(broadcastIntent);
				}
			}
		}, 0, 100);
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
		sensorManager.unregisterListener(sensorEventListener);
		
//		file.close();
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// No bind needed
		return null;
	}
	
	
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
