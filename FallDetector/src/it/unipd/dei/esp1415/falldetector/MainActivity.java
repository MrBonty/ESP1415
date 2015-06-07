package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.extraview.SessionDialog;
import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import it.unipd.dei.esp1415.falldetector.utility.ColorUtil;

import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private Context mContext;
	
	private Mediator mMed;

	private SessionDialog mDialog;
	
	public static final int SCREENLAYOUT_SIZE_XLARGE = 0x04; //For compatibility with API 8 same value of Configuration.SCREENLAYOUT_SIZE_XLARGE
	public static final String SAVE_ADD_DIALOG = "addDialog";
	
	public static final int NEW_SESSION_POSITION = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		Configuration conf = getResources().getConfiguration();
		mContext = getApplication();

		mMed = new Mediator(mContext, this);
		
		mMed.isLand(conf.orientation == Configuration.ORIENTATION_LANDSCAPE);
		boolean xlarge = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    
		mMed.isLarge(xlarge || large);
		
		if(!mMed.hasDataSession()){

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
		}
		
		FragmentManager manager = getSupportFragmentManager();
		
		Fragment listFragment = manager.findFragmentById(R.id.main_list);

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_list, listFragment);
		
		transaction.commit();
		
		if(savedInstanceState != null){
			String restore = savedInstanceState.getString(SAVE_ADD_DIALOG);
			if(restore != null && (!(restore.equals("")))){
				
				String[] splitted = restore.split(SessionDialog.DIVISOR);

				int pos = Integer.parseInt(splitted[0]);
				int color = Integer.parseInt(splitted[2]);
				
				mDialog = new SessionDialog(this, pos, true);
				mDialog.restoreValue(splitted[1], color);
				mDialog.show();
				mDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						ListSessionFragment.mAdapter.notifyDataSetChanged();
						mDialog = null;
					}
				});
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

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
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if(mDialog != null && mDialog.isShowing()){
        	outState.putString(SAVE_ADD_DIALOG, mDialog.getStringToSave());
		}
    }

	private void openAdd(){
		if(mMed.getDataSession().get(0).isActive()){
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage(R.string.dialog_message)
			       .setTitle(R.string.dialog_title);

			AlertDialog dialog = builder.create();
			
			dialog.show();
			
		}else{
			mDialog = new SessionDialog(this, NEW_SESSION_POSITION, true);
			mDialog.show();
			mDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					ListSessionFragment.mAdapter.notifyDataSetChanged();
					mDialog = null;
					
					//TODO intent to start the new session;
				}
			});
		}
	}
}
