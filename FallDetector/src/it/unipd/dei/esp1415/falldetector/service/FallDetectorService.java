package it.unipd.dei.esp1415.falldetector.service;

import it.unipd.dei.esp1415.falldetector.CurrentSessionActivity;
import it.unipd.dei.esp1415.falldetector.MainActivity;
import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.SettingsActivity;
import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import it.unipd.dei.esp1415.falldetector.mail.Mail;
import it.unipd.dei.esp1415.falldetector.mail.MailSender;
import it.unipd.dei.esp1415.falldetector.utility.AccelData;
import it.unipd.dei.esp1415.falldetector.utility.ConnectivityStatus;
import it.unipd.dei.esp1415.falldetector.utility.Fall;
import it.unipd.dei.esp1415.falldetector.utility.FallAlgorithmUtility;
import it.unipd.dei.esp1415.falldetector.utility.MailAddress;
import it.unipd.dei.esp1415.falldetector.utility.Session;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class FallDetectorService extends Service {
	
	public static final String X_AXIS_ARRAY = "X_AXIS_ARRAY";
	public static final String Y_AXIS_ARRAY = "Y_AXIS_ARRAY";
	public static final String Z_AXIS_ARRAY = "Z_AXIS_ARRAY";
	public static final String ARRAY_SIZE = "ARRAY_SIZE";
	public static final String XYZ_ARRAY = "XYZ_DATA";
	
	public static final String IS_PLAY = "IS_PLAY";
	public static final String IS_PAUSE = "IS_PAUSE";
	public static final String GET_ARRAY = "GET_ARRAY";
	
	private float[] accDataX;
	private float[] accDataY;
	private float[] accDataZ;
	
	private AccelData newData;
	
	private int accDataIndex;
	
	private long lastSampleTime;
	private long elapsedTime;
	
	private SensorManager sensorManager;
	
	private LocationManager locationManager;
	private Location location;
	private Criteria criteria;
	private String provider;
	
	private LocalBroadcastManager broadcaster;
	
	private Intent broadcastDataIntent;
	
	private double latitude, longitude;

	private boolean isPlaying;
	
	private boolean isPotentialFall;
	
	private int potentialFallcount;
	
	private DatabaseManager dm;
	
	private long sensorTime = 0l;
	private long myTime = 0l;
	
	private int posTmp;
	private Fall fallTmp;
	private int isSaving = 0;
	 
	
	public void onCreate() {
		super.onCreate();
		Log.i("onCreate: ", "onCreate called");
		
		dm = new DatabaseManager(this.getApplicationContext());
		
		ArrayList<AccelData> tmpAccData 
			= dm.getTempAccelDataAsArray(DatabaseTable.COLUMN_PK_ID + " " + DatabaseManager.ASC);
		
		accDataX = new float[FallAlgorithmUtility.ACC_DATA_SIZE];
		accDataY = new float[FallAlgorithmUtility.ACC_DATA_SIZE];
		accDataZ = new float[FallAlgorithmUtility.ACC_DATA_SIZE];
		
		for(AccelData accData : tmpAccData){
			
			accDataX[accDataIndex] = (float) accData.getX();
			accDataY[accDataIndex] = (float) accData.getY();
			accDataZ[accDataIndex] = (float) accData.getZ();
			accDataIndex++;
		}
		
		if(accDataIndex >= FallAlgorithmUtility.ACC_DATA_SIZE){
			accDataIndex = dm.getTempAccelDataLastIndex();
			accDataIndex = accDataIndex + FallAlgorithmUtility.ACC_DATA_SIZE + 1;
		}
		
		newData = new AccelData();
		
		lastSampleTime = 0l;
		
		isPlaying = false;
		isPotentialFall = false;
		
		potentialFallcount = 0;
		
		broadcaster = LocalBroadcastManager.getInstance(this);
		broadcastDataIntent = new Intent(FallDetectorService.XYZ_ARRAY);
		
		// Initialize the sensor manager
		sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		
		// Initialize the location manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW); 
		criteria.setAltitudeRequired(false); 
		criteria.setBearingRequired(false); 
		criteria.setSpeedRequired(false); 
		criteria.setCostAllowed(true);
		provider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(provider);
		if(location != null){
			latitude = location.getLatitude();
			longitude = location.getLongitude();
		}
			
		// Get default accelerometer sensor
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
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
				location = locationManager.getLastKnownLocation(provider);
				
				// Get default accelerometer sensor
				Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
				sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
				
				// for GPS, change the first parameter to GPS_PROVIDER
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locationListener);
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
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locationListener);
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
	
	
	private final LocationListener locationListener = new LocationListener(){
		@Override
		public void onLocationChanged(Location location) {
			// Called when a new location is find by the network location provider, not GPS.
			if(location != null){
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
			else{
				latitude = 180;
				longitude = 180;
			}
			
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
				
				if(sensorTime == 0l && myTime == 0l) {
			        sensorTime = event.timestamp;
			        myTime = System.currentTimeMillis();
			    }
				
				elapsedTime = System.currentTimeMillis() - lastSampleTime;
				
				if(elapsedTime > FallAlgorithmUtility.MIN_SAMPLE_RATE){
					
					if(isPlaying){
						
						if(accDataIndex >= FallAlgorithmUtility.ACC_DATA_SIZE){
							newData.setId(accDataIndex % FallAlgorithmUtility.ACC_DATA_SIZE);
							newData.setTimestamp(event.timestamp);
							newData.setX((double) (accDataX[accDataIndex % FallAlgorithmUtility.ACC_DATA_SIZE]
									= event.values[0]));
							newData.setY((double) (accDataY[accDataIndex % FallAlgorithmUtility.ACC_DATA_SIZE]
									= event.values[1]));
							newData.setZ((double) (accDataZ[accDataIndex % FallAlgorithmUtility.ACC_DATA_SIZE]
									= event.values[2]));
							lastSampleTime = System.currentTimeMillis();
							dm.upgradeATempAccelData(newData);
							broadcastDataIntent
								.putExtra(FallDetectorService.ARRAY_SIZE, accDataIndex);
							
						}else{
							newData.setId(accDataIndex);
							newData.setTimestamp(event.timestamp);
							newData.setX((double) (accDataX[accDataIndex] = event.values[0]));
							newData.setY((double) (accDataY[accDataIndex] = event.values[1]));
							newData.setZ((double) (accDataZ[accDataIndex] = event.values[2]));
							lastSampleTime = System.currentTimeMillis();
							dm.insertATempAccelData(newData);
							broadcastDataIntent.putExtra(FallDetectorService.ARRAY_SIZE, accDataIndex);
						}
						

						if(isSaving <= 0){
							if(isPotentialFall){
								if(FallAlgorithmUtility.module(newData) <= FallAlgorithmUtility.FALL_LOWER_BOUND)
									fallDetected(newData);
								potentialFallcount--;
								if(potentialFallcount <= 0)
									isPotentialFall = false; 
							}

							if(FallAlgorithmUtility.module(newData) >= FallAlgorithmUtility.FALL_UPPER_BOUND){
								isPotentialFall = true;
								potentialFallcount = 5;
							}
						
						}else{
							isSaving--;
							if(isSaving <= 0){
								saveAndShare();
							}
						}
						
						Log.e("onSensorChanged", "module: " + FallAlgorithmUtility.module(newData));
						if(isPotentialFall)
							Log.e("onSensorChanged", "isPotentialFall: true");
						else
							Log.e("onSensorChanged", "isPotentialFall: false");

						Log.i("sensorChanged", "X: " + newData.getX());
						Log.i("sensorChanged", "Y: " + newData.getY());
						Log.i("sensorChanged", "Z: " + newData.getZ());
						Log.i("sensorChanged", "size: " + accDataIndex);
						Log.i("sensorChanged", "Latitude_location: " + latitude);
						Log.i("sensorChanged", "Longitude_location: " + longitude);
						Log.i("sensorChanged", "provider: " + provider);

						broadcastDataIntent.putExtra(FallDetectorService.X_AXIS_ARRAY, accDataX);
						broadcastDataIntent.putExtra(FallDetectorService.Y_AXIS_ARRAY, accDataY);
						broadcastDataIntent.putExtra(FallDetectorService.Z_AXIS_ARRAY, accDataZ);
						broadcaster.sendBroadcast(broadcastDataIntent);

						accDataIndex++;
					}
					
				}
			}
		}
	};
	
	//What to do when the fall occurs
	private void fallDetected(AccelData data){
		Log.i("fallDetected", "fall!");
		isPotentialFall = false;

		Fall fall = new Fall((myTime+ (data.getTimestamp()- sensorTime)/1000000l), 
							 dm.getLastSession().getId());

		fall.setLatitude(latitude);
		fall.setLongitude(longitude);
		fall.setId(dm.insertAFall(fall));
		
		
		if((CurrentSessionActivity.arrayAdapter != null) && (CurrentSessionActivity.falls != null)){
			Log.e("Add fall:", "Adding fall");
			CurrentSessionActivity.falls.add(fall);
			CurrentSessionActivity.lstvFalls.setAdapter(CurrentSessionActivity.arrayAdapter);
			
			Toast toast = Toast.makeText(getApplicationContext(), "Item Added, size: " + CurrentSessionActivity.arrayAdapter.getCount()
					, Toast.LENGTH_SHORT);
			toast.show();
		}
		
		posTmp = (int) data.getId();
		fallTmp = fall;
		isSaving = FallAlgorithmUtility.GET_HALF_SEC;
	}
	
	private void saveAndShare(){
		isSaving = 0;
		
		ArrayList<AccelData> array = new ArrayList<AccelData>();
		int maxDataPos = (FallAlgorithmUtility.GET_HALF_SEC+posTmp) % FallAlgorithmUtility.ACC_DATA_SIZE;
		
		long fallId = fallTmp.getId();
		for(int i = 1; i<= FallAlgorithmUtility.DATA_SAVE; i++){
			AccelData temp = new AccelData(FallAlgorithmUtility.DATA_SAVE+1-i, fallId);
			temp.setX(accDataX[maxDataPos]);
			temp.setY(accDataY[maxDataPos]);
			temp.setZ(accDataZ[maxDataPos]);
			array.add(temp);
			maxDataPos--;
			if(maxDataPos<0){
				maxDataPos = FallAlgorithmUtility.ACC_DATA_SIZE;
				maxDataPos--;
			}
		}
		
		for(int i = array.size() -1; i>= 0; i--){
			dm.insertAnAccelData(array.get(i));
		}
		
		Session last = dm.getLastSession();
		ContentValues values = new ContentValues();
		values.put(DatabaseTable.COLUMN_SS_FALLS_NUMBER, last.getFallsNum() + 1);
		dm.upgradeASession(last.getId(), values);
		
		if(ListSessionFragment.mArray != null){
			ListSessionFragment.mArray.get(0).setFallsNum(last.getFallsNum() + 1);
		}
		
		boolean isSend = false;
		ConnectivityStatus status = new ConnectivityStatus(getApplicationContext());
		
		if(status.hasMobileConnectionON() || status.hasWifiConnectionON()){
			ArrayList<MailAddress> receiversMail = dm.getMailAddressAsArray();
			
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(fallTmp.getTimeStampFallEvent());

			String sbj = getApplicationContext().getResources().getString(R.string.mail_sbj);

			String msg = getApplicationContext().getResources().getString(R.string.mail_msg1);
			msg = msg + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + " of "
					+ cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH + 1) 
					+ cal.get(Calendar.YEAR);
			msg = msg + getApplicationContext().getResources().getString(R.string.mail_msg2) + "\n";
			msg = msg + getApplicationContext().getResources().getString(R.string.latitude) + ":" 
					+ fallTmp.getLatitude() + "\n";
			msg = msg + getApplicationContext().getResources().getString(R.string.longitude) + ":" 
					+ fallTmp.getLongitude() + "\n";
			msg = msg + getApplicationContext().getResources().getString(R.string.mail_end1) + "\n\n";
			msg = msg + getApplicationContext().getResources().getString(R.string.mail_end2) + "\n";

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()); 
			if(preferences.getBoolean(SettingsMainFragment.SAVE_MAIL_CHK, false)){
				ArrayList<String> receivers = new ArrayList<String>();
				for(int i = 0; i < receiversMail.size(); i++){
					receivers.add(receiversMail.get(i).getAddress());
				}
				String[] data = preferences.getString(SettingsMainFragment.SAVE_MAIL_DATABASE64, "").split(SettingsActivity.DIVISOR_DATA);
				String sender = preferences.getString(SettingsMainFragment.SAVE_MAIL_ACCOUNT, "");
				
				if(data.length > 1){
					MailSender mailSend = new MailSender(MailSender.GMAIL_PORT, MailSender.GMAIL_SERVER, data[0], data[1], false);
					
					if(mailSend.connect()){
						Mail mail = new Mail(sender, receivers);
						mail.setSubject(sbj);
						mail.setMessage(msg);
						
						mailSend.send(mail);
						
						mailSend.close();
						
						isSend = true;
					}
				}
			}else{
				String[] receivers = new String[receiversMail.size()];
				for(int i = 0; i < receiversMail.size(); i++){
					receivers[i] = receiversMail.get(i).getAddress();
				}
				
				Intent sendTo = new Intent(Intent.ACTION_SENDTO);
				sendTo.setData(Uri.parse("mailto:"));
				sendTo.putExtra(Intent.EXTRA_EMAIL, receivers);
				sendTo.putExtra(Intent.EXTRA_SUBJECT, sbj);
				sendTo.putExtra(Intent.EXTRA_TEXT, msg);
				

				isSend = true;
				
				if (sendTo.resolveActivity(getPackageManager()) != null) {
					sendTo.setFlags(sendTo.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
			        startActivity(sendTo);
			    }
			}
			
			if(isSend){
				ContentValues val = new ContentValues();
				val.put(DatabaseTable.COLUMN_FE_IS_NOTIFIED, isSend);
				dm.upgradeAFall(fallTmp.getId(), val);

				if((CurrentSessionActivity.arrayAdapter != null) && (CurrentSessionActivity.falls != null)){
					Log.e("Add fall:", "Adding fall");
					CurrentSessionActivity.falls.get(CurrentSessionActivity.falls.size()-1).setNotification(isSend);
					CurrentSessionActivity.lstvFalls.setAdapter(CurrentSessionActivity.arrayAdapter);
				}
			}
		}
	}
}
