package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.DetailActivity;
import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.fragment.adapter.ListSessionAdapter;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ListSessionFragment extends ListFragment {

	private static ArrayList<Session> mArray;
	private static Context mContext;
	private static boolean mIsLandscape;
	private static int mCurCheckPosition;
	
	public static final String SAVE_CURRENT_CHOICE = "curChoice";

	/**
	 * [c] Void Constructor use instead ListFragmentSession name =
	 * newInstance(Context context, ArrayList<Session> objects, boolean
	 * isLandsacape); at the creation of a new ListFragmentSession
	 */
	public ListSessionFragment() {

	}// [c] ListFragmentSession

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) { 
		Log.i("CLICK", "[onListItemClick] Selected Position "+ position);
		showDetail(position);
	}// [m] onListItemClick

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ListSessionAdapter adapter = new ListSessionAdapter(mContext, mArray,
				mIsLandscape);
		setListAdapter(adapter);
		
		if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt(SAVE_CURRENT_CHOICE, 0);
        }
		
		if (mIsLandscape) {
			// In dual-pane mode, the list view highlights the selected item.
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// Make sure our UI is in the correct state.
			showDetail(mCurCheckPosition);
        }
	}// [m] onActivityCreated
	
	
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("SAVE", "save of " + mCurCheckPosition);
        outState.putInt(SAVE_CURRENT_CHOICE, mCurCheckPosition);
    }

	/**
	 * {c} build a List Fragment Session
	 * 
	 * @param context
	 *            the application context
	 * @param objects
	 *            the data set
	 * @param isLandsacape
	 *            true if the application is in landscape
	 */
	public static ListSessionFragment newInstance(Context context,
			ArrayList<Session> objects, boolean isLandsacape) {
		ListSessionFragment tmp = new ListSessionFragment();
		mArray = objects;
		mContext = context;
		mIsLandscape = isLandsacape;
		mCurCheckPosition = 0;
		return tmp;
	}// Builder class
	
	/**
	 * [m]
	 * 
	 * 
	 * @param pos
	 */
	private void showDetail(int pos){
		mCurCheckPosition = pos;
		if (mIsLandscape) {
			Fragment detailSession = DetailSessionFragment.newInstance(mCurCheckPosition, mArray);
			FragmentManager manager = getFragmentManager();

			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.main_secondary, detailSession);

			transaction.commit();
			

		} else {
			Intent intent = new Intent();
			intent.setClass(getActivity(), DetailActivity.class);
			intent.putExtra("index", pos);
			startActivity(intent);
		}
	}

}
