package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailSessionFragment extends Fragment {
	
	private static ArrayList<Session> mArray;
	private static int mIndex;
	private Mediator mMod; 
	
	private RelativeLayout lt;
	private TextView sessionName;
	

	public DetailSessionFragment() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMod = new Mediator();
		mArray = mMod.getDataSession();
		
		View rootView = inflater.inflate(R.layout.activity_detail_fragment,
				container, false);
		
		//FOR TEST CREATE AN ADAPTER creating the adapter private of the class
		lt = (RelativeLayout) rootView.findViewById(R.id.detail_layout);
		sessionName = (TextView) rootView.findViewById(R.id.detailFrag_session_name);
		
		changeLayout();
		//END FOR TEST
		
		return rootView;
	}
	
	public void changeLayout(){
		mIndex = mMod.getCurretnPosSession();
		
		if(mIndex == Mediator.START_FRAG_POS){
			lt.setVisibility(View.GONE);
		}else{
			if(lt != null){
				lt.setVisibility(View.VISIBLE);
			}

			//FOR TEST CREATE AN ADAPTER creating the adapter private of the class
			sessionName.setText(mArray.get(mIndex).getName());
			//END FOR TEST
		}
	}
}
