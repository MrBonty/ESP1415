package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.service.FallDetectorService;
import it.unipd.dei.esp1415.falldetector.utility.ChartView;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
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
	 * Displays the start time of the session
	 */
	private TextView txtvStartTime;

	/**
	 * Indicates whether this session starts
	 */
	private boolean isSessionStarted;

	/**
	 * Indicates whether this session pauses
	 */
	private boolean isSessionPaused;

	/**
	 * Display the duration of the session
	 */
	private Chronometer chroDuration;

	/**
	 * Total duration of the session being active
	 */
	private long duration;

	/**
	 * Button which start, play and pause the session
	 */
	private ImageButton ibtnPlayPause;

	// Customize view for chart
	private ChartView xChart;
	private ChartView yChart;
	private ChartView zChart;

	// Text view for displaying acceleromter data
	private TextView txtvAccDataX;
	private TextView txtvAccDataY;
	private TextView txtvAccDataZ;

	// Accelerometer data
	private float x;
	private float y;
	private float z;
	
	private ImageView thmb;
	
	private BroadcastReceiver broadcastDataReceiver;
	private BroadcastReceiver broadcastArrayReceiver;

	public CurrentSessionActivity() {

		// Initialize variables

		isSessionStarted = false;
		isSessionPaused = true;

		txtvStartTime = null;
		ibtnPlayPause = null;
		chroDuration = null;

		duration = 0l;

		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_session_interface);

		broadcastDataReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            x = intent.getFloatExtra(FallDetectorService.X_AXIS_NEW_DATA, 0.0f);
	            y = intent.getFloatExtra(FallDetectorService.Y_AXIS_NEW_DATA, 0.0f);
	            z = intent.getFloatExtra(FallDetectorService.Z_AXIS_NEW_DATA, 0.0f);
	            
				// Update chart X axis data and text
				xChart.addNewData(x);
				xChart.invalidate();
				txtvAccDataX.setText("X: " + x);
				txtvAccDataX.invalidate();

				// Update chart Y axis data and text
				yChart.addNewData(y);
				yChart.invalidate();
				txtvAccDataY.setText("Y: " + y);
				txtvAccDataY.invalidate();

				// Update chart Z axis data and text
				zChart.addNewData(z);
				zChart.invalidate();
				txtvAccDataZ.setText("Z: " + z);
				txtvAccDataZ.invalidate();
	        }
		};
		
		broadcastArrayReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
            	xChart.setChartData(
            			intent.getFloatArrayExtra(FallDetectorService.X_AXIS_ARRAY_DATA),
            			intent.getIntExtra(FallDetectorService.SIZE_DATA, 0));
            	xChart.invalidate();
            	yChart.setChartData(
            			intent.getFloatArrayExtra(FallDetectorService.Y_AXIS_ARRAY_DATA),
            			intent.getIntExtra(FallDetectorService.SIZE_DATA, 0));
            	yChart.invalidate();
            	zChart.setChartData(
            			intent.getFloatArrayExtra(FallDetectorService.Z_AXIS_ARRAY_DATA),
            			intent.getIntExtra(FallDetectorService.SIZE_DATA, 0));
            	zChart.invalidate();	
	        }
		};
		
		mMed = new Mediator();
		
		isSessionStarted = mMed.isSessionStarted();
		isSessionPaused = mMed.isSessionPaused();
		
		mCurrent = mMed.getDataSession().get(ListSessionFragment.FIRST_ITEM);
		
		thmb = (ImageView) findViewById(R.id.current_thumbnail);
		thmb.setImageBitmap(mCurrent.getBitmap());

		// Initialize txtvStartTime view
		txtvStartTime = (TextView) findViewById(R.id.session_start_time);

		// Initialize the chronometer
		chroDuration = (Chronometer) findViewById(R.id.session_duration);

		// Initialize the play-pause button view
		ibtnPlayPause = (ImageButton) findViewById(R.id.play_button);

		// TODO end the session and add to UI2
		final ImageButton ibtnEnd = (ImageButton) findViewById(R.id.end_button);

		// lstvFalls referes to the ListView in the layout
		ListView lstvFalls = (ListView) findViewById(R.id.session_falls);

		// List of falls occur during the session
		final ArrayList<String> falls = new ArrayList<String>();

		// Array adapter of the list view
		final ArrayAdapter<String> arrayAdapter;

		// bind the array adapter to the list view
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, falls);
		lstvFalls.setAdapter(arrayAdapter);

		// Shows the acceleration axises data in a graphic way
		xChart = (ChartView) findViewById(R.id.chart_x_axis);
		yChart = (ChartView) findViewById(R.id.chart_y_axis);
		zChart = (ChartView) findViewById(R.id.chart_z_axis);

		// Displays acceleration value in each axis
		txtvAccDataX = (TextView) findViewById(R.id.acc_data_x);
		txtvAccDataY = (TextView) findViewById(R.id.acc_data_y);
		txtvAccDataZ = (TextView) findViewById(R.id.acc_data_z);

		// Manages play and pause events
		ibtnPlayPause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (isSessionStarted) {
					// (Paused) The session has already started and it's paused
					if (isSessionPaused)
						sessionResume();						
					// (Running) the session has already started and is not paused
					else
						sessionPause();
				}
				// First time starting the session
				// TODO save to the DB
				else
					sessionStart();
			}
		});

		// TODO manages end button press
		ibtnEnd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO this is just a TEMPORARY code
				sessionStartTime = Calendar.getInstance(TimeZone.getDefault());
				falls.add(0, sessionStartTime.getTime().toString() + "  SENT");
				arrayAdapter.notifyDataSetChanged();
				Toast.makeText(getApplicationContext(), "Item added",
						Toast.LENGTH_LONG).show();
			}
		});

//		// UpdateGUI throw thread every 100milliseconds
//		Timer updateTimer = new Timer("AccelerometerTimer");
//		updateTimer.scheduleAtFixedRate(new TimerTask() {
//			public void run() {
//				if (isSessionStarted && !isSessionPaused){
//					updateGUI();
//				}
//			}
//		}, 0, 100);

	}

	private void sessionStart(){
		// Get the current time
		sessionStartTime = Calendar.getInstance(TimeZone
				.getDefault());
		
		// Set the current time to the text view field
		txtvStartTime
				.setText(sessionStartTime.getTime().toString());

		// Start the chronometer
		chroDuration.setBase(SystemClock.elapsedRealtime());
		chroDuration.start();

		// Change the icon of play-pause button
		ibtnPlayPause
				.setImageResource(R.drawable.pause_button_default);
		
		// Session starts
		isSessionStarted = true;
		isSessionPaused = false;
		
		// Start service 
		Intent serviceIntent = new Intent(getApplicationContext(), FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.ACTIVITY_ACTIVE, true);
		startService(serviceIntent);
		
	}
	
	private void sessionPause(){
		// Save the passed time and stop the chronometer
		duration = SystemClock.elapsedRealtime()
				- chroDuration.getBase();

		chroDuration.stop();

		// Change the icon of play-pause button to play
		ibtnPlayPause
				.setImageResource(R.drawable.play_button_default);

		// Flags need to be set before the intent!!
		isSessionPaused = true;
		
		// Stop service after button press
		Intent serviceIntent = new Intent(getApplicationContext(), FallDetectorService.class);
		stopService(serviceIntent);

	}
	
	private void sessionResume(){
		// Resume the chronometer from the stopped time
		chroDuration.setBase(SystemClock.elapsedRealtime()
				- duration);
		chroDuration.start();

		// Change the icon of play-pause button to pause
		ibtnPlayPause
				.setImageResource(R.drawable.pause_button_default);
		
		
		// Flags need to be set before the intent!!
		isSessionPaused = false;
		
		Intent serviceIntent = new Intent(getApplicationContext(), FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.ACTIVITY_ACTIVE, true);
		startService(serviceIntent);

	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    LocalBroadcastManager.getInstance(this).registerReceiver((broadcastDataReceiver), 
	    		new IntentFilter(FallDetectorService.XYZ_DATA));
	    
	    LocalBroadcastManager.getInstance(this).registerReceiver((broadcastArrayReceiver), 
	    		new IntentFilter(FallDetectorService.XYZ_ARRAY));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	};

	@Override
	protected void onPause() {
		isSessionPaused = true;
		super.onPause();
	};

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Saves session start time
		savedInstanceState.putString("sessionStartTime", txtvStartTime
				.getText().toString());

		// Saves whether this session starts
		savedInstanceState.putBoolean("isSessionStarted", isSessionStarted);

		// Saves whether this session pauses
		savedInstanceState.putBoolean("isSessionPaused", isSessionPaused);

		// Saves play-pause button status and chronometer
		if (isSessionStarted) {
			if (isSessionPaused) {
				savedInstanceState.putLong("duration", duration);
				savedInstanceState.putInt("ibtnPlayPause",
						R.drawable.play_button_default);
			} else {
				savedInstanceState.putLong("duration",
						SystemClock.elapsedRealtime() - chroDuration.getBase());
				savedInstanceState.putInt("ibtnPlayPause",
						R.drawable.pause_button_default);
			}
		} else {
			savedInstanceState.putInt("ibtnPlayPause",
					R.drawable.play_button_default);
			savedInstanceState.putLong("duration", 0l);
		}

		// Saves chart axes data
		savedInstanceState.putFloatArray("xChart", xChart.getChartData());
		savedInstanceState.putFloatArray("yChart", yChart.getChartData());
		savedInstanceState.putFloatArray("zChart", zChart.getChartData());
		savedInstanceState.putInt("xSize", xChart.getSize());
		savedInstanceState.putInt("ySize", yChart.getSize());
		savedInstanceState.putInt("zSize", zChart.getSize());
	};

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Restore session start time
		txtvStartTime.setText(savedInstanceState.getString("sessionStartTime"));

		// Restore session start flag
		isSessionStarted = savedInstanceState.getBoolean("isSessionStarted");

		// Set session paused everytime runtime changes occur
		isSessionPaused = savedInstanceState.getBoolean("isSessionPaused");

		// Set play-pause button to play
		ibtnPlayPause.setImageResource(savedInstanceState
				.getInt("ibtnPlayPause"));

		// Restore session duration
		duration = savedInstanceState.getLong("duration");

		// Restore chronometer data
		chroDuration.setBase(SystemClock.elapsedRealtime() - duration);

		if (isSessionStarted && !isSessionPaused)
			chroDuration.start();
		else
			chroDuration.stop();

		// Restore chart's axes data
		xChart.setChartData(
				savedInstanceState.getFloatArray("xChart"),
				savedInstanceState.getInt("xSize"));
		yChart.setChartData(
				savedInstanceState.getFloatArray("yChart"),
				savedInstanceState.getInt("ySize"));
		zChart.setChartData(
				savedInstanceState.getFloatArray("zChart"),
				savedInstanceState.getInt("zSize"));
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

	@Override
	public void onBackPressed() {
		mMed.resetCurretnPosSessionFromBack();
		super.onBackPressed();
	}
}
