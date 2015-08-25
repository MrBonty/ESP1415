package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.CurrentSessionActivity;
import it.unipd.dei.esp1415.falldetector.DetailActivity;
import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.extraview.SessionDialog;
import it.unipd.dei.esp1415.falldetector.fragment.adapter.ListSessionAdapter;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ListSessionFragment extends ListFragment {

	public static ArrayList<Session> mArray;
	private static Context mContext;
	private static int mCurCheckPosition;
	private static Mediator mMed;
	public static ListSessionAdapter mAdapter;
	
	private SessionDialog mDialog;
	
	private Fragment mDetailFrag;
	private boolean mDualPanel;
	
	public static final String SAVE_CURRENT_CHOICE = "curChoice";
	public static final String SAVE_MODIFY_DIALOG = "modifyDialog";
	public static final String TAG_DUAL = "isDual";
	
	public static final int FIRST_ITEM = 0;
	
	/**
	 * [c] Void Constructor use instead ListFragmentSession name =
	 * newInstance(Context context, ArrayList<Session> objects, boolean
	 * isLandsacape); at the creation of a new ListFragmentSession
	 */
	public ListSessionFragment() {
		mMed = new Mediator();
		mArray = mMed.getDataSession();
		mContext = mMed.getContext();
		mCurCheckPosition = mMed.getCurretnPosSession();
	}// [c] ListFragmentSession

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) { 
		Log.i("CLICK", "[onListItemClick] Selected Position "+ position);
		showDetail(position);
		
	}// [m] onListItemClick
	
	@Override
	public void onCreateContextMenu(android.view.ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = this.getActivity().getMenuInflater();
	    inflater.inflate(R.menu.list_modify_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int pos = info.position;
		
	    switch (item.getItemId()) {

	        case R.id.action_modify:
	        	modify(pos);
	        	return true;

	        case R.id.action_delete:
	        	String tmp = "";
	        	String name = mArray.get(pos).getName();
	        	if(delete(pos)){
	        		tmp = mContext.getResources().getString(R.string.delete) + ": " + name;
	        	}else {
	        		tmp = mContext.getResources().getString(R.string.error_advise); 
	        	}
	        	Toast.makeText(mContext, tmp, Toast.LENGTH_SHORT).show();
	        	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}//[m] onContextItemSelected()

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		
		mMed = new Mediator();
		mArray = mMed.getDataSession();
		mContext = mMed.getContext();
		mCurCheckPosition = mMed.getCurretnPosSession();
		
		mDualPanel = mMed.isLarge() && mMed.isLand();
		
		mAdapter = new ListSessionAdapter(mContext, mArray,
				mDualPanel);
	
		mCurCheckPosition = mMed.getCurretnPosSession();
		Log.i("POSITION","Current pos= " + mCurCheckPosition);

		if (mDualPanel) {
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
		
		setListAdapter(mAdapter);
		
		if(savedInstanceState != null && savedInstanceState.getBoolean(SAVE_MODIFY_DIALOG)){
			createModifyDialog(true, savedInstanceState, -1);
		}
	}// [m] onActivityCreated

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);        
        Log.i("SAVE", "save of " + mCurCheckPosition);
        //outState.putInt(SAVE_CURRENT_CHOICE, mCurCheckPosition);
        if(!mMed.isCalledFromBack()){
            mMed.setCurretnPosSession(mCurCheckPosition);
        }else {
        	mMed.resetIsCalledFromBack();
        }
        
        if(mDialog != null && mDialog.isShowing()&& !mDialog.isNew()){
        	outState.putAll(mDialog.onSaveInstanceState());
        	mDialog.setOnDismissListener(null);
        	mDialog.dismiss();
        	
        	outState.putBoolean(SAVE_MODIFY_DIALOG, true);

		}
    }//[m] onSaveInstanceState() 
	
	/**
	 * [m]
	 * show detail of a Session as fragment on landscape or as a new activity if on portrait
	 * 
	 * @param pos receive the position to show
	 */
	public void showDetail(int pos){
		
		mMed.setCurretnPosSession(pos);
		mCurCheckPosition = pos;
		boolean toSwipe = mCurCheckPosition != Mediator.START_FRAG_POS;

		if(pos == FIRST_ITEM && 
				(mMed.getDataSession().get(FIRST_ITEM).isActive() || 
						mMed.getDataSession().get(FIRST_ITEM).getStartTimestamp() == 0)){
			
			Intent intent = new Intent();
			intent.setClass(getActivity(), CurrentSessionActivity.class);
			startActivity(intent);
			
		}else{
			if (mDualPanel) {
				
				FragmentManager manager = getFragmentManager();

				mDetailFrag = new DetailSessionFragment();
				
				//hide background image on void fragment
				ImageView  image = (ImageView)  mMed.getMain().findViewById(R.id.main_secondary_image_on_void);
				if(toSwipe){
					image.setVisibility(View.GONE);
				}else {
					image.setVisibility(View.VISIBLE);
				}
				
				FragmentTransaction transiction = manager.beginTransaction();
				transiction.replace(R.id.main_details, mDetailFrag).commit();	

			}else { 
				if (toSwipe){			
					mMed.setCurretnPosSession(pos);

					Intent intent = new Intent();
					intent.setClass(getActivity(), DetailActivity.class);
					startActivity(intent);
				}
			}
		}

	}//[m] showDetail
	
	/**
	 * [m]
	 * Method to delete a session
	 * 
	 * @param pos the position of the session to delete on the list view
	 * @return
	 */
	private boolean delete(int pos){
		Session tmp= mArray.get(pos);
		DatabaseManager dbm = new DatabaseManager(mContext);
		
		int i = dbm.deleteASession(tmp.getId());
		if(i>0){
			mArray.remove(pos);
			mMed.getDataSession().remove(pos);
			
			mAdapter.resetArray(pos, false);
			
			mAdapter.notifyDataSetChanged();
			return true;
		}
		
		return false;
	}//[m] delete()
	
	/**
	 * [m]
	 * Method to modify a session
	 * 
	 * @param pos the position of the session to modify on the list view
	 * @return
	 */
	private void modify(int pos){
		createModifyDialog(false, null, pos);
	}//[m] modify()

	/**
	 * [m]
	 * Method to create a dialog to modify a session
	 * 
	 * @param restore set to true if dialog has to be restored, false otherwise
	 * @param data the bundle for restoring dialog, or null if restore is set to false 
	 * @param pos the position of the session to modify
	 */
	private void createModifyDialog(boolean restore, Bundle data, int pos){

		if(restore && data != null){
			mDialog = new SessionDialog(mMed.getMain(), false);
			mDialog.onRestoreInstanceState(data);
			
		}else{
			mDialog = new SessionDialog(mMed.getMain(), pos, false);
		}
		
		mDialog.show();
		
		// set of OnDismissListener for dialog
		mDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mAdapter.notifyDataSetChanged();
				mDialog = null;
			}//[m] onDismiss()
		});
		
	}//[m] createModifyDialog()
	
	
}
