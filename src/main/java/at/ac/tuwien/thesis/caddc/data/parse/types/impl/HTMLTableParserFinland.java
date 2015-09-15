package at.ac.tuwien.thesis.caddc.data.parse.types.impl;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;

/**
 * Specialized parser for retrieval of prices from aggregated data sheets
 * provided in an HTML table format. 
 * This parser implementation is used to get energy prices from the 
 * Nord Pool Spot market
 */
public class HTMLTableParserFinland implements HTMLTableParser {

	private int rowOffset;
	private Integer[] colIndices;
	
	/**
	 * Initialize parser with default values
	 */
	public HTMLTableParserFinland() {
		this.rowOffset = 0;
		this.colIndices = getColumnIndices();
	}
	
	/**
	 * Initialize Parser with parameters
	 * @param rowOffset the rowOffset within the table to start scraping data
	 * @param colIndices the indices of the (price) columns to be retrieved 
	 * 			(starts at 0 for the first price column)
	 */
	public HTMLTableParserFinland(int rowOffset, Integer[] colIndices) {
		this.rowOffset = rowOffset;
		this.colIndices = colIndices;
	}

	/**
	 * Method to parse the prices arranged in a HTML table
	 * @param htmlContent the html content of the page to parse
	 * 
	 */
	@Override
	public List<String> parsePrices(String htmlString) {
		Document doc = Jsoup.parse(htmlString);
		Elements hourlyPrices = doc.select("tbody > tr");
		System.out.println("hourly prices length = "+hourlyPrices.size());
		ArrayList<String> arrPrices = new ArrayList<String>();
		int count = 0;
		for(Element hourPrice : hourlyPrices) {
			if(count++ < this.rowOffset)
				continue;
			for(int colPriceIndex : this.colIndices) {
				Elements date = hourPrice.select("td[style=text-align:left;]").eq(0);
				Elements time = hourPrice.select("td[style=text-align:left;]").eq(1);
				Elements prices = hourPrice.select("td[style=text-align:right;]").eq(colPriceIndex);
				
				String dateString = date.get(0).text();
				String timeString = time.get(0).text().substring(0, 2);
				String price = prices.get(0).text();
				
				if(price.length() < 2) {
					System.out.println(dateString +";"+ timeString +";"+ price);
					continue;
				}
				arrPrices.add(dateString +";"+ timeString +";"+ price);
			}
		}
		System.out.println("\nprices length: "+arrPrices.size());
		return arrPrices;
	}
	
	/**
	 * Get the indices of the columns to parse
	 * @return an integer array containing indices to parse
	 */
	private Integer[] getColumnIndices() {
		return new Integer[]{5}; // price column index
	}
}
