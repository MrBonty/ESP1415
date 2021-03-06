package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.fragment.DetailSessionFragment;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class DetailActivity extends ActionBarActivity {

	private Mediator mMed;
	private boolean mIsDual = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Configuration conf = getResources().getConfiguration(); //take the configuration of the device
		
		mMed = new Mediator();
		
		//check if the device is a tablet in landscape
		mIsDual = mMed.isLarge() && mMed.isLand(conf.orientation == Configuration.ORIENTATION_LANDSCAPE);
		
		if (mIsDual ) {
			finish();
			return;
		}
		
		setContentView(R.layout.activity_detail);
		
		Fragment detailSession = new DetailSessionFragment();
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.detail, detailSession);

		transaction.commit();
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

	
	@Override
	public void onBackPressed(){
		mMed.resetCurretnPosSessionFromBack();
		super.onBackPressed();
	}
}
