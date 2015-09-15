package at.ac.tuwien.thesis.caddc.data.parse.types.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.parse.ParseException;
import at.ac.tuwien.thesis.caddc.data.parse.types.XLSParser;

/**
 * 
 */
public class XLSParserMassachussetts implements XLSParser {

	private Integer sheetNumber;
	private Integer rowOffset;
	private Integer[] colIndices;
	
	/**
	 * Get default values
	 */
	public XLSParserMassachussetts() {
		this.sheetNumber = 9;
		this.rowOffset = 1;
		this.colIndices = getColumnIndices();
	}
	
	/**
	 * @param file
	 * @param sheetNumber
	 * @param rowOffset
	 * @param colIndices
	 * @return
	 * @throws ParseException
	 * @see at.ac.tuwien.thesis.caddc.data.parse.types.XLSParser#parse(java.io.File, int, int, int[])
	 */
	@Override
	public List<String> parse(File file) throws ParseException {
		List<String> priceList;
		XLSParser parser = new XLSParserGeneric(this.sheetNumber, this.rowOffset, this.colIndices);
		priceList = parser.parse(file);
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
