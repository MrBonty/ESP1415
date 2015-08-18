package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.service.FallDetectorService;
import it.unipd.dei.esp1415.falldetector.utility.ChartView;
import it.unipd.dei.esp1415.falldetector.utility.ColorUtil;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CurrentSessionActivity extends ActionBarActivity {

	private Mediator mMed;
	
	//current session
	private Session mCurrent;

	private DatabaseManager dm;

	//Text view for session name and session start time
	private TextView sessionName;
	private TextView txtvStartTime;
	
	/**
	 * Display the duration of the session
	 */
	private Chronometer chroDuration;

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

	private int size;
	
	private ImageView thmb;

	private BroadcastReceiver broadcastDataReceiver;

	public CurrentSessionActivity() {

		// Initialize variables

		txtvStartTime = null;
		ibtnPlayPause = null;
		chroDuration = null;
		
		size = 0;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_session_interface);

		sessionName = (TextView) findViewById(R.id.current_sesstion_name);
		
		thmb = (ImageView) findViewById(R.id.current_thumbnail);
		
		txtvStartTime = (TextView) findViewById(R.id.session_start_time);
		
		// Initialize the chronometer
		chroDuration = (Chronometer) findViewById(R.id.session_duration);
		
		// Initialize the play-pause button view
		ibtnPlayPause = (ImageButton) findViewById(R.id.play_button);
		
		// Shows the acceleration axises data in a graphic way
		txtvAccDataX = (TextView) findViewById(R.id.acc_data_x);
		xChart = (ChartView) findViewById(R.id.chart_x_axis);
		
		txtvAccDataY = (TextView) findViewById(R.id.acc_data_y);
		yChart = (ChartView) findViewById(R.id.chart_y_axis);
		
		txtvAccDataZ = (TextView) findViewById(R.id.acc_data_z);
		zChart = (ChartView) findViewById(R.id.chart_z_axis);
		
		// lstvFalls referes to the ListView in the layout
		ListView lstvFalls = (ListView) findViewById(R.id.session_falls);
		
		// end button
		final ImageButton ibtnEnd = (ImageButton) findViewById(R.id.end_button);
		
		mMed = new Mediator();
		dm = new DatabaseManager(mMed.getContext());

		// Fetch session from database
		mCurrent = dm.getLastSession(null, DatabaseTable.COLUMN_SS_START_DATE
				+ " " + DatabaseManager.DESC);
		
		sessionName.setText(mCurrent.getName());
		
		// Set up thumbnail
		if (mCurrent.getBitmap() == null) {
			Bitmap image = null;
			image = BitmapFactory.decodeResource(mMed.getContext()
					.getResources(), R.drawable.thumbnail);
			image = ColorUtil.recolorIconBicolor(mCurrent.getColorThumbnail(),
					image);
			mCurrent.setBitmap(image);
		}
		
		thmb.setImageBitmap(ColorUtil.recolorIconBicolor(
				mCurrent.getColorThumbnail(), mCurrent.getBitmap()));
		
		// Set up start time
		if (mCurrent.getStartTimestamp() <= 0) {
			// Initialize txtvStartTime view
			ibtnPlayPause.setImageResource(R.drawable.play_button_default);
		} else {
			// Fetch data from current session
			txtvStartTime.setText(mCurrent.getStartDateCalendar().getTime()
					.toString());
			// Set up chronometer
			if (mMed.isSessionPaused()){
				chroDuration.setBase(SystemClock.elapsedRealtime()
						- mCurrent.getDuration());
				ibtnPlayPause.setImageResource(R.drawable.play_button_default);
			}
			else {
				chroDuration.setBase(mMed.getChronoBaseTime()
						- mCurrent.getDuration());
				ibtnPlayPause.setImageResource(R.drawable.pause_button_default);
			}

			if (mCurrent.isActive() && !mMed.isSessionPaused())
				chroDuration.start();
		}// timestamp

		// List of falls occur during the session
		final ArrayList<String> falls = new ArrayList<String>();

		// Array adapter of the list view
		final ArrayAdapter<String> arrayAdapter;

		// bind the array adapter to the list view
		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, falls);
		lstvFalls.setAdapter(arrayAdapter);

		// Manages play and pause events
		ibtnPlayPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrent.getStartTimestamp() > 0) {
					// (Paused) The session has already started and it's paused
					if (mMed.isSessionPaused())
						sessionResume();
					// (Running) the session has already started and is not paused
					else
						sessionPause();
				}
				// First time starting the session
				else
					sessionStart();
			}
		});

		// Manages end button event
		ibtnEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sessionEnd();
			}
		});
		
		// Updating chartView
		broadcastDataReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
					
				size = intent.getIntExtra(FallDetectorService.ARRAY_SIZE, 0);

				// Update chart X axis data and text
				xChart.setChartData(intent.getFloatArrayExtra(FallDetectorService.X_AXIS_ARRAY), size);
				xChart.invalidate();
				txtvAccDataX.setText("X: " + xChart.getLastElement());
				txtvAccDataX.invalidate();

				// Update chart Y axis data and text
				yChart.setChartData(intent.getFloatArrayExtra(FallDetectorService.Y_AXIS_ARRAY), size);
				yChart.invalidate();
				txtvAccDataY.setText("Y: " + yChart.getLastElement());
				txtvAccDataY.invalidate();

				// Update chart Z axis data and text
				zChart.setChartData(intent.getFloatArrayExtra(FallDetectorService.Z_AXIS_ARRAY), size);
				zChart.invalidate();
				txtvAccDataZ.setText("Z: " + zChart.getLastElement());
				txtvAccDataZ.invalidate();
				
				Log.w("x: ", xChart.getLastElement() + "");
				Log.w("y: ", yChart.getLastElement() + "");
				Log.w("z: ", zChart.getLastElement() + "");
				Log.w("size: ", (size - 1) + " size");
			}
		};
	}

	private void sessionStart() {

		// Get the current time and set the session to active
		mCurrent.setStartDateAndTimestamp(Calendar.getInstance(TimeZone.getDefault()));
		mCurrent.setToActive(true);

		mMed.setSessionPaused(false);

		// Update database
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_START_DATE,
				mCurrent.getStartTimestamp());
		values.put(DatabaseTable.COLUMN_SS_IS_ACTIVE,
				mCurrent.isActiveAsInteger());
		dm.upgradeASession(mCurrent.getId(), values);

		// Set the current time to the text view field
		txtvStartTime.setText(mCurrent.getStartDateCalendar().getTime()
				.toString());

		// Start the chronometer
		chroDuration.setBase(SystemClock.elapsedRealtime());
		chroDuration.start();

		// Change the icon of play-pause button
		ibtnPlayPause.setImageResource(R.drawable.pause_button_default);
		
		// Start service
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PLAY, true);
		startService(serviceIntent);
	}

	private void sessionPause() {

		// Save the passed time and stop the chronometer
		mCurrent.setDuration(SystemClock.elapsedRealtime()
				- chroDuration.getBase());
		chroDuration.stop();

		// Update duration in db
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_DURATION, mCurrent.getDuration());
		dm.upgradeASession(mCurrent.getId(), values);

		mMed.setSessionPaused(true);

		// Change the icon of play-pause button to play
		ibtnPlayPause.setImageResource(R.drawable.play_button_default);

		// Stop service after button press
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PAUSE, true);
		startService(serviceIntent);
	}

	private void sessionResume() {
		// Resume the chronometer from the stopped time
		chroDuration.setBase(SystemClock.elapsedRealtime()
				- mCurrent.getDuration());
		chroDuration.start();

		mMed.setSessionPaused(false);

		// Change the icon of play-pause button to pause
		ibtnPlayPause.setImageResource(R.drawable.pause_button_default);

		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PLAY, true);
		startService(serviceIntent);

	}

	private void sessionEnd() {
		ContentValues values = new ContentValues();

		// Save the passed time and stop the chronometer
		if (!mMed.isSessionPaused()) {
			mCurrent.setDuration(SystemClock.elapsedRealtime()
					- chroDuration.getBase());
			chroDuration.stop();
			mMed.setChronoBaseTime(SystemClock.elapsedRealtime());
			values.put(DatabaseTable.COLUMN_SS_DURATION, mCurrent.getDuration());
		}

		mCurrent.setToActive(false);
		values.put(DatabaseTable.COLUMN_SS_IS_ACTIVE,
				mCurrent.isActiveAsInteger());
		dm.upgradeASession(mCurrent.getId(), values);
		
		// Clear tmp acc data from database
		dm.deleteTempAccDataTable();

		// Stop service after button press
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PAUSE, true);
		stopService(serviceIntent);
		finish();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		LocalBroadcastManager
			.getInstance(this).registerReceiver((broadcastDataReceiver),
			new IntentFilter(FallDetectorService.XYZ_ARRAY));
		
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		startService(serviceIntent); 
	}

	@Override
	protected void onResume() {
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.GET_ARRAY, true);
		startService(serviceIntent); 
		super.onResume();
	};

	@Override
	protected void onPause() {
		super.onPause();

		if (mCurrent.isActive() && !mMed.isSessionPaused()) {
			mCurrent.setDuration(SystemClock.elapsedRealtime()
					- chroDuration.getBase());
			mMed.setChronoBaseTime(SystemClock.elapsedRealtime());
		}

		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_DURATION, mCurrent.getDuration());
		dm.upgradeASession(mCurrent.getId(), values);
	};

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				broadcastDataReceiver);
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	};

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		// Saves session start time
		savedInstanceState.putString("sessionStartTime", txtvStartTime
				.getText().toString());

		// Saves play-pause button status and chronometer
		if (mCurrent.getStartTimestamp() > 0) {
			if (mMed.isSessionPaused()) {
				savedInstanceState.putLong("duration", mCurrent.getDuration());
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

	};

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		// Restore session start time
		txtvStartTime.setText(savedInstanceState.getString("sessionStartTime"));

		// Set play-pause button to play
		ibtnPlayPause.setImageResource(savedInstanceState
				.getInt("ibtnPlayPause"));

		// Restore session duration
		mCurrent.setDuration(savedInstanceState.getLong("duration"));

		// Restore chronometer data
		chroDuration.setBase(SystemClock.elapsedRealtime()
				- mCurrent.getDuration());

		if ((mCurrent.getStartTimestamp() > 0) && !mMed.isSessionPaused())
			chroDuration.start();
		else
			chroDuration.stop();
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