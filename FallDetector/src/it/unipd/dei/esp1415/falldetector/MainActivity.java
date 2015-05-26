package it.unipd.dei.esp1415.falldetector;

import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import it.unipd.dei.esp1415.falldetector.utility.ColorUtil;
import java.util.ArrayList;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		Configuration conf = getResources().getConfiguration();
		mContext = getApplication();

		Mediator med = new Mediator(mContext, this);
		
		boolean isLand = med.isLand(conf.orientation == Configuration.ORIENTATION_LANDSCAPE);
		boolean xlarge = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
	    boolean large = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
	    
		med.isLarge(xlarge || large);
		
		if(!med.hasDataSession()){

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
			
			tmp.get(0).setToActive(true);
			
			for(int i = tmp.size()-1; i >= 0; i--){
				if(i+1 == tmp.size()){
					tmp.get(i).setColorThumbnail(ColorUtil.imageColorSelector(null));
				}else{
					tmp.get(i).setColorThumbnail(ColorUtil.imageColorSelector(tmp.get(i+1)));
				}
			}

			med.setDataSession(tmp);
			//TODO END REMOVE ARRAY FOR TEST
		}
		
		FragmentManager manager = getSupportFragmentManager();
		
		Fragment listFragment = manager.findFragmentById(R.id.main_list);

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.main_list, listFragment);
		
		Fragment detailFragment = manager.findFragmentById(R.id.main_details);

		if(detailFragment != null && isLand){
			transaction.replace(R.id.main_details, detailFragment);
		}

		transaction.commit();
		

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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}