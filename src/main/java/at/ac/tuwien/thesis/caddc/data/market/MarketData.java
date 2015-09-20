package at.ac.tuwien.thesis.caddc.data.market;

import java.util.List;

import javax.inject.Inject;

import at.ac.tuwien.thesis.caddc.data.fetch.DataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.DataFetcher;
import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.ParserFactory;
import at.ac.tuwien.thesis.caddc.data.parse.types.HTMLTableParser;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.ImportDataException;
import at.ac.tuwien.thesis.caddc.persistence.LocationNotFoundException;

/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from different sources and locations
 */
public abstract class MarketData {
	
	@Inject
    private DAPricePersistence daPriceResource;
	
	/**
	 * The location associated with this MarketData Instance
	 */
	private Location location;
	
	/**
	 * The resourceManager for this MarketData Instance
	 */
	protected ResourceManager resourceManager;
	
	/**
	 * Create a MarketData Instance with the given location and parameters
	 * @param location the location for this MarketData Instance
	 */
	public MarketData(Location location) {
		this.location = location;
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
		Resource resource = getResourceType().fetchData(year);
		return getResourceType().getParser().parse(resource);
	}
	
	/**
	 * Get the preferred resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public abstract ResourceType getResourceType();
	
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
