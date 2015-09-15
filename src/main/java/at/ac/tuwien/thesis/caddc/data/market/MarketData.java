package at.ac.tuwien.thesis.caddc.data.market;

import java.util.List;

import javax.inject.Inject;

import at.ac.tuwien.thesis.caddc.data.fetch.DataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.DataFetcher;
import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.ImportDataException;
import at.ac.tuwien.thesis.caddc.persistence.LocationNotFoundException;

/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from different sources and locations
 */
public class MarketData {
	
	@Inject
    private DAPricePersistence daPriceResource;
	
	/**
	 * The location associated with this MarketData Instance
	 */
	private Location location;
	
	/**
	 * The data fetcher responsible for data retrieval
	 */
	private DataFetcher fetcher;
	
	/**
	 * The parser responsible for data parsing
	 */
	private Parser parser;
	
	/**
	 * Create a MarketData Instance with the given location and parameters
	 * @param location the location for this MarketData Instance
	 * @param fetcher the data fetcher for data retrieval
	 * @param parser the parser for data parsing
	 */
	public MarketData(Location location, DataFetcher dataFetcher, Parser parser) {
		this.location = location;
		this.fetcher = dataFetcher;
		this.parser = parser;
	}
	
	/**
	 * Retrieve the location associated with this MarketData Instance
	 * @return the location of this instance
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Get the energy prices as a list of Strings for the given year
	 * @param year the year for which to obtain energy prices
	 * @return a list of Strings containing the energy price time series
	 * @throws FetchDataException is thrown when data fetch failed
	 */
	public List<String> fetchPrices(Integer year) throws FetchDataException {
		DataFetch dataFetch = this.fetcher.getDataFetch(year);
    	HTMLTableParser htmlParser = this.parser.getHTMLTableParser();
		return htmlParser.parsePrices(dataFetch.fetchToString());
	}
	
	/**
	 * Import prices for the given year into the database
	 * @param year the year for which to save the energy prices
	 * @throws ImportDataException is thrown when data import failed
	 */
	public void importPrices(Integer year) throws ImportDataException {
		try {
			daPriceResource.saveDAPrices(fetchPrices(year), getLocation().getName());
		} catch (LocationNotFoundException | FetchDataException e) {
			throw new ImportDataException("ImportDataException: "+e.getLocalizedMessage());
		}
	}
}
