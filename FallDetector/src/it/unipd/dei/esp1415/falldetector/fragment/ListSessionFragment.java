package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.DetailActivity;
import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.fragment.adapter.ListSessionAdapter;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
	private static Mediator mMod;
	
	public static final String SAVE_CURRENT_CHOICE = "curChoice";
	
	/**
	 * [c] Void Constructor use instead ListFragmentSession name =
	 * newInstance(Context context, ArrayList<Session> objects, boolean
	 * isLandsacape); at the creation of a new ListFragmentSession
	 */
	public ListSessionFragment() {
		mMod = new Mediator();
		mArray = mMod.getDataSession();
		mContext = mMod.getContext();
		mCurCheckPosition = mMod.getCurretnPosSession();
	}// [c] ListFragmentSession

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) { 
		Log.i("CLICK", "[onListItemClick] Selected Position "+ position);
		showDetail(position);
		
	}// [m] onListItemClick

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mIsLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
		ListSessionAdapter adapter = new ListSessionAdapter(mContext, mArray,
				mIsLandscape);
		
		/*
		if (!mod.hasCurretnPosSessionSet()) {
			// Restore last state for checked position.
			//mCurCheckPosition = savedInstanceState.getInt(Moderator.SAVE_CURRENT_CHOICE, START_FRAG_POS);
			mod.resetCurretnPosSession();
		}*/
		
		mCurCheckPosition = mMod.getCurretnPosSession();
		Log.i("POSITION","Current pos= " + mCurCheckPosition);

		if (mIsLandscape) {
			// In dual-pane mode, the list view highlights the selected item.
			ListView tmp = getListView();
			tmp.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

			if(mCurCheckPosition != Mediator.START_FRAG_POS){
				tmp.setItemChecked(mCurCheckPosition, true);
				Log.i("CHECKED", "Checked Pos = " + mCurCheckPosition);
				tmp.requestFocus();
			}			

			// Make sure our UI is in the correct state.
			showDetail(mCurCheckPosition);
			
		}else{

			Log.i("CALLOF", "on " + mCurCheckPosition);
			showDetail(mCurCheckPosition);

		}
		
		setListAdapter(adapter);
	}// [m] onActivityCreated
	

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("SAVE", "save of " + mCurCheckPosition);
        //outState.putInt(SAVE_CURRENT_CHOICE, mCurCheckPosition);
        if(!mMod.isCalledFromBack()){
            mMod.setCurretnPosSession(mCurCheckPosition);
        }else {
        	mMod.resetIsCalledFromBack();
        }
    }
	
	/**
	 * [m]
	 * 
	 * 
	 * @param pos
	 */
	private void showDetail(int pos){
		mMod.setCurretnPosSession(pos);
		mCurCheckPosition = pos;
		if(mCurCheckPosition != Mediator.START_FRAG_POS){
			if (mIsLandscape) {

				Fragment detailSession = new DetailSessionFragment();
				FragmentManager manager = getFragmentManager();

				FragmentTransaction transaction = manager.beginTransaction();
				transaction.replace(R.id.main_secondary, detailSession);
				transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				transaction.commit();


			} else {

				Intent intent = new Intent();
				intent.setClass(getActivity(), DetailActivity.class);
				intent.putExtra("index", pos);
				mMod.setCurretnPosSession(pos);
				startActivity(intent);
			}
		}
	}
}