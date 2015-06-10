package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class SettingsActivity extends ActionBarActivity {

	private static Fragment mFragment;
	private static int mCurFrag;
	
	private static FragmentTransaction mTransaction;
	
	private static final String SAVE_FRAG = "saveFragment";
	public static final int FRAG_MAIN = 0;
	public static final int FRAG_SEC = 1;
	
	@SuppressLint("Recycle") //for instantiation of FragmentTransaction
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		mTransaction = getSupportFragmentManager().beginTransaction();
		
		if(savedInstanceState != null){
			mCurFrag = savedInstanceState.getInt(SAVE_FRAG);
		}else{
			mCurFrag = FRAG_MAIN;
		}
		
		changeFragment(mCurFrag);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVE_FRAG, mCurFrag);
		
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public static void changeFragment(int frag){
		switch (frag) {
		case FRAG_MAIN:
			
			//TODO
			mFragment = new SettingsMainFragment();
			
			mCurFrag = frag;
			mTransaction.replace(R.id.frame_layout, mFragment);
			mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			mTransaction.commit();
			break;

		case FRAG_SEC:
			
			//TODO
			mFragment = null;
			
			mCurFrag = frag;
			mTransaction.replace(R.id.frame_layout, mFragment);
			mTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			mTransaction.commit();
			break;
		}
	}
	
}
