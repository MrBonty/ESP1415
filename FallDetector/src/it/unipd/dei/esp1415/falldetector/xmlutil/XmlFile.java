package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.content.Context;

public class XmlFile {
	
	private String mPath;
	private FileOutputStream mFileOS;
	private FileInputStream mFileIS;
	private boolean isReady = false;
	
	private Context mContext;
	private String mName;
	
	private Document mDoc = null;
	private boolean hasDoc = false;
	private Transformer mTransf;
	
	protected Element mainNode;
	
	
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
			
			
			mContext = context;
			mName = name;

			
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if(mFileIS.available()>0 ){	
				mDoc = docBuilder.parse(mFileIS);
			}else{
				mDoc = docBuilder.newDocument();
			}


			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			mTransf  = transformerFactory.newTransformer();
			
			mainNode = (Element) mDoc.getFirstChild();
			
			hasDoc = true;
			isReady = true;

		} catch (FileNotFoundException e) {
			isReady = false;
			hasDoc = false;
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			isReady = false;
			hasDoc = false;
			e.printStackTrace();
		} catch (SAXException e) {
			isReady = false;
			hasDoc = false;
			e.printStackTrace();
		} catch (IOException e) {
			isReady = false;
			hasDoc = false;
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			isReady = false;
			hasDoc = false;
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
	
	protected Document getDoc(){
		return mDoc;
	}
	
	protected boolean hasDoc(){
		return hasDoc;
	}
	
	protected Transformer getTransformer(){
		return mTransf;
	}
	

}
