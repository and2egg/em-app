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

	/**
	 * Gets a well defined CSV parser designed to parse
	 * datasets related to the class implementing this interface
	 * @return if available, the CSV parser for the current implementation,
	 * 			null otherwise
	 */
	CSVParser getCSVParser();
	
	/**
	 * Gets a well defined XLS parser designed to parse
	 * datasets related to the class implementing this interface
	 * @return if available, the XLS parser for the current implementation,
	 * 			null otherwise
	 */
	XLSParser getXLSParser();
	
	/**
	 * Gets a well defined HTMLTable parser designed to parse
	 * datasets related to the class implementing this interface
	 * @return if available, the HTMLTable parser for the current implementation,
	 * 			null otherwise
	 */
	HTMLTableParser getHTMLTableParser();
	
	/**
	 * Gets a well defined XML parser designed to parse
	 * datasets related to the class implementing this interface
	 * @return if available, the XML parser for the current implementation,
	 * 			null otherwise
	 */
	XMLParser getXMLParser();
	
	/**
	 * Gets a well defined JSON parser designed to parse
	 * datasets related to the class implementing this interface
	 * @return if available, the JSON parser for the current implementation,
	 * 			null otherwise
	 */
	JSONParser getJSONParser();
}
