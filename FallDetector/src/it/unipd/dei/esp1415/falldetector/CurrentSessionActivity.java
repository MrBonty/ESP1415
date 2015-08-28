package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.service.FallDetectorService;
import it.unipd.dei.esp1415.falldetector.utility.ChartView;
import it.unipd.dei.esp1415.falldetector.utility.ColorUtil;
import it.unipd.dei.esp1415.falldetector.utility.Fall;
import it.unipd.dei.esp1415.falldetector.utility.FallAlgorithmUtility;
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
	
	/*---- Utilities variables ----*/
	
	// Temporary variables for update charts
	private float[] xTmpArray;
	private float[] yTmpArray;
	private float[] zTmpArray;
	private int index;
	
	// Indicates whether the session expired
	private boolean timeExceed;
	
	private DatabaseManager dm;
	private Mediator mMed;
	
	// Contains current session's information
	private Session mCurrent;
	
	// Public variables used by the service
	public static ArrayList<Fall> falls = null;
	public static ArrayAdapter<Fall> arrayAdapter = null;
	public static ListView lstvFalls = null;

	// Receiver which receive data from service
	private BroadcastReceiver broadcastDataReceiver;

	/*---- Activity layout variables ----*/
	
	// TextView for session name and session start time
	private TextView sessionName;
	private TextView txtvStartTime;
	
	// Display the duration of the session
	private Chronometer chroDuration;

	// Button which start, play, pause and end the session
	private ImageButton ibtnPlayPause;
	private ImageButton ibtnEnd;

	// Customize view for chart
	private ChartView xChart;
	private ChartView yChart;
	private ChartView zChart;
	
	// TextView for displaying acceleromter data
	private TextView txtvAccDataX;
	private TextView txtvAccDataY;
	private TextView txtvAccDataZ;
	
	// ImageView indicates the thumbnail
	private ImageView thmb;

	public CurrentSessionActivity() {
		
		// Initialize variables
		xTmpArray = new float[FallAlgorithmUtility.ACC_DATA_SIZE];
		xTmpArray = new float[FallAlgorithmUtility.ACC_DATA_SIZE];
		xTmpArray = new float[FallAlgorithmUtility.ACC_DATA_SIZE];
		
		index = 0;
		
		timeExceed = false;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Initialize variables
		mMed = new Mediator();
		dm = new DatabaseManager(getApplicationContext());
		
		// Fetch session's information from database
		mCurrent = dm.getLastSession();
		
		/*---- Initialize activity interface ----*/
		setContentView(R.layout.current_session_interface);
		
		sessionName = (TextView) findViewById(R.id.current_sesstion_name);
		sessionName.setText(mCurrent.getName());
		
		// Initialize thumbnail
		thmb = (ImageView) findViewById(R.id.current_thumbnail);
		if (mCurrent.getBitmap() == null) {
			Bitmap image = null;
			image = BitmapFactory.decodeResource(getApplicationContext()
					.getResources(), R.drawable.thumbnail);
			image = ColorUtil.recolorIconBicolor(mCurrent.getColorThumbnail(),
					image);
			mCurrent.setBitmap(image);
		}
		thmb.setImageBitmap(ColorUtil.recolorIconBicolor(
				mCurrent.getColorThumbnail(), mCurrent.getBitmap()));
		
		// Initialize session start time
		txtvStartTime = (TextView) findViewById(R.id.session_start_time);
		
		// Initialize the chronometer
		chroDuration = (Chronometer) findViewById(R.id.session_duration);
		
		// Initialize the play-pause button view
		ibtnPlayPause = (ImageButton) findViewById(R.id.play_button);
		
		// Initialize the end button
		ibtnEnd = (ImageButton) findViewById(R.id.end_button);
		
		// Set the start time, the button status and the chronometer 
		if (mCurrent.getStartTimestamp() <= 0) {
			ibtnPlayPause.setImageResource(R.drawable.play_button_default);
			ibtnEnd.setClickable(false);
		} else {
			txtvStartTime.setText(mCurrent.getStartDateCalendar().getTime()
					.toString());
			if (mCurrent.isPause()){
				chroDuration.setBase(SystemClock.elapsedRealtime()
						- mCurrent.getDuration());
				ibtnPlayPause.setImageResource(R.drawable.play_button_default);
			} else {
				chroDuration.setBase(mCurrent.getChrono_tmp()
						- mCurrent.getDuration());
				ibtnPlayPause.setImageResource(R.drawable.pause_button_default);
			}// else
			if (mCurrent.isActive() && !mCurrent.isPause())
				chroDuration.start();
		}// else
		
		// Initialize the x axis chart
		txtvAccDataX = (TextView) findViewById(R.id.acc_data_x);
		xChart = (ChartView) findViewById(R.id.chart_x_axis);
		
		// Initialize the y axis chart		
		txtvAccDataY = (TextView) findViewById(R.id.acc_data_y);
		yChart = (ChartView) findViewById(R.id.chart_y_axis);
		
		// Initialize the z axis chart
		txtvAccDataZ = (TextView) findViewById(R.id.acc_data_z);
		zChart = (ChartView) findViewById(R.id.chart_z_axis);
		
		// Initialize ListView for falls
		lstvFalls = (ListView) findViewById(R.id.session_falls);
		
		// List of falls occur during the session
		falls = new ArrayList<Fall>();
		falls = dm.getFallForSessionAsArray(mCurrent.getId(), DatabaseTable.COLUMN_FE_DATE
				+ " " + DatabaseManager.DESC);

		// bind the array adapter to the ListView of falls
		arrayAdapter = new ArrayAdapter<Fall>(this,
				android.R.layout.simple_list_item_1, falls);
		lstvFalls.setAdapter(arrayAdapter);

		// Manages play and pause events
		ibtnPlayPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrent.getStartTimestamp() > 0) {
					// (Paused) The session has already started and it's paused
					if (mCurrent.isPause())
						sessionResume();
					// (Running) the session has already started and is not paused
					else
						sessionPause();
				}
				// First time starting the session
				else
					sessionStart();
			} // onClick
		}); // Listener
		
		// Manages end button event
		ibtnEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mCurrent.isActive())
					sessionEnd();
			} // onClick
		}); // End button listener
		
		// Updating charts
		broadcastDataReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
					
				index = intent.getIntExtra(FallDetectorService.ARRAY_SIZE, 0);
				xTmpArray = intent.getFloatArrayExtra(FallDetectorService.X_AXIS_ARRAY);
				yTmpArray = intent.getFloatArrayExtra(FallDetectorService.Y_AXIS_ARRAY);
				zTmpArray = intent.getFloatArrayExtra(FallDetectorService.Z_AXIS_ARRAY);
				timeExceed = intent.getBooleanExtra(FallDetectorService.TIME_EXCEED, false);
				
				if(timeExceed)
					sessionEnd();
				
				if(index >= FallAlgorithmUtility.ACC_DATA_SIZE){
					
					// Fix the index of the array
					index = index % FallAlgorithmUtility.ACC_DATA_SIZE;
					
					// Updates x axis chart and data
					xTmpArray = swapArrayData(xTmpArray, index);
					xChart.setChartData(xTmpArray, FallAlgorithmUtility.ACC_DATA_SIZE);
					xChart.invalidate();
					txtvAccDataX.setText("X: " + xChart.getLastElement());
					txtvAccDataX.invalidate();
					
					// Updates y axis chart and data
					yTmpArray = swapArrayData(yTmpArray, index);
					yChart.setChartData(yTmpArray, FallAlgorithmUtility.ACC_DATA_SIZE);
					yChart.invalidate();
					txtvAccDataY.setText("Y: " + yChart.getLastElement());
					txtvAccDataY.invalidate();
					
					// Updates z axis chart and data
					zTmpArray = swapArrayData(zTmpArray, index);
					zChart.setChartData(zTmpArray, FallAlgorithmUtility.ACC_DATA_SIZE);
					zChart.invalidate();
					txtvAccDataZ.setText("Z: " + zChart.getLastElement());
					txtvAccDataZ.invalidate();
				}
					
				else{
					
					// Updates x axis chart and data
					xChart.setChartData(intent.getFloatArrayExtra(FallDetectorService.X_AXIS_ARRAY), index);
					xChart.invalidate();
					txtvAccDataX.setText("X: " + xChart.getLastElement());
					txtvAccDataX.invalidate();

					// Updates y axis chart and data
					yChart.setChartData(intent.getFloatArrayExtra(FallDetectorService.Y_AXIS_ARRAY), index);
					yChart.invalidate();
					txtvAccDataY.setText("Y: " + yChart.getLastElement());
					txtvAccDataY.invalidate();

					// Updates z axis chart and data
					zChart.setChartData(intent.getFloatArrayExtra(FallDetectorService.Z_AXIS_ARRAY), index);
					zChart.invalidate();
					txtvAccDataZ.setText("Z: " + zChart.getLastElement());
					txtvAccDataZ.invalidate();
					
				} // else
			} // onReceive
		}; // broadcastReceiver
	}

	// private method for session start event
	private void sessionStart() {
		
		// Get the current time and set the session to active
		mCurrent.setStartDateAndTimestamp(
				Calendar.getInstance(TimeZone.getDefault()));
		mCurrent.setToActive(true);
		mCurrent.setPause(false);
		ibtnEnd.setClickable(true);
		
		// Updates other activities
		if(ListSessionFragment.mArray != null){
			ListSessionFragment.mArray.get(0)
				.setStartDateAndTimestamp(mCurrent.getStartDateCalendar());
			ListSessionFragment.mArray.get(0).setToActive(true);	
		}
		
		// Updates database
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_START_DATE,
				mCurrent.getStartTimestamp());
		values.put(DatabaseTable.COLUMN_SS_IS_ACTIVE,
				mCurrent.isActiveAsInteger());
		values.put(DatabaseTable.COLUMN_SS_IS_PAUSE,
				mCurrent.isPauseAsInteger());
		dm.upgradeASession(mCurrent.getId(), values);

		// Updates start time
		txtvStartTime.setText(mCurrent.getStartDateCalendar().getTime()
				.toString());

		// Start the chronometer
		chroDuration.setBase(SystemClock.elapsedRealtime());
		chroDuration.start();

		// Change play-pause button status
		ibtnPlayPause.setImageResource(R.drawable.pause_button_default);
		
		// Start service
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PLAY, true);
		startService(serviceIntent);
	}

	// private method for session pause event
	private void sessionPause() {
		
		// Saves the passed time and stops the chronometer
		mCurrent.setDuration(SystemClock.elapsedRealtime()
				- chroDuration.getBase());
		chroDuration.stop();
		mCurrent.setPause(true);
		
		// Updates other activities
		if(ListSessionFragment.mArray != null)
			ListSessionFragment.mArray.get(0).setDuration(mCurrent.getDuration());
		
		// Updates duration in the database
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_DURATION, mCurrent.getDuration());
		values.put(DatabaseTable.COLUMN_SS_IS_PAUSE, mCurrent.isPauseAsInteger());
		dm.upgradeASession(mCurrent.getId(), values);

		// Changes the play-pause button status
		ibtnPlayPause.setImageResource(R.drawable.play_button_default);

		// Stops service after
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PAUSE, true);
		startService(serviceIntent);
	}

	// private method for session resume event
	private void sessionResume() {
		
		// Resumes the chronometer from the stopped time
		chroDuration.setBase(SystemClock.elapsedRealtime()
				- mCurrent.getDuration());
		chroDuration.start();
		mCurrent.setPause(false);
		
		// Updates the database
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_IS_PAUSE, mCurrent.isPauseAsInteger());
		dm.upgradeASession(mCurrent.getId(), values);

		// Changes play-pause button status
		ibtnPlayPause.setImageResource(R.drawable.pause_button_default);

		// Resumes the service
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PLAY, true);
		startService(serviceIntent);

	}

	// Private method for session end event
	private void sessionEnd() {

		ContentValues values = new ContentValues();
		
		// Set the current session status to not active
		mCurrent.setToActive(false);
		
		// Saves the passed time and stops the chronometer
		if (!mCurrent.isPause()) {
			mCurrent.setDuration(SystemClock.elapsedRealtime()
					- chroDuration.getBase());
			chroDuration.stop();
			
			// Updates other activities
			if(ListSessionFragment.mArray != null)
				ListSessionFragment.mArray.get(0).setDuration(mCurrent.getDuration());

			// Preparing data for database update
			mCurrent.setChrono_tmp(SystemClock.elapsedRealtime());
			values.put(DatabaseTable.COLUMN_SS_CHRONO_TMP, mCurrent.getChrono_tmp());
			values.put(DatabaseTable.COLUMN_SS_DURATION, mCurrent.getDuration());
		}

		// Updates other activities
		if(ListSessionFragment.mArray != null){
			ListSessionFragment.mArray.get(0).setDuration(mCurrent.getDuration());
			ListSessionFragment.mArray.get(0).setToActive(false);
		}
		
		// Updates the database
		values.put(DatabaseTable.COLUMN_SS_IS_ACTIVE,
				mCurrent.isActiveAsInteger());
		dm.upgradeASession(mCurrent.getId(), values);
		
		// Stops service
		Intent serviceIntent = new Intent(getApplicationContext(),
				FallDetectorService.class);
		serviceIntent.putExtra(FallDetectorService.IS_PAUSE, true);
		startService(serviceIntent);
		
		// Ends service
		stopService(serviceIntent);
		
		// Clear temporary accelerometer data from database
		dm.deleteTempAccDataTable();
		
		// Finish the UI3 and pass to UI2
		Intent activityIntent = new Intent(this, MainActivity.class);
		activityIntent.putExtra(MainActivity.OPEN_UI2, true);
		activityIntent.setFlags(activityIntent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(activityIntent);
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
		
		falls = dm.getFallForSessionAsArray(mCurrent.getId(), DatabaseTable.COLUMN_FE_DATE
				+ " " + DatabaseManager.DESC);
		arrayAdapter = new ArrayAdapter<Fall>(this,
				android.R.layout.simple_list_item_1, falls);
		super.onResume();
	};

	@Override
	protected void onPause() {
		super.onPause();
		
		ContentValues values = new ContentValues();
		
		if (mCurrent.isActive() && !mCurrent.isPause()) {
			mCurrent.setDuration(SystemClock.elapsedRealtime()
					- chroDuration.getBase());
			
			mCurrent.setChrono_tmp(SystemClock.elapsedRealtime());
			values.put(DatabaseTable.COLUMN_SS_CHRONO_TMP, mCurrent.getChrono_tmp());
		}

		values.put(DatabaseTable.COLUMN_SS_DURATION, mCurrent.getDuration());
		dm.upgradeASession(mCurrent.getId(), values);
		
		arrayAdapter = null;
		falls = null;
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
			if (mCurrent.isPause()) {
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

		if ((mCurrent.getStartTimestamp() > 0) && !mCurrent.isPause())
			chroDuration.start();
		else
			chroDuration.stop();
	};
	
	// private method used for fix chart array data updates
	private float[] swapArrayData(float[] array, int index){
		float[] tmp = new float[FallAlgorithmUtility.ACC_DATA_SIZE];
		int i = 0;
		while((i + index + 1) < FallAlgorithmUtility.ACC_DATA_SIZE){
			tmp[i] = array[i + index + 1];
			i++;
		}
		int j = 0;
		while(j <= index){
			tmp[i + j] = array[j];
			j++;
		}
		return tmp;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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