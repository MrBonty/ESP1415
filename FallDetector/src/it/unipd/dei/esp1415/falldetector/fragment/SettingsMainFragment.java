package it.unipd.dei.esp1415.falldetector.fragment;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.SettingsActivity;
import it.unipd.dei.esp1415.falldetector.extraview.FreqDialog;
import it.unipd.dei.esp1415.falldetector.extraview.SignInDialog;
import it.unipd.dei.esp1415.falldetector.service.AlarmServiceHelper;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsMainFragment extends Fragment{

	private ViewHolder viewHolder;
	private SharedPreferences preferences;

	private Animation animAlpha;


	//stored value
	private boolean adviseChkValue;
	private int adviseHourOfDay;
	private int adviseMinutes;

	private int frequencyValue;

	private boolean mailChkValue;
	private String mailAccount;
	private String mailDataConvertedBase64;

	private SignInDialog mSignInDialog = null;
	private TimePickerDialog mTimeSelectDialog = null;
	private FreqDialog mAccelDialog = null;

	private String savingString;

	private Context mContext;
	private Activity mAct;
	
	private int mSessionDuration;
	
	private boolean isAdiviseChanged = false;
	private boolean isDialogTimeActive = false;

	// Constants
	public static final String SAVE_ADVISE_CHK = "isToAdvise";
	public static final String SAVE_ADVISE_TIME = "adviseTime";
	
	public static final String SAVE_SESSION_DURATION = "sessionDuration";

	public static final String SAVE_ACCEL = "accellerometer";
	public static final String SAVE_FREQ = "frequency";

	public static final String SAVE_MAIL_CHK = "isRapidMail";
	public static final String SAVE_MAIL_ACCOUNT = "account";
	public static final String SAVE_MAIL_DATABASE64 = "data";

	public static final int FREQ_LOW = 5;
	public static final int FREQ_MIDDLE = 7; 
	public static final int FREQ_HIGH = 9; 

	public static final String STANDARD_TIME = "8:00"; //default time 8:00 am
	
	public static final int MIN_DURATION = 1;
	public static final int MAX_DURATION = 24;
	public static final int DEFAULT_DURATION = 12;

	public static final String DIVISOR_ON_SAVE_STATE = "&&&";
	private static final String DIALOG_TYPE = "type_dial";
	private static final int SIGN_IN = 0;
	private static final int TIME = 1;
	private static final int ACCEL = 2;
	private static final int NO_DIAL = -1;
	private static final String DIALOG_DATA = "data_dialog";

	public SettingsMainFragment() {

	}//[c] void constructor

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_main_fragment, container, false);

		//Instantiate viewHolder
		viewHolder = new ViewHolder();

		//Get handle to the views
		viewHolder.adviseChk = (CheckBox) view.findViewById(R.id.advise_checkbox);
		viewHolder.timeSetLay = (LinearLayout) view.findViewById(R.id.time_set);
		viewHolder.adviseSummary = (TextView) view.findViewById(R.id.advise_summary);
		
		viewHolder.freqSetLay = (LinearLayout) view.findViewById(R.id.freq_set);
		viewHolder.freqSummary = (TextView) view.findViewById(R.id.freq_summary);

		viewHolder.mailChk = (CheckBox) view.findViewById(R.id.mail_checkbox);
		viewHolder.mailSetLay = (LinearLayout) view.findViewById(R.id.mail_list_set);
		viewHolder.mailSummary = (TextView) view.findViewById(R.id.mail_summary);
		
		viewHolder.minus = (Button) view.findViewById(R.id.minus);
		viewHolder.plus = (Button) view.findViewById(R.id.plus);
		viewHolder.duration = (TextView) view.findViewById(R.id.number);

		return view;
	}//[m] onCreateView()


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mAct =  this.getActivity();
		mContext = mAct.getBaseContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(mAct);
		animAlpha = AnimationUtils.loadAnimation(mAct, R.anim.alpha);
		
		int type = NO_DIAL;
		if(savedInstanceState != null){
			type = savedInstanceState.getInt(DIALOG_TYPE, NO_DIAL);
			savingString = savedInstanceState.getString(DIALOG_DATA);

		}

		//Get stored values from preferences
		adviseChkValue = preferences.getBoolean(SAVE_ADVISE_CHK, false);
		splitTime(preferences.getString(SAVE_ADVISE_TIME, STANDARD_TIME));
		
		mSessionDuration = preferences.getInt(SAVE_SESSION_DURATION, DEFAULT_DURATION);

		frequencyValue = preferences.getInt(SAVE_FREQ, FREQ_MIDDLE);

		mailChkValue = preferences.getBoolean(SAVE_MAIL_CHK, false);
		mailAccount = preferences.getString(SAVE_MAIL_ACCOUNT, null);
		mailDataConvertedBase64 = preferences.getString(SAVE_MAIL_DATABASE64, null);

		//Initiates views
		if(mailAccount == null || mailAccount.equals("")){
			mailChkValue = false;
		}
		
		viewHolder.minus.setOnClickListener(durationPickerListener());
		viewHolder.plus.setOnClickListener(durationPickerListener());
		viewHolder.duration.setText(mSessionDuration + "");
		
		switch (type) {
		case SIGN_IN:
			mailChkValue = true;
			break;
		case TIME:
			adviseChkValue = true;
			break;
		}

		viewHolder.adviseChk.setChecked(adviseChkValue);
		viewHolder.mailChk.setChecked(mailChkValue);

		if(adviseChkValue){
			viewHolder.timeSetLay.setOnClickListener(timeDialogListener());
		}else{
			viewHolder.timeSetLay.setBackgroundColor(getResources().getColor(R.color.ligth_gray));
		}

		changeSummary(viewHolder.adviseSummary);
		changeSummary(viewHolder.freqSummary);
		changeSummary(viewHolder.mailSummary);

		viewHolder.freqSetLay.setOnClickListener(accelDialogListener());
		viewHolder.mailSetLay.setOnClickListener(mailListener());

		viewHolder.adviseChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAdiviseChanged = true;
				
				adviseChkValue = viewHolder.adviseChk.isChecked();
				if(adviseChkValue){
					viewHolder.timeSetLay.setBackgroundColor(Color.TRANSPARENT);
					viewHolder.timeSetLay.setOnClickListener(timeDialogListener());
				}else{
					viewHolder.timeSetLay.setBackgroundColor(getResources().getColor(R.color.ligth_gray));
					viewHolder.timeSetLay.setClickable(false);
				}
				changeSummary(viewHolder.adviseSummary);
			}
		});
		if(Build.VERSION.SDK_INT <= 8){
			viewHolder.mailChk.setVisibility(View.GONE);
			//TODO check for a better solution (problem with asyngTask and thread)
			viewHolder.mailSummary.setText("Android version not support it");
		}else{
			viewHolder.mailChk.setOnCheckedChangeListener(mailOnChkListener());
		}


		switch (type) {
		case SIGN_IN:
			createSignInDialog(true);
			break;
		case TIME:
			createTimeDialog(true, savedInstanceState);
			break;
		case ACCEL:
			if(savedInstanceState != null){
				createFreqDialog(true, savedInstanceState.getInt(SAVE_FREQ));
			}
			break;
		}
	}//[m] onActivityCreated()

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		if(mSignInDialog != null && mSignInDialog.isShowing()){
			mSignInDialog.setOnDismissListener(null);
			outState.putInt(DIALOG_TYPE, SIGN_IN);
			savingString = mSignInDialog.getSavingString();
			mSignInDialog.dismiss();
			mSignInDialog = null;

		}else if(mTimeSelectDialog != null && mTimeSelectDialog.isShowing()){
			
			outState.putAll(mTimeSelectDialog.onSaveInstanceState());
			outState.putInt(DIALOG_TYPE, TIME);
			mTimeSelectDialog.dismiss();
			mTimeSelectDialog = null;
			isDialogTimeActive = true;
			
		}else if(mAccelDialog != null && mAccelDialog.isShowing()){
			mAccelDialog.setOnDismissListener(null);
			outState.putInt(DIALOG_TYPE, ACCEL);
			outState.putInt(SAVE_FREQ, mAccelDialog.getState());
			mAccelDialog.dismiss();
			mAccelDialog = null;
		}else{
			outState.putInt(DIALOG_TYPE, NO_DIAL);
		}

		outState.putString(DIALOG_DATA, savingString);
		
	}//[m] onSaveInstanceState()


	@Override
	public void onPause() {
		super.onPause();

		SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean(SAVE_ADVISE_CHK, adviseChkValue);
		editor.putBoolean(SAVE_MAIL_CHK, mailChkValue);
		
		editor.putString(SAVE_ADVISE_TIME, getTimeString());
		editor.putInt(SAVE_FREQ, frequencyValue);
		
		editor.putInt(SAVE_SESSION_DURATION, mSessionDuration);

		editor.putString(SAVE_MAIL_ACCOUNT, mailAccount);
		editor.putString(SAVE_MAIL_DATABASE64, mailDataConvertedBase64);
		editor.commit();

		if(isAdiviseChanged && !isDialogTimeActive){
			LocalBroadcastManager broadcasting = LocalBroadcastManager.getInstance(mContext);
			AlarmServiceHelper broadcaster = new AlarmServiceHelper();
			
			if(adviseChkValue){
				broadcasting.registerReceiver(broadcaster, new IntentFilter(AlarmServiceHelper.GET_A_NEW_ALARM));
				Intent i = new Intent(AlarmServiceHelper.GET_A_NEW_ALARM);
				broadcasting.sendBroadcast(i);
				Log.i("SEND", "sended");
			} else {
				broadcasting.registerReceiver(broadcaster, new IntentFilter(AlarmServiceHelper.DELETE_ALARM));
				Intent i = new Intent(AlarmServiceHelper.DELETE_ALARM);
				broadcasting.sendBroadcast(i);
			}
			
			broadcasting.unregisterReceiver(broadcaster);
		}
	}//[m] OnPause()
	
	/**[m]
	 * Method to create lister for button minus and plus
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private OnClickListener durationPickerListener(){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				switch (v.getId()) {
				case R.id.minus:
					mSessionDuration--;
					viewHolder.duration.setText(mSessionDuration + "");
					
					if (mSessionDuration == MIN_DURATION){
						viewHolder.minus.setClickable(false);
					}
					
					if(mSessionDuration < MAX_DURATION && !viewHolder.plus.isClickable()){
						viewHolder.plus.setClickable(true);
					}
					break;
				case R.id.plus:
					mSessionDuration ++;
					viewHolder.duration.setText(mSessionDuration + "");
					
					if (mSessionDuration == MAX_DURATION){
						viewHolder.plus.setClickable(false);
					}
					
					if(mSessionDuration > MIN_DURATION && !viewHolder.minus.isClickable()){
						viewHolder.minus.setClickable(true);
					}
					break;
				}
				
			}
		};
	}//[m] durationPickerListener()

	/**[m]
	 * Method to create lister for time view
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private OnClickListener timeDialogListener(){
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				//TODO
				v.startAnimation(animAlpha);
				
				createTimeDialog(false, null);
			}//[m] onClick

		};
	}// [m] timeDialogListener()
	
	/**[m]
	 * Method to create lister for frequency view
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private OnClickListener accelDialogListener(){
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(animAlpha);
				
				createFreqDialog(false, NO_DIAL);
			}//[m] onClick
		};
	}// [m] accelDialogListener()

	/**[m]
	 * Method to create lister for mail view
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private OnClickListener mailListener(){
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.startAnimation(animAlpha);
				SettingsActivity.changeFragment(SettingsActivity.FRAG_SEC);
			}//[m] onClick
		};//return
	}//[m] mailListener()


	/**
	 * [m]
	 * Method to get a OnCheckedChangeListener for mail checkbox
	 * 
	 * @return the {@link OnCheckedChangeListener}
	 */
	private OnCheckedChangeListener mailOnChkListener() {

		return new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mailChkValue = viewHolder.mailChk.isChecked();
				if(mailChkValue){
					createSignInDialog(false);
				}else{
					mailAccount = null;
					mailDataConvertedBase64 = null;
					changeSummary(viewHolder.mailSummary);
				}
			}// [m]  onCheckedChanged()
		};// return
	}// [m] mailOnChkListener()

	/**
	 * [m]
	 * Method to change test to summary obj
	 * 
	 * @param tx the text view to change
	 */
	private void changeSummary(TextView tx){
		String summary = "";
		switch (tx.getId()) {
		case R.id.advise_summary:
			if(adviseChkValue){
				summary = mContext.getResources().getString(R.string.time_set_at).toString();
				summary += getTimeString();
			}else{
				summary = mContext.getResources().getString(R.string.no_active_adv).toString();
			}
			break;

		case R.id.freq_summary:
			switch(frequencyValue){
			case FREQ_LOW:
				summary = mContext.getResources().getString(R.string.low).toString();
				break;
			case FREQ_MIDDLE:
				summary = mContext.getResources().getString(R.string.middle).toString();
				break;
			case FREQ_HIGH:
				summary = mContext.getResources().getString(R.string.high).toString();
				break;
			}//switch on freq_summary

			summary += " " + mContext.getResources().getString(R.string.frequency).toString();
			break;
		case R.id.mail_summary:
			if(mailChkValue && mailAccount != null){
				summary = mailAccount;
			}else{
				summary = mContext.getResources().getString(R.string.no_active_mail).toString();
			}
			break;
		}//switch on textView

		tx.setText(summary);
	}// [m] changeSummary()

	/**
	 * private inner class for hold the view
	 */
	private class ViewHolder{

		private CheckBox adviseChk;
		private LinearLayout timeSetLay;
		private TextView adviseSummary;
		
		private Button minus;
		private Button plus;
		private TextView duration;

		private LinearLayout freqSetLay;
		private TextView freqSummary;

		private CheckBox mailChk;
		private TextView mailSummary;
		private LinearLayout mailSetLay;
	}//{c} ViewHolder

	/**
	 * [m]
	 * Method to create the dialog for get and test sign-in data
	 * 
	 * @param toRestore set to true if the dialog has to restore
	 */
	private void createSignInDialog(boolean toRestore){

		mSignInDialog = new SignInDialog(mAct);

		if(toRestore){
			String[] restoring = savingString.split(DIVISOR_ON_SAVE_STATE);
			String account = restoring[0];
			String password = restoring[1];
			int state = Integer.parseInt(restoring[2]);

			savingString = null;

			mSignInDialog.restoreValue(account, password, state);
		}

		// set of OnDismissListener fo the dialog
		mSignInDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {

				if(mSignInDialog.hasData()){

					mailAccount = mSignInDialog.getAccount();
					mailDataConvertedBase64 = mSignInDialog.getData();

					changeSummary(viewHolder.mailSummary);
				}else{
					mailChkValue = false;
					viewHolder.mailChk.setChecked(mailChkValue);
					mailAccount = null;
					mailDataConvertedBase64 = null;
					changeSummary(viewHolder.mailSummary);

				}

				mSignInDialog = null;
			}//[m] onDismiss
		});

		if(!mSignInDialog.isShowing()){
			mSignInDialog.show();
		}
	}//[m] createSignInDialog()

	/**
	 * [m]
	 * Method for create a time-picker dialog 
	 * 
	 * @param restore set to true if the dialog has to restore
	 * @param savedIstanceState the bundle to restore the dialog, set null if restore is false
	 */
	private void createTimeDialog(boolean restore, Bundle savedIstanceState){
		isDialogTimeActive = true;
		// creation of the callback for dialog
		OnTimeSetListener callBack = new OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				
				if(adviseHourOfDay != hourOfDay || adviseMinutes != minute){
					isAdiviseChanged = true;
					
					adviseHourOfDay = hourOfDay;
					adviseMinutes = minute;
					
					changeSummary(viewHolder.adviseSummary);
				}
				isDialogTimeActive = false;
				mTimeSelectDialog = null;
			}//[m] onTimeSet()
		}; 
		
		mTimeSelectDialog = new TimePickerDialog(mAct, callBack, adviseHourOfDay, adviseMinutes, true);
		mTimeSelectDialog.show();
		
		if(restore){
			mTimeSelectDialog.onRestoreInstanceState(savedIstanceState);
		}
		
	}//[m] createTimeDialog()
	
	/**
	 * [m]
	 * Method to make a string from hour and minute
	 * 
	 * @return a string as hh:mm
	 */
	private String getTimeString(){
		return adviseHourOfDay + ":" + ((adviseMinutes < 10) ? "0" : "") + adviseMinutes ;
	}//[m] getTimeString()
	
	/**
	 * [m]
	 * Method to split time in minute and hour from hh:mm
	 * 
	 * @param time to split
	 */
	private void splitTime(String time){
		String[] tmp = time.split(":");
		
		adviseHourOfDay = Integer.parseInt(tmp[0]);
		adviseMinutes = Integer.parseInt(tmp[1]);
		
	}//[m] splitTime()
	
	/**
	 * [m]
	 * Method to create a dialog for set the frequency
	 * 
	 * @param restore set to true if the dialog has to restore
	 * @param restoreValue the value 
	 */
	private void createFreqDialog(boolean restore, int restoreValue){
		mAccelDialog = new FreqDialog(mAct, frequencyValue);
		
		if(restore){
			mAccelDialog.restoreValue(restoreValue);
		}
		
		mAccelDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(mAccelDialog.hasSaved()){
					frequencyValue = mAccelDialog.getState();
				}
				changeSummary(viewHolder.freqSummary);
				mAccelDialog = null;
			}
		});
		
		mAccelDialog.show();
	}//[m] createFreqDialog()
	
}
