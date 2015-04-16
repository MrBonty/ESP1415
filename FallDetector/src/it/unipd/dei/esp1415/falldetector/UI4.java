package it.unipd.dei.esp1415.falldetector;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/** 
 * This class detects the acceleration of the device
 * 
 * @version 1.0
 */
public class UI4 extends ActionBarActivity {
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_interface_4);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.menu_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}


/* JUNK CODE WAITING FOR RECYCLE */
//	private SensorManager sensorManager;
//	private TextView accelerationTextView;
//	private TextView maxAccelerationTextView;
//	private float currentAcceleration = 0;
//	private float maxAcceleration = 0;
//	
//	/* calibration constant that represents earth gravity */
//	private final double calibration = SensorManager.STANDARD_GRAVITY;
//	
//	/* Sensor listener that sums acceleration detected along each axis
//	 * and negates the acceleration due to gravity */
//	private final SensorEventListener sensorEventListener
//		= new SensorEventListener(){
//			public void onAccuracyChanged(Sensor sensor, int accuracy){}
//			
//			public void onSensorChanged(SensorEvent event) {
//				double x = event.values[0];
//				double y = event.values[1];
//				double z = event.values[2];
//				
//				double acceleration = Math.round(Math.sqrt(Math.pow(x, 2) +
//														   Math.pow(y, 2) +
//														   Math.pow(z, 2)));
//				
//				currentAcceleration = Math.abs((float) (acceleration - calibration));
//				
//				if (currentAcceleration > maxAcceleration)
//					maxAcceleration = currentAcceleration;
//			}
//	};

//	accelerationTextView = (TextView)findViewById(R.id.acceleration);
//	maxAccelerationTextView = (TextView)findViewById(R.id.maxAcceleration);
//	sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
//	
//	Sensor accelerometer
//		= sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//	sensorManager.registerListener(sensorEventListener,
//								   accelerometer,
//								   SensorManager.SENSOR_DELAY_FASTEST);
//	
//	Timer updateTimer = new Timer("gForceUpdate");
//	updateTimer.scheduleAtFixedRate(new TimerTask(){
//		public void run(){
//			updateGUI();
//		}
//	}, 0, 100);	//update rates

//@Override
//protected void onResume(){
//	super.onResume();
//	Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//	sensorManager.registerListener(sensorEventListener,
//								   accelerometer,
//								   SensorManager.SENSOR_DELAY_FASTEST);	
//}
//
//@Override
//protected void onPause(){
//	sensorManager.unregisterListener(sensorEventListener);
//	super.onPause();
//}
	
//	private void updateGUI(){
//		runOnUiThread(new Runnable() {
//			public void run(){
//				String currentG = currentAcceleration/SensorManager.STANDARD_GRAVITY + "Gs";
//				accelerationTextView.setText(currentG);
//				accelerationTextView.invalidate();
//				String maxG = maxAcceleration/SensorManager.STANDARD_GRAVITY + "Gs";
//				maxAccelerationTextView.setText(maxG);
//				maxAccelerationTextView.invalidate();
//			}
//		});
//	}

