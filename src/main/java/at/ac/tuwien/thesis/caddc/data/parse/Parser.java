package at.ac.tuwien.thesis.caddc.data.parse;

import at.ac.tuwien.thesis.caddc.data.parse.types.CSVParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.JSONParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XLSParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XMLParser;

/**
 * 
 */
public interface Parser {

	CSVParser getCSVParser();
	XLSParser getXLSParser();
	HTMLTableParser getHTMLTableParser();
	XMLParser getXMLParser();
	JSONParser getJSONParser();
}
