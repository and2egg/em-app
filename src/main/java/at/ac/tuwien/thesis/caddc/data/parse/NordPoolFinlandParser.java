package at.ac.tuwien.thesis.caddc.data.parse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 */
public class NordPoolFinlandParser {

	public static List<String> parsePrices(String htmlString) {
		
		// index of the column of locations
		Integer colPriceIndex = 5;
		
		Document doc = Jsoup.parse(htmlString);
		Elements hourlyPrices = doc.select("tbody > tr");
		System.out.println("hourly prices length = "+hourlyPrices.size());
		ArrayList<String> arrPrices = new ArrayList<String>();
		for(Element hourPrice : hourlyPrices) {
			Elements date = hourPrice.select("td[style=text-align:left;]").eq(0);
			Elements time = hourPrice.select("td[style=text-align:left;]").eq(1);
			Elements prices = hourPrice.select("td[style=text-align:right;]").eq(colPriceIndex);
			
			String dateString = date.get(0).text();
			String timeString = time.get(0).text();
			String price = prices.get(0).text();
			
			if(price.length() < 2) {
				System.out.println(dateString +";"+ timeString +";"+ price);
				continue;
			}
			arrPrices.add(dateString +";"+ timeString +";"+ price);
		}
		System.out.println("\nprices length: "+arrPrices.size());
		return arrPrices;
	}
}
