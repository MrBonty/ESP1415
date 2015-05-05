package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class XmlEditor{
	
	//TODO
	private String mPath;
	private XmlFile mFile;
	private Document mDoc;
	private Transformer mTransf;
	 
	private boolean isReady = false;
	
	public static final int ALL_GOOD = 0;
	public static final int MISS_MATCH_ERROR = -1;
	public static final int NODE_NOT_ADD = -2;
	public static final int NEW_DATA_NOT_SAVED = -3;
	public static final int NODE_NOT_FOUND = -4;
	
	
	private static final int NOT_VALUES = 0;
	
	
	protected XmlEditor(XmlFile file){
		mFile = file;
		mPath = mFile.getPath();
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			mDoc = documentBuilder.parse(mPath);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			mTransf  = transformerFactory.newTransformer();

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
		} catch (TransformerConfigurationException e) {
			isReady = false;
			e.printStackTrace();
		}
	}
	
	public boolean isReady(){
		return isReady;
	}
	
	public int addNode(String nodeName, String[] attributeKey, String[] attributeValue){
		int kl = attributeKey.length; 
		int vl = attributeValue.length;
		
		if(kl != vl){
			return MISS_MATCH_ERROR;
		}
		
		Element rootEl =  mDoc.createElement(nodeName);
		mDoc.appendChild(rootEl);
		if(kl != NOT_VALUES){
			for(int i = 0; i < kl; i++){
				rootEl.setAttribute(attributeKey[i], attributeValue[i]);
			}
		}
		
		return save();
	}
	
	public int addInternalNode(String parentName, String[] parentKey, 
			String[] parentValue, String nodeName, String[] attributeKey, String[] attributeValue){
		Element parent = getANode(parentName, parentKey, parentValue);
		if(parent == null){
			return NODE_NOT_FOUND;
		}
		
		return addInternalNode(parent, nodeName, attributeKey, attributeValue);
	}
	
	public int addInternalNode(Element parent, String nodeName, String[] attributeKey, String[] attributeValue){
		int kl = attributeKey.length; 
		int vl = attributeValue.length;
		
		if(kl != vl){
			return MISS_MATCH_ERROR;
		}
		
		Element rootEl = mDoc.createElement(nodeName);
		parent.appendChild(rootEl);
		if(kl != NOT_VALUES){
			for(int i = 0; i < kl; i++){
				rootEl.setAttribute(attributeKey[i], attributeValue[i]);
			}
		}
		
		return save();
	}
	
	
	
	public Element getANode(String nodeName, String[] attributeKey, String[] attributeValue){
		Element toSearch = null;
		NodeList nodes = mDoc.getElementsByTagName(nodeName);
		for(int i= 0; i < nodes.getLength(); i++){
			
		}
		
		return toSearch;
	}
	
	
	private int save(){
		DOMSource domSource = new DOMSource(mDoc);
		StreamResult streamResult = new StreamResult(new File(mPath));
		
		try {
			mTransf.transform(domSource, streamResult);
		} catch (TransformerException e) {
			e.printStackTrace();
			return NEW_DATA_NOT_SAVED;
		}
		
		return ALL_GOOD;
	}
}

