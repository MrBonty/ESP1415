package it.unipd.dei.esp1415.falldetector.extraview;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Dialog for start intent to open connection
 */
public class ConnectionDialog extends Dialog{

	private Context mCtx;
	private boolean isLocationOn = false; 
	private boolean isWifiOn = false; 
	private boolean isMobileOn = false; 
	private boolean isMobileAvailable = false; 
	
	private Mediator mMed;
	
	private ViewHolder viewHolder;
	
	/**[c]
	 * @param context
	 */
	public ConnectionDialog(Context context, boolean isWifiOn, boolean isMobileOn, boolean isMobileAvailable, boolean isLocationOn) {
		super(context);
		// TODO Auto-generated constructor stub
		mCtx = context;
		mMed = new Mediator();
		
		this.isLocationOn = isLocationOn;
		this.isWifiOn = isWifiOn;
		this.isMobileOn = isMobileOn;
		this.isMobileAvailable = isMobileAvailable;
	}//[c] ConnectionDialog()
	

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.connectivity_dialog);
		setCanceledOnTouchOutside(false);
		setTitle(R.string.con_dial_title);
	
		viewHolder = new ViewHolder();
		
		viewHolder.locationLL = (LinearLayout) findViewById(R.id.location_ll);
		viewHolder.locationBut = (Button) findViewById(R.id.location_button);
		viewHolder.mobileLL = (LinearLayout) findViewById(R.id.mobile_ll);
		viewHolder.mobileBut = (Button) findViewById(R.id.mobile_button); 
		viewHolder.wifiLL = (LinearLayout) findViewById(R.id.wifi_ll);
		viewHolder.wifiBut = (Button) findViewById(R.id.wifi_button);
		viewHolder.cancel = (Button) findViewById(R.id.cancel);
		
		if(!isMobileAvailable || isMobileOn){
			viewHolder.mobileLL.setVisibility(View.GONE);
		}
		
		if(isMobileAvailable  && isMobileOn){
			viewHolder.wifiLL.setVisibility(View.GONE);
		}
		
		if(isLocationOn){
			viewHolder.locationLL.setVisibility(View.GONE);
		}
		
		if(isWifiOn){
			viewHolder.wifiLL.setVisibility(View.GONE);
		}
		
		viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mMed.isConnectionControlled(true, true);
				mMed.isLocationControlled(true);
				dismiss();
			}//[m] onClick
		});
		
		viewHolder.locationBut.setOnClickListener(goToSettings());
		viewHolder.mobileBut.setOnClickListener(goToSettings());
		viewHolder.wifiBut.setOnClickListener(goToSettings());
		
	}//[m] onCreate()
	
	/**
	 * [m]
	 * Method to get a click listener to go to setting 
	 * 
	 * @return the {@link View.OnClickListener}
	 */
	private View.OnClickListener goToSettings(){
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.location_button:
					dismiss();
					
					mMed.isLocationControlled(true);
					
					Intent locationSettingIntent = new Intent(
	                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                mCtx.startActivity(locationSettingIntent);
					break;
				case R.id.mobile_button:
					dismiss();
					mMed.isConnectionControlled(true, true);
					
					Intent mobileSettingIntent = new Intent(
	                        android.provider.Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
	                mCtx.startActivity(mobileSettingIntent);
					break;
				case R.id.wifi_button:
					dismiss();
					mMed.isConnectionControlled(true, true);
					
					Intent wifiSettingIntent = new Intent(
	                        android.provider.Settings.ACTION_WIFI_SETTINGS);
	                mCtx.startActivity(wifiSettingIntent);
					
					break;
				}
			}//[m] onClick
		};
	}//[m] goToSettings()
	
	/**
	 * private inner class to hold the view
	 */
	private class ViewHolder{
		private LinearLayout locationLL;
		private Button locationBut;
		private LinearLayout mobileLL;
		private Button mobileBut;
		private LinearLayout wifiLL;
		private Button wifiBut;
		private Button cancel;
	}//{c} ViewHolder
	
}
