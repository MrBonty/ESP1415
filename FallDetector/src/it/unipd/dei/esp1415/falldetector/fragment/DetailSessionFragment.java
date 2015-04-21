package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.Moderator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailSessionFragment extends Fragment {
	
	private static ArrayList<Session> mArray;
	private static int mIndex;
	private static Moderator mMod; 

	public DetailSessionFragment() {
		mMod = new Moderator();
		mArray = mMod.getDataSession();
		mIndex = mMod.getCurretnPosSession();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.activity_detail_fragment,
				container, false);
		
		//FOR TEST CREATE AN ADAPTER
		TextView sessionName = (TextView) rootView.findViewById(R.id.detailFrag_session_name);
		sessionName.setText(mArray.get(mIndex).getName());
		//END FOR TEST
		return rootView;
	}
	
	public static DetailSessionFragment newInstance(int index, ArrayList<Session> objects){
		DetailSessionFragment tmp = new DetailSessionFragment();
		mArray = objects;
		mIndex = index;
		return tmp;
	}
}
