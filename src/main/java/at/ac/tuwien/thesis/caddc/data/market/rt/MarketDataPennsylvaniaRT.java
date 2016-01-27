package at.ac.tuwien.thesis.caddc.data.market.rt;

import at.ac.tuwien.thesis.caddc.data.market.MarketData;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataDA;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataRT;
import at.ac.tuwien.thesis.caddc.data.resource.da.ResourceManagerMaineDA;
import at.ac.tuwien.thesis.caddc.data.resource.rt.ResourceManagerMaineRT;
import at.ac.tuwien.thesis.caddc.data.resource.rt.ResourceManagerPennsylvaniaRT;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.RTPricePersistence;




/**
 * Defines a MarketData Instance responsible for retrieving real time 
 * energy market data from Maine for different data sources
 */
public class MarketDataPennsylvaniaRT extends MarketDataRT {
	
	/**
	 * Create a MarketData Instance with the given location
	 * @param location the location for this MarketData Instance
	 */
	public MarketDataPennsylvaniaRT(Location location, RTPricePersistence persistence) {
		super(location, persistence);
		this.resourceManager = new ResourceManagerPennsylvaniaRT();
	}
}
