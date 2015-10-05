package at.ac.tuwien.thesis.caddc.data.parse.types;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;

/**
 * 
 */
public class ParserMaineXLS implements Parser {
	
	private Integer sheetNumber;
	private Integer rowOffset;
	private Integer[] colIndices;
	
	/**
	 * Get default values
	 */
	public ParserMaineXLS() {
		this.sheetNumber = 2;
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
		Parser parser = new ParserGenericXLS(this.sheetNumber, this.rowOffset, this.colIndices);
		priceList = parser.parse(resource);
		List<String> prices = new ArrayList<String>();
		// iterate through every line and add to csv
		for(String price : priceList) {
			String[] split = price.split(";");
			int hour = (int)Double.parseDouble(split[1]); // parse hour
			hour --; // reduce hour by one to get hours from 0-23 instead of 1-24
			String result = split[0] + ";" + String.valueOf(hour) + ";" + split[2]; // Date;Hour;Price
			prices.add(result);
		}
		return prices;
	}
	
	private Integer[] getColumnIndices() {
		return new Integer[]{0,1,4}; // Column Types: Date, Hour, DA_LMP
	}
	
}