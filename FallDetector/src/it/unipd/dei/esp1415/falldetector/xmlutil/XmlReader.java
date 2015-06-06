package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * Class to read an exemplar of XmlFile
 */
public class XmlReader {

	//TODO
	private XmlFile mFile;
	private Document mDoc;
	
	
	private boolean isReady = false;
	private boolean hasElement= false;
	private Element mainNode;
	

	private static final int NOT_VALUES = 0;
	
	protected XmlReader(XmlFile file){
		mFile = file;
		InputStream in = mFile.getFileInputStream();
		
		if(mFile.hasDocSet()){
			mDoc = mFile.getDoc();
			mDoc.getDocumentElement().normalize();
			mainNode = (Element) mDoc.getFirstChild();
			
			hasElement = true;
			
			isReady = true;
			
//			try {
//				if(in.available()>0 ){	
//					mDoc.getDocumentElement().normalize();
//					mainNode = (Element) mDoc.getFirstChild();
//
//					hasElement = true;
//				}else{
//
//					hasElement = false;
//				}
//
//				isReady = true;
//			} catch (IOException e) {
//				isReady = false;
//				e.printStackTrace();
//			}
			
		}else{
			try {
				DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

				if(in.available()>0 ){	
					mDoc = docBuilder.parse(in);
					mDoc.getDocumentElement().normalize();
					mainNode = (Element) mDoc.getFirstChild();

					hasElement = true;
				}else{

					hasElement = false;
				}

				mFile.setDoc(mDoc);
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
	
	/**
	 * [m]
	 * Method to see if a document has element
	 * 
	 * @return true if the document has element, false otherwise
	 */
	public boolean hasElement(){
		return hasElement;
	}
	
	/**
	 * [m]
	 * Method to see if a node has child
	 * 
	 * @param node the node to look in
	 * @return true if the document has childNode, false otherwise
	 */
	public boolean hasChild(Element node){
		return node.hasChildNodes();
	}
	
	/**
	 * [m]
	 * Method to see if a  node has attributes
	 * 
	 * @param node the node to look in
	 * @return true if the document has attributes, false otherwise
	 */
	public boolean hasAttributes(Element node){
		return node.hasAttributes();
	}
	
	/**
	 * [m]
	 * Method to get the main node
	 * @return the main node as Element
	 */
	public Element getMainNode(){
		return mainNode;
	}
	
	/**
	 * [m]
	 * Return all internal nodes of a node
	 * 
	 * @param node the node to look in
	 * @return an ArrayList of Element, null if has no internal node
	 */
	public ArrayList<Element> getInternalNodes(Element node){
		ArrayList<Element> tmp = new ArrayList<Element>();
		
		NodeList list = node.getChildNodes();
		for(int i= 0; i< list.getLength(); i++){
			tmp.add((Element) list.item(i));
		}
		
		return tmp;
	}

	/**
	 * [m]
	 * Return specific internal nodes of a node
	 * 
	 * @param node the parent node
	 * @param attributeKey the key of attributes to search for internal node
	 * @param attributeValue the value of attributes to search for internal node
	 * @param isFirstId 
	 * @return an ArrayList of Element, null if has no internal node
	 */
	public ArrayList<Element> getInternalNodes(Element node, String attributeKey, String attributeValue, boolean isFirstId){
		String[] tmp1 = {attributeKey};
		String[] tmp2 = {attributeValue};
		return getInternalNodes(node, tmp1, tmp2, isFirstId);
	}
	
	
	/**
	 * [m]
	 * Return specific internal nodes of a node
	 * 
	 * @param node the parent node
	 * @param attributeKey the keys of attributes to search for internal node
	 * @param attributeValue the values of attributes to search for internal node
	 * @param isFirstId 
	 * @return an ArrayList of Element, null if has no internal node
	 */
	public ArrayList<Element> getInternalNodes(Element node, String[] attributeKey, String[] attributeValue, boolean isFirstId){
		ArrayList<Element> tmp = new ArrayList<Element>();
		
		int kl = NOT_VALUES;
		int vl = NOT_VALUES;
		if(attributeKey != null || attributeValue != null){
			kl = attributeKey.length; 
			vl = attributeValue.length;
			
			if(kl != vl){
				return tmp;
			}
		}
		
		NodeList list = node.getChildNodes();
		for(int i= 0; i< list.getLength(); i++){
			Element toSearch = (Element) list.item(i);
			
			boolean found = false;
			if(isFirstId){
				if(toSearch.getAttribute(attributeKey[0]).equals(attributeValue[0])){
					found = true;
				}
				
			}else{
				if(kl != NOT_VALUES){
				for(int j = 0; j < kl; j++){
					if(toSearch.getAttribute(attributeKey[j]).equals(attributeValue[j])){
						if(j == kl-1){
							found = true;
						}
					}else{
						break;
					}
				}
				}else{
					found = true;
				}
			}
			
			if (found == true){
				tmp.add((Element) list.item(i));
			}
		}
		
		return tmp;
	}
	
	/**
	 * [m]
	 * Return specific internal nodes of a node
	 * 
	 * @param node the parent node	
	 * @param nodeName the name of the node to search
	 * @param attributeKey the keys of attributes to search for internal node
	 * @param attributeValue the values of attributes to search for internal node
	 * @param isFirstId 
	 * @return an ArrayList of Element, null if has no internal node
	 */
	public Element getAInternalNode(Element parent, String nodeName, String[] attributeKey, String[] attributeValue, boolean isFirstId){
		Element toSearch = null;
		
		NodeList nodes = parent.getElementsByTagName(nodeName);

		int kl = NOT_VALUES;
		int vl = NOT_VALUES;
		if(attributeKey != null || attributeValue != null){
			kl = attributeKey.length; 
			vl = attributeValue.length;
			
			if(kl != vl){
				return toSearch;
			}
		}
		
		for(int i= 0; i < nodes.getLength(); i++){
			toSearch = (Element) nodes.item(i);
			
			boolean found = false;
			if(isFirstId){
				
				if(toSearch.getAttribute(attributeKey[0]).equals(attributeValue[0])){
					found = true;
				}				
			}else{
				if(kl != NOT_VALUES){
				for(int j = 0; j < kl; j++){
					if(toSearch.getAttribute(attributeKey[j]).equals(attributeValue[j])){
						if(j == kl-1){
							found = true;
						}
					}else{
						break;
					}
				}
				}else{
					found = true;
				}
			}
			
			if (found == true){
				break;
			}
		}
		
		return toSearch;
	}
	
	/**
	 * [m]
	 * Method to read Attributes of the node
	 * 
	 * @param node the node to read the attributes of
	 * @return a bidimensional array of String (lines x Columns) of length (n x 2)  
	 */
	public String[][] readNodeAttributes(Element node){
		NamedNodeMap nnm = node.getAttributes();
		int l = nnm.getLength();
		
		String[][] tmp= new String[l][2];
		for(int i = 0; i<l ;i++){
			if(nnm.item(i) instanceof Attr){
				Attr e = (Attr) nnm.item(i);
				String name = e.getNodeName();
				String value = e.getNodeValue();
			
				tmp[i][0] = ""+ name;
				tmp[i][1] = ""+ value;
			}
		}
		
		return tmp;
	}
	
	/**
	 * [m]
	 * Method to return name of Attribute's keys
	 * 
	 * @param node the node to look of attribute's keys 
	 * @return an array of String with the value of keys
	 */
	public String[] getAttributesKeys(Element node){
		NamedNodeMap nnm = node.getAttributes();
		int l = nnm.getLength();
		
		String[]tmp= new String[l];
		for(int i = 0; i<l ;i++){
			if(nnm.item(i) instanceof Attr){
				Attr e = (Attr) nnm.item(i);
				String name = e.getNodeName();
			
				tmp[i] = ""+ name;
			}
		}
		
		return tmp;
	}
	
	/**
	 * [m]
	 * Method to return name of Attribute's value
	 * 
	 * @param node
	 * @param attributeKey
	 * @return
	 */
	public String[] getAttributesValue(Element node, String[] attributeKey){
		int l = attributeKey.length;
		String[] tmp= new String[l];
		for(int i = 0; i<l ;i++){
			tmp[i]= node.getAttribute(attributeKey[i]);
		}
		
		return tmp;
	}
	
	/**
	 * [m]
	 * Method to get a Node
	 * 
	 * @param nodeName the name of the node to get
	 * @param attributeKey the keys of node
	 * @param attributeValue the values of the node
	 * @param isFirstId set to true if first attribute is a id 
	 * @return the node searched, or null if is not found or if attributeKey.length is different from attributeValue.length
	 */
	public Element getANode(String nodeName, String[] attributeKey, String[] attributeValue, boolean isFirstId){
		Element toSearch = null;
		
		int kl = NOT_VALUES;
		int vl = NOT_VALUES;
		if(attributeKey != null || attributeValue != null){
			kl = attributeKey.length; 
			vl = attributeValue.length;
			
			if(kl != vl){
				return toSearch;
			}
		}
		
		NodeList nodes = mDoc.getElementsByTagName(nodeName);
		
		for(int i= 0; i < nodes.getLength(); i++){
			toSearch = (Element) nodes.item(i);
			boolean found = false;
			if(isFirstId){
				
				if(toSearch.getAttribute(attributeKey[0]).equals(attributeValue[0])){
					found = true;
				}				
			}else{
				if(kl != NOT_VALUES){
				for(int j = 0; j < kl; j++){
					if(toSearch.getAttribute(attributeKey[j]).equals(attributeValue[j])){
						if(j == kl-1){
							found = true;
						}
					}else{
						break;
					}
				}
				}else{
					found = true;
				}
			}
			
			if (found == true){
				break;
			}
		}
		
		return toSearch;
	}
}
