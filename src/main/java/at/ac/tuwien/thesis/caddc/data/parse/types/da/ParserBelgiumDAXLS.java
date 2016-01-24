package at.ac.tuwien.thesis.caddc.data.parse.types.da;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;
import at.ac.tuwien.thesis.caddc.data.parse.types.ParserXLS;

/**
 * Class to fetch an XLS file containing day ahead (DA) 
 * prices from energy Market Belpex, location Belgium
 */
public class ParserBelgiumDAXLS implements Parser {

	
	private Integer sheetNumber;
	private Integer rowOffset;
	private Integer[] colIndices;
	
	/**
	 * Get default values
	 */
	public ParserBelgiumDAXLS() {
		this.sheetNumber = 1;
		this.rowOffset = 1;
		this.colIndices = getColumnIndices();
	}

	
	/**
	 * Method to parse the prices arranged in a XLS file
	 * @param resource the resource to parse
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#parse()
	 */
	@Override
	public List<String> parse(Resource resource) throws ParseException {
		List<String> priceList;
		Parser parser = new ParserXLS(this.sheetNumber, this.rowOffset, this.colIndices);
		priceList = parser.parse(resource);
		List<String> transformedPrices = new ArrayList<String>();
		for(String prices: priceList) {
			String[] split = prices.split(";");
			// go through all 24 prices in the row (24 hours)
			for(int i = 1; i < split.length; i++) {
				String result = split[0] + ";"; // date
				String price = split[i].trim();
				if(i == 25 && !price.isEmpty()) {
					result += 1 + ";" + price; // when hour = 25 (i=25), encode hour as '1'
				}
				else {
					result += (i-1) + ";" + price; // encoding hour as i-1
				}
				transformedPrices.add(result);
			}
		}
		return transformedPrices;
	}
	
	
	private Integer[] getColumnIndices() {
		Integer[] colIdx = new Integer[26];
		for(int i = 0; i < 26; i++) {
			colIdx[i] = i;
		}
		return colIdx;
	}
}
