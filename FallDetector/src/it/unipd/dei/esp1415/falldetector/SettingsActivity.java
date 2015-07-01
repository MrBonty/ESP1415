package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.fragment.SettingsMainFragment;
import it.unipd.dei.esp1415.falldetector.fragment.SettingsSecFragment;
import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Activity for the settings of the application
 */
public class SettingsActivity extends ActionBarActivity {

	private static int mCurFrag;
	
	private static FragmentManager mManager;
	
	private static final String SAVE_FRAG = "saveFragment";
	public static final int FRAG_MAIN = 0;
	public static final int FRAG_SEC = 1;
	
	//Constants
	public static final String DIVISOR_DATA = "&-&-&";
	
	
	@SuppressLint("Recycle") //for instantiation of FragmentTransaction
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		
		mManager = getSupportFragmentManager();
		
        if (findViewById(R.id.frame_layout) != null) {

			changeFragment(FRAG_MAIN);

        }
        
        if(savedInstanceState != null){

			mCurFrag = savedInstanceState.getInt(SAVE_FRAG);
			
			if(mCurFrag == FRAG_SEC){
				changeFragment(mCurFrag);
			}
		}
	}//[m] onCreate()
	
	@Override
	protected void onResume() {
		super.onResume();
	}//[m] onResume()
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_FRAG, mCurFrag);
		
    }//[m] onSaveInstanceState()
	
	/**
	 * [m]
	 * Methods use for change fragment
	 * 
	 * @param frag the value of the fragment to set, use FRAG_MAIN or FRAG_SEC
	 */
	public static void changeFragment(int frag){
		Fragment tmp = null;
		
		switch (frag) {
		case FRAG_MAIN:
			
			mCurFrag = FRAG_MAIN;

			SettingsMainFragment firstFragment = null;
					
			// get fragment on screen 
			tmp = mManager.findFragmentById(R.id.frame_layout);
			
			//control if the fragment on screen is the right one
			if(tmp != null && tmp instanceof SettingsMainFragment){
				firstFragment = (SettingsMainFragment) tmp;
			}
			
			// if is not setted
			if(firstFragment == null){
				firstFragment = new SettingsMainFragment();
			}
			mManager.beginTransaction()
                    .replace(R.id.frame_layout, firstFragment).commit();
			break;

		case FRAG_SEC:
			mCurFrag = FRAG_SEC;
			
			SettingsSecFragment newFragment = null;
			
			tmp = mManager.findFragmentById(R.id.frame_layout);
			
			if(tmp != null && tmp instanceof SettingsSecFragment){
				newFragment = (SettingsSecFragment) tmp;
			}
			
			if(newFragment == null){
				newFragment = new SettingsSecFragment();
			}
			
			
			FragmentTransaction transaction = mManager.beginTransaction();

			transaction.replace(R.id.frame_layout, newFragment);
			transaction.addToBackStack(null);
			transaction.commit();
			break;
		}// switch(frag) ... case...
	}//[m] changeFragment()
}
