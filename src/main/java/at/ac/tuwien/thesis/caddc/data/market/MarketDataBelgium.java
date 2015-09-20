package at.ac.tuwien.thesis.caddc.data.market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.fetch.DataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.FileDataFetch;
import at.ac.tuwien.thesis.caddc.data.parse.ParserBelgium;
import at.ac.tuwien.thesis.caddc.data.parse.ParseException;
import at.ac.tuwien.thesis.caddc.data.parse.ParserFactory;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.XLSParser;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserBelgium;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserGeneric;
import at.ac.tuwien.thesis.caddc.data.parse.types.impl.HTMLTableParserFinland;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.ImportDataException;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;



/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Massachussetts for different data sources
 */
public class MarketDataBelgium extends MarketData {
	
	private ParserFactory localParser;

	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataBelgium(Location location) {
		super(location);
		this.localParser = new ParserBelgium();
	}

	/**
	 * Get the energy prices as a list of Strings for the given year
	 * @param year the year for which to obtain energy prices
	 * @return a list of Strings containing the energy price time series
	 * @throws FetchDataException is thrown when the data import failed
	 */
	@Override
	public List<String> fetchPrices(Integer year) throws FetchDataException {
		String path = getPath("data","energydata/BELPEX/spotmarket_data_"+year+".xls");
		DataFetch dataFetch = new FileDataFetch(path);
		List<String> priceList;
		try {
			XLSParser parser = this.localParser.getXLSParser();
			priceList = parser.parse(dataFetch.fetchToFile());
		} catch (ParseException e) {
			throw new FetchDataException("ParseException: "+e.getLocalizedMessage());
		}
		return priceList;
	}
	
}
