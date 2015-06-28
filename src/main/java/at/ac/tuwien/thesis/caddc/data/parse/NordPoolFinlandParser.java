package at.ac.tuwien.thesis.caddc.data.parse;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 
 */
public class NordPoolFinlandParser {

	public static String parsePrices(String htmlString) {
		Document doc = Jsoup.parse(htmlString);
		
		Elements hourlyPrices = doc.select("tbody tr td:eq(7)");
		System.out.println("prices length: "+hourlyPrices.size());
		return hourlyPrices.text();
	}
}
