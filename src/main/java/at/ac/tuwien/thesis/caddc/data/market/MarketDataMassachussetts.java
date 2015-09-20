package at.ac.tuwien.thesis.caddc.data.market;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.fetch.DataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.URLDataFetch;
import at.ac.tuwien.thesis.caddc.data.parse.ParseException;
import at.ac.tuwien.thesis.caddc.data.parse.ParserFactory;
import at.ac.tuwien.thesis.caddc.data.parse.ParserMassachussetts;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XLSParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserGeneric;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.HTMLTableParserFinland;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserMaine;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserMassachussetts;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.ImportDataException;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;



/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Massachussetts for different data sources
 */
public class MarketDataMassachussetts extends MarketData {
	
	private ParserFactory localParser;
	
	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataMassachussetts(Location location) {
		super(location);
		this.localParser = new ParserMassachussetts();
	}

	/**
	 * Get the energy prices as a list of Strings for the given year
	 * @param year the year for which to obtain energy prices
	 * @return a list of Strings containing the energy price time series
	 * @throws FetchDataException is thrown when the data import failed
	 */
	@Override
	public List<String> fetchPrices(Integer year) throws FetchDataException {
		DataFetch dataFetch = new URLDataFetch(getURL(year));
		List<String> priceList;
		XLSParser parser = this.localParser.getXLSParser();
		try {
			priceList = parser.parse(dataFetch.fetchToFile());
		} catch (ParseException e) {
			throw new FetchDataException(e.getClass().getSimpleName()+": "+e.getLocalizedMessage());
		}
		return priceList;
	}
	
	/**
	 * Method to retrieve a custom price URL based on the year
	 * @param year the year for which to obtain energy prices
	 * @return a url String to get prices from
	 */
	private String getURL(Integer year) {
		if(year == 2014) {
    		return "http://www.iso-ne.com/static-assets/documents/2015/05/2014_smd_hourly.xls";
    	}
    	else {
    		return "http://www.iso-ne.com/static-assets/documents/markets/hstdata/znl_info/hourly/"+year+"_smd_hourly.xls";
    	}
	}
	
}
