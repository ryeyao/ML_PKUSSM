package EntityDisambiguous;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

	public Parser(String charSet) throws ParserConfigurationException {
		this.charSet = charSet;
		builderFactory = DocumentBuilderFactory.newInstance();
		builder = builderFactory.newDocumentBuilder();
	}

	public EntityList parse(String filePath) {

		try {
//			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), this.charSet));
//			String xml = "";
//			String line;
//			while ((line = br.readLine()) != null) {
//				xml += line;
//				xml += "\n";
//			}
//			br.close();
//			ByteArrayInputStream encXml = new ByteArrayInputStream(xml.getBytes("utf-8"));
//			document = builder.parse(encXml);

			document = builder.parse(new File(filePath));
			EntityList target = new EntityList();
			Element root = document.getDocumentElement();
			NodeList e_list = root.getElementsByTagName("Entity");
			for (int i = 0; i < e_list.getLength(); i++) {
				Node n = e_list.item(i);
				Element e = (Element) n;
				String id = e.getAttribute("id");
				// String id = n.getAttribute("id");
																		// child
				target.put(id, e.getElementsByTagName("text").item(0).getTextContent());
			}
			return target;

		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public HashMap<String, EntityList> parseKB(String dirPath) {
		HashMap<String, EntityList> ell = new HashMap<String, EntityList>();
		File dir = new File(dirPath);

		if (!dir.isDirectory()) {
			System.out.println("Not a directory!");
			return ell;
		}

		String[] files = dir.list();
		for (String fname : files) {
			ell.put(fname, this.parse(dirPath + "/" + fname.replace(".arff", "")));
		}
		return ell;
	}
}
