package it.unipd.dei.esp1415.falldetector;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class CurrentSessionActivity extends ActionBarActivity {
	
	// startDateTime is the start time of the current session
	private Calendar startDateTime;
	
	// isSessionStarted is a flag which indicates if the session has started
	private boolean isSessionStarted = false;
	
	/* isSessionPaused is a flag which indicates if the session has paused
	 *  and stoppedTime saves the elapsed time */
	private boolean isSessionPaused = false;
	private long stoppedTime;
	
	// Sensor manager which manages accelerometer sensor
	private SensorManager sensorManager;
	
	// TODO finish comments chartView
	private ChartView chartXAxis;
	private ChartView chartYAxis;
	private ChartView chartZAxis;
	
	private Paint paint;
	
//	//Counter
//	private long count;
//	private int chartWidth;
	
	// Accelerometer text view
	private TextView accelerationDataX;
	private TextView accelerationDataY;
	private TextView accelerationDataZ;
	
	// Accelerometer data
	private double x;
	private double y;
	private double z;
	
	public CurrentSessionActivity(){

		//chartWidth = chartXAxis.getWidht();
		
		stoppedTime = 0l;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		x = 0.0d;
		y = 0.0d;
		z = 0.0d;
	}
	
	private final SensorEventListener sensorEventListener 
		= new SensorEventListener() {
			
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
			
			public void onSensorChanged(SensorEvent event) { 
				
				x = event.values[0];
				y = event.values[1];
				z = event.values[2];
			} 
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_session_interface);
		
//		// TODO tmp
//		ChartView cv = new ChartView(this);
//		cv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//		this.addContentView(cv, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//		
//		//Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//		Canvas c = new Canvas();
//		Paint paint = new Paint();
//		paint.setColor(Color.WHITE);
//		c.drawLine(0, 0, 30, 30, paint);
//		chartX = (View) findViewById(R.id.chart_x_axis);
//		chartX.draw(c);
//		chartX.invalidate();
//		//-----------tmp
		
		// txtvStartTime display the start time of the session
		final TextView txtvStartTime 
			= (TextView) findViewById(R.id.session_start_time);
		
		// chrmDuration display the duration of the session
		final Chronometer chroDuration 
			= (Chronometer) findViewById(R.id.session_duration);
		
		// ibtnPlayPause button which start, play and pause the session 
		final ImageButton ibtnPlayPause 
			= (ImageButton) findViewById(R.id.play_button);
		
		// TODO end the session and add to UI2
		final ImageButton ibtnEnd 
			= (ImageButton) findViewById(R.id.end_button);
		
		// Initialize the sensor manager
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		
		//chartXAxis shows the acceleration x axis data in a graphic way
		chartXAxis = (ChartView) findViewById(R.id.chart_x_axis);
		chartYAxis = (ChartView) findViewById(R.id.chart_y_axis);
		chartZAxis = (ChartView) findViewById(R.id.chart_z_axis);
		
		// accelerationData* displays acceleration value in each axis
		accelerationDataX = (TextView) findViewById(R.id.acc_data_x);
		accelerationDataY = (TextView) findViewById(R.id.acc_data_y);
		accelerationDataZ = (TextView) findViewById(R.id.acc_data_z);
		
		// lstvFalls referes to the ListView in the layout
		ListView lstvFalls = (ListView) findViewById(R.id.session_falls);
		
		// List of falls occurs during the session
		final ArrayList<String> falls = new ArrayList<String>();
		
		// Array adapter of the list view
		final ArrayAdapter<String> arrayAdapter;
		
		// bind the array adapter to the list view
		arrayAdapter = new ArrayAdapter<String>(
				this,android.R.layout.simple_list_item_1,falls);
		lstvFalls.setAdapter(arrayAdapter);
		
		Sensor accelerometer =
			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(sensorEventListener,
									   accelerometer,
									   SensorManager.SENSOR_DELAY_FASTEST);
		
		// Manages play and pause events
		ibtnPlayPause.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isSessionStarted){
					// The session has already started and has paused
					if(isSessionPaused){
						// Resume the chronometer from the stopped time
						chroDuration.setBase(stoppedTime + SystemClock.elapsedRealtime());
						chroDuration.start();
						
						// Change the icon of play-pause button
						ibtnPlayPause.setImageResource(R.drawable.pause_button_default);
						
						isSessionPaused = false;
					}
					// The session has already started and is not paused
					else{
						// Save the passed time and stop the chronometer
						stoppedTime = chroDuration.getBase() - SystemClock.elapsedRealtime();
						chroDuration.stop();
						
						// Change the icon of play-pause button
						ibtnPlayPause.setImageResource(R.drawable.play_button_default);
						
						isSessionPaused = true;
						
					}
				}
				// First time starting the session
				else{
					
					// Get the current time
					startDateTime = Calendar.getInstance(TimeZone.getDefault());
					
					// Set the current time to the text view field
					txtvStartTime.setText(startDateTime.getTime().toString());
					
					// Start the chronometer
					chroDuration.setBase(SystemClock.elapsedRealtime());
					chroDuration.start();
					
					// Change the icon of play-pause button
					ibtnPlayPause.setImageResource(R.drawable.pause_button_default);
					
					// Set session started
					isSessionStarted = true;
				}
			}
		});
		
		// TODO manages end button press
		ibtnEnd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO this is just a TEMPORARY code
				startDateTime = Calendar.getInstance(TimeZone.getDefault());
				falls.add(0,startDateTime.getTime().toString()
							+ "  SENT");
				arrayAdapter.notifyDataSetChanged();
			}
		});
		
		// Update UI every 100milliseconds
		Timer updateTimer = new Timer("AccelerometerTimer");
		updateTimer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				updateGUI();
			}
		}, 0, 100);
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(sensorEventListener,
									   accelerometer,
									   SensorManager.SENSOR_DELAY_FASTEST);
		
	};
	
	@Override
	protected void onPause() {
		sensorManager.unregisterListener(sensorEventListener);
		super.onPause();
	};
	
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// Updates the text view which new x, y, z values
	private void updateGUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				
				// Chart X axis data and text
				chartXAxis.setHeight ((float) x);
				chartXAxis.invalidate();
				accelerationDataX.setText("X: " + x);
				accelerationDataX.invalidate();
				
				// Chart Y axis data and text
				chartYAxis.setHeight ((float) y);
				chartYAxis.invalidate();
				accelerationDataY.setText("Y: " + y);
				accelerationDataY.invalidate();
				
				// Chart Z axis data and text
				chartZAxis.setHeight ((float) z);
				chartZAxis.invalidate();
				accelerationDataZ.setText("Z: " + z);
				accelerationDataZ.invalidate();
			}
		});
	}
}
