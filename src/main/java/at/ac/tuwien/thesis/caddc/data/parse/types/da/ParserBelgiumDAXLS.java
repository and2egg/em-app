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
		int hour = 0;
		
		for(String prices: priceList) {
			String[] split = prices.split(";");
			
			
			// go through all 24 prices in the row (24 hours)
			for(int i = 1; i < split.length; i++) {
				
				String result = split[0] + ";"; // date
				
				String price = split[i].trim();
				
				// check if one hour is missing -> DST activated
				if(split.length < 25) {
					if(i <= 2) {
						result += (i-1) + ";" + price; // encoding hour as i-1
					}
					else {
						result += i + ";" + price; // SWITCH to DST -> one hour more!
					}
				}
				// if one additional hour is added -> DST deactivated
				else if(split.length > 25  &&  !split[25].trim().isEmpty()) {
					if(i == 4) {
						String dstChange = result + (i-2) + ";" + split[25].trim(); // insert dst hour for hour "2"
						transformedPrices.add(dstChange);
						
						result += (i-1) + ";" + price; // price at hour 3
					}
					else if(i == 25) {
						continue;
					}
					else {
						result += (i-1) + ";" + price;
					}
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
