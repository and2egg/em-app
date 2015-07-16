package at.ac.tuwien.thesis.caddc.data.parse;

import java.util.List;

/**
 * 
 * @author Andreas Egger
 */
public interface HTMLParser {

	List<String> parsePrices(String contentString, int rowOffset, int[] colIndices);
}
