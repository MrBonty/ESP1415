package it.unipd.dei.esp1415.falldetector.fragment;

import java.util.ArrayList;

import it.unipd.dei.esp1415.falldetector.FallDetailActivity;
import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import it.unipd.dei.esp1415.falldetector.utility.Fall;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.widget.EditText;
import android.app.Activity;
import android.widget.AdapterView;
import android.widget.Chronometer;

public class DetailSessionFragment extends Fragment {
	public static final String SAVE_MODIFY_DIALOG = "modifyDialog";
	
	private static ArrayList<Session> mArray;
	private static int mIndex;
	private Mediator mMod;
	private static ArrayList<Fall> mFallsEvent;
	private static ArrayAdapter<String> mAdapter;
	private static ViewHolder viewHolder;
	private static Context mContext;
    private EditOk mEditOk;
    private Activity act;
    private AlertDialog alert;
    private static EditText input;
    private static boolean create=false; //establishes if an AlertDialog is active
    private DatabaseManager dm;
	
	public DetailSessionFragment(){
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		act = this.getActivity();
		View rootView = inflater.inflate(R.layout.activity_detail_fragment, container, false);
		
		mMod = new Mediator();
		mArray = mMod.getDataSession();

		viewHolder = new ViewHolder();
		
		viewHolder.lt = (LinearLayout) rootView.findViewById(R.id.detail_layout);
		viewHolder.sessionName = (TextView) rootView.findViewById(R.id.detailFrag_session_name);
		viewHolder.sessionImage = (ImageView) rootView.findViewById(R.id.current_thumbnail);
		viewHolder.sessionDate = (TextView) rootView.findViewById(R.id.date);
		viewHolder.sessionTime = (TextView) rootView.findViewById(R.id.session_start_time);
		viewHolder.sessionDuration = (Chronometer) rootView.findViewById(R.id.duration);
		viewHolder.sessionFalls = (ListView) rootView.findViewById(R.id.session_falls);
		
		mIndex = mMod.getCurretnPosSession();

		if(mIndex == Mediator.START_FRAG_POS){
			if(viewHolder.lt != null){
				viewHolder.lt.setVisibility(View.GONE);
			}
		}else{
			if(viewHolder.lt != null){
				viewHolder.lt.setVisibility(View.VISIBLE);
			}
			
			viewHolder.sessionName.setText(mArray.get(mIndex).getName());
			viewHolder.sessionImage.setImageBitmap(mArray.get(mIndex).getBitmap());
			viewHolder.sessionDate.setText("Starts on "+mArray.get(mIndex).getStartDate());
			viewHolder.sessionTime.setText("at "+mArray.get(mIndex).getStartTimeToString());
			viewHolder.sessionDuration.setBase(SystemClock.elapsedRealtime()-mArray.get(mIndex).getDuration());
			
			mContext = mMod.getContext();
			dm = new DatabaseManager(mContext);
			
			//creation of EditOk and definition of the method ok(String result)
	        mEditOk = new EditOk() {
	            @Override
	            public void ok(String result) {
	            	Log.i("INPUT", "result " + result);
	            	Toast show = null;
	            	
	            	viewHolder.sessionName.setText(result);
	            	
					String name = result;
	            	
					if(name.length() > 0){
						if(show != null && show.getView().getWindowVisibility() == View.VISIBLE){
							show.cancel();
						}
							
						if(!(name.equals(mArray.get(mIndex).getName()))){
							mArray.get(mIndex).setName(name);

							ContentValues cv = new ContentValues();
							cv.put(DatabaseTable.COLUMN_SS_NAME, name);

							int error = 0;
							do{
								error = dm.upgradeASession(mArray.get(mIndex).getId(), cv);
							}while(error == DatabaseManager.ON_OPEN_ERROR);

							show = Toast.makeText(mContext, R.string.modify_made, Toast.LENGTH_SHORT);
						}else{
							show = Toast.makeText(mContext, R.string.modify_no_made, Toast.LENGTH_SHORT);
						}
						show.show();
					}else{
						show = Toast.makeText(mContext, R.string.no_value, Toast.LENGTH_SHORT);
						show.show();
					}
	            }
	        };

	        viewHolder.sessionName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					editTextDialog(R.string.dialog_modify_name, viewHolder.sessionName.getText().toString(), mEditOk);
				}
			});
			
			mFallsEvent = dm.getFallForSessionAsArray(mArray.get(mIndex).getId(), DatabaseTable.COLUMN_SS_START_DATE); //mArray.get(mIndex).getFallEvents(); 
			
			if(mFallsEvent != null){
				if(mFallsEvent.size() > 0){
					ArrayList<String> mList = new ArrayList<String>();
					
					for (int i = 0; i < mFallsEvent.size(); ++i) {
						 String[] tmp = mFallsEvent.get(i).dateTimeStampFallEven();
						 boolean send = mFallsEvent.get(i).isNotified();
						 
						 String s;
						 if(send) s = "Sended";
						 else s = "Not sended";
					     
						 mList.add(tmp[0]+" "+tmp[1]+"\n"+s+""); 
					} 
					
					mAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1, mList) {
		                @Override
		                public View getView(int position, View convertView, ViewGroup parent) {
		                	TextView textView = (TextView) super.getView(position, convertView, parent);
		                	textView.setTextColor(Color.BLACK);
		                	return textView;
		                }
		            };
					
		            viewHolder.sessionFalls.setAdapter(mAdapter);
		            viewHolder.sessionFalls.setOnItemClickListener(new AdapterView.OnItemClickListener() {  
		                @Override  
		                public void onItemClick(AdapterView<?> adapter, final View component, int pos, long id){  
		                	Intent intent = new Intent();
		        			intent.setClass(getActivity(), FallDetailActivity.class);
		        			intent.putExtra("nFall", pos);
		        			startActivity(intent);
		                }   
		            });  
				}
			}
		}
		
        if(savedInstanceState != null){
			String restore = savedInstanceState.getString(SAVE_MODIFY_DIALOG);
			if(restore != null){
				Log.i("INPUT", "restore " + restore);
				editTextDialog(R.string.dialog_modify_name, restore, mEditOk);
			}
		}
		
		return rootView;
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Log.i("SAVE1 "+(alert==null), "save of " + mIndex);
        if(!mMod.isCalledFromBack()){
            mMod.setCurretnPosSession(mIndex);
        }else {
        	mMod.resetIsCalledFromBack();
        }
        if(create){
            Log.i("INPUT", "save of " + input.getText().toString());
        	outState.putString(SAVE_MODIFY_DIALOG, input.getText().toString());
		}
    }
	
	/**
	 * [m]
	 * Method to create an AlertDialog to modify the name of the session
	 * 
	 * @param titleId The title of the AlertDialog
	 * @param text The name of the session for the EditText input
	 * @param ok The object EditOk  that implements the action of press the button "ok" in the AlertDialog
	 */
    public void editTextDialog(int titleId, String text, final EditOk ok) {
        LayoutInflater myInflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = myInflater.inflate(R.layout.edittext_dialog, (ViewGroup) act.findViewById(R.id.layout_root));

        input = (EditText) layout.findViewById(R.id.editTextDialog);
        input.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        input.setText(text);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle(titleId)
            .setView(layout)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String newName = input.getText().toString();
                            Log.i("INPUT", "newName " + newName);
                            if (newName.length() != 0) {
                                ok.ok(newName);
                                create=false; //the AlertDialog can disappear
                            }
                        }})
            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    act.setResult(Activity.RESULT_CANCELED);
                    create=false; //the AlertDialog can disappear
                }
            });
        alert = builder.create();
        create = true; //the AlertDialog must appear until create becomes false
        //Log.i("SAVE ALERT "+(alert==null), "save of " + mIndex);
        alert.show();
        //Log.i("SAVE ALERT "+(alert==null), "save of " + mIndex);
        alert.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				alert = null;
			}
		});
    }
	
    /**
	 * private inner class to hold the view
	 */
	private class ViewHolder{
		private LinearLayout lt;
		private TextView sessionName;
		private TextView sessionDate;
		private TextView sessionTime;
		private Chronometer sessionDuration;
		private ImageView sessionImage;
		private ListView sessionFalls;
	}
	
	/**
	 * private abstract class to define method ok(String result) 
	 * that implements the action of press the button "ok" in the AlertDialog
	 */
    private abstract class EditOk {
        abstract public void ok(String result);
    }
}
