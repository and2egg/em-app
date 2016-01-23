package at.ac.tuwien.thesis.caddc.data.parse.types.da;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;

/**
 * Class to fetch XML content from a URL
 */
public class ParserMaineDAXML implements Parser {

	/**
	 * Method to parse the prices arranged in a XLS file
	 * @param resource the resource to parse
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#parse()
	 */
	@Override
	public List<String> parse(Resource resource) throws ParseException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		List<String> priceList = new ArrayList<String>();
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(resource.getFile());  //db.parse(new URL(url).openStream())
			NodeList list = doc.getElementsByTagName("HourlyLmp");
			
			for(int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				Node date = n.getFirstChild();
				Node lmp = date.getNextSibling().getNextSibling();
				System.out.println("Node "+i+": date = "+date+", lmp Price: "+lmp);
				// Date;Hour;Price
				// TODO Check format and parse correctly!
				priceList.add(date.toString()+";"+";"+lmp.toString());
			}
			return priceList;
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
