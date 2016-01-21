package at.ac.tuwien.thesis.caddc.data.market.da;

import at.ac.tuwien.thesis.caddc.data.market.MarketData;
import at.ac.tuwien.thesis.caddc.data.resource.da.ResourceManagerMaine;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;




/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Maine for different data sources
 */
public class MarketDataMaine extends MarketData {
	
	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataMaine(Location location, DAPricePersistence persistence) {
		super(location, persistence);
		this.resourceManager = new ResourceManagerMaine();
	}
}
