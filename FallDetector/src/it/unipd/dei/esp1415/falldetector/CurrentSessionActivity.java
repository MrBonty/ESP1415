package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.utility.ChartView;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CurrentSessionActivity extends ActionBarActivity {
	
	private Mediator mMed;
	private Session mCurrent;
	
	/**
	 * Indicates session started time
	 */
	private Calendar sessionStartTime;
	
	/**
	 *  Displays the start time of the session
	 */
	private TextView txtvStartTime;
	
	/**
	 * Indicates whether this session starts
	 */
	private boolean isSessionStarted;
	
	
	/**
	 *  Indicates whether this session pauses
	 */
	private boolean isSessionPaused;
	
	
	/**
	 *  Display the duration of the session
	 */
	private Chronometer chroDuration;
	
	
	/**  
	 *  Total duration of the session being active
	 */
	private long duration;
	
	
	/**
	 * Button which start, play and pause the session 
	 */
	private ImageButton ibtnPlayPause;
	
	// Customize view for chart
	private ChartView chartXAxis;
	private ChartView chartYAxis;
	private ChartView chartZAxis;
	
	// Uses for drawing chart
	private Paint paint;
	
	// Sensor manager which manages accelerometer sensor
	private SensorManager sensorManager;
	
	// Text view for displaying acceleromter data
	private TextView txtvAccDataX;
	private TextView txtvAccDataY;
	private TextView txtvAccDataZ;
	
	// Accelerometer data
	private double x;
	private double y;
	private double z;
	
	private ImageView thmb;
	
	public CurrentSessionActivity(){
		
		// Initialize variables
		
		isSessionStarted = false;
		isSessionPaused = false;
		
		txtvStartTime = null;
		ibtnPlayPause = null;
		chroDuration = null;
		
		duration = 0l;
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		
		x = 0.0d;
		y = 0.0d;
		z = 0.0d;
	}
	
	/**
	 * Used for gathering accelerometer data
	 */
	private final SensorEventListener sensorEventListener 
		= new SensorEventListener() {
			
			public void onAccuracyChanged(Sensor sensor, int accuracy) {}
			
			// Store the data from the accelerometer
			public void onSensorChanged(SensorEvent event) {
				
				// Ensure mutually exclusive access to the sensor.
				synchronized(this){
					x = event.values[0];
					y = event.values[1];
					z = event.values[2];
				}
			} 
		};
	
	/**
	 *  Updates accelerometer text view and chart using thread
	 */
	private void updateGUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				
				// Update chart X axis data and text
				chartXAxis.setChartHeightData ((float) x);
				chartXAxis.invalidate();
				txtvAccDataX.setText("X: " + x);
				txtvAccDataX.invalidate();
				
				// Update chart Y axis data and text
				chartYAxis.setChartHeightData ((float) y);
				chartYAxis.invalidate();
				txtvAccDataY.setText("Y: " + y);
				txtvAccDataY.invalidate();
				
				// Update chart Z axis data and text
				chartZAxis.setChartHeightData ((float) z);
				chartZAxis.invalidate();
				txtvAccDataZ.setText("Z: " + z);
				txtvAccDataZ.invalidate();
			}
		});
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_session_interface);
		
		mMed = new Mediator();
		mCurrent = mMed.getDataSession().get(ListSessionFragment.FIRST_ITEM);
		
		thmb= (ImageView) findViewById(R.id.current_thumbnail);
		thmb.setImageBitmap(mCurrent.getBitmap());
		
		// Initialize txtvStartTime view
		txtvStartTime = (TextView) findViewById(R.id.session_start_time);
		
		// Initialize the sensor manager
		sensorManager =
				(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		
		// Initialize the chronometer
		chroDuration = (Chronometer) findViewById(R.id.session_duration);
		
		// Initialize the play-pause button view
		ibtnPlayPause = (ImageButton) findViewById(R.id.play_button);
		
		// TODO end the session and add to UI2
		final ImageButton ibtnEnd 
			= (ImageButton) findViewById(R.id.end_button);
		
		// lstvFalls referes to the ListView in the layout
		ListView lstvFalls = (ListView) findViewById(R.id.session_falls);
		
		// List of falls occur during the session
		final ArrayList<String> falls = new ArrayList<String>();
		
		// Array adapter of the list view
		final ArrayAdapter<String> arrayAdapter;
		
		// bind the array adapter to the list view
		arrayAdapter = new ArrayAdapter<String>(
				this,android.R.layout.simple_list_item_1,falls);
		lstvFalls.setAdapter(arrayAdapter);
		
		// Get default accelerometer sensor
		Sensor accelerometer =
			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		// Attach sensorListener to our accelerometer sensor
		sensorManager.registerListener(sensorEventListener,
									   accelerometer,
									   SensorManager.SENSOR_DELAY_FASTEST);
		
		//Shows the acceleration axises data in a graphic way
		chartXAxis = (ChartView) findViewById(R.id.chart_x_axis);
		chartYAxis = (ChartView) findViewById(R.id.chart_y_axis);
		chartZAxis = (ChartView) findViewById(R.id.chart_z_axis);
		
		//Displays acceleration value in each axis
		txtvAccDataX = (TextView) findViewById(R.id.acc_data_x);
		txtvAccDataY = (TextView) findViewById(R.id.acc_data_y);
		txtvAccDataZ = (TextView) findViewById(R.id.acc_data_z);
		
		// Manages play and pause events
		ibtnPlayPause.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(isSessionStarted){
					
					// The session has already started and it's paused
					if(isSessionPaused){
						// Resume the chronometer from the stopped time
						chroDuration.setBase(SystemClock.elapsedRealtime() -
								duration);
						chroDuration.start();
						
						// Change the icon of play-pause button to pause
						ibtnPlayPause.setImageResource(
								R.drawable.pause_button_default);
						
						isSessionPaused = false;
					}
					// The session has already started and is not paused
					else{
						// Save the passed time and stop the chronometer
						duration = SystemClock.elapsedRealtime() -
								chroDuration.getBase();
						
						chroDuration.stop();
						
						// Change the icon of play-pause button to play
						ibtnPlayPause.setImageResource(
								R.drawable.play_button_default);
						
						// Swtich flag
						isSessionPaused = true;
						
					}
				}
				// First time starting the session
				else{
					
					// Get the current time
					sessionStartTime = Calendar.getInstance(
							TimeZone.getDefault());
					
					// Set the current time to the text view field
					txtvStartTime.setText(
							sessionStartTime.getTime().toString());
					
					// Start the chronometer
					chroDuration.setBase(SystemClock.elapsedRealtime());
					chroDuration.start();
					
					// Change the icon of play-pause button
					ibtnPlayPause.setImageResource(
							R.drawable.pause_button_default);
					
					// Session starts
					isSessionStarted = true;
				}
			}
		});
		
		// TODO manages end button press
		ibtnEnd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// TODO this is just a TEMPORARY code
				sessionStartTime = Calendar.getInstance(TimeZone.getDefault());
				falls.add(0,sessionStartTime.getTime().toString()
							+ "  SENT");
				arrayAdapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "Item added",
						   Toast.LENGTH_LONG).show();
			}
		});
		
		// UpdateGUI throw thread every 100milliseconds
		Timer updateTimer = new Timer("AccelerometerTimer");
		updateTimer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				if(isSessionStarted && !isSessionPaused)
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
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Saves session start time
		savedInstanceState.putString("sessionStartTime",
				txtvStartTime.getText().toString());
		
		// Saves whether this session starts
		savedInstanceState.putBoolean("isSessionStarted", isSessionStarted);
		
		// Saves whether this session pauses
		savedInstanceState.putBoolean("isSessionPaused", isSessionPaused);
		
		// Saves play-pause button status and chronometer
		if(isSessionStarted){
			if(isSessionPaused){
				savedInstanceState.putLong("duration", duration);
				savedInstanceState.putInt("ibtnPlayPause", 
						R.drawable.play_button_default);
			}				
			else{
				savedInstanceState.putLong("duration",
					SystemClock.elapsedRealtime() - chroDuration.getBase());
				savedInstanceState.putInt("ibtnPlayPause", 
						R.drawable.pause_button_default);
			}
		}else{
			savedInstanceState.putInt("ibtnPlayPause", 
					R.drawable.play_button_default);
			savedInstanceState.putLong("duration", 0l);
		}
		
		// Saves chart axes data
		savedInstanceState.putFloatArray("chartXAxis",
				chartXAxis.getArrayFloat());
		savedInstanceState.putFloatArray("chartYAxis",
				chartYAxis.getArrayFloat());
		savedInstanceState.putFloatArray("chartZAxis", 
				chartZAxis.getArrayFloat());
	};
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		// Restore session start time
		txtvStartTime.setText(
				savedInstanceState.getString("sessionStartTime"));
		
		// Restore session start flag
		isSessionStarted = savedInstanceState.getBoolean("isSessionStarted");
		
		// Set session paused everytime runtime changes occur
		isSessionPaused = savedInstanceState.getBoolean("isSessionPaused");
		
		// Set play-pause button to play
		ibtnPlayPause.setImageResource(
				savedInstanceState.getInt("ibtnPlayPause"));
		
		// Restore session duration
		duration = savedInstanceState.getLong("duration");
		
		// Restore chronometer data
		chroDuration.setBase(SystemClock.elapsedRealtime() - duration);
		
		if(isSessionStarted && !isSessionPaused)
			chroDuration.start();
		else
			chroDuration.stop();
		
		// Restore chart's axes data
		chartXAxis.setChartHeightData(
				savedInstanceState.getFloatArray("chartXAxis"));
		chartYAxis.setChartHeightData(
				savedInstanceState.getFloatArray("chartYAxis"));
		chartZAxis.setChartHeightData(
				savedInstanceState.getFloatArray("chartZAxis"));
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
	
	
}
