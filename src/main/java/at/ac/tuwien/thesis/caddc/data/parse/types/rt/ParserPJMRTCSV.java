package at.ac.tuwien.thesis.caddc.data.parse.types.rt;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;
import at.ac.tuwien.thesis.caddc.data.parse.types.ParserCSV;
import at.ac.tuwien.thesis.caddc.data.parse.types.ParserXLS;

/**
 * Class to fetch an XLS file containing real time (RT) 
 * prices from energy Market ISO-NE, location Maine
 */
public class ParserPJMRTCSV implements Parser {
	
	private Integer rowOffset;
	private Integer[] colIndices;
	
	/**
	 * Get default values
	 */
	public ParserPJMRTCSV() {
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
		
		Parser parser = new ParserCSV(this.rowOffset, this.colIndices);
		List<String> priceList = parser.parse(resource);
		List<String> prices = new ArrayList<String>();
		// iterate through every line and add to csv
		for(String price : priceList) {
			String[] split = price.split(Parser.SEPARATOR);
			
			String date = split[0];
			int hour = 0;
			String realPrice;
			
			for(int i = 1; i < split.length; i++) {
				realPrice = split[i];
				hour = i-1;
				if(split.length < 25  &&  hour >= 2) {
					hour = i;
				}
				else if(split.length > 25) {
					// duplicate entry at hour 1
					if(hour == 1) {
						StringBuilder result = new StringBuilder();
						result.append(date);
						result.append(Parser.SEPARATOR);
						result.append(hour);
						result.append(Parser.SEPARATOR);
						result.append(realPrice);
						prices.add(result.toString());
					}
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
		// add columns 7 to 31 (prices)
		for(Integer i = 7; i < 32; i++) {
			cols.add(i);
		}
		return cols.toArray(new Integer[]{});
	}
	
}
