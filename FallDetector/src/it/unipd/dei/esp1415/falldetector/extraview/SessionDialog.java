package it.unipd.dei.esp1415.falldetector.extraview;

import it.unipd.dei.esp1415.falldetector.R;
import it.unipd.dei.esp1415.falldetector.database.DatabaseManager;
import it.unipd.dei.esp1415.falldetector.database.DatabaseTable;
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
	
	public final static String DIVISOR = "&&";
	
	/**[c]
	 * @param context 
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
	}
	
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
		
		if(mIsNew){
			if(!colorSet){
				mSessionColor = ColorUtil.imageColorSelector();
				colorSet = true;
			}
			
			if(!(mSessionName == null || mSessionName.length() <= 0)){
				mViewHolder.name.setText(mSessionName);
			}
			mSessionImage = ColorUtil.recolorIconBicolor(mSessionColor, mThumb);
		}else{
			
			if(mSessionName == null || mSessionName.length() <= 0){
				mSessionName = mSession.getName();
			}
			mViewHolder.name.setText(mSessionName);
			mViewHolder.name.setSelection(mSessionName.length());
			
			if(colorSet){
				mSessionImage = ColorUtil.recolorIconBicolor(mSessionColor, mThumb);
				mSession.setColorThumbnail(mSessionColor);
				mSession.setBitmap(mSessionImage);
			}else{
				if((mSessionImage = mSession.getBitmap()) == null) {
					mSessionImage = ColorUtil.recolorIconBicolor((mSessionColor = mSession.getColorThumbnail()), mThumb);
					mSession.setBitmap(mSessionImage);
				}
			}
		}

		mViewHolder.image.setImageBitmap(mSessionImage);
		
		mViewHolder.image.setOnClickListener(changeImageListener());
		mViewHolder.cancel.setOnClickListener(cancelListener());
		mViewHolder.save.setOnClickListener(saveListener());
	}
	
	private android.view.View.OnClickListener changeImageListener() {
		
		return new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSessionColor= ColorUtil.imageColorSelector();
				mSessionImage = ColorUtil.recolorIconBicolor(mSessionColor, mThumb);

				mViewHolder.image.setImageBitmap(mSessionImage);
			}
		};
	}
	
	private android.view.View.OnClickListener cancelListener(){
		return new android.view.View.OnClickListener(){
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		};
	}
	
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
						}
						
						mSession = new Session(name);
						mSession.setColorThumbnail(mSessionColor);
						mSession.setBitmap(mSessionImage);
						
						mSession.setId(dm.insertASession(mSession));

						(new Mediator()).getDataSession().add(mPos, mSession);
						
						toShow = Toast.makeText(mContext, R.string.add_session, Toast.LENGTH_SHORT);
					}else{
						if(toShow != null && toShow.getView().getWindowVisibility() == View.VISIBLE){
							toShow.cancel();
						}
						if(!(name.equals(mSession.getName()))){
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
					}
					toShow.show();
					dismiss();
				}else{
					toShow = Toast.makeText(mContext, R.string.no_value, Toast.LENGTH_SHORT);
					toShow.show();
				}
			}
		};
	}
	
	public String getStringToSave(){
		return mPos + DIVISOR + mViewHolder.name.getText().toString()+ DIVISOR + mSessionColor;
	}
	
	public void restoreValue(String name, int color){
		mSessionName = name;
		mSessionColor = color;
		colorSet = true;
	}
	 
	private class ViewHolder{
		private EditText name;
		private Button cancel;
		private Button save;
		private ImageView image;
	}
}