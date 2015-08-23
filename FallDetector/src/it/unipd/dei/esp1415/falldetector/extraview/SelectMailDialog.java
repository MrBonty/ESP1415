package it.unipd.dei.esp1415.falldetector.extraview;

import it.unipd.dei.esp1415.falldetector.R;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SelectMailDialog extends Dialog{
	
	private Context mCtx;
	
	private AccountManager mAccMan;
	private Account[] mAccounts;

	private ViewHolder viewHolder;
	
	private static final String GOOGLE_TYPE = "com.google";

	/**[c]
	 * @param context
	 */
	public SelectMailDialog(Context context) {
		super(context);
		mCtx = context;
		mAccMan = AccountManager.get(context);
		mAccounts = mAccMan.getAccountsByType(GOOGLE_TYPE);
		viewHolder = new ViewHolder();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		viewHolder.radio = (RadioGroup) findViewById(R.id.mail_radiogroup);
		viewHolder.cancel = (Button) findViewById(R.id.button_cancel);
		viewHolder.save = (Button) findViewById(R.id.button_save);
		
		
		for(int i = 0; i< mAccounts.length; i++){
			RadioButton tmp = new RadioButton(mCtx);
			tmp.setText(mAccounts[i].name);
			viewHolder.radio.addView(tmp);
		}
		
		viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();				
			}
		});
		
		viewHolder.save.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		super.onCreate(savedInstanceState);
	}
	
	
	
	private class ViewHolder{
		RadioGroup radio;
		Button save;
		Button cancel;
	}

}
