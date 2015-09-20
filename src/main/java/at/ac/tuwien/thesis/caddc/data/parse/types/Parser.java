package at.ac.tuwien.thesis.caddc.data.parse.types;

import java.util.List;

import at.ac.tuwien.thesis.caddc.data.format.Resource;

/**
 * The interface for all types of parsers
 */
public interface Parser {
	
	/**
	 * Method to parse prices as a list of Strings from a given Resource
	 * @param resource the resource from which to parse prices
	 * @return a list of strings containing the prices and associated timestamps
	 */
	List<String> parse(Resource resource);
}
