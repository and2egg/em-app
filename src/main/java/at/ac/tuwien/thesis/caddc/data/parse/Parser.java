package at.ac.tuwien.thesis.caddc.data.parse;

import java.util.List;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;

/**
 * The super interface for all types of parsers
 */
public interface Parser {
	
	/**
	 * Method to parse prices to a list of Strings from a given Resource
	 * @param resource the resource from which to parse prices
	 * @return a list of strings containing the prices and associated timestamps
	 */
	List<String> parse(Resource resource) throws ParseException;
}
