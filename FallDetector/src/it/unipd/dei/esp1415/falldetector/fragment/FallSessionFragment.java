package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import it.unipd.dei.esp1415.falldetector.utility.Fall;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

public class FallSessionFragment extends Fragment {
	
	private static ArrayList<Session> mArray;
	private static int mIndex;
	private Mediator mMod;
	private static ViewHolder viewHolder;
	private Fall current;
    private static int mCurrentFall; //the position of the current Fall
	
	public FallSessionFragment(int mCurrentFall){
		this.mCurrentFall = mCurrentFall;
	}
	
	public FallSessionFragment(){
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

    	Log.i("Fall Session", "Fragment");
		View rootView = inflater.inflate(R.layout.activity_fall_fragment, container, false);
		
		mMod = new Mediator();
		mArray = mMod.getDataSession();

		viewHolder= new ViewHolder();
		
		viewHolder.lt = (LinearLayout) rootView.findViewById(R.id.fall_layout);
		viewHolder.sessionImage = (ImageView) rootView.findViewById(R.id.fall_thumbnail);
		viewHolder.sessionDate = (TextView) rootView.findViewById(R.id.fall_date);
		viewHolder.sessionTime = (TextView) rootView.findViewById(R.id.fall_time);
		viewHolder.sessionLatitude = (TextView) rootView.findViewById(R.id.latitude);
		viewHolder.sessionLongitude = (TextView) rootView.findViewById(R.id.longitude);
		viewHolder.sessionSended = (TextView) rootView.findViewById(R.id.sended);
		
		viewHolder.lt.setVisibility(View.VISIBLE);
		
		mIndex = mMod.getCurretnPosSession();
		
		Log.i("Fall Session", "Fragment "+mIndex);
		Log.i("Fall Session", "Fragment "+mCurrentFall);
		
		current = mArray.get(mIndex).getFall(mCurrentFall); //get the current Fall
		
		String[] tmp = current.dateTimeStampFallEven();

		viewHolder.sessionImage.setImageBitmap(mArray.get(mIndex).getBitmap());
		viewHolder.sessionDate.setText("Happen on "+tmp[0]);
		viewHolder.sessionTime.setText("at "+tmp[1]);
		viewHolder.sessionLatitude.setText("Latitude: "); //TODO INSERT METHOD TO GET THE LATITUDE
		viewHolder.sessionLongitude.setText("Longitude:"); //TODO INSERT METHOD TO GET THE LONGITUDE
		
		Log.i("Fall Session", "Fragment "+tmp[0]);
		Log.i("Fall Session", "Fragment "+tmp[1]);
		
		boolean send = current.isNotified();
		 
		String s;
		if(send) s = "Sended";
		else s = "Not sended";
		
		viewHolder.sessionSended.setText(s);
		
		//TODO INSERT METHOD TO GET DATA AND DISPLAY THEM INTO A GRAPH

		return rootView;
	}
	
	private class ViewHolder{
		private LinearLayout lt;
		private TextView sessionDate;
		private TextView sessionTime;
		private TextView sessionLongitude;
		private ImageView sessionImage;
		private TextView sessionLatitude;
		private TextView sessionSended;
	}
}