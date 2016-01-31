package at.ac.tuwien.thesis.caddc.data.market;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import at.ac.tuwien.thesis.caddc.data.fetch.exception.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerDA;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.exception.ImportDataException;
import at.ac.tuwien.thesis.caddc.persistence.exception.LocationNotFoundException;
import at.ac.tuwien.thesis.caddc.util.DateUtils;

/**
 * Defines a MarketData Instance responsible for retrieving day ahead 
 * energy market data from different sources and locations
 */
public abstract class MarketDataDA extends MarketData {
	
	/**
	 * The resourceManager for this MarketData Instance
	 * Assigned in subclasses
	 */
	protected ResourceManagerDA resourceManager;
	
	/**
	 * The interface to the da price persistence provider
	 */
    private DAPricePersistence daPriceResource;
	
	
	/**
	 * Create a MarketData Instance with the given location and parameters
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataDA(Location location, DAPricePersistence persistence) {
		super(location);
		this.daPriceResource = persistence;
	}
	
	/**
	 * Get the preferred resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public ResourceType getResourceType() {
		return resourceManager.getFirstDAResource();
	}
	
	/**
	 * Get a specific resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public ResourceType getResourceType(Integer index) {
		return resourceManager.getDAResource(index);
	}
	
	/**
	 * Import prices for the given year into the database
	 * @param year the year for which to save the energy prices
	 * @throws ImportDataException is thrown when data import failed
	 */
	public void importPrices(Integer year) throws ImportDataException {
		Date currentDate = DateUtils.getCurrentDateWithTimeZone(getLocation().getTimeZone());
		Date maxDate = daPriceResource.findMaxDate(getLocation());
		// scheduler logic: skip data older than last stored date
		if(maxDate != null  &&  !currentDate.after(maxDate)) 
			return;
		try {
			daPriceResource.saveDAPrices(fetchPrices(year), getLocation(), maxDate);
		} catch (LocationNotFoundException | FetchDataException | ParseException e) {
			throw new ImportDataException("ImportDataException: "+e.getLocalizedMessage());
		}
	}
}
