package it.unipd.dei.esp1415.falldetector.xmlutil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
/**
 * Class to create or modify an exemplar of XmlFile
 */
public class XmlEditor{

	private String mPath;
	private XmlFile mFile;
	private Document mDoc;
	private Transformer mTransf;
	 
	private Element lastNode;
	private Element mainNode;
	
	private boolean isReady = false;
	
	public static final int ALL_GOOD = 0;
	public static final int MISS_MATCH_ERROR = -1;
	public static final int NODE_NOT_ADD = -2;
	public static final int NEW_DATA_NOT_SAVED = -3;
	public static final int NODE_NOT_FOUND = -4;
	public static final int NOTHING_TO_DO = -5;
	public static final int NO_MAIN_NODE = -6;
	
	
	private static final int NOT_VALUES = 0;
	
	/**
	 * [c]
	 * Protected constructor invoked by method edit() on XmlFile.
	 * 
	 * @param file the file to edit
	 * @see edit
	 */
	protected XmlEditor(XmlFile file){
		mFile = file;
		mPath = mFile.getPath();
		
		try {
			InputStream in = mFile.getFileInputStream();
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if(in.available()>0 ){	
				mDoc = docBuilder.parse(in);
				mainNode = (Element) mDoc.getFirstChild();
			}else{
				mDoc = docBuilder.newDocument();
			}
			
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			mTransf  = transformerFactory.newTransformer();

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
		} catch (TransformerConfigurationException e) {
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
	
	/**
	 * [m]
	 * Method to add the main nodes
	 * 
	 * @param nodeName the name for the node
	 * @param attributeKey the array of the name of the attribute
	 * @param attributeValue the array of the value of the attribute
	 * @param isFirstId set to true if the element stored in position 0 on the array is the ID
	 * @return ALL_GOOD if has not encounter error, or MISS_MATCH_ERROR if length of attributeValue is different form attributeKey, or NEW_DATA_NOT_SAVED if has encounter an problem on saving data.
	 * The node is stored, to pick it use method getLastNodeUsed
	 * @see getLastNodeUsed
	 */
	public int addMainNode(String nodeName, String[] attributeKey, String[] attributeValue, boolean isFirstId){
		int kl = NOT_VALUES;
		int vl = NOT_VALUES;
		if(attributeKey != null || attributeValue != null){
			kl = attributeKey.length; 
			vl = attributeValue.length;
			
			if(kl != vl){
				return MISS_MATCH_ERROR;
			}
		}
		
		Element rootEl =  mDoc.createElement(nodeName);
		mDoc.appendChild(rootEl);
		
		if(kl != NOT_VALUES){
			for(int i = 0; i < kl; i++){
				rootEl.setAttribute(attributeKey[i], attributeValue[i]);
			}
			
			if(isFirstId){
				rootEl.setIdAttribute(attributeKey[0], isFirstId);
			}
		}
		
		lastNode = rootEl;
		if(mainNode == null){
			mainNode =  rootEl;
		}
		return save();
	}
	
	/**
	 * [m]
	 * Method to add nodes to main the main nodes
	 * 
	 * @param nodeName the name for the node
	 * @param attributeKey the array of the name of the attribute
	 * @param attributeValue the array of the value of the attribute
	 * @param isFirstId set to true if the element stored in position 0 on the array is the ID
	 * @return ALL_GOOD if has not encounter error, or MISS_MATCH_ERROR if length of attributeValue is different form attributeKey, or NEW_DATA_NOT_SAVED if has encounter an problem on saving data.
	 * The node is stored, to pick it use method getLastNodeUsed
	 * @see getLastNodeUsed
	 */
	public int addNode(String nodeName, String[] attributeKey, String[] attributeValue, boolean isFirstId){
		if(mainNode == null){
			return NO_MAIN_NODE; 
		}
		
		return addInternalNode(mainNode, nodeName, attributeKey, attributeValue, isFirstId);
	}
	
	/**
	 * [m]
	 * Method to append an internal node to a main node, it use method getANode to find parent one.
	 * 
	 * @param parentName the name of the parent node
	 * @param parentKey the array of the name of the attribute for the parent node 
	 * @param parentValue the array of the value of the attribute for the parent node
	 * @param parentHasId set to true if the element stored in position 0 on the array of parent is the ID, if is true can use only a item for parent array
	 * @param nodeName the name for the internal node
	 * @param attributeKey the array of the name of the attribute for the internal node 
	 * @param attributeValue the array of the value of the attribute for the internal node
	 * @param isFirstId set to true if the element stored in position 0 on the array is the ID for the internal node
	 * @return ALL_GOOD if has not encounter error, or MISS_MATCH_ERROR if length of attributeValue is different form attributeKey, 
	 * or NEW_DATA_NOT_SAVED if has encounter an problem on saving data, or NODE_NOT_FOUND if parent node has not found.
	 * The node is stored, to pick it use method getLastNodeUsed
	 * 
	 * @see getANode
	 * @see getLastNodeUsed
	 */
	public int addInternalNode(String parentName, String[] parentKey, 
			String[] parentValue, boolean parentHasId,String nodeName, String[] attributeKey, String[] attributeValue, boolean isFirstId){
		Element parent = getANode(parentName, parentKey, parentValue, parentHasId);
		if(parent == null){
			return NODE_NOT_FOUND;
		}
		
		return addInternalNode(parent, nodeName, attributeKey, attributeValue, isFirstId);
	}
	
	/**
	 * [m]
	 * Method to append an internal node to a main node passed to the method.
	 * 
	 * @param parent the main node
	 * @param nodeName the name for the internal node
	 * @param attributeKey the array of the name of the attribute for the internal node 
	 * @param attributeValue the array of the value of the attribute for the internal node
	 * @param isFirstId set to true if the element stored in position 0 on the array is the ID for the internal node
	 * @return ALL_GOOD if has not encounter error, or MISS_MATCH_ERROR if length of attributeValue is different form attributeKey, 
	 * or NEW_DATA_NOT_SAVED if has encounter an problem on saving data, or NODE_NOT_FOUND if parent node has not found.
	 * The node is stored, to pick it use method getLastNodeUsed
	 * 
	 * @see getLastNodeUsed
	 */
	public int addInternalNode(Element parent, String nodeName,String[] attributeKey, String[] attributeValue, boolean isFirstId){
		int kl = NOT_VALUES;
		int vl = NOT_VALUES;
		if(attributeKey != null || attributeValue != null){
			kl = attributeKey.length; 
			vl = attributeValue.length;
			
			if(kl != vl){
				return MISS_MATCH_ERROR;
			}
		}
		
		Element rootEl = mDoc.createElement(nodeName);
		parent.appendChild(rootEl);
		

		
		if(kl != NOT_VALUES){
			for(int i = 0; i < kl; i++){
				rootEl.setAttribute(attributeKey[i], attributeValue[i]);
			}	

			if(isFirstId){
				rootEl.setIdAttribute(attributeKey[0], isFirstId);
			}
		}

		
		lastNode = rootEl;
		return save();
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
	
	/**
	 * [m]
	 * Private method to upgrade file
	 * 
	 * @return NEW_DATA_NOT_SAVED or ALL_GOOD
	 */
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
	
	/**
	 * [m]
	 * Method to get the last Node Used
	 * 
	 * @return the last node used
	 */
	public Element getLastNodeUsed(){
		return lastNode;
	}

	/**
	 * [m]
	 * Method to modify attribute value or add new attribute
	 * 
	 * @param node the node to modify
	 * @param attributeKey new key or key to modify value
	 * @param attributeValue the values of the attributes
	 * @return ALL_GOOD if has not encounter error, or MISS_MATCH_ERROR if length of attributeValue is different form attributeKey, 
	 * or NOTHING_TO_DO if (attributeKey.length == 0 || attributeValue.length == 0) 
	 * or NEW_DATA_NOT_SAVED if has encounter an problem on saving data.
	 */
	public int modifyOrAddAttribute(Element node, String[] attributeKey, String[] attributeValue){
		int kl = attributeKey.length; 
		int vl = attributeValue.length;
		
		if(kl != vl){
			return MISS_MATCH_ERROR;
		}
		if(kl == 0 || vl == 0){
			return NOTHING_TO_DO;
		}
		
		for(int i= 0; i< kl; i++){
			node.setAttribute(attributeKey[i], attributeValue[i]);
		}
		
		return save();
	}
	
	/**
	 * [m]
	 * Method to remove attribute from a node
	 * 
	 * @param node the node to modify
	 * @param attributeKey the key of attribute to remove
	 * @return ALL_GOOD if has not encounter error, or NEW_DATA_NOT_SAVED if has encounter an problem on saving data. 
	 */
	public int removeAttribute(Element node, String[] attributeKey){
		int kl = attributeKey.length; 
		
		for(int i= 0; i< kl; i++){
			node.removeAttribute(attributeKey[i]);
		}
		
		return save();
	}
	
	/**
	 * [m]
	 * Method to remove a node
	 * 
	 * @param node the node to remove
	 * @return ALL_GOOD if has not encounter error, or NEW_DATA_NOT_SAVED if has encounter an problem on saving data. 
	 */
	public int removeNode(Element node){
		mainNode.removeChild(node);
		
		return save();
	}
	
	/**
	 * [m]
	 * Method to remove a inner node
	 * 
	 * @param parent the parent node
	 * @param node the node to remove 
	 * @return ALL_GOOD if has not encounter error, or NEW_DATA_NOT_SAVED if has encounter an problem on saving data.
	 */
	public int removeInnerNode(Element parent, Element node){
		parent.removeChild(node);
		
		return save();
	}
	
	/**
	 * [m]
	 * Method to remove main node
	 * 
	 * @return ALL_GOOD if has not encounter error, or NEW_DATA_NOT_SAVED if has encounter an problem on saving data.
	 */
	public int removeMainNode(){
		mDoc.removeChild(mainNode);
		
		return save();
	}
	
	/**
	 * [m]
	 * Method to count all node with same name
	 * 
	 * @param nodeName
	 * @return the number of node matching
	 */
	public int countNodesByName(String nodeName){
		NodeList nodes = mDoc.getElementsByTagName(nodeName);
		return nodes.getLength();
	}
	
	/**
	 * [m]
	 * Method to count all inner node with same name for a node
	 * 
	 * @param node the parent node
	 * @param nodeName the inner node name
	 * @return the number of inner node matching
	 */
	public int countInnerNodesByName(Element node, String nodeName){
		NodeList nodes = node.getElementsByTagName(nodeName);
		return nodes.getLength();
	}
	
	/**
	 * [m]
	 * Method to get main node (the root of the document)
	 * 
	 * @return main node
	 */
	public Element getMainNode(){
		return mainNode;
	}
}
