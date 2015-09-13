package at.ac.tuwien.thesis.caddc.data.market;

import java.util.List;

import javax.inject.Inject;

import at.ac.tuwien.thesis.caddc.data.fetch.FetchDataException;
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
	protected Location location;
	
	/**
	 * Create a MarketData Instance with the given location
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
	public abstract List<String> fetchPrices(Integer year) throws FetchDataException;
	
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
	
	/**
     * Returns the path for a given resource at a resource root
     * @param resourceRoot the root folder of the resource
     * @param resourcePath the path relative to the root folder
     * @return the complete real resource path
     */
    protected String getResourcePath(String resourceRoot, String resourcePath) {
    	String path = getClass().getClassLoader().getResource(resourceRoot).getPath();
		path = path + resourcePath;
	    path = path.substring(1); // remove leading slash
	    return path;
	}
}
