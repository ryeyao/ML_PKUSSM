package EntityDisambiguous;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Parser {
	DocumentBuilderFactory builderFactory;
	DocumentBuilder builder;
	Document document;
	String charSet;
	public Parser (String charSet) throws ParserConfigurationException {
		this.charSet = charSet;
		builderFactory = DocumentBuilderFactory
					.newInstance();
		builder = builderFactory.newDocumentBuilder();
	}
	
	public EntityList parse (String filePath) {
		
		try {
			document = builder.parse(new File(filePath));
			
			EntityList target = new EntityList();

			NodeList e_list = document.getChildNodes();
			for (int i = 0; i < e_list.getLength(); i++) {
				Node n = e_list.item(i);
				String id = ((Element)n).getAttribute("id");
				NodeList text_list = n.getChildNodes(); // Only one child
				for (int j = 0; j < text_list.getLength(); j++) {
					Node t = text_list.item(j);
					target.add(Integer.valueOf(id), t.getTextContent());
				}
			}
			return target;
			
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public HashMap<String, EntityList> parseKB (String dirPath) {
		HashMap<String, EntityList> ell = new HashMap<String, EntityList>();
		File dir = new File (dirPath);
		
		if (!dir.isDirectory()) {
			System.out.println("Not a directory!");
			return ell;
		}
		
		String[] files = dir.list();
		for (String fname: files) {
			ell.put(fname, this.parse(fname));
		}
		return ell;
	}
}
