package it.unipd.dei.esp1415.falldetector.extraview;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
import it.unipd.dei.esp1415.falldetector.fragment.ListSessionFragment;
import it.unipd.dei.esp1415.falldetector.utility.ColorUtil;
import it.unipd.dei.esp1415.falldetector.utility.Mediator;
import it.unipd.dei.esp1415.falldetector.utility.Session;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

/**
 * Implementation of a dialog for add or modify a session
 */
public class SessionDialog extends Dialog{

	private Context mContext;
	private Session mSession;
	private ViewHolder mViewHolder;
	private Bitmap mThumb;
	
	private String mSessionName;
	private int mSessionColor;
	private boolean colorSet = false;
	private Bitmap mSessionImage;
	
	private int mPos;
	private boolean mIsNew;
	
	private boolean mIsModColor = false;
	
	public final static String SAVE_NAME_SESSION = "name";
	public final static String SAVE_COLOR = "color";
	public final static String SAVE_POS = "position";
	
	/**
	 * [c]
	 * Constructor for add/modify dialog for a session
	 * 
	 * @param context application context
	 * @param position the position for the session, set to 0 if is a new one
	 * @param isNew set to true if is a new session, false otherwise 
	 */
	public SessionDialog(Context context, int position, boolean isNew) {
		super(context);
		mContext = context;
		mIsNew = isNew;
		
		mThumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.thumbnail);
		
		if(mIsNew){
			mSessionName = "";
		}else{
			mSession = (new Mediator()).getDataSession().get(position);
		}
		
		
		mPos = position;
	}// [c] SessionDialog
	
	/**
	 * [c]	 
	 * Constructor for add/modify dialog for a session
	 * 
	 * @param context application context
	 * @param isNew set to true if is a new session, false otherwise
	 */
	public SessionDialog(Context context, boolean isNew) {
		super(context);
		mContext = context;
		mIsNew = isNew;
		
		mThumb = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.thumbnail);
	}// [c] SessionDialog()
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		setContentView(R.layout.session_dialog);
		setCanceledOnTouchOutside(false);
		
		if(mIsNew){
			setTitle(R.string.title_add_dialog);
		}else{
			setTitle(R.string.title_modify_dialog);
		}
		mViewHolder = new ViewHolder();
		
		mViewHolder.name = (EditText) findViewById(R.id.session_name);
		mViewHolder.image = (ImageView) findViewById(R.id.session_image);
		mViewHolder.cancel = (Button) findViewById(R.id.button_cancel);
		mViewHolder.save = (Button) findViewById(R.id.button_save);
		
		mViewHolder.image.setOnClickListener(changeImageListener());
		mViewHolder.cancel.setOnClickListener(cancelListener());
		mViewHolder.save.setOnClickListener(saveListener());
	}// [m] onCreate()
	
	@Override
	public void show() {
		super.show();
		
		if(mIsNew){
			
			// control if the color for the image is set
			if(!colorSet){
				mSessionColor = ColorUtil.imageColorSelector();
				colorSet = true;
			}
			
			// control if the name for the session is set
			if(!(mSessionName == null || mSessionName.length() <= 0)){
				mViewHolder.name.setText(mSessionName);
			}
			
			mSessionImage = ColorUtil.recolorIconBicolor(mSessionColor, mThumb);
			
		}else{
			
			// control if the name for the session is set
			if(mSessionName == null || mSessionName.length() <= 0){
				mSessionName = mSession.getName();
			}
			
			mViewHolder.name.setText(mSessionName);
			mViewHolder.name.setSelection(mSessionName.length());
			
			// control if the color for the image is set
			if(colorSet){
				
				mSessionImage = ColorUtil.recolorIconBicolor(mSessionColor, mThumb);
				
			}else{
				if((mSessionImage = mSession.getBitmap()) == null) {
					mSessionImage = ColorUtil.recolorIconBicolor((mSessionColor = mSession.getColorThumbnail()), mThumb);
					mSession.setBitmap(mSessionImage);
				}else{
					mSessionColor = mSession.getColorThumbnail();
				}
			}
			
		}// if(mIsNew).... else ...

		mViewHolder.image.setImageBitmap(mSessionImage);
		
	}// [m] show()
	
	/**[m]
	 * Method to create lister for change image
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private android.view.View.OnClickListener changeImageListener() {
		
		return new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSessionColor= ColorUtil.imageColorSelector();
				mSessionImage = ColorUtil.recolorIconBicolor(mSessionColor, mThumb);

				mViewHolder.image.setImageBitmap(mSessionImage);
				mIsModColor= true;
			}// [m] onClick()
		};
	}// [m] changeImageListener()  
	
	/**[m]
	 * Method to create lister for cancel button
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private android.view.View.OnClickListener cancelListener(){
		return new android.view.View.OnClickListener(){
			
			@Override
			public void onClick(View v) {
				dismiss();
			}// [m] onClick()
		};
	}// [m] cancelListener()
	
	/**[m]
	 * Method to create lister for save button, it control all params
	 * 
	 * @return the {@link android.view.View.OnClickListener}
	 */
	private android.view.View.OnClickListener saveListener() {
		return new android.view.View.OnClickListener(){
			
			private Toast toShow = null;
			
			@Override
			public void onClick(View v) {
				String name = mViewHolder.name.getText().toString();
				DatabaseManager dm = new DatabaseManager(mContext);
				
				if(name.length() > 0){
					if(mIsNew){
						if(toShow != null && toShow.getView().getWindowVisibility() == View.VISIBLE){
							toShow.cancel();
						} // close all toast if is visible
						
						mSession = new Session(name, 0);
						mSession.setColorThumbnail(mSessionColor);
						mSession.setBitmap(mSessionImage);
						
						mSession.setId(dm.insertASession(mSession));

						(new Mediator()).getDataSession().add(mPos, mSession);
						
						toShow = Toast.makeText(mContext, R.string.add_session, Toast.LENGTH_SHORT);
						

						ListSessionFragment.mArray.add(mPos, mSession);
						
						ListSessionFragment.mAdapter.resetArray(mPos, true);
						ListSessionFragment.mAdapter.notifyDataSetChanged();
						
					}else{
						
						if(toShow != null && toShow.getView().getWindowVisibility() == View.VISIBLE){
							toShow.cancel();
						} // close all toast if is visible
						
						// control if a modification is made
						if((!(name.equals(mSession.getName()))) || mIsModColor){
							mSession.setName(name);

							mSession.setColorThumbnail(mSessionColor);
							mSession.setBitmap(mSessionImage);
							
							
							ContentValues cv = new ContentValues();
							cv.put(DatabaseTable.COLUMN_SS_NAME, name);
							cv.put(DatabaseTable.COLUMN_SS_COLOR_THUMBNAIL, mSessionColor);

							int error = 0;
							do{
								error = dm.upgradeASession(mSession.getId(), cv);

							}while(error == DatabaseManager.ON_OPEN_ERROR);

							toShow = Toast.makeText(mContext, R.string.modify_made, Toast.LENGTH_SHORT);
						}else{
							toShow = Toast.makeText(mContext, R.string.modify_no_made, Toast.LENGTH_SHORT);
						}
						
					}// if(mIsNew).... else ...
					toShow.show();
					dismiss();
					
				}else{
					toShow = Toast.makeText(mContext, R.string.no_value, Toast.LENGTH_SHORT);
					toShow.show();
					
				}// if(name.length > 0 ) ... else...
			}
		};
	}//[m] saveListener()
	
	
    @Override
    public Bundle onSaveInstanceState() {
    	final Bundle state = super.onSaveInstanceState();
    	
    	state.putInt(SAVE_POS, mPos);
    	state.putString(SAVE_NAME_SESSION, mViewHolder.name.getText().toString());
    	state.putInt(SAVE_COLOR, mSessionColor);
    	
    	return state;
    }// [m] onSaveInstanceState()
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	colorSet = true;
    	
    	mPos = savedInstanceState.getInt(SAVE_POS);
    	mSessionName = savedInstanceState.getString(SAVE_NAME_SESSION);
    	mSessionColor = savedInstanceState.getInt(SAVE_COLOR);
    	
    	if(!mIsNew){
    		mSession = (new Mediator()).getDataSession().get(mPos);
    	}
    	
    	super.onRestoreInstanceState(savedInstanceState);
    }// [m] onRestoreInstanceState()
	 
    /**
     * private internal class to hold the view 
     */
	private class ViewHolder{
		private EditText name;
		private Button cancel;
		private Button save;
		private ImageView image;
	}// {c} ViewHolder
	
	/**[m]
	 * Method to see if is a dialog for a new session
	 * 
	 * @return true if is a dialog for a new session, false otherwise
	 */
	public boolean isNew(){
		return mIsNew;
	}// [m] isNew()
}