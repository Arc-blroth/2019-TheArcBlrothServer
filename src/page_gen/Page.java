package page_gen;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import server.ResourceLoader;

public class Page {
	
	private String path;
	private Document doc;
	private DocumentBuilderFactory dbf;
	
	public Page(String dir) {
		this.path = dir;
		//Get file xml
		try {
			dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(false); //UGH
			dbf.setCoalescing(true);
			dbf.setExpandEntityReferences(true);
			dbf.setIgnoringComments(false);
			doc = dbf.newDocumentBuilder().parse(ResourceLoader.getResourceAsStream(path.substring(1)));
			doc = constructHTML();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private Document constructHTML() {
		try {
			//Step 1: HTML, HEAD, and BODY
			Document html_doc =  dbf.newDocumentBuilder().newDocument();
			Element html = html_doc.createElement("html");
			Element head = html_doc.createElement("head");
			Element title = html_doc.createElement("title");
			Element meta_descript = html_doc.createElement("meta");
			Element body = html_doc.createElement("body");
			html_doc.appendChild(html);
			html.appendChild(head);
			head.appendChild(title);
			head.appendChild(meta_descript);
			html.appendChild(body);
			
			//Step 2: Meta
			Node doc_head = PageUtils.findSubNode("arc-head", doc.getDocumentElement());
			if(doc_head != null) {
				PageUtils.setMetaText(doc_head, title, ArcTags.title);
				PageUtils.copyAll(doc_head, head, "link");
				PageUtils.setMetaAttr(doc_head, meta_descript, ArcTags.descript, "description");
			}
			
			//Step 3: The Body
			PageUtils.copyAndResolveNodes(doc.getDocumentElement(), body, ArcTags.body);
			
			//Step 4: Layout
			Node lay = PageUtils.findSubNode("arc-layout", doc_head);
			if(lay != null && lay instanceof Element) {
				String lay_src = ((Element) lay).getAttribute("src");
				if(lay_src != "") {
					PageLayout pl = PageGenerator.getLayout(lay_src);
					pl.wrapPage(head, body);
				}
			}
			
			return html_doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public byte[] toBytes() {
		try {
			StringWriter writer = new StringWriter();
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.METHOD, "html");
			t.setOutputProperty(OutputKeys.INDENT, "no");
			t.transform(new DOMSource(doc), new StreamResult(writer));
			return ("<!DOCTYPE html>\n" + writer.getBuffer().toString()).getBytes();
		} catch (TransformerException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
			return null;
		}
	}
}
