package at.ac.tuwien.thesis.caddc.persistence;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.handler.EnergyPriceManager;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType.IntervalType;
import at.ac.tuwien.thesis.caddc.persistence.exception.LocationNotFoundException;

/**
 * 
 * @author Andreas Egger
 */
@Stateless
public class DAPricePersistence {

	@Inject
	private EntityManager em;
	
	public void saveDAPrice(DAPrice price) {
    	em.persist(price);
    }
    
    public void saveDAPriceList(List<DAPrice> daPriceList) {
    	for(DAPrice daPrice : daPriceList)
    		saveDAPrice(daPrice);
    }
    
    /**
     * Method to save a list of prices encoded in Strings
     * @param priceData a String array containing prices encoded in Strings
     * 			The encoding for a single price needs to have the following form: 
     * 			datestring;hour;price
     * 			Example: 2015-07-16;02;44,23
     * @param location a location Object of one of the locations stored in the 
     * 			database
     * @param lastDate the last date where energy price data has been saved for the given location
     * @throws LocationNotFoundException is thrown when the given location is not registered
     */
    public void saveDAPrices(List<String> priceData, Location location, Date lastDate) throws LocationNotFoundException {
		if(location == null) {
			throw new LocationNotFoundException("Please provide a registered location");
		}
		
		List<EnergyPriceType> prices = EnergyPriceManager.parsePrices(priceData, location, lastDate);
		
		Integer interval = IntervalType.HOURLY.interval();
		String intervalUnit = IntervalType.HOURLY.intervalUnit();
		
		for(EnergyPriceType priceType : prices) {
			DAPrice daPrice = new DAPrice();
        	daPrice.setBiddingDate(priceType.getDate());
        	daPrice.setInterval(interval);
        	daPrice.setIntervalUnit(intervalUnit);
        	daPrice.setLocation(location);
        	daPrice.setPrice(priceType.getPrice());
        	daPrice.setTimelag(priceType.getTimeLag());
        	
        	saveDAPrice(daPrice);
		}
    }
    
    public Date findMaxDate(Location location) {
    	TypedQuery<Date> q = em.createNamedQuery("DAPrice.findMaxDate", Date.class);
    	q.setParameter("locationId", location.getId());
    	return q.getSingleResult();
    }
}
