package at.ac.tuwien.thesis.caddc.model.handler;

import java.util.Date;
import java.util.List;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.type.EnergyMarketType;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType;
import at.ac.tuwien.thesis.caddc.model.type.LocationType;

/**
 * 
 */
public class EnergyPriceManager {

	
	public static List<EnergyPriceType> parsePrices(List<String> priceData, Location location, Date lastDate) {
		
		EnergyPriceHandler handler = null;
		boolean debug = true;
		
		if(location.getId().equals(LocationType.LOCATION_HAMINA))
			handler = new EnergyPriceHandlerFinland();
		if(location.getEm().getId().equals(EnergyMarketType.EMARKET_ISO_NE))
			handler = new EnergyPriceHandlerISONE();
		if(location.getEm().getId().equals(EnergyMarketType.EMARKET_BELPEX))
			handler = new EnergyPriceHandlerBelgium();
		
		return handler.parseEnergyPriceData(priceData, location, lastDate, debug);
	}
}
