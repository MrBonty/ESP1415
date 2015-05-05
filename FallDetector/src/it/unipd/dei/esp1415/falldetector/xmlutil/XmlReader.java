package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.FileInputStream;

public class XmlReader {

	//TODO
	private FileInputStream mFileIS;
	private XmlFile mFile;
	
	protected XmlReader(XmlFile file){
		mFile = file;
		mFileIS = mFile.getFileInputStream();
		
		
	}
	
	public String[] getKeys(){
		return null;
	}
	
	
}
