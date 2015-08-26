package at.ac.tuwien.thesis.caddc.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import at.ac.tuwien.thesis.caddc.data.format.XMLFormat;

/**
 * Class to fetch XML content from a URL
 */
public class XMLDataFetch implements IDataFetch {

	/* (non-Javadoc)
	 * @see at.ac.tuwien.thesis.caddc.data.IDataFetch#fetch(java.lang.String)
	 */
	@Override
	public XMLFormat fetch(String url) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(url).openStream());
			NodeList list = doc.getElementsByTagName("HourlyLmp");
			for(int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				Node date = n.getFirstChild();
				Node lmp = date.getNextSibling().getNextSibling();
				System.out.println("Node "+i+": date = "+date+", lmp Price: "+lmp);
			}
			return new XMLFormat(doc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
