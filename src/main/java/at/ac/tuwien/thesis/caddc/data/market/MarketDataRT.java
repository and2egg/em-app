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
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.RTPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.exception.ImportDataException;
import at.ac.tuwien.thesis.caddc.persistence.exception.LocationNotFoundException;
import at.ac.tuwien.thesis.caddc.util.DateUtils;

/**
 * Defines a MarketData Instance responsible for retrieving real time
 * energy market data from different sources and locations
 */
public abstract class MarketDataRT extends MarketData {
	
	/**
	 * The resourceManager for this MarketData Instance
	 * Assigned in subclasses
	 */
	protected ResourceManagerRT resourceManager;
	
	/**
	 * The interface to the da price persistence provider
	 */
    private RTPricePersistence rtPriceResource;
	
	
	/**
	 * Create a MarketData Instance with the given location and parameters
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataRT(Location location, RTPricePersistence persistence) {
		super(location);
		this.rtPriceResource = persistence;
	}
	
	/**
	 * Get the preferred resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public ResourceType getResourceType() {
		return resourceManager.getFirstRTResource();
	}
	
	/**
	 * Get a specific resource type for this MarketData Instance
	 * @return the preferred resource type
	 */
	public ResourceType getResourceType(Integer index) {
		return resourceManager.getRTResource(index);
	}
	
	/**
	 * Import prices for the given year into the database
	 * @param year the year for which to save the energy prices
	 * @throws ImportDataException is thrown when data import failed
	 */
	public void importPrices(Integer year) throws ImportDataException {
		Date currentDate = DateUtils.getCurrentDateWithTimeZone(getLocation().getTimeZone());
		Date maxDate = rtPriceResource.findMaxDate(getLocation());
		if(maxDate != null  &&  !currentDate.after(maxDate)) 
			return;
		try {
			rtPriceResource.saveRTPrices(fetchPrices(year), getLocation(), maxDate);
		} catch (LocationNotFoundException | FetchDataException | ParseException e) {
			throw new ImportDataException("ImportDataException: "+e.getLocalizedMessage());
		}
	}
}
