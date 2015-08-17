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
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private Context mContext;
	
	private Mediator mMed;

	private SessionDialog mDialog;
	
	// Constants
	public static final int SCREENLAYOUT_SIZE_XLARGE = 0x04; //For compatibility with API 8 same value of Configuration.SCREENLAYOUT_SIZE_XLARGE
	public static final String SAVE_ADD_DIALOG = "addDialog";
	
	public static final int NEW_SESSION_POSITION = 0;

	
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
		
		if(!mMed.hasDataSession()){
			
			DatabaseManager db = new DatabaseManager(mContext);
			mMed.setDataSession(db.getSessionAsArray(null, DatabaseTable.COLUMN_SS_START_DATE + " " + DatabaseManager.ASC));

			/*
			
			//TODO REMOVE ARRAY FOR TEST
			ArrayList<Session> tmp = new ArrayList<Session>();
			tmp.add(new Session("a"));
			tmp.add(new Session("b"));
			tmp.add(new Session("c"));
			tmp.add(new Session("d"));
			tmp.add(new Session("e"));
			tmp.add(new Session("f"));
			tmp.add(new Session("g"));
			tmp.add(new Session("h"));
			tmp.add(new Session("i"));
			tmp.add(new Session("l"));
			tmp.add(new Session("m"));
			tmp.add(new Session("n"));
			tmp.add(new Session("o"));
			tmp.add(new Session("p"));
			tmp.add(new Session("q"));
			
			//tmp.get(0).setToActive(true);
			
			for(int i = tmp.size()-1; i >= 0; i--){
				
				tmp.get(i).setColorThumbnail(ColorUtil.imageColorSelector());
				
			}

			mMed.setDataSession(tmp);
			
			//TODO END REMOVE ARRAY FOR TEST
			
			*/
		}
		
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

				Log.i("SERVER MSG", "Received");
				
				openAdd();
			}
		}
		
		deleteService().run();
		
	}//[m] onCreate()
	
	@Override
	protected void onResume() {
		super.onResume();

		
		if(!(mMed.isLocationControlled() && mMed.isConnectionControlled())){
			createConnectivityDialog();
		}
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
				ListSessionFragment.mAdapter.resetArray(NEW_SESSION_POSITION);
				
				ListSessionFragment.mAdapter.notifyDataSetChanged();
				mDialog = null;
				
				//TODO intent to start the new session;
			}//[m] onDismiss()
		});
	}//[m] createAddDialog()
	
	/**
	 * [m]
	 * @param isWifiOn
	 * @param isMobileOn
	 * @param isMobileAvailable
	 * @param isLocationOn
	 */
	private void createConnectivityDialog(){

		ConnectivityStatus conStat = new ConnectivityStatus(mContext);
		
		boolean isLocationOn = conStat.hasLocationON();
		boolean isWifiOn = conStat.hasWifiConnectionON();
		boolean isMobileOn = conStat.hasMobileConnectionON();
		boolean isMobileAvailable = conStat.isMobileConnectionAvailable();
		
		if(!isLocationOn || (isMobileAvailable && !isMobileOn) || !isWifiOn){
			ConnectionDialog dialog = new ConnectionDialog(this, isWifiOn, isMobileOn, isMobileAvailable, isLocationOn);
			dialog.show();
		}
	}//[m] createConnectivityDialog()
	
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
						
						Log.i("DELETE SERVICE", "Service stopped");
						break;
					}
				}
				
				interrupt();
			}
		};
	}
}
