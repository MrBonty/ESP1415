package it.unipd.dei.esp1415.falldetector.extraview;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.SettingsActivity;
import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import it.unipd.dei.esp1415.falldetector.mail.MailSender;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Implementation of a dialog for Sign-in
 * 
 */
public class SignInDialog extends Dialog{

	private Context mContext;
	private String mAccount;
	private String mPassword;
	private String mData;
	private boolean hasData;
	
	private boolean mIsRestored;
	
	private int lastState;
	
	private ViewHolder vh;
	
	private static final int START_INFO = 0;
	private static final int GENERAL_ERROR_INFO = 1;
	private static final int NOT_GMAIL_INFO = 2;
	
	/**
	 * [c]
	 * Constructor for the dialog
	 * 
	 * @param context
	 */
	public SignInDialog(Context context) {
		super(context);
		mContext = context;
		hasData = false;
		mAccount = null;
		mPassword = null;
		mData = null;
		
		mIsRestored = false;
		lastState = START_INFO;
	}//[c] SignInDialog
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.sign_in_layout);
		setCanceledOnTouchOutside(false);
		setTitle(R.string.log_in);
		
		vh= new ViewHolder();
		
		vh.info = (TextView) findViewById(R.id.email_info);
		vh.account = (EditText) findViewById(R.id.email_addr);
		vh.password = (EditText) findViewById(R.id.password_mail);
		vh.cancel = (Button) findViewById(R.id.button_cancel);
		vh.save = (Button) findViewById(R.id.button_save);
		
		vh.cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				hasData = false;
				mAccount = null;
				mPassword = null;
				mData = null;
				
			}// [m] onClick();
		});
		
		vh.save.setOnClickListener(saveListener());

		if(mIsRestored){
			if(mAccount != null){
				vh.account.setText(mAccount);
				vh.account.setSelection(mAccount.length());
			}
			if(mPassword != null){
				vh.password.setText(mPassword);
				vh.password.setSelection(mPassword.length());
			}
			
			switchInfo(lastState);
			hasData = false;
			mAccount = null;
			mPassword = null;
			mData = null;
			mIsRestored = false;
		}
	}//[m] onCreate()
	
	/**
	 * private inner class to hold the view
	 *
	 */
	class ViewHolder{
		private TextView info;
		private EditText account;
		private EditText password;
		private Button save;
		private Button cancel;
	}//{c} ViewHold

	/**[m]
	 * Method to create lister for save button, it control all params
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private View.OnClickListener saveListener(){
		return new View.OnClickListener() {
			
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
					
					if(controlEmail[0].length() > 0 && (new MailSender(MailSender.GMAIL_PORT, MailSender.GMAIL_SERVER, account, password, false)).connect()){
						mAccount = accountVisible;
						mData = account+ SettingsActivity.DIVISOR_DATA + password;
						hasData = true;
						dismiss();
					}else{
						switchInfo(GENERAL_ERROR_INFO);
					}

					
				}else{
					switchInfo(NOT_GMAIL_INFO);
				}// if(.... .equals("gmail.com")) else...
			}// [m] onClick
			
		};// return
	}// [m] saveListener()
	
	/**
	 * [m]
	 * Method change advise on insert
	 * 
	 * @param toView the value of the advise to view
	 */
	private void switchInfo(int toView){
		String toSet= "";
		lastState = toView;
		
		switch (toView){
		case START_INFO:
			toSet= mContext.getResources().getString(R.string.on_start_sign_in).toString();
			break;
			
		case GENERAL_ERROR_INFO:
			toSet= mContext.getResources().getString(R.string.not_valid_value).toString();
			break;
			
		case NOT_GMAIL_INFO:
			toSet= mContext.getResources().getString(R.string.not_valid_address).toString();
			break;
		}
		
		vh.info.setText(toSet);
	}//[m] switchInfo()
	
	/**
	 * [m]
	 * @return true if the data is set on save 
	 */
	public boolean hasData(){
		return hasData;
	}//[m] hasData()
	
	/**
	 * [m]
	 * @return the account 
	 */
	public String getAccount(){
		return mAccount;
	}//[m] getAccount()
	
	/**
	 * [m]
	 * @return a base64 string with a divisor
	 */
	public String getData(){
		return mData;
	}//[m] getData()

	/**
	 * [m]
	 * Method to get data to save
	 * 
	 * @return a formatted string with data
	 */
	public String getSavingString(){
		return vh.account.getText().toString() + SettingsMainFragment.DIVISOR_ON_SAVE_STATE 
				+ vh.password.getText().toString() + SettingsMainFragment.DIVISOR_ON_SAVE_STATE 
				+ lastState; 
	}//[m] getSavingString()
	
	/**
	 * [m]
	 * Method to restore the data on dialog
	 * 
	 * @param account
	 * @param password
	 * @param state
	 */
	public void restoreValue(String account, String password, int state){
		mAccount = account;
		mPassword = password;
		lastState = state;
		mIsRestored = true;
	}//[m] restoreValue() 
}
