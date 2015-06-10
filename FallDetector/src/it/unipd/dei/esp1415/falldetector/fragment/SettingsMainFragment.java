package it.unipd.dei.esp1415.falldetector.fragment;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.SettingsActivity;
import it.unipd.dei.esp1415.falldetector.mail.MailSender;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
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
	
	//Constants
	public static final String DIVISOR_DATA = "&-&-&";
	
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
	
	public SettingsMainFragment() {
		
	}//[c] void constructor
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_main_fragment, container, false);
		
		animAlpha = AnimationUtils.loadAnimation(getActivity(),
			    R.anim.alpha);
		
		//Instantiate preferences and viewHolder
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
			//TODO
			viewHolder.mailSummary.setText("Android version not support it");
		}else{
			viewHolder.mailChk.setOnCheckedChangeListener(mailOnChkListener());
		}
		return view;
	}//[m] onCreateView


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
			
			//TODO save dialog for rotation
			private Dialog dialog;
			private ViewHold vh;
			
			private static final int START_INFO = 0;
			private static final int GENERAL_ERROR_INFO = 1;
			private static final int NOT_GMAIL_INFO = 2;
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mailChkValue = viewHolder.mailChk.isChecked();
				if(mailChkValue){
					dialog = new Dialog(getActivity());
					dialog.setContentView(R.layout.sign_in_layout);
					dialog.setTitle(R.string.log_in);
					dialog.setCanceledOnTouchOutside(false);
					
					vh= new ViewHold();
					
					vh.info = (TextView) dialog.findViewById(R.id.email_info);
					vh.account = (EditText) dialog.findViewById(R.id.email_addr);
					vh.password = (EditText) dialog.findViewById(R.id.password_mail);
					vh.cancel = (Button) dialog.findViewById(R.id.button_cancel);
					vh.save = (Button) dialog.findViewById(R.id.button_save);
					
					vh.cancel.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							mailChkValue = false;
							viewHolder.mailChk.setChecked(mailChkValue);
							mailAccount = null;
							mailDataConvertedBase64 = null;
							changeSummary(viewHolder.mailSummary);
							
						}// [m] onClick();
					});
					
					vh.save.setOnClickListener(saveListener());
					
					if(!dialog.isShowing()){
						dialog.show();
					}
					
					
				}else{
					mailAccount = null;
					mailDataConvertedBase64 = null;
					changeSummary(viewHolder.mailSummary);
				}
			}// [m]  onCheckedChanged()
			
			private void switchInfo(int toView){
				String toSet= "";
				
				switch (toView){
				case START_INFO:
					toSet= getResources().getString(R.string.on_start_sign_in).toString();
					break;
					
				case GENERAL_ERROR_INFO:
					toSet= getResources().getString(R.string.not_valid_value).toString();
					break;
					
				case NOT_GMAIL_INFO:
					toSet= getResources().getString(R.string.not_valid_address).toString();
					break;
				}
				
				vh.info.setText(toSet);
			}//[m] switchInfo()
			
			private OnClickListener saveListener(){
				return new OnClickListener() {
					
					@SuppressLint("DefaultLocale")
					@Override
					public void onClick(View v) {
						
						
						String accountVisible= (vh.account.getText().toString()).toLowerCase();
						String password = vh.password.getText().toString();
						
						accountVisible = accountVisible.replaceAll(" ", "");

						
						String[] controlEmail = accountVisible.split("@");
						
						if(controlEmail.length == 2 && controlEmail[1].equals("gmail.com")){
							String account = Base64.encodeToString(accountVisible.getBytes(), Base64.DEFAULT);
							password = Base64.encodeToString(password.getBytes(), Base64.DEFAULT);
							
							char c = 10;
							account = account.replaceAll(c+"", "");
							password = password.replaceAll(c+"", "");
							
							if((new MailSender(MailSender.GMAIL_PORT, MailSender.GMAIL_SERVER, account, password, false)).connect()){
								mailAccount = accountVisible;
								mailDataConvertedBase64 = account+DIVISOR_DATA+password;

								changeSummary(viewHolder.mailSummary);
								dialog.dismiss();
							}else{
								switchInfo(GENERAL_ERROR_INFO);
							}

							
						}else{
							switchInfo(NOT_GMAIL_INFO);
						}// if(.... .equals("gmail.com")) else...
					}// [m] onClick
					
				};// return
			}// [m] saveListener()
			
			class ViewHold{
				private TextView info;
				private EditText account;
				private EditText password;
				private Button save;
				private Button cancel;
			}//{c} ViewHold
		};// return
	}// [m] mailOnChkListener()
	
	private void changeSummary(TextView tx){
		String summary = "";
		switch (tx.getId()) {
		case R.id.advise_summary:
			if(adviseChkValue){
				summary = getResources().getString(R.string.time_set_at).toString();
				summary += fromMillToString(adviseTime);
			}else{
				summary = getResources().getString(R.string.no_active_adv).toString();
			}
			break;

		case R.id.accel_summary:
			switch(accelSelect){
			case ACCEL_STANDARD:
				summary = getResources().getString(R.string.standard).toString();
				break;
			case ACCEL_LOW:
				summary = getResources().getString(R.string.low).toString();
				summary += " " + getResources().getString(R.string.quality).toString();
				break;
			case ACCEL_MIDDLE:
				summary = getResources().getString(R.string.middle).toString();
				summary += " " + getResources().getString(R.string.quality).toString();
				break;
			case ACCEL_HIGH:
				summary = getResources().getString(R.string.high).toString();
				summary += " " + getResources().getString(R.string.quality).toString();
				break;
			}//switch on accel_summary
			
			summary += " " + getResources().getString(R.string.accelerometer).toString();
			break;
			
		case R.id.freq_summary:
			switch(frequencyValue){
			case FREQ_LOW:
				summary = getResources().getString(R.string.low).toString();
				break;
			case FREQ_MIDDLE:
				summary = getResources().getString(R.string.middle).toString();
				break;
			case FREQ_HIGH:
				summary = getResources().getString(R.string.high).toString();
				break;
			}//switch on freq_summary
			
			summary += " " + getResources().getString(R.string.frequency).toString();
			break;
		case R.id.mail_summary:
			if(mailChkValue){
				summary = mailAccount;
			}else{
				summary = getResources().getString(R.string.no_active_mail).toString();
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
	
}
