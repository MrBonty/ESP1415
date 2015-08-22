package it.unipd.dei.esp1415.falldetector;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.utility.AccelData;
import it.unipd.dei.esp1415.falldetector.utility.ChartView;
import it.unipd.dei.esp1415.falldetector.utility.Fall;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
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
	private static Context mContext;
    private DatabaseManager dm;
    
	private ChartView xChart;
	private ChartView yChart;
	private ChartView zChart;
	
	private static float[] x_data;
	private static float[] y_data;
	private static float[] z_data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fall);
		
		mCurrentFall = getIntent().getExtras().getInt("nFall");
		
    	Log.i("Fall Session", "Activity");
		
		mMod = new Mediator();
		mArray = mMod.getDataSession();
		
		mContext = mMod.getContext();
		dm = new DatabaseManager(mContext);

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
		
		//TODO substitute with mArray.get(mIndex).getFall(mCurrentFall); 
		current = dm.getFallForSessionAsArray(mArray.get(mIndex).getId(), DatabaseTable.COLUMN_FE_DATE).get(mCurrentFall); //get the current Fall
		
		String[] tmp = current.dateTimeStampFallEven();

		viewHolder.sessionImage.setImageBitmap(mArray.get(mIndex).getBitmap());
		viewHolder.sessionDate.setText("Happen on "+tmp[0]);
		viewHolder.sessionTime.setText("at "+tmp[1]);
		viewHolder.sessionLatitude.setText("Latitude: "+current.getLatitude()); 
		viewHolder.sessionLongitude.setText("Longitude: "+current.getLongitude()); 
		
		Log.i("Fall Session", "Activity "+tmp[0]);
		Log.i("Fall Session", "Activity "+tmp[1]);
		
		boolean send = current.isNotified();
		 
		String s;
		if(send) s = "Sended";
		else s = "Not sended";
		
		viewHolder.sessionSended.setText(s);
		
		x_data = new float[1000];
		y_data = new float[1000];
		z_data = new float[1000];
		
		ArrayList<AccelData> temp = dm.getAccelDataAsArrayForAFall(current.getId(), DatabaseTable.COLUMN_AC_TS);
		
		for(int i=0; i<temp.size(); i++){
			x_data[i] = (float)temp.get(i).getX();
			y_data[i] = (float)temp.get(i).getY();
			z_data[i] = (float)temp.get(i).getZ();
		}
		
		xChart = (ChartView) findViewById(R.id.chart_x_axis);
		xChart.setChartData(x_data, x_data.length);
		
		yChart = (ChartView) findViewById(R.id.chart_y_axis);
		yChart.setChartData(y_data, y_data.length);
		
		zChart = (ChartView) findViewById(R.id.chart_z_axis);
		zChart.setChartData(z_data, z_data.length);
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
