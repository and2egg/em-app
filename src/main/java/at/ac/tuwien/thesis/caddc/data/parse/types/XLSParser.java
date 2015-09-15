package at.ac.tuwien.thesis.caddc.data.parse.types;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.parse.ParseException;

/**
 * 
 */
public interface XLSParser {

	/**
	 * Parse the given XLS file and return a date / price list as a list of strings
	 * @param file the xls file to parse
	 * @return a list of strings of energy price data
	 * @throws ParseException is thrown if the file could not be parsed
	 */
	List<String> parse(File file) throws ParseException;
}
