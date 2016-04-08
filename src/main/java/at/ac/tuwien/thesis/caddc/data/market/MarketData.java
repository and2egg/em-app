package at.ac.tuwien.thesis.caddc.data.market;

import java.util.List;

import at.ac.tuwien.thesis.caddc.data.fetch.exception.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.exception.ImportDataException;

/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from different sources and locations
 */
public abstract class MarketData {
	
	/**
	 * The location associated with this MarketData Instance
	 */
	private Location location;
	
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
	public List<String> fetchPrices(Integer year) throws FetchDataException, ParseException {
		Resource resource = getResourceType().fetchData(year);
		return getResourceType().getParser().parse(resource);
	}
	
	/**
	 * Get the preferred resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public abstract ResourceType getResourceType();
	
	/**
	 * Get a specific resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public abstract ResourceType getResourceType(Integer index);
	
	/**
	 * Import prices for the given year into the database
	 * @param year the year for which to save the energy prices
	 * @throws ImportDataException is thrown when data import failed
	 */
	public abstract void importPrices(Integer year) throws ImportDataException;
}
