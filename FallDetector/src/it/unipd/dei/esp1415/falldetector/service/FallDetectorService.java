package it.unipd.dei.esp1415.falldetector.service;

import it.unipd.dei.esp1415.falldetector.utility.Mediator;
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
	
	public static final String X_AXIS_NEW_DATA = "X_AXIS_NEW_DATA";
	public static final String Y_AXIS_NEW_DATA = "Y_AXIS_NEW_DATA";
	public static final String Z_AXIS_NEW_DATA = "Z_AXIS_NEW_DATA";
	
	public static final String XYZ_DATA = "XYZ_DATA";

	private Mediator mMed;
	
	private SensorManager sensorManager;
	
	private LocationManager locationManager;
	
	private LocalBroadcastManager broadcaster;
	
	private Intent broadcastDataIntent;
	
	private double latitude, longitude;

	public void onCreate() {
		super.onCreate();
		
		// Initialize the sensor manager
		sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		
		// Initialize the location manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// Get default accelerometer sensor
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Attach sensorListener to our accelerometer sensor/ every 100000 microseconds = 100milliseconds
		sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
		
		// for GPS, change the first parameter to GPS_PROVIDER
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
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
		
		// Remove listener on sensorManager
		sensorManager.unregisterListener(sensorEventListener);
		
		// Remove the listener on locationManager
		locationManager.removeUpdates(locationListener);
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
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
	};
	
	// Accelerometer listener
	private SensorEventListener sensorEventListener = new SensorEventListener() {

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		// Store the data from the accelerometer
		public void onSensorChanged(final SensorEvent event) {

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