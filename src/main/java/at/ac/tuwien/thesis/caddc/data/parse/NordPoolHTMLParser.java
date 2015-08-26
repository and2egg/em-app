package at.ac.tuwien.thesis.caddc.data.parse;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 */
public class NordPoolHTMLParser implements HTMLParser {


	/**
	 * Method to parse the prices arranged in a HTML table
	 * @param htmlContent the html content of the page to parse
	 * @param rowOffset the rowOffset within the table to start scraping data
	 * @param colIndices the indices of the (price) columns to be retrieved 
	 * 			(starts at 0 for the first price column)
	 */
	@Override
	public List<String> parsePrices(String htmlString, int rowOffset, int[] colIndices) {
		Document doc = Jsoup.parse(htmlString);
		Elements hourlyPrices = doc.select("tbody > tr");
		System.out.println("hourly prices length = "+hourlyPrices.size());
		ArrayList<String> arrPrices = new ArrayList<String>();
		int count = 0;
		for(Element hourPrice : hourlyPrices) {
			if(count++ < rowOffset)
				continue;
			for(int colPriceIndex : colIndices) {
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
}
