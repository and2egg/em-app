package at.ac.tuwien.thesis.caddc.data.market;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.fetch.DataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.FileDataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.URLDataFetch;
import at.ac.tuwien.thesis.caddc.data.parse.FinlandParser;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.HTMLParserNordPool;
import at.ac.tuwien.thesis.caddc.model.Location;
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
	 * @throws FetchDataException is thrown when the data import failed
	 */
	@Override
	public List<String> fetchPrices(Integer year) throws FetchDataException {
    	String result = null;
    	DataFetch dataFetch;
    	if(year == 2012) {
    	    String path = getResourcePath("data", "energydata/NPS/Elspot_Prices_2012_Hourly_EUR.xls");
    	    dataFetch = new FileDataFetch(path);
    	    result = dataFetch.fetchToString();
    	}
    	else {
    		String url = "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_"+year.toString()+"_hourly_eur.xls";
    		dataFetch = new URLDataFetch(url);
    		result = dataFetch.fetchToString();
    	}
    	HTMLTableParser htmlParser = new HTMLParserNordPool();
		List<String> prices = htmlParser.parsePrices(result, // html content to parse
				0,   // row offset
				new int[]{5} // price column indices
		);
		return prices;
	}
	
}
