package it.unipd.dei.esp1415.falldetector.fragment.adapter;

import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import it.unipd.dei.esp1415.falldetector.utility.ColorUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Array Adapter of Session
 */
public class ListSessionAdapter extends ArrayAdapter<Session> {

	private ArrayList<Session> mArray;
	private Context mContext;
	private boolean mDualPanel;
	private LayoutInflater mInflater;
	private boolean mIsLarge;
	private ViewHolder mViewHolder;
	private int[] mExpCol; // 0 to expand, 1 to collapse
	
	private Session mSession;
	
	//constants
	private static final int COLLAPSED = 0;
	private static final int TO_COLLAPSE = 1;
	private static final int TO_EXPAND = 2;
	private static final int EXPANDED = 3;
	
	private static final int FIRST_POS = 0;

	/**
	 * [c]
	 * 
	 * @param context
	 *            receive the context of the application
	 * @param objects
	 *            receive an ArrayList of Session object
	 */
	public ListSessionAdapter(Context context, ArrayList<Session> objects,
			boolean isLand) {
		this(context, R.layout.activity_main_fragment_list_row, objects, isLand);
	}// [c] ListSessionAdapter

	/**
	 * [c]
	 * 
	 * @param context
	 *            receive the context of the application
	 * @param resource
	 *            Receive the xml resource to adapt
	 * @param objects
	 *            receive an ArrayList of Session object
	 */
	private ListSessionAdapter(Context context, int resource,
			List<Session> objects, boolean isDual) {
		super(context, resource, objects);

		mArray = (ArrayList<Session>) objects;
		mContext = context;
		mDualPanel = isDual;
		mInflater = LayoutInflater.from(context);
		mIsLarge = true;
		mExpCol = new int[mArray.size()];
	}// [c] ListSessionAdapter

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		mViewHolder = new ViewHolder();
		Session session = mArray.get(position);
		mSession = session;

		if (view == null) {
			view = mInflater.inflate(R.layout.activity_main_fragment_list_row,
					parent, false);
			mViewHolder.thumbnail = (ImageView) view
					.findViewById(R.id.row_thumbnail);
			mViewHolder.sessionName = (TextView) view
					.findViewById(R.id.row_session_name);
			mViewHolder.date = (TextView) view
					.findViewById(R.id.row_expand_date);
			mViewHolder.at = (TextView) view
					.findViewById(R.id.row_expand_second_tag);
			mViewHolder.time = (TextView) view
					.findViewById(R.id.row_expand_time);

			// // hide expandable layout
			// mViewHolder.expandLayout.setVisibility(View.GONE);
			// // hide play/pause and stop button
			// mViewHolder.executeLayout.setVisibility(View.GONE);

			view.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) view.getTag();

		}// if... else...

		if (!mDualPanel) {
			
			mViewHolder.expandButton = (ImageButton) view
					.findViewById(R.id.row_expand_button);
			mViewHolder.expandLayout = (LinearLayout) view
					.findViewById(R.id.row_expand_layout);
			mViewHolder.executeLayout = (RelativeLayout) view
					.findViewById(R.id.row_execute_layout);
			mViewHolder.playPause = (ImageButton) view
					.findViewById(R.id.row_execute_play_pause_button);
			mViewHolder.stop = (ImageButton) view
					.findViewById(R.id.row_execute_stop_button);
			mViewHolder.duration = (TextView) view
					.findViewById(R.id.row_expand_duration);
			mViewHolder.falls = (TextView) view
					.findViewById(R.id.row_expand_falls);

			mViewHolder.expandButton
					.setOnClickListener(expandListener(position, view, parent));

			mViewHolder.expandButton.setFocusable(false);
			mViewHolder.expandButton.setFocusableInTouchMode(false);
			
			switch(mExpCol[position]){
			case COLLAPSED:
			case TO_COLLAPSE:
				collapse(position);
				mExpCol[position] = COLLAPSED;
				break;
			case TO_EXPAND:
			case EXPANDED:
				expand(position);
				mExpCol[position] = EXPANDED;
				break;

			}
			
		} else {

			if (mIsLarge) {
				mViewHolder.sessionName.setVisibility(View.VISIBLE);
			} else {
				mViewHolder.sessionName.setVisibility(View.GONE);
			}// if(isLarge)... else...

		}// if(!mIsLandscape)... else...

		
		
		// TODO get color from session
		Bitmap image = null;
		if((image = session.getBitmap()) == null) {
			image = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.thumbnail);
			image = ColorUtil.recolorIconBicolor(session.getColorThumbnail(), image);
			session.setBitmap(image);
		}
		
		mViewHolder.thumbnail.setImageBitmap(image);
		
		mViewHolder.sessionName.setText(session.getName());// TODO get name from session
		
		if (session.getStartTimestamp()> 0){
			mViewHolder.date.setVisibility(View.VISIBLE);
			mViewHolder.at.setVisibility(View.VISIBLE);
			
			mViewHolder.date.setText(session.getStartDate());// TODO get date from session
			mViewHolder.time.setText(session.getStartTimeToString());// TODO get time from session
		}else{

			mViewHolder.date.setVisibility(View.GONE);
			mViewHolder.at.setVisibility(View.GONE);
			mViewHolder.time.setText(R.string.not_start);
		}
		
		return view;
	}// [m] getView

	private OnClickListener expandListener(int position, View view, ViewGroup parent) {
		final View vw = view;
		final int pos = position;
		final ViewGroup vg = parent;
		
		return new OnClickListener() {

			@Override
			public void onClick(View v) {

				switch (mExpCol[pos]) {
				case COLLAPSED:
					mExpCol[pos]= TO_EXPAND;
					getView(pos, vw, vg);
					break;
				case EXPANDED:
					mExpCol[pos]= TO_COLLAPSE;
				    getView(pos, vw, vg);
					break;
				}// switch

			}// [m] onClick
		};
	}// [m] expandListener

	/**
	 * [m] expand the item of the list view
	 * 
	 * @param position
	 *            get the position of the item to expand
	 */
	// for use of old method for API8
	private void expand(int position) {
		mViewHolder.expandButton.setImageResource(R.drawable.ic_action_collapse);
		mViewHolder.expandLayout.setVisibility(View.VISIBLE);
		
		boolean hasToStart = mSession.getStartTimestamp() == 0;

		if (position == FIRST_POS && (mSession.isActive() || hasToStart)) { // TODO get is active from session
												// position == 0
			mViewHolder.executeLayout.setVisibility(View.VISIBLE);
			mViewHolder.playPause.setOnClickListener(null); // TODO listener
			mViewHolder.stop.setOnClickListener(null); // TODO listener
			
			mViewHolder.playPause.setFocusable(false);
			mViewHolder.playPause.setFocusableInTouchMode(false);
			
			mViewHolder.stop.setFocusable(false);
			mViewHolder.stop.setFocusableInTouchMode(false);
		}// if
		
		mViewHolder.duration.setText(mSession.getDurationString());
		mViewHolder.falls.setText(mSession.getFallsNum()+"");
	}// [m] expand

	/**
	 * [m] collapse the item of the list view
	 * 
	 * @param position
	 *            get the position of the item to collapse
	 */
	// for use of old method for API8
	private void collapse(int position) {
		mViewHolder.expandButton.setImageResource(R.drawable.ic_action_expand);
		mViewHolder.expandLayout.setVisibility(View.GONE);

		if (position == FIRST_POS && mSession.isActive()) { // TODO get is active from session
												// position == 0
			mViewHolder.executeLayout.setVisibility(View.GONE);
		}// if
		
	}// [m] collapse

	static class ViewHolder {
		private ImageView thumbnail;
		private TextView sessionName;
		private ImageButton expandButton;
		private LinearLayout expandLayout;
		private RelativeLayout executeLayout;
		private ImageButton playPause;
		private ImageButton stop;
		private TextView date;
		private TextView time;
		private TextView at;
		private TextView duration;
		private TextView falls;
	}// {c} ViewHolder

	public void resetArray(int pos){
		int[] tmp = null;
		boolean isVoid= mExpCol.length > 0;

		if(isVoid){
			tmp = copyArray(mExpCol);

		}

		mExpCol = new int[mArray.size()];

		if(isVoid){

			for(int i = 0; i < pos; i++){
				mExpCol[i] = tmp[i];
			}

			int j= pos;

			mExpCol[j] = COLLAPSED;


			for(int i = pos + 1; i < mExpCol.length; i++ ){

				if(j< tmp.length){
					mExpCol[i] = tmp[j];
				}
				j++;
			}
		}
	}

	private int[] copyArray(int[] ar){
		int[] tmp = new int[ar.length];
		for(int i = 0; i< ar.length; i++){
			tmp[i] = ar[i];
		}

		return tmp;
	}
}// {c} ListSessionAdapter