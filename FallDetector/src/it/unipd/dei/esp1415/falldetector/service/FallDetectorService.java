package it.unipd.dei.esp1415.falldetector.service;

import it.unipd.dei.esp1415.falldetector.xmlutil.XmlEditor;
import it.unipd.dei.esp1415.falldetector.xmlutil.XmlFile;
import it.unipd.dei.esp1415.falldetector.xmlutil.XmlReader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.Element;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class FallDetectorService extends Service {
	
	private XmlFile file;
	private XmlEditor edit;
	private Element mainNode;
	private XmlReader read;
	
	// TODO sensor
	private SensorManager sensorManager;
	
	// TODO accelerometer data
	private double x;
	private double y;
	private double z;
	
	private int count;
	
	private Calendar currentTime;
	
	private String[] attrKey = {"TimeStamp","X","Y","Z","Read"};
	private String[] attrVal = {null, null, null, null, null};
	
	public void onCreate() {
		super.onCreate();
		
		// Instantiate xml file, editor, reader
		file = new XmlFile("ServiceAccData", this.getApplicationContext());
		edit = file.edit();
		read = file.read();
		
		// TODO Delete toast message
		Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Initialize the sensor manager
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		// Get default accelerometer sensor
		Sensor accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// Attach sensorListener to our accelerometer sensor
		sensorManager.registerListener(sensorEventListener, accelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);
		
		Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
		
		count = 0;
		
		// UpdateGUI throw thread every 100milliseconds
		Timer updateTimer = new Timer("AccelerometerTimer");
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if(edit.isReady()){
					mainNode = edit.getMainNode();
					
					// If main node doesn't exists
					if(mainNode == null){
						edit.addMainNode("root", null, null, false);
						mainNode = edit.getMainNode();
					}
					
					currentTime = Calendar.getInstance(TimeZone.getDefault());
					attrVal[0] = currentTime.getTime().toString();
					attrVal[1] = Double.toString(x);
					attrVal[2] = Double.toString(y);
					attrVal[3] = Double.toString(z);
					attrVal[4] = Integer.toString(0);
					
					edit.addNode("data", attrKey, attrVal, true);
				}
				if(read.isReady() && read.hasElement()){
					mainNode = read.getMainNode();
					
					ArrayList<Element> tp = read.getInternalNodes(mainNode);
					
					Log.w("count",Integer.toString(count));
					Element tmp = tp.get(count);
					
					Log.w("TimeStamp",tmp.getAttribute("TimeStamp"));
					Log.w("X",tmp.getAttribute("X"));
					Log.w("Y",tmp.getAttribute("Y"));
					Log.w("Z",tmp.getAttribute("Z"));
					Log.w("read",tmp.getAttribute("Read"));
					
					count++;
				}
				
//				if(count > 10){
//					stopSelf();
//				}
			}
		}, 0, 100);
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
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
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];
			}
		}
	};
}
