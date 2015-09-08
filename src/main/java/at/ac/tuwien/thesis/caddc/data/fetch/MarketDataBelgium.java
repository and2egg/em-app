package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.parse.HTMLParser;
import at.ac.tuwien.thesis.caddc.data.parse.NordPoolHTMLParser;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.ImportDataException;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;



/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Massachussetts for different data sources
 */
public class MarketDataBelgium extends MarketData {
	

	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataBelgium(Location location) {
		super(location);
	}

	/**
	 * Get the energy prices as a list of Strings for the given year
	 * @param year the year for which to obtain energy prices
	 * @return a list of Strings containing the energy price time series
	 * @throws ImportDataException is thrown when the data import failed
	 */
	@Override
	public List<String> getPrices(Integer year) throws ImportDataException {
		String path = getResourcePath("energydata","/BELPEX/spotmarket_data_"+year+".xls");
		int[] colIdx = new int[26];
		for(int i = 0; i < 26; i++) {
			colIdx[i] = i;
		}
		List<String> priceList;
		try {
			priceList = RESTClient.fileFetchAndParseXLS(path, // fetch URL
																	1, // sheet number
																	1, // row Offset
																	colIdx // column indices array
																);
		} catch (ConnectException e) {
			throw new ImportDataException("ImportDataException: "+e.getLocalizedMessage());
		}
		
		List<String> transformedPrices = new ArrayList<String>();
		int count = 0;
		for(String prices: priceList) {
			String[] split = prices.split(";");
			
			for(int i = 1; i < split.length; i++) {
				String result = split[0] + ";"; // date
				String price = split[i].trim();
				if(i == 25 && !price.isEmpty()) {
					result += 1 + ";" + price;
				}
				else {
					result += (i-1) + ";" + price;
				}
				transformedPrices.add(result);
			}
			
			if(count < 10) {
				System.out.println("price: "+transformedPrices.get(count));
				count++;
			}
		}
		return transformedPrices;
	}
	
}
