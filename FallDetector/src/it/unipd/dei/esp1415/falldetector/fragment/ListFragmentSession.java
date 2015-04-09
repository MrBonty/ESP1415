package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.fragment.adapter.ListSessionAdapter;
import it.unipd.dei.esp1415.falldetector.utility.Session;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class ListFragmentSession extends ListFragment {

	private static ArrayList<Session> mArray;
	private static Context mContext;
	private static boolean mIsLandscape;

	/**
	 * [c]
	 * Void Constructor use instead 
     * ListFragmentSession name = newInstance(Context context, ArrayList<Session> objects, boolean isLandsacape); 
     * at the creation of a new ListFragmentSession
	 */
	public ListFragmentSession() {

	}// [c] ListFragmentSession

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// do something with the data
	}//[m] onListItemClick

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		ListSessionAdapter adapter = new ListSessionAdapter(mContext, mArray,
				mIsLandscape);
		setListAdapter(adapter);
	}//[m] onActivityCreated

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
	public static final ListFragmentSession newInstance(Context context,
			ArrayList<Session> objects, boolean isLandsacape) {
		ListFragmentSession tmp = new ListFragmentSession();
		mArray = objects;
		mContext = context;
		mIsLandscape = isLandsacape;
		return tmp;
	}// Builder class
}
