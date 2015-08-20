package it.unipd.dei.esp1415.falldetector.service;

import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.utility.AccelData;
import it.unipd.dei.esp1415.falldetector.utility.SimpleFallAlgorithm;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class FallDetectorService extends Service {
	
	public static final String X_AXIS_ARRAY = "X_AXIS_ARRAY";
	public static final String Y_AXIS_ARRAY = "Y_AXIS_ARRAY";
	public static final String Z_AXIS_ARRAY = "Z_AXIS_ARRAY";
	public static final String ARRAY_SIZE = "ARRAY_SIZE";
	public static final String XYZ_ARRAY = "XYZ_DATA";
	
	public static final String IS_PLAY = "IS_PLAY";
	public static final String IS_PAUSE = "IS_PAUSE";
	public static final String GET_ARRAY = "GET_ARRAY";
	
	public static final int MIN_SAMPLE_RATE = 60;
	
	private float[] accDataX;
	private float[] accDataY;
	private float[] accDataZ;
	
	private AccelData newData;
	
	private int accDataIndex;
	
	private long lastSampleTime;
	private long elapsedTime;
	
	private SensorManager sensorManager;
	
	private LocationManager locationManager;
	
	private LocalBroadcastManager broadcaster;
	
	private Intent broadcastDataIntent;
	
	private double latitude, longitude;

	private boolean isPlaying;
	
	private DatabaseManager dm;
	
	public void onCreate() {
		super.onCreate();
		
		dm = new DatabaseManager(this.getApplicationContext());
		
		ArrayList<AccelData> tmpAccData 
			= dm.getTempAccelDataAsArray(DatabaseTable.COLUMN_PK_ID + " " + DatabaseManager.ASC);
		
		accDataX = new float[SimpleFallAlgorithm.ACC_DATA_SIZE];
		accDataY = new float[SimpleFallAlgorithm.ACC_DATA_SIZE];
		accDataZ = new float[SimpleFallAlgorithm.ACC_DATA_SIZE];
		
		accDataIndex = 0;
		
		for(AccelData accData : tmpAccData){
			
			accDataX[accDataIndex] = (float) accData.getX();
			accDataY[accDataIndex] = (float) accData.getY();
			accDataZ[accDataIndex] = (float) accData.getZ();
			accDataIndex++;
		}
		
		newData = new AccelData();
		
		lastSampleTime = 0l;
		
		isPlaying = false;
		
		broadcaster = LocalBroadcastManager.getInstance(this);
		broadcastDataIntent = new Intent(FallDetectorService.XYZ_ARRAY);

		Log.i("onCreate: ", "onCreate called");
		
		// Initialize the sensor manager
		sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		
		// Initialize the location manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// Get default accelerometer sensor
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		
		// for GPS, change the first parameter to GPS_PROVIDER
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			if(intent.getBooleanExtra(FallDetectorService.GET_ARRAY, false)){
				
				broadcastDataIntent.putExtra(FallDetectorService.ARRAY_SIZE, accDataIndex);
				broadcastDataIntent.putExtra(FallDetectorService.X_AXIS_ARRAY, accDataX);
				broadcastDataIntent.putExtra(FallDetectorService.Y_AXIS_ARRAY, accDataY);
				broadcastDataIntent.putExtra(FallDetectorService.Z_AXIS_ARRAY, accDataZ);
				
				broadcaster.sendBroadcast(broadcastDataIntent);
			}
			
			if(intent.getBooleanExtra(FallDetectorService.IS_PLAY, false)){
				
				isPlaying = true;
				
				// Initialize the sensor manager
				sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
				
				// Initialize the location manager
				locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				
				// Get default accelerometer sensor
				Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
				sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
				
				// for GPS, change the first parameter to GPS_PROVIDER
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			}
			
			if(intent.getBooleanExtra(FallDetectorService.IS_PAUSE, false)){
				
				// Remove listener on sensorManager
				sensorManager.unregisterListener(sensorEventListener);
				// Remove the listener on locationManager
				locationManager.removeUpdates(locationListener);
				
				isPlaying = false;
			}
		}//if(intent != null)
		
		else{
			if(isPlaying){
				// Initialize the sensor manager
				sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
				
				// Initialize the location manager
				locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
				
				// Get default accelerometer sensor
				Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
				sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
				
				// for GPS, change the first parameter to GPS_PROVIDER
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			}
		}

		Log.i("onStartCommand: ", "onStartCommand called");
		return Service.START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy() {
		Log.e("destroy", "distrutto");
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// No bind needed
		return null;
	}
	
	
	private LocationListener locationListener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location) {
			// Called when a new location is find by the network location provider, not GPS.
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			Log.i("Location", "Latitude_location: " + latitude);
			Log.i("Location", "Longitude_location: " + longitude);
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onProviderDisabled(String provider) {}
	};
	
	// Accelerometer listener
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		// Store the data from the accelerometer
		public void onSensorChanged(final SensorEvent event) {

			// Ensure mutually exclusive access to the sensor.
			synchronized (this) {
				
				elapsedTime = System.currentTimeMillis() - lastSampleTime;
				
				if(elapsedTime > MIN_SAMPLE_RATE){
					
					if(isPlaying){
						
						if(accDataIndex >= SimpleFallAlgorithm.ACC_DATA_SIZE){
							newData.setId(accDataIndex % SimpleFallAlgorithm.ACC_DATA_SIZE);
							newData.setTimestamp(event.timestamp);
							newData.setX((double) (accDataX[accDataIndex % SimpleFallAlgorithm.ACC_DATA_SIZE]
									= event.values[0]));
							newData.setY((double) (accDataY[accDataIndex % SimpleFallAlgorithm.ACC_DATA_SIZE]
									= event.values[1]));
							newData.setZ((double) (accDataZ[accDataIndex % SimpleFallAlgorithm.ACC_DATA_SIZE]
									= event.values[2]));
							dm.upgradeATempAccelData(newData);
							broadcastDataIntent
								.putExtra(FallDetectorService.ARRAY_SIZE, accDataIndex);
							
						}else{
							newData.setId(accDataIndex);
							newData.setTimestamp(event.timestamp);
							newData.setX((double) (accDataX[accDataIndex] = event.values[0]));
							newData.setY((double) (accDataY[accDataIndex] = event.values[1]));
							newData.setZ((double) (accDataZ[accDataIndex] = event.values[2]));
							dm.insertATempAccelData(newData);
							broadcastDataIntent.putExtra(FallDetectorService.ARRAY_SIZE, accDataIndex);
						}
						
						Log.i("sensorChanged", "X: " + newData.getX());
						Log.i("sensorChanged", "Y: " + newData.getY());
						Log.i("sensorChanged", "Z: " + newData.getZ());
						Log.i("size: ", accDataIndex + "");
						
						broadcastDataIntent.putExtra(FallDetectorService.X_AXIS_ARRAY, accDataX);
						broadcastDataIntent.putExtra(FallDetectorService.Y_AXIS_ARRAY, accDataY);
						broadcastDataIntent.putExtra(FallDetectorService.Z_AXIS_ARRAY, accDataZ);
						broadcaster.sendBroadcast(broadcastDataIntent);
						
						accDataIndex++;
					}
					
					lastSampleTime = System.currentTimeMillis();
				}
			}
		}
	};
	
//	private class Algorithm{
//		public static final double FALL_UPPER_BOUND = 18.5;
//		public static final double FALL_LOWER_BOUND = 2.5;
//		
////		public fallDetector(int from, int to, AccelData)
//	}
}