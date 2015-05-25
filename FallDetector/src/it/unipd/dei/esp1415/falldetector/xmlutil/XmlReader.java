package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class XmlReader {

	//TODO
	private XmlFile mFile;
	private Document mDoc;
	
	
	private boolean isReady = false;
	private boolean hasElement= false;
	private Element mainNode;
	
	protected XmlReader(XmlFile file){
		mFile = file;
		
		try {
			InputStream in = mFile.getFileInputStream();
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			
			if(in.available()>0 ){	
				mDoc = docBuilder.parse(in);
				mDoc.getDocumentElement().normalize();
				mainNode = (Element) mDoc.getFirstChild();
				
				hasElement = true;
			}else{
				
				hasElement = false;
			}
			
			isReady = true;
			
		} catch (ParserConfigurationException e) {
			isReady = false;
			e.printStackTrace();
		} catch (SAXException e) {
			isReady = false;
			e.printStackTrace();
		} catch (IOException e) {
			isReady = false;
			e.printStackTrace();
		}
		
	}
	
	/**
	 * [m]
	 * Method to see if the editor is ready or has not instance of it. 
	 * 
	 * @return true if is ready to work, false otherwise
	 */
	public boolean isReady(){
		return isReady;
	}
	
	public boolean hasElement(){
		return hasElement;
	}
	
	public Element getMainNode(){
		return mainNode;
	}
	
	public ArrayList<Element> getInternalNode(Element node){
		ArrayList<Element> tmp = new ArrayList<Element>();
		
		NodeList list = node.getChildNodes();
		for(int i= 0; i< list.getLength(); i++){
			tmp.add((Element) list.item(i));
		}
		
		return tmp;
	}
	
	public String[][] readNode(Element node){
		NamedNodeMap nnm = node.getAttributes();
		int l = nnm.getLength();
		
		String[][] tmp= new String[l][2];
		for(int i = 0; i<l ;i++){
			tmp[i][0] = ""+ nnm.;
			tmp[i][1] = "";
		}
		
		return tmp;
	}
	
	public String[] getKeys(){
		return null;
	}
	
	
}
