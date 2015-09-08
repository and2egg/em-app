package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.parse.HTMLParser;
import at.ac.tuwien.thesis.caddc.data.parse.NordPoolHTMLParser;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.ImportDataException;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;



/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Finland for different data sources
 */
public class MarketDataFinland extends MarketData {
	

	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataFinland(Location location) {
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
    	String result = null;
    	if(year == 2012) {
    	    String path = getResourcePath("energydata", "/NPS/Elspot_Prices_2012_Hourly_EUR.xls");
    	    try {
    	    	result = new String(Files.readAllBytes(Paths.get(path)));
			} catch (IOException e) {
				throw new ImportDataException("ImportDataException: Could not read resource file: "+path);
			}
    	}
    	else {
    		String url = "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_"+year.toString()+"_hourly_eur.xls";
    		try {
				result = RESTClient.fetchDataString(url);
			} catch (ConnectException e) {
				throw new ImportDataException("ImportDataException: "+e.getLocalizedMessage());
			}
    	}
    	HTMLParser htmlParser = new NordPoolHTMLParser();
		List<String> prices = htmlParser.parsePrices(result, // html content to parse
				0,   // row offset
				new int[]{5} // price column indices
		);
		return prices;
	}
	
}
