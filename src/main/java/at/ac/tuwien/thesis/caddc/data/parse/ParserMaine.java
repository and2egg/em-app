package at.ac.tuwien.thesis.caddc.data.parse;

import at.ac.tuwien.thesis.caddc.data.parse.types.CSVParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.JSONParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XLSParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XMLParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserBelgium;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserMaine;

/**
 * 
 */
public class ParserMaine implements ParserFactory {

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.ParserFactory#getCSVParser()
	 */
	@Override
	public CSVParser getCSVParser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.ParserFactory#getXLSParser()
	 */
	@Override
	public XLSParser getXLSParser() {
		return new XLSParserMaine();
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.ParserFactory#getHTMLTableParser()
	 */
	@Override
	public HTMLTableParser getHTMLTableParser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.ParserFactory#getXMLParser()
	 */
	@Override
	public XMLParser getXMLParser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.parse.ParserFactory#getJSONParser()
	 */
	@Override
	public JSONParser getJSONParser() {
		// TODO Auto-generated method stub
		return null;
	}

}
