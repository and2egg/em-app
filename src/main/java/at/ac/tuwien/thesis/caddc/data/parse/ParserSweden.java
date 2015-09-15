package at.ac.tuwien.thesis.caddc.data.parse;

import at.ac.tuwien.thesis.caddc.data.parse.types.CSVParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.JSONParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XLSParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XMLParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.HTMLTableParserSweden;

/**
 * 
 */
public class ParserSweden implements Parser {

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#getCSVParser()
	 */
	@Override
	public CSVParser getCSVParser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#getXLSParser()
	 */
	@Override
	public XLSParser getXLSParser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#getHTMLTableParser()
	 */
	@Override
	public HTMLTableParser getHTMLTableParser() {
		return new HTMLTableParserSweden();
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#getXMLParser()
	 */
	@Override
	public XMLParser getXMLParser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#getJSONParser()
	 */
	@Override
	public JSONParser getJSONParser() {
		// TODO Auto-generated method stub
		return null;
	}

	
}