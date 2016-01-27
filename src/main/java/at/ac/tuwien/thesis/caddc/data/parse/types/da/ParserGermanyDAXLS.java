package at.ac.tuwien.thesis.caddc.data.parse.types.da;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;
import at.ac.tuwien.thesis.caddc.data.parse.types.ParserXLS;

/**
 * Class to fetch an XLS file containing day ahead (DA) 
 * prices from energy Market ISO-NE, location Maine
 */
public class ParserGermanyDAXLS implements Parser {
	
	private Integer sheetNumber;
	private Integer rowOffset;
	private Integer[] colIndices;
	
	/**
	 * Get default values
	 */
	public ParserGermanyDAXLS() {
		this.sheetNumber = 0;
		this.rowOffset = 3;
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
		List<String> prices = new ArrayList<String>();
		
		// iterate through every line and add to csv
		for(int j = priceList.size()-1; j >= 0; j--) {
			String[] split = priceList.get(j).split(Parser.SEPARATOR);
			String date = split[0];
			int hour = 0;
			String realPrice;
			
			for(int i = 1; i < split.length; i++) {
				realPrice = split[i];
				hour = i-1;
				if(split.length < 25  &&  hour >= 2) {
					hour = i;
				}
				StringBuilder result = new StringBuilder();
				result.append(date);
				result.append(Parser.SEPARATOR);
				result.append(hour);
				result.append(Parser.SEPARATOR);
				result.append(realPrice);
				prices.add(result.toString());
			}
			
		}
		return prices;
	}
	
	private Integer[] getColumnIndices() {
		
		List<Integer> cols = new ArrayList<Integer>();
		// add column 0 (date)
		cols.add(0);
		// add columns 7 to 30 (prices)
		for(Integer i = 7; i < 31; i++) {
			cols.add(i);
		}
		return cols.toArray(new Integer[]{});
	}
}
