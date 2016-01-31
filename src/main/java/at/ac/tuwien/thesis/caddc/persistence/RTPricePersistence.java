package at.ac.tuwien.thesis.caddc.persistence;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.RTPrice;
import at.ac.tuwien.thesis.caddc.model.handler.EnergyPriceHandler;
import at.ac.tuwien.thesis.caddc.model.handler.EnergyPriceManager;
import at.ac.tuwien.thesis.caddc.model.type.EnergyMarketType;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType.IntervalType;
import at.ac.tuwien.thesis.caddc.persistence.exception.LocationNotFoundException;
import at.ac.tuwien.thesis.caddc.util.DateParser;

/**
 * 
 * @author Andreas Egger
 */
@Stateless
public class RTPricePersistence {

	@Inject
	private EntityManager em;
	
	public void saveRTPrice(RTPrice price) {
    	em.persist(price);
    }
    
    public void saveRTPriceList(List<RTPrice> daPriceList) {
    	for(RTPrice daPrice : daPriceList)
    		saveRTPrice(daPrice);
    }
    
    /**
     * Method to save a list of prices encoded in Strings
     * @param prices a String array containing prices encoded in Strings
     * 			The encoding for a single price needs to have the following form: 
     * 			datestring;hour;price
     * 			Example: 2015-07-11;02;44,23
     * @param location a location Object of one of the locations stored in the 
     * 			database
     * @param lastDate the last bidding date saved for the given location
     * @throws LocationNotFoundException is thrown when the given location is not registered
     */
    public void saveRTPrices(List<String> priceData, Location location, Date lastDate) throws LocationNotFoundException {
		if(location == null) {
			throw new LocationNotFoundException("Please provide a registered location");
		}
		
		List<EnergyPriceType> prices = EnergyPriceManager.parsePrices(priceData, location, lastDate);
		
		Integer interval = IntervalType.HOURLY.interval();
		String intervalUnit = IntervalType.HOURLY.intervalUnit();
		
		for(EnergyPriceType priceType : prices) {
			RTPrice rtPrice = new RTPrice();
        	rtPrice.setBiddingDate(priceType.getDate());
        	rtPrice.setInterval(interval);
        	rtPrice.setIntervalUnit(intervalUnit);
        	rtPrice.setLocation(location);
        	rtPrice.setPrice(priceType.getPrice());
        	rtPrice.setTimelag(priceType.getTimeLag());
        	
        	saveRTPrice(rtPrice);
		}
    }
    
    public Date findMaxDate(Location location) {
    	TypedQuery<Date> q = em.createNamedQuery("RTPrice.findMaxDate", Date.class);
    	q.setParameter("locationId", location.getId());
    	return q.getSingleResult();
    }
}
