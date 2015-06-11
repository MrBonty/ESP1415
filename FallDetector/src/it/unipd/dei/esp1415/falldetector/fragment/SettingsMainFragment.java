package it.unipd.dei.esp1415.falldetector.fragment;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.SettingsActivity;
import it.unipd.dei.esp1415.falldetector.extraview.SignInDialog;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsMainFragment extends Fragment{
	
	private ViewHolder viewHolder;
	private SharedPreferences preferences;
	
	private Animation animAlpha;
	
	
	//stored value
	private boolean adviseChkValue;
	private int adviseTime;
	
	private int accelSelect;
	private int frequencyValue;

	private boolean mailChkValue;
	private String mailAccount;
	private String mailDataConvertedBase64;
	
	private SignInDialog mSignInDialog = null;
	
	//TODO APPROPRIETE DIALOG
	private Dialog mTimeSelectDialog = null;
	private Dialog mAccelDialog = null;
	
	private String savingString;
	
	private Context mContext;
	private Activity mAct;
	
	public static final String SAVE_ADVISE_CHK = "isToAdvise";
	public static final String SAVE_ADVISE_TIME = "adviseTime";
	
	public static final String SAVE_ACCEL = "accellerometer";
	public static final String SAVE_FREQ = "frequency";

	public static final String SAVE_MAIL_CHK = "isRapidMail";
	public static final String SAVE_MAIL_ACCOUNT = "account";
	public static final String SAVE_MAIL_DATABASE64 = "data";
	
	public static final int FREQ_LOW = 0;
	public static final int FREQ_MIDDLE = 1; 
	public static final int FREQ_HIGH = 2; 
	
	public static final int ACCEL_STANDARD = 0;
	public static final int ACCEL_LOW = 1;
	public static final int ACCEL_MIDDLE = 2; 
	public static final int ACCEL_HIGH = 3; 
	
	private static final int STANDARD_TIME = 8*3600*1000; //default time 8:00 am
	

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
		
		mAct =  getActivity();

		mContext = mAct.getBaseContext();
		
		animAlpha = AnimationUtils.loadAnimation(mAct,
			    R.anim.alpha);
		
		//Instantiate preferences and viewHolder
		preferences = PreferenceManager.getDefaultSharedPreferences(mAct);
		viewHolder = new ViewHolder();
		
		//Get handle to the views
		viewHolder.adviseChk = (CheckBox) view.findViewById(R.id.advise_checkbox);
		viewHolder.timeSetLay = (LinearLayout) view.findViewById(R.id.time_set);
		viewHolder.adviseSummary = (TextView) view.findViewById(R.id.advise_summary);
		
		viewHolder.accelSetLay = (LinearLayout) view.findViewById(R.id.accel_set);
		viewHolder.accelSummary = (TextView) view.findViewById(R.id.accel_summary);
		viewHolder.freqSetLay = (LinearLayout) view.findViewById(R.id.freq_set);
		viewHolder.freqSummary = (TextView) view.findViewById(R.id.freq_summary);
		
		viewHolder.mailChk = (CheckBox) view.findViewById(R.id.mail_checkbox);
		viewHolder.mailSetLay = (LinearLayout) view.findViewById(R.id.mail_list_set);
		viewHolder.mailSummary = (TextView) view.findViewById(R.id.mail_summary);
		
		return view;
	}//[m] onCreateView


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		int type = NO_DIAL;
		if(savedInstanceState != null){
			type = savedInstanceState.getInt(DIALOG_TYPE, NO_DIAL);
			savingString = savedInstanceState.getString(DIALOG_DATA);

		}

		//Get stored values from preferences
		adviseChkValue = preferences.getBoolean(SAVE_ADVISE_CHK, false);
		adviseTime = preferences.getInt(SAVE_ADVISE_TIME, STANDARD_TIME);
		
		accelSelect = preferences.getInt(SAVE_ACCEL, ACCEL_STANDARD);
		frequencyValue = preferences.getInt(SAVE_FREQ, FREQ_MIDDLE);
		
		mailChkValue = preferences.getBoolean(SAVE_MAIL_CHK, false);
		mailAccount = preferences.getString(SAVE_MAIL_ACCOUNT, null);
		mailDataConvertedBase64 = preferences.getString(SAVE_MAIL_DATABASE64, null);
		
		//Initiates views
		if(mailAccount == null || mailAccount.equals("")){
			mailChkValue = false;
		}
		
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
		changeSummary(viewHolder.accelSummary);
		changeSummary(viewHolder.freqSummary);
		changeSummary(viewHolder.mailSummary);
		
		viewHolder.accelSetLay.setOnClickListener(accelDialogListener());
		viewHolder.freqSetLay.setOnClickListener(accelDialogListener());
		viewHolder.mailSetLay.setOnClickListener(mailListener());
		
		viewHolder.adviseChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
		
		//TODO PROBLEM

		
		switch (type) {
		case SIGN_IN:
			createSignInDialog(true);
			break;
		case TIME:
			break;
		case ACCEL:
			break;
		}
	};
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		
		if(mSignInDialog != null && mSignInDialog.isShowing()){
			mSignInDialog.setOnDismissListener(null);
			outState.putInt(DIALOG_TYPE, SIGN_IN);
			savingString = mSignInDialog.getSavingString();
			mSignInDialog.dismiss();
			mSignInDialog = null;
			
		}else if(mTimeSelectDialog != null){
			outState.putInt(DIALOG_TYPE, TIME);
		}else if(mAccelDialog != null){
			outState.putInt(DIALOG_TYPE, ACCEL);
		}else{
			outState.putInt(DIALOG_TYPE, NO_DIAL);
		}
		
		outState.putString(DIALOG_DATA, savingString);
	};
	
	
	@Override
	public void onPause() {
		super.onPause();
		SharedPreferences.Editor editor = preferences.edit();
		
		editor.putBoolean(SAVE_ADVISE_CHK, adviseChkValue);
		editor.putInt(SAVE_ADVISE_TIME, adviseTime);
		editor.putInt(SAVE_ACCEL, accelSelect);
		editor.putInt(SAVE_FREQ, frequencyValue);
		editor.putBoolean(SAVE_MAIL_CHK, mailChkValue);
		
		editor.putString(SAVE_MAIL_ACCOUNT, mailAccount);
		editor.putString(SAVE_MAIL_DATABASE64, mailDataConvertedBase64);
		
		editor.commit();
		
	}//[m] OnPause()
	
	
	
	private OnClickListener timeDialogListener(){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
				v.startAnimation(animAlpha);
				
			}//[m] onClick
		};
	}// [m] timeDialogListener()
	
	private OnClickListener accelDialogListener(){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.startAnimation(animAlpha);
				switch (v.getId()) {
				case R.id.accel_set:
					//TODO
					break;
				case R.id.freq_set:
					//TODO
					break;
				}
			}//[m] onClick
		};
	}// [m] accelDialogListener()
	
	private OnClickListener mailListener(){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.startAnimation(animAlpha);
				SettingsActivity.changeFragment(SettingsActivity.FRAG_SEC);
			}//[m] onClick
		};//return
	}//[m] mailListener()
	


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
	
	private void changeSummary(TextView tx){
		String summary = "";
		switch (tx.getId()) {
		case R.id.advise_summary:
			if(adviseChkValue){
				summary = mContext.getResources().getString(R.string.time_set_at).toString();
				summary += fromMillToString(adviseTime);
			}else{
				summary = mContext.getResources().getString(R.string.no_active_adv).toString();
			}
			break;

		case R.id.accel_summary:
			switch(accelSelect){
			case ACCEL_STANDARD:
				summary = mContext.getResources().getString(R.string.standard).toString();
				break;
			case ACCEL_LOW:
				summary = mContext.getResources().getString(R.string.low).toString();
				summary += " " + mContext.getResources().getString(R.string.quality).toString();
				break;
			case ACCEL_MIDDLE:
				summary = mContext.getResources().getString(R.string.middle).toString();
				summary += " " + mContext.getResources().getString(R.string.quality).toString();
				break;
			case ACCEL_HIGH:
				summary = mContext.getResources().getString(R.string.high).toString();
				summary += " " + mContext.getResources().getString(R.string.quality).toString();
				break;
			}//switch on accel_summary
			
			summary += " " + mContext.getResources().getString(R.string.accelerometer).toString();
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
	
	private String fromMillToString(int time){
		time = (time / 1000)/60;
		int h = time/60;
		int m = time%60;
		
		String minute = (m<10)? "0"+m : m+"" ;
		
		return h + ":" + minute;
	}//[m]
	
	private class ViewHolder{

		private CheckBox adviseChk;
		private LinearLayout timeSetLay;
		private TextView adviseSummary;
		
		private LinearLayout accelSetLay;
		private TextView accelSummary;
		private LinearLayout freqSetLay;
		private TextView freqSummary;
		
		private CheckBox mailChk;
		private TextView mailSummary;
		private LinearLayout mailSetLay;
	}//{c} ViewHolder
	
	
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
			}
		});
		
		if(!mSignInDialog.isShowing()){
			mSignInDialog.show();
		}
	}
}
