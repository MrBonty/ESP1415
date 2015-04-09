package it.unipd.dei.esp1415.falldetector.fragment.adapter;

import java.util.ArrayList;
import java.util.List;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListSessionAdapter extends ArrayAdapter<Session> {

	private ArrayList<Session> mArray;
	private Context mContext;
	private boolean mIsLandscape;
	private LayoutInflater mInflater;
	private boolean mIsLarge;
	private ViewHolder mViewHolder;
	private int[] mExpCol; // 0 to expand, 1 to collapse
	
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
			List<Session> objects, boolean isLand) {
		super(context, resource, objects);

		mArray = (ArrayList<Session>) objects;
		mContext = context;
		mIsLandscape = isLand;
		mInflater = LayoutInflater.from(context);
		mIsLarge = true;
		mExpCol = new int[mArray.size()];
	}// [c] ListSessionAdapter

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		mViewHolder = new ViewHolder();

		if (view == null) {
			view = mInflater.inflate(R.layout.activity_main_fragment_list_row,
					parent, false);
			mViewHolder.thumbnail = (ImageView) view
					.findViewById(R.id.row_thumbnail);
			mViewHolder.sessionName = (TextView) view
					.findViewById(R.id.row_session_name);

			// // hide expandable layout
			// mViewHolder.expandLayout.setVisibility(View.GONE);
			// // hide play/pause and stop button
			// mViewHolder.executeLayout.setVisibility(View.GONE);

			view.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) view.getTag();

		}// if... else...

		if (!mIsLandscape) {
			
			mViewHolder.expandButton = (Button) view
					.findViewById(R.id.row_expand_button);
			mViewHolder.expandLayout = (LinearLayout) view
					.findViewById(R.id.row_expand_layout);
			mViewHolder.executeLayout = (RelativeLayout) view
					.findViewById(R.id.row_execute_layout);
			mViewHolder.playPause = (ImageButton) view
					.findViewById(R.id.row_execute_play_pause_button);
			mViewHolder.stop = (ImageButton) view
					.findViewById(R.id.row_execute_stop_button);
			mViewHolder.date = (TextView) view
					.findViewById(R.id.row_expand_date);
			mViewHolder.time = (TextView) view
					.findViewById(R.id.row_expand_time);
			mViewHolder.duration = (TextView) view
					.findViewById(R.id.row_expand_duration);
			mViewHolder.falls = (TextView) view
					.findViewById(R.id.row_expand_falls);

			mViewHolder.expandButton
					.setOnClickListener(expandListener(position, view, parent));

		} else {

			if (mIsLarge) {
				mViewHolder.sessionName.setVisibility(View.VISIBLE);
			} else {
				mViewHolder.sessionName.setVisibility(View.GONE);
			}// if(isLarge)... else...

		}// if(!mIsLandscape)... else...

		// TODO get color from session
		mViewHolder.thumbnail.setBackgroundColor(Color.parseColor("BLUE"));
		mViewHolder.sessionName.setText(mArray.get(position).getName());// TODO get name from session
		
		for(int i=0; i<mExpCol.length; i++){
			switch(mExpCol[position]){
			case TO_COLLAPSE:
				collapse(position);
				mExpCol[position] = COLLAPSED;
				break;
			case TO_EXPAND:
			    expand(position);
			    mExpCol[position] = EXPANDED;
				break;
			}
		}

		return view;
	}// [m] getView

	private OnClickListener expandListener(int position, View view, ViewGroup parent) {
		final View vw = view;
		final int pos = position;
		final ViewGroup vg = parent;
		final View tmp;
		
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
	@SuppressWarnings("deprecation")
	// for use of old method for API8
	private void expand(int position) {
		mViewHolder.expandButton.setBackgroundDrawable(mContext.getResources()
				.getDrawable(R.drawable.ic_action_collapse));
		mViewHolder.expandLayout.setVisibility(View.VISIBLE);

		if (position == FIRST_POS && true) { // TODO get is active from session
												// position == 0
			mViewHolder.executeLayout.setVisibility(View.VISIBLE);
			mViewHolder.playPause.setOnClickListener(null); // TODO listener
			mViewHolder.stop.setOnClickListener(null); // TODO listener
		}// if
		
		mViewHolder.date.setText("TODO");// TODO get date from session
		mViewHolder.time.setText("TODO");// TODO get time from session
		mViewHolder.duration.setText("TODO");// TODO get duration from session
		mViewHolder.falls.setText("TODO");// TODO get falls from session
	}// [m] expand

	/**
	 * [m] collapse the item of the list view
	 * 
	 * @param position
	 *            get the position of the item to collapse
	 */
	@SuppressWarnings("deprecation")
	// for use of old method for API8
	private void collapse(int position) {
		mViewHolder.expandButton.setBackgroundDrawable(mContext.getResources()
				.getDrawable(R.drawable.ic_action_expand));
		mViewHolder.expandLayout.setVisibility(View.GONE);

		if (position == FIRST_POS && true) { // TODO get is active from session
												// position == 0
			mViewHolder.executeLayout.setVisibility(View.GONE);
		}// if
		
	}// [m] collapse

	static class ViewHolder {
		private ImageView thumbnail;
		private TextView sessionName;
		private Button expandButton;
		private LinearLayout expandLayout;
		private RelativeLayout executeLayout;
		private ImageButton playPause;
		private ImageButton stop;
		private TextView date;
		private TextView time;
		private TextView duration;
		private TextView falls;
	}// {c} ViewHolder

}// {c} ListSessionAdapter