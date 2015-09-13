package at.ac.tuwien.thesis.caddc.data.parse.types;

import java.util.List;

/**
 * 
 * @author Andreas Egger
 */
public interface CSVParser {

	List<String> parsePrices(String htmlString, int rowOffset, int[] colIndices);
}
