package at.ac.tuwien.thesis.caddc.data.parse;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 
 */
public class NordPoolFinlandParser {

	public static String parsePrices(String htmlString) {
		
		Document doc = Jsoup.parse(htmlString);
		Elements hourlyPrices = doc.select("tbody > tr");
		System.out.println("hourly prices length = "+hourlyPrices.size());
		ArrayList<String> arrPrices = new ArrayList<String>();
		for(Element hourPrice : hourlyPrices) {
			Elements list = hourPrice.select("td[style=text-align:right;]").eq(5);
			arrPrices.add(list.get(0).text());
			System.out.print(" "+arrPrices.get(arrPrices.size()-1));
		}
		System.out.println("\nprices length: "+arrPrices.size());
		return arrPrices.toString();
	}
}
