package at.ac.tuwien.thesis.caddc.data.market;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerSweden;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;




/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Sweden for different data sources
 */
public class MarketDataSweden extends MarketData {
	
	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataSweden(Location location, DAPricePersistence persistence) {
		super(location, persistence);
		this.resourceManager = new ResourceManagerSweden();
	}
}
