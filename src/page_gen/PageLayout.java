package page_gen;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import server.ResourceLoader;

public class PageLayout {
	
	private String path;
	private PageLayout layout;
	private Document doc;
	private DocumentBuilderFactory dbf;
	
	public PageLayout(String dir) {
		this.path = dir;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(false); //UGH
			dbf.setCoalescing(true);
			dbf.setExpandEntityReferences(true);
			dbf.setIgnoringComments(true); //'Cause who needs comments in layouts?
			doc = dbf.newDocumentBuilder().parse(ResourceLoader.getResourceAsStream(path.substring(1)));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void wrapPage(Node head, Node body) {
		Node doc_head = PageUtils.findSubNode(ArcTags.head, doc.getDocumentElement());
		if(doc_head != null) {
			PageUtils.copyAll(doc_head, head);
		}
		Node doc_body = PageUtils.findSubNode(ArcTags.body, doc.getDocumentElement());
		Node doc_page = PageUtils.findSubNodeRecursive(ArcTags.page, doc_body);
		if(doc_body != null && doc_page != null) {
			PageUtils.copyAll(body, doc_page.getParentNode());
			doc_page.getParentNode().removeChild(doc_page);
			Node new_body = doc_body.cloneNode(true);
			PageUtils.removeAllChilds(body);
			PageUtils.insertInto(new_body.getChildNodes(), body);
		}
	}
}
