package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.extraview.MailDialog;
import it.unipd.dei.esp1415.falldetector.fragment.adapter.ListMailAdapter;
import it.unipd.dei.esp1415.falldetector.utility.MailAddress;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsSecFragment extends Fragment{
	
	
	private Context mContext;
	private ListView mList;
	private Button mAdd;
	
	private ArrayList<MailAddress> mArray;
	private ListMailAdapter mAdapter;
	
	private MailDialog mDialog;
	
	private String mSavingString;
	private int mSavingPos;
	private final static String SAVING_DATA_TO_RESTORE = "dataToRestore";
	private final static String SAVING_POS_TO_RESTORE = "positionToRestore";
	private final static int NO_POS = -1;
	
	public static final int NEW_MAIL = -1;
	public static final String DIVISOR = "&&";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext= getActivity();
		
		DatabaseManager dm = new DatabaseManager(getActivity());
		mArray = dm.getMailAddressAsArray();
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.settings_sec_fragment, container, false);

		mList = (ListView) view.findViewById(R.id.list_mail);
		mAdd = (Button) view.findViewById(R.id.add_new_address);
		
		mAdapter = new ListMailAdapter(mContext, mArray);
		mList.setAdapter(mAdapter);
				
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mList.setOnItemClickListener(onListItemClick());
		mAdd.setOnClickListener(addListerListener());
		
		if(savedInstanceState != null){
			mSavingString = savedInstanceState.getString(SAVING_DATA_TO_RESTORE);
			
			if(mSavingString != null && mSavingString.length() > 0){
				
				mSavingPos = savedInstanceState.getInt(SAVING_POS_TO_RESTORE, NO_POS);
				createDialog(true);
			}
		}
	};
	
	@Override
	public void onPause() {
		super.onPause();
		if(mDialog != null){
			mSavingString = mDialog.getSavingString();
			mDialog.setOnDismissListener(null);
			mDialog.dismiss();
		}
	};
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(mSavingString != null && mSavingString.length() > 0){
			outState.putString(SAVING_DATA_TO_RESTORE, mSavingString);
			outState.putInt(SAVING_POS_TO_RESTORE, mSavingPos);
		}
	};
	
	private OnClickListener addListerListener(){
		return new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSavingPos = NEW_MAIL;
				createDialog(false);
			}
		};
	}
	
	private OnItemClickListener onListItemClick(){
		return new OnItemClickListener() {

			int pos;
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				pos = position;
				showPopup(view);
			}



			private void showPopup(View v) {
				PopupMenu popup = new PopupMenu(mContext, v);

				popup.setOnMenuItemClickListener(menuClickEvent());
				MenuInflater inflater = popup.getMenuInflater();
				inflater.inflate(R.menu.list_modify_menu, popup.getMenu());
				popup.show();
			}

			private OnMenuItemClickListener menuClickEvent(){
				return new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {

				        case R.id.action_modify:
				        	mSavingPos = pos;
				        	modify();
				        	return true;

				        case R.id.action_delete:
				        	String tmp = "";
				        	String address = mArray.get(pos).getAddress();
				        	mSavingPos = NO_POS;
				        	
				            if(delete()){
				        		tmp = mContext.getResources().getString(R.string.delete) + ": " + address;
				            }else{
				        		tmp = mContext.getResources().getString(R.string.error_advise); 				            
				            }

				        	Toast.makeText(mContext, tmp, Toast.LENGTH_SHORT).show();
				            return true;
				        default:
				            return false;
						}
					}
				};
			}
			
			private void modify(){
				createDialog(false);
			}
		
			private boolean delete(){
				DatabaseManager dm = new DatabaseManager(mContext);
				int i = dm.deleteAMailAddress(mArray.get(pos).getId());
			
				if(i>0){
					mArray.remove(pos);
					mAdapter.notifyDataSetChanged();
					return true;
				}
				
				return false;
			}
		};
	}

	private void createDialog(boolean restore){
		mDialog = new MailDialog(mContext, mArray, mSavingPos);
		
		if(restore){
			String[] splied = mSavingString.split(DIVISOR);
			int state = Integer.parseInt(splied[3]);
			
			mDialog.restoreValue(splied[0], splied[1], splied[2], state);
					
		}
		
		mDialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mAdapter.notifyDataSetChanged();
				if(mSavingPos == NO_POS){
					mList.setSelection(mArray.size());
				}
				
				mDialog = null;
			}
		});
		
		mDialog.show();
	}
}
