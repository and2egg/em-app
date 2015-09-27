package at.ac.tuwien.thesis.caddc.data.market;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerMassachussetts;
import at.ac.tuwien.thesis.caddc.model.Location;




/**
 * Defines a MarketData Instance responsible for retrieving energy
 * market data from Massachussetts for different data sources
 */
public class MarketDataMassachussetts extends MarketData {
	
	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataMassachussetts(Location location) {
		super(location);
		this.resourceManager = new ResourceManagerMassachussetts();
	}	
}
