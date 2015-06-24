package it.unipd.dei.esp1415.falldetector.extraview;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class FreqDialog extends Dialog{

	private int mState;

	private ViewHolder viewHolder;
	
	private boolean hasSave;
	
	public FreqDialog(Context context, int state) {
		super(context);
		mState = state;
		
		hasSave = false;
	}

	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.frequence_dialog);
		setCanceledOnTouchOutside(false);
		setTitle(R.string.select_freq);
		
		viewHolder = new ViewHolder();
		
		viewHolder.low = (RadioButton) findViewById(R.id.low_radio);
		viewHolder.middle = (RadioButton) findViewById(R.id.middle_radio);
		viewHolder.high = (RadioButton) findViewById(R.id.high_radio);
		viewHolder.cancel = (Button) findViewById(R.id.button_cancel);
		viewHolder.save = (Button) findViewById(R.id.button_save);
		
		viewHolder.low.setOnClickListener(radioOnClick());
		viewHolder.middle.setOnClickListener(radioOnClick());
		viewHolder.high.setOnClickListener(radioOnClick());
		
		viewHolder.cancel.setOnClickListener(cancelListener());
		viewHolder.save.setOnClickListener(cancelListener());
		
		switch (mState) {
		case SettingsMainFragment.FREQ_LOW:
			viewHolder.low.setChecked(true);
			break;
		case SettingsMainFragment.FREQ_MIDDLE:
			viewHolder.middle.setChecked(true);
			break;
		case SettingsMainFragment.FREQ_HIGH:
			viewHolder.high.setChecked(true);
			break;
		}
	}
	
	private class ViewHolder{
		private RadioButton low;
		private RadioButton middle;
		private RadioButton high;
		private Button save;
		private Button cancel;
	}
	
	private View.OnClickListener radioOnClick(){
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean checked = ((RadioButton) v).isChecked();
				if(checked){
					switch (v.getId()) {
					case R.id.low_radio:
						mState = SettingsMainFragment.FREQ_LOW;
						viewHolder.middle.setChecked(false);
						viewHolder.high.setChecked(false);
						break;
					case R.id.middle_radio:
						mState = SettingsMainFragment.FREQ_MIDDLE;

						viewHolder.low.setChecked(false);
						viewHolder.high.setChecked(false);
						break;
					case R.id.high_radio:
						mState = SettingsMainFragment.FREQ_HIGH;

						viewHolder.low.setChecked(false);
						viewHolder.middle.setChecked(false);
						break;
					}
				}
			}
		};
	}
	
	private android.view.View.OnClickListener cancelListener() {
		
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch(v.getId()){
				case R.id.button_save:
					hasSave = true;
				case R.id.button_cancel:
					dismiss();
					break;
				}
			}
		};
	}
	
	public boolean hasSaved(){
		return hasSave;
	}
	
	public int getState(){
		return mState;
	}
	
	public void restoreValue(int state){
		mState = state;
	}
}
