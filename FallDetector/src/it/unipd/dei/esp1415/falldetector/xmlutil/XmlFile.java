package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Xml;

public class XmlFile {
	
	private String mPath;
	private FileOutputStream mFileOS;
	private FileInputStream mFileIS;
	private boolean isReady = false;
	
	
	public XmlFile(String name, Context context){
		try {
			name = name + ".xml";
			mFileOS = context.openFileOutput(name, Context.MODE_PRIVATE);
			mFileIS = context.openFileInput(name);
			
			mPath = context.getFileStreamPath(name).getAbsolutePath();
			
			isReady = true;
			
		} catch (FileNotFoundException e) {
			isReady = false;
			e.printStackTrace();
		}
		
	}
	
	public XmlEditor edit(){
		try {
			if(mFileIS.available() == 0){
			    XmlSerializer xmlSerializer = Xml.newSerializer();              
			    StringWriter writer = new StringWriter();
			    xmlSerializer.setOutput(writer);
			    xmlSerializer.startDocument("UTF-8", true);
			    xmlSerializer.endDocument();
			    xmlSerializer.flush();
			    String dataWrite = writer.toString();
			    mFileOS.write(dataWrite.getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		catch (IllegalStateException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
		
		return new XmlEditor(this);
	}
	
	public XmlReader read(){
		return new XmlReader(this);
	}
	
	public void close(){
		try {
			mFileOS.close();
			mFileIS.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
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
}
