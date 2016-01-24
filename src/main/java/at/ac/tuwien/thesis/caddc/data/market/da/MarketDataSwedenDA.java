package at.ac.tuwien.thesis.caddc.data.market.da;

import at.ac.tuwien.thesis.caddc.data.market.MarketData;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataDA;
import at.ac.tuwien.thesis.caddc.data.resource.da.ResourceManagerSwedenDA;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;




/**
 * Defines a MarketData Instance responsible for retrieving day ahead 
 * energy market data from Sweden for different data sources
 */
public class MarketDataSwedenDA extends MarketDataDA {
	
	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataSwedenDA(Location location, DAPricePersistence persistence) {
		super(location, persistence);
		this.resourceManager = new ResourceManagerSwedenDA();
	}
}
