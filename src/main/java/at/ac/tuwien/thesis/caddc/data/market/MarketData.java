package at.ac.tuwien.thesis.caddc.data.market;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import at.ac.tuwien.thesis.caddc.data.fetch.exception.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.exception.ImportDataException;
import at.ac.tuwien.thesis.caddc.persistence.exception.LocationNotFoundException;

/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from different sources and locations
 */
//@ApplicationScoped
public abstract class MarketData {
	
	/**
	 * The interface to the da price persistence provider
	 */
    private DAPricePersistence daPriceResource;
	
	/**
	 * The location associated with this MarketData Instance
	 */
	private Location location;
	
	/**
	 * The resourceManager for this MarketData Instance
	 * Assigned in subclasses
	 */
	protected ResourceManager resourceManager;
	
	/**
	 * Create a MarketData Instance with the given location and parameters
	 * @param location the location for this MarketData Instance
	 */
	public MarketData(Location location, DAPricePersistence persistence) {
		this.location = location;
		this.daPriceResource = persistence;
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
	public ResourceType getResourceType() {
		return resourceManager.get();
	}
	
	/**
	 * Get a specific resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public ResourceType getResourceType(Integer index) {
		return resourceManager.get(index);
	}
	
	/**
	 * Import prices for the given year into the database
	 * @param year the year for which to save the energy prices
	 * @throws ImportDataException is thrown when data import failed
	 */
	public void importPrices(Integer year) throws ImportDataException {
		try {
			daPriceResource.saveDAPrices(fetchPrices(year), getLocation());
		} catch (LocationNotFoundException | FetchDataException | ParseException e) {
			throw new ImportDataException("ImportDataException: "+e.getLocalizedMessage());
		}
	}
}
