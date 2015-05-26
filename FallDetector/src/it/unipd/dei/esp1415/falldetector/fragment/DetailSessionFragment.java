package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailSessionFragment extends Fragment {
	
	private static ArrayList<Session> mArray;
	private static int mIndex;
	private Mediator mMod; 
	
	private static LayoutInflater mInflater;
	private static ViewGroup mContainer;
	
	private ViewHolder viewHolder;
	

	public DetailSessionFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.activity_detail_fragment,
				container, false);
		
		mMod = new Mediator();
		mArray = mMod.getDataSession();

		viewHolder= new ViewHolder();
		
		//FOR TEST
		viewHolder.lt = (RelativeLayout) rootView.findViewById(R.id.detail_layout);
		viewHolder.sessionName = (TextView) rootView.findViewById(R.id.detailFrag_session_name);
		mIndex = mMod.getCurretnPosSession();

		if(mIndex == Mediator.START_FRAG_POS){
			if(viewHolder.lt != null){
				viewHolder.lt.setVisibility(View.GONE);
			}
		}else{
			if(viewHolder.lt != null){
				viewHolder.lt.setVisibility(View.VISIBLE);
			}
			
			//FOR TEST
			viewHolder.sessionName.setText(mArray.get(mIndex).getName());
			//END FOR TEST
			
		}
		
		return rootView;
	}
	
	private class ViewHolder{
		private RelativeLayout lt;
		private TextView sessionName;
	}
}
