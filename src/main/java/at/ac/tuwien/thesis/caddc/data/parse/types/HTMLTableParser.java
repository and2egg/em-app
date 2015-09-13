package at.ac.tuwien.thesis.caddc.data.parse.types;

import java.util.List;

/**
 * Interface to provide functionality of parsing energy prices
 * out of a html table
 */
public interface HTMLTableParser {

	/**
	 * Method to parse the prices arranged in a HTML table
	 * @param htmlContent the html content of the page to parse
	 * @param rowOffset the rowOffset within the table to start scraping data
	 * @param colIndices the indices of the (price) columns to be retrieved 
	 * 			(starts at 0 for the first price column)
	 */
	List<String> parsePrices(String contentString, int rowOffset, int[] colIndices);
}
