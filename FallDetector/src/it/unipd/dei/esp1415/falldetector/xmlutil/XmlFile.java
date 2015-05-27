package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.w3c.dom.Document;

import android.content.Context;

public class XmlFile {
	
	private String mPath;
	private FileOutputStream mFileOS;
	private FileInputStream mFileIS;
	private boolean isReady = false;
	
	private Context mContext;
	private String mName;
	
	private Document mDoc = null;
	
	
	/**
	 * [c]
	 * Create a Xml file or get it if exist. USE ONLY A MAIN NODE (or ROOT)
	 * CLOSE IT AFTER USE
	 * 
	 * @param name the name of file
	 * @param context the Application context
	 */
	public XmlFile(String name, Context context){
		try {
			name = name + ".xml";
			mFileOS = context.openFileOutput(name, Context.MODE_APPEND);
			mFileIS = context.openFileInput(name);
			
			mPath = context.getFileStreamPath(name).getAbsolutePath();
			
			isReady = true;
			mContext = context;
			mName = name;
			
		} catch (FileNotFoundException e) {
			isReady = false;
			e.printStackTrace();
		}
		
	}
	
	/**
	 * [m]
	 * Method to edit the file
	 * 
	 * @return an exemplar of {@link XmlEditor}, or null if an error occurred
	 */
	public XmlEditor edit(){
		if(mFileIS == null){
			try {
				mFileIS = mContext.openFileInput(mName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return new XmlEditor(this);
	}
	
	/**
	 * [m]
	 * Method to read the file
	 * 
	 * @return an exemplar of {@link XmlReader}, or null if an error occurred
	 */
	public XmlReader read(){
		if(mFileIS == null){
			try {
				mFileIS = mContext.openFileInput(mName);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return new XmlReader(this);
	}
	
	/**
	 * [m]
	 * Method to close file
	 */
	public void close(){
		mFileOS = null;
		mFileIS = null;
	}
	
	/**
	 * [m]
	 * Method to see if is all ready 
	 * 
	 * @return true if no error occurred, false otherwise
	 */
	public boolean isReady(){
		return isReady;
	}
	
	protected String getPath(){
		return mPath;
	}
	
	protected FileInputStream getFileInputStream(){
		return mFileIS;
	}
	
	protected FileOutputStream getFileOutputStream(){
		return mFileOS;
	}
	
	protected void setDoc(Document doc){
		mDoc = doc;
	}
	
	protected Document getDoc(){
		return mDoc;
	}
	
	protected boolean hasDocSet(){
		return mDoc == null;
	}
}
