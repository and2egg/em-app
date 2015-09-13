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
public class XLSParserBelgium implements XLSParser {

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
	public List<String> parse(File file, int sheetNumber, int rowOffset, int[] colIndices) throws ParseException {
		List<String> priceList;
		XLSParser parser = new XLSParserGeneric();
		priceList = parser.parse(file, sheetNumber, rowOffset, colIndices);
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
}
