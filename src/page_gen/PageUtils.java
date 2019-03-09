package page_gen;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PageUtils {

	static Node findSubNode(String name, Node node) {
	    if (node.getNodeType() != Node.ELEMENT_NODE) {
	    	System.err.println("Error: Search node not of element type");
	    	return null;
	    }
	    if (!node.hasChildNodes()) return null;
	
	    NodeList list = node.getChildNodes();
	    for (int i=0; i < list.getLength(); i++) {
	        Node subnode = list.item(i);
	        if (subnode.getNodeType() == Node.ELEMENT_NODE) {
	           if (subnode.getNodeName().equals(name)) 
	               return subnode;
	        }
	    }
	    return null;
	}
	
	static Node findSubNodeRecursive(String name, Node node) {
	    if (node.getNodeType() != Node.ELEMENT_NODE) {
	    	System.err.println("Error: Search node not of element type");
	    	return null;
	    }
	    if (!node.hasChildNodes()) return null;
	
	    NodeList list = node.getChildNodes();
	    for (int i=0; i < list.getLength(); i++) {
	        Node subnode = list.item(i);
	        if (subnode.getNodeType() == Node.ELEMENT_NODE) {
	           if (subnode.getNodeName().equals(name)) return subnode;
	           else if(subnode.hasChildNodes()) {
	        	   Node subsubnode = findSubNodeRecursive(name, subnode);
	        	   if(subsubnode != null) return subsubnode;
	           }
	        }
	    }
	    return null;
	}

	static void setMetaText(Node from, Node toBeSet, String elementType) {
		Node n = findSubNode(elementType, from);
		if(n != null) toBeSet.setTextContent(n.getTextContent());
	}

	static void setMetaAttr(Node from, Element toBeSet, String elementType, String attributeName) {
		Node n = findSubNode(elementType, from);
		if(n != null) toBeSet.setAttribute(attributeName, n.getTextContent());
	}
	
	static void copyAll(Node from, Node toBeSet) {
		Node n = from;
		if(n != null && n.hasChildNodes()) {
			NodeList nl = n.getChildNodes();
			for(int i = 0; i < nl.getLength(); i++) {
				Node n2 = nl.item(i).cloneNode(true);
				toBeSet.getOwnerDocument().adoptNode(n2);
				toBeSet.appendChild(n2);
			}
		}
	}	
	
	static void copyAll(Node from, Node toBeSet, String elementType) {
		Node n = from;
		if(n != null && n.hasChildNodes()) {
			NodeList nl = n.getChildNodes();
			for(int i = 0; i < nl.getLength(); i++) {
				if(nl.item(i) instanceof Element) {
					Node n2 = (Element)(nl.item(i).cloneNode(true));
					if(n2.getNodeName().equals(elementType)) {
						toBeSet.getOwnerDocument().adoptNode(n2);
						toBeSet.appendChild(n2);
					}
				}
			}
		}
	}
	
	static void copyAndResolveNodes(Node from, Node toBeSet, String elementType) {
		Node n = findSubNode(elementType, from);
		if(n != null && n.hasChildNodes()) {
			NodeList nl = n.getChildNodes();
			for(int i = 0; i < nl.getLength(); i++) {
				Node n2 = nl.item(i).cloneNode(true);
				toBeSet.getOwnerDocument().adoptNode(n2);
				toBeSet.appendChild(n2);
			}
		}
	}
	
	static void insertInto(NodeList from, Node into) {
		if(from.getLength() > 0) {
			for(int i = 0; i < from.getLength(); i++) {
				Node n2 = from.item(i).cloneNode(true);
				into.getOwnerDocument().adoptNode(n2);
				into.appendChild(n2);
			}
		}
	}
	
	static void removeAllChilds(Node parent) {
		if(parent != null) {
			if(parent.hasChildNodes()) {
				NodeList nl = parent.getChildNodes();
				for(int i = 0; i < nl.getLength(); i++) {
					parent.removeChild(nl.item(i));
				}
			}
			parent.setNodeValue("");
			parent.setTextContent("");
		}
	}
	
}
