package at.ac.tuwien.thesis.caddc.data.format;

import org.w3c.dom.Document;

/**
 * Format to store xml data
 */
public class XMLFormat extends Format {

	private Document xmlDocument;
	
	public XMLFormat(Document xmlDocument) {
		this.xmlDocument = xmlDocument;
	}
	
	
}
