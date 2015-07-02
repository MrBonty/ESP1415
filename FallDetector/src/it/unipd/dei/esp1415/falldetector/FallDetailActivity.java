package it.unipd.dei.esp1415.falldetector;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.utility.Fall;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FallDetailActivity extends ActionBarActivity {
	
	private static ArrayList<Session> mArray;
	private static int mIndex;
	private Mediator mMod;
	private static ViewHolder viewHolder;
	private Fall current;
    private static int mCurrentFall; //the position of the current Fall

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fall);
		
		mCurrentFall = getIntent().getExtras().getInt("nFall");
		
    	Log.i("Fall Session", "Activity");
		
		mMod = new Mediator();
		mArray = mMod.getDataSession();

		viewHolder= new ViewHolder();
		
		viewHolder.lt = (LinearLayout) findViewById(R.id.fall_layout);
		viewHolder.sessionImage = (ImageView) findViewById(R.id.fall_thumbnail);
		viewHolder.sessionDate = (TextView) findViewById(R.id.fall_date);
		viewHolder.sessionTime = (TextView) findViewById(R.id.fall_time);
		viewHolder.sessionLatitude = (TextView) findViewById(R.id.latitude);
		viewHolder.sessionLongitude = (TextView) findViewById(R.id.longitude);
		viewHolder.sessionSended = (TextView) findViewById(R.id.sended);
		
		viewHolder.lt.setVisibility(View.VISIBLE);
		
		mIndex = mMod.getCurretnPosSession();
		
		Log.i("Fall Session", "Activity "+mIndex);
		Log.i("Fall Session", "Activity "+mCurrentFall);
		
		current = mArray.get(mIndex).getFall(mCurrentFall); //get the current Fall
		
		String[] tmp = current.dateTimeStampFallEven();

		viewHolder.sessionImage.setImageBitmap(mArray.get(mIndex).getBitmap());
		viewHolder.sessionDate.setText("Happen on "+tmp[0]);
		viewHolder.sessionTime.setText("at "+tmp[1]);
		viewHolder.sessionLatitude.setText("Latitude: "); //TODO INSERT METHOD TO GET THE LATITUDE
		viewHolder.sessionLongitude.setText("Longitude:"); //TODO INSERT METHOD TO GET THE LONGITUDE
		
		Log.i("Fall Session", "Activity "+tmp[0]);
		Log.i("Fall Session", "Activity "+tmp[1]);
		
		boolean send = current.isNotified();
		 
		String s;
		if(send) s = "Sended";
		else s = "Not sended";
		
		viewHolder.sessionSended.setText(s);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fall, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_fall) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * private inner class to hold the view
	 */
	private class ViewHolder{
		private LinearLayout lt;
		private TextView sessionDate;
		private TextView sessionTime;
		private TextView sessionLongitude;
		private ImageView sessionImage;
		private TextView sessionLatitude;
		private TextView sessionSended;
	}//{c} ViewHolder
}
