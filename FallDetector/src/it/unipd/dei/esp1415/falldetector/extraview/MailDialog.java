package it.unipd.dei.esp1415.falldetector.extraview;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.fragment.SettingsSecFragment;
import it.unipd.dei.esp1415.falldetector.utility.MailAddress;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MailDialog extends Dialog{

	private Context mContext;
	
	private boolean mToMod;
	private int mPos;
	private ArrayList<MailAddress> mArray;
	
	private boolean isRestored;
	private String mName;
	private String mSurname;
	private String mAddress;
	private int mState;
	
	private ViewHolder viewHolder;
	
	private final static int START_INFO = 0;
	private final static int ADD_NAME_INFO = 1;
	private final static int NO_VALID_MAIL_INFO = 2;
	
	public MailDialog(Context context, ArrayList<MailAddress> array, int position) {
		super(context);

		mContext = context;
		
		mToMod = position != SettingsSecFragment.NEW_MAIL;
		mPos = position;
		
		isRestored = false;
		mName = null;
		mSurname = null;
		mAddress = null;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.mail_dialog);
		setCanceledOnTouchOutside(false);
		setTitle(R.string.add_mail);
	
		viewHolder = new ViewHolder();

		viewHolder.info = (TextView) findViewById(R.id.add_mail_info);
		viewHolder.name  = (EditText) findViewById(R.id.name_edit);
		viewHolder.surname  = (EditText) findViewById(R.id.surname_edit);
		viewHolder.address = (EditText) findViewById(R.id.address_mail_edit);
		viewHolder.cancel = (Button) findViewById(R.id.button_cancel);
		viewHolder.save = (Button) findViewById(R.id.button_save);
		
		if(mToMod && !isRestored){
			mName = mArray.get(mPos).getName();
			mSurname = mArray.get(mPos).getSurname();
			mAddress = mArray.get(mPos).getAddress();
			
			mState = START_INFO;
		}
	
		if(isRestored || mToMod){
			viewHolder.name.setText(mName);
			viewHolder.surname.setText(mSurname);
			viewHolder.address.setText(mAddress);
			
			viewHolder.name.setSelection(mName.length());
			viewHolder.surname.setSelection(mSurname.length());
			viewHolder.address.setSelection(mAddress.length());
			
			mName = null;
			mSurname = null;
			mAddress = null;
		}else{
			mState = START_INFO;
		}
		
		changeInfo();

		viewHolder.cancel.setOnClickListener(cancelListener());
		viewHolder.save.setOnClickListener(saveListener());

	}

	private android.view.View.OnClickListener saveListener() {

		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mName = viewHolder.name.getText().toString();
				mSurname = viewHolder.surname.getText().toString(); 
				mAddress = viewHolder.address.getText().toString();
				if(mName.length() > 0 || mSurname.length() > 0){
					String[] toControl = mAddress.split("@");
					
					if(toControl.length == 2 && (toControl[1].split(".")).length >= 2){
						DatabaseManager db = new DatabaseManager(mContext);
						if(mToMod){
							MailAddress tmp = mArray.get(mPos);
							
							String name = tmp.getName();
							String surname = tmp.getSurname();
							String address = tmp.getAddress();
							
							if(!(name.equals(name)) || !(surname.equals(mSurname)) || !(address.equals(mAddress))){
								tmp.setName(mName);
								tmp.setSurname(mSurname);
								tmp.setAddress(mAddress);
								
								db.upgradeAMailAddress(tmp);
							
								Toast.makeText(mContext, R.string.modify_made, Toast.LENGTH_SHORT).show();
							}else{
								
								Toast.makeText(mContext, R.string.modify_no_made, Toast.LENGTH_SHORT).show();
							}
						}else{
							MailAddress tmp = new MailAddress(mAddress);
							tmp.setName(mName);
							tmp.setSurname(mSurname);
							
							tmp.setId(db.insertAMailAddress(tmp));
							
							mArray.add(tmp);
							
							Toast.makeText(mContext, R.string.add_new_mail, Toast.LENGTH_SHORT).show();
						}
					}else{
						mState = NO_VALID_MAIL_INFO;
						changeInfo();
					}
				}else{
					mState = ADD_NAME_INFO;
					changeInfo();
					mName = null;
					mSurname = null;
					mAddress = null;
				}	
			}
		};
	}

	private android.view.View.OnClickListener cancelListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		};
	}
	
	private void changeInfo(){
		switch (mState) {
		case START_INFO:
			viewHolder.info.setText(R.string.address_edit_info_start);
			break;
		case ADD_NAME_INFO:
			viewHolder.info.setText(R.string.address_edit_info_add);
			break;
		case NO_VALID_MAIL_INFO:
			viewHolder.info.setText(R.string.address_edit_info_no);
			break;
		}
	}

	private class ViewHolder{
		private TextView info;
		private EditText name;
		private EditText surname;
		private EditText address;
		private Button save;
		private Button cancel;
	}
	
	public void restoreValue(String name, String surname, String address, int state){
		isRestored = true;
		mName = name;
		mSurname = surname;
		mAddress = address;
		mState = state;
	}
	
	public String getSavingString(){
		String name = viewHolder.name.getText().toString();
		String surname = viewHolder.surname.getText().toString(); 
		String address = viewHolder.address.getText().toString();
		
		return name + SettingsSecFragment.DIVISOR + surname + 
				SettingsSecFragment.DIVISOR + address + 
				SettingsSecFragment.DIVISOR + mState;
	}
}