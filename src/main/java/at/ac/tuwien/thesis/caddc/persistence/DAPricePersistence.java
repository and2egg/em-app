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
import at.ac.tuwien.thesis.caddc.model.type.EnergyMarketType;
import at.ac.tuwien.thesis.caddc.model.type.LocationType;
import at.ac.tuwien.thesis.caddc.persistence.exception.LocationNotFoundException;
import at.ac.tuwien.thesis.caddc.util.DateParser;
import at.ac.tuwien.thesis.caddc.util.DateUtils;

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
		
		String tz = location.getTimeZone();
		TimeZone timeZone;
		if(!tz.isEmpty()) {
			timeZone = TimeZone.getTimeZone(tz);
		}
		else {
			timeZone = TimeZone.getDefault();
		}
		Calendar cal = Calendar.getInstance(timeZone);
		Calendar temp = Calendar.getInstance();
		
		long timeLag, timeLagOld = 0;
		int dstChange = 0;
		long offset = 0;
		
		boolean dstOn = false;
		boolean dstOff = false;
		
		
    	for(int i = 0; i < priceData.size(); i++) {
    		String[] split = priceData.get(i).split(";");
    		if(split.length != 3) {
    			System.err.println("Price data format at index "+i+" is invalid: "+priceData.get(i));
    			continue;
    		}
    		String dateString = split[0];
    		String timeString = split[1];
    		String price = split[2];
    		
    		int finalPrice;
    		boolean negative = false;
    		if(price.contains("-")) {
    			negative = true;
    		}
    		// if price is given without comma, save the price
    		// multiplied by 100 (to include cents)
    		if(!price.contains(".") && !price.contains(",")) {
    			finalPrice = Integer.parseInt(price) * 100;
    		}
    		else {
    			String[] priceParts = null;
    			if(price.contains(","))
        			priceParts = price.split(",");
        		if(price.contains("."))
        			priceParts = price.split("\\.");
        		
        		// value after comma
        		if(priceParts[1].length() == 1) {
        			priceParts[1] = priceParts[1] + "0";
        		}
        		
        		int priceBeforeComma = Integer.parseInt(priceParts[0])*100;
        		int priceAfterComma = Integer.parseInt(priceParts[1]);
        		finalPrice = priceBeforeComma + priceAfterComma; // price in integer, multiplied by 100
        		if(negative)
        			finalPrice *= -1;
    		}
    		// debug output
    		if(i < 25) {
    			System.out.println("price for location "+location.getId()+": "+dateString+", "+timeString+", "+price);
    			System.out.println("price for location "+location.getId()+": "+dateString+", "+timeString+", "+finalPrice);
    		}
    		
    		Date d = DateParser.parseDate(dateString);
    		temp.setTime(d);
    		cal.set(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DATE));
    		
    		Integer hour = Integer.parseInt(timeString);
    		cal.set(Calendar.HOUR_OF_DAY, hour); // hour is set on the calendar with local timezone
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 0);
    		cal.set(Calendar.MILLISECOND, 0);
    		
    		if(location.getId().equals(LocationType.LOCATION_HAMINA)) {
    			if(d.compareTo(LocationType.getDSTDateOn(location, cal.get(Calendar.YEAR))) == 0) {
        			if(hour == 3) {
        				cal.add(Calendar.HOUR_OF_DAY, -1);
        				hour = 2;
        				timeString = String.valueOf(hour);
        			}
        		}
    			if(d.compareTo(LocationType.getDSTDateOff(location, cal.get(Calendar.YEAR))) == 0) {
    				if(dstOff  &&  hour == 2) {
    					cal.add(Calendar.HOUR_OF_DAY, 1);
    					hour = 3;
    					dstOff = false;
    				}
    				if(hour == 2) {
    					dstOff = true;
    				}
    			}
    		}
    		
    		
    		
    		
    		// getTimeInMillis() will always return number of milliseconds since 1970 for UTC time
    		timeLag = timeZone.getOffset(cal.getTimeInMillis()) / EnergyMarketType.MIN_DST_TIME;
//    		if(i == 0) {
//    			timeLagOld = timeLag;
//    		}
//    		// DST activated
//    		else if(timeLagOld < timeLag) {
//    			dstChange = 1;
//    		}
//    		// DST deactivated
//    		else if(timeLagOld > timeLag) {
//    			dstChange = -1;
//    		}
    	
    		
    		Calendar from = Calendar.getInstance(timeZone);
    		Calendar to = Calendar.getInstance(timeZone);
    		
    		from.set(2012, Calendar.MARCH, 24);
    		to.set(2012, Calendar.MARCH, 26);
    		// get range for dst time change 4th nov
    		
    		DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        	formatter.setTimeZone(timeZone);
    		if(cal.after(from) && cal.before(to)) {
    			System.out.println("price for location "+location.getId()+": "+dateString+", "+timeString+", "+finalPrice+", "+timeLag
    					+", "+formatter.format(cal.getTime()) + ", "+cal.getTimeInMillis());
    		}
    		
    		// skip saving data for dates before the last saved date
    		if(lastDate != null  &&  !cal.getTime().after(lastDate)) {
    			continue;
    		}
    		
    		DAPrice daPrice = new DAPrice();
        	daPrice.setBiddingDate(cal.getTime());
        	daPrice.setInterval(1);
        	daPrice.setIntervalUnit("hour");
        	daPrice.setLocation(location);
        	daPrice.setPrice(finalPrice);
        	daPrice.setTimelag((int)timeLag);
        	
//        	saveDAPrice(daPrice);
    	}
    }
    
    public Date findMaxDate(Location location) {
    	TypedQuery<Date> q = em.createNamedQuery("DAPrice.findMaxDate", Date.class);
    	q.setParameter("locationId", location.getId());
    	return q.getSingleResult();
    }
}
