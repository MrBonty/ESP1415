package it.unipd.dei.esp1415.falldetector;

import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.extraview.ConnectionDialog;
import it.unipd.dei.esp1415.falldetector.extraview.SessionDialog;
import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.service.AlarmService;
import it.unipd.dei.esp1415.falldetector.utility.ConnectivityStatus;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private Context mContext;
	
	private Mediator mMed;

	private SessionDialog mDialog;
	private ConnectionDialog mStatusDialog;
	
	// Constants
	public static final int SCREENLAYOUT_SIZE_XLARGE = 0x04; //For compatibility with API 8 same value of Configuration.SCREENLAYOUT_SIZE_XLARGE
	public static final String SAVE_ADD_DIALOG = "addDialog";
	
	public static final int NEW_SESSION_POSITION = 0;
	
	public static final String OPEN_UI2 = "Open_UI2";

	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		//for a problem to test account gmail
		//for extra function of rapid mail send 

		if(Build.VERSION.SDK_INT > 8){
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy); 
		}
		
		Configuration conf = getResources().getConfiguration();
		mContext = getApplication();

		mMed = new Mediator(mContext, this);
		
		mMed.isLand(conf.orientation == Configuration.ORIENTATION_LANDSCAPE);
		boolean xlarge = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    
		mMed.isLarge(xlarge || large);

		downloadData();
		
		FragmentManager manager = getSupportFragmentManager();
		
		Fragment listFragment = manager.findFragmentById(R.id.main_list);

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_list, listFragment);
		
		transaction.commit();
		
		if(savedInstanceState != null && savedInstanceState.getBoolean(SAVE_ADD_DIALOG)){
			createAddDialog(true, savedInstanceState);
		}
		
		Intent intent = getIntent();
		if(intent != null){
			boolean newSession  = intent.getBooleanExtra(AlarmService.NEW_TASK, false);
			
			if(newSession){
				
				openAdd();
			}
		}
		
		deleteService().run();
		if(savedInstanceState != null && savedInstanceState.getBoolean(OPEN_UI2)){
			if(listFragment != null && listFragment instanceof ListSessionFragment){
				((ListSessionFragment) listFragment).showDetail(0);
			}
		}
		
		
	}//[m] onCreate()
	
	@Override
	protected void onResume() {
		super.onResume();

		if(!(mMed.isLocationControlled() && mMed.isConnectionControlled())){
			createConnectivityDialog();
		}

		downloadData();
		
		ListSessionFragment.mAdapter.notifyDataSetChanged();
		
	}//[m] onResume()

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}//[m] onCreateOptionsMenu()

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		switch (item.getItemId()) {
		case R.id.action_add:
			openAdd();
			return true;
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}// [m] onOptionsItemSelected()
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if(mDialog != null && mDialog.isShowing() && mDialog.isNew()){
        	outState.putAll(mDialog.onSaveInstanceState());
        	mDialog.setOnDismissListener(null);
        	mDialog.dismiss();
        	
        	outState.putBoolean(SAVE_ADD_DIALOG, true);
		}
        
        if(mStatusDialog != null){
        	mStatusDialog.setOnDismissListener(null);
        	mStatusDialog.dismiss();
        }
    }//[m] onSaveInstanceState()

	/**
	 * [m]
	 * Method use for open add dialog or an advise
	 */
	private void openAdd(){
		if(mMed.getDataSession()!= null && mMed.getDataSession().size()> 0){
			
			if(mMed.getDataSession().get(0).getStartTimestamp() == 0){

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setMessage(R.string.dialog_message_tostart)
				.setTitle(R.string.dialog_title);


				AlertDialog dialog = builder.create();

				dialog.show();
			}else if(mMed.getDataSession().get(0).isActive()){

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				builder.setMessage(R.string.dialog_message)
				.setTitle(R.string.dialog_title);

				AlertDialog dialog = builder.create();

				dialog.show();
			
			}else{
				createAddDialog(false, null);

			}
		}else{
			createAddDialog(false, null);

		}
	}//[m] openAdd()
	
	/**
	 * [m]
	 * Method to create a Dialog for add or modify a session
	 * 
	 * @param restore pass true if the dialog has to restored, false otherwise
	 * @param data the bundle for restoring dialog, pass null if restore is set to false 
	 */
	private void createAddDialog(boolean restore, Bundle data){

		if(restore && data != null){
			mDialog = new SessionDialog(this, true);
			mDialog.onRestoreInstanceState(data);
			
		}else{
			mDialog = new SessionDialog(this, NEW_SESSION_POSITION, true);
		}
		
		mDialog.show();
		
		// set of dismiss listener for the dialog
		mDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				
				mDialog = null;
			}//[m] onDismiss()
		});
	}//[m] createAddDialog()
	
	/**
	 * [m]
	 * Method to create the dialog for verify connection 
	 */
	private void createConnectivityDialog(){

		ConnectivityStatus conStat = new ConnectivityStatus(mContext);
		
		boolean isLocationOn = conStat.hasLocationON();
		boolean isWifiOn = conStat.hasWifiConnectionON();
		boolean isMobileOn = conStat.hasMobileConnectionON();
		boolean isMobileAvailable = conStat.isMobileConnectionAvailable();
		
		if(!isLocationOn || (isMobileAvailable && !isMobileOn) || !isWifiOn){
			mStatusDialog = new ConnectionDialog(this, isWifiOn, isMobileOn, isMobileAvailable, isLocationOn);
			mStatusDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					mStatusDialog = null;
				}
			});
			mStatusDialog.show();
		}
	}//[m] createConnectivityDialog()
	
	
	/**
	 * [m]
	 * Method to create a thread for delete service or Alarm, if it's running
	 * 
	 * @return the thread
	 */
	private Thread deleteService(){
		return new Thread(){
			@Override
			public void run(){

				ActivityManager actMan = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				List<RunningServiceInfo> list = actMan.getRunningServices(Integer.MAX_VALUE);
				
				
				for(RunningServiceInfo info : list){
					if(info.service.getClassName().equals("it.unipd.dei.esp1415.falldetector.service.AlarmService")){
						Intent intnt = new Intent(mContext, AlarmService.class);
						stopService(intnt);
						break;
					}
				}
				
				interrupt();
			}
		};
	}//[m] deleteService()
	
	/**
	 * [m]
	 * Method to download data from db
	 */
	private void downloadData(){
		DatabaseManager db = new DatabaseManager(mContext);
		
		ArrayList<Session> tmp = db.getSessionAsArray(DatabaseTable.COLUMN_SS_IS_ACTIVE + " > " + Session.FALSE + " OR " + DatabaseTable.COLUMN_SS_START_DATE  + " = 0" , null);

		addToArray(tmp,db.getSessionAsArray(DatabaseTable.COLUMN_SS_IS_ACTIVE + " = " + Session.FALSE + " AND " + DatabaseTable.COLUMN_SS_START_DATE  + " > 0", DatabaseTable.COLUMN_SS_START_DATE + " " + DatabaseManager.DESC));
		
		mMed.setDataSession(tmp);
	}//[m] downloadData()
	
	/**
	 * [m]
	 * Method to copy array into an array
	 * 
	 * @param tmp1 array that receive tmp2
	 * @param tmp2 array to copy into tmp1
	 */
	private void addToArray(ArrayList<Session> tmp1, ArrayList<Session> tmp2){
		for(int i = 0; i< tmp2.size(); i++){
			tmp1.add(tmp2.get(i));
		}
		
	}//[m] addToArray()
}
