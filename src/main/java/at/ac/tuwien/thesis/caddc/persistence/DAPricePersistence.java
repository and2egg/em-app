package at.ac.tuwien.thesis.caddc.persistence;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.Location;

/**
 * 
 * @author Andreas Egger
 */
@Stateless
public class DAPricePersistence {

	@Inject
	private EntityManager em;
	
	@Inject
    private LocationRepository locationRepository;
	
	public void saveDAPrice(DAPrice price) {
    	em.persist(price);
    }
    
    public void saveDAPriceList(List<DAPrice> daPriceList) {
    	for(DAPrice daPrice : daPriceList)
    		saveDAPrice(daPrice);
    }
    
    /**
     * Method to save a list of prices encoded in Strings
     * @param prices a String array containing prices encoded in Strings
     * 			The encoding for a single price needs to have the following form: 
     * 			datestring;timestring;price
     */
    public void saveDAPrices(List<String> priceData, String location) {
    	
    	TimeZone timeZone = getTimeZone(location);
    	
    	Location loc = locationRepository.findByName(location);
		if(loc == null) {
			System.err.println("Please provide a valid location");
			return;
		}
    	
    	for(int i = 0; i < priceData.size(); i++) {
    		String[] split = priceData.get(i).split(";");
    		if(split.length != 3) {
    			System.err.println("Price data format at index "+i+" is invalid");
    			continue;
    		}
    		String dateString = split[0];
    		String timeString = split[1];
    		String price = split[2];
    		
    		String[] priceParts = price.split(",");
    		Integer calcPrice = Integer.parseInt(priceParts[0])*100 + Integer.parseInt(priceParts[1]);
    		
    		if(i < 10)
    			System.out.println(dateString+","+timeString+","+price);
    		
    		Calendar cal = Calendar.getInstance(timeZone);
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    		try {
				cal.setTime(sdf.parse(dateString));
			} catch (ParseException e) {
				System.err.println("DAPricePersistence: Could not parse date string: "+e.getMessage());
			}
    		
    		String start = timeString.substring(0, 2);
    		
    		if(i < 10) 
    			System.out.println("start = "+start);
    		
    		cal.set(Calendar.HOUR, Integer.parseInt(start));
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 0);
    		cal.set(Calendar.MILLISECOND, 0);
    		
    		DAPrice daPrice = new DAPrice();
        	daPrice.setBiddingDate(cal.getTime());
        	daPrice.setInterval(1);
        	daPrice.setIntervalUnit("hour");
        	daPrice.setLocation(loc);
        	daPrice.setPrice(calcPrice);
        	daPrice.setTimelag(0);
        	
        	saveDAPrice(daPrice);
    	}
    }
    
    private TimeZone getTimeZone(String location) {
    	if(location.equals("Helsinki")) {
    		return TimeZone.getTimeZone("Europe/Helsinki");
    	} else if(location.equals("Stockholm")) {
    		return TimeZone.getTimeZone("Europe/Stockholm");
    	} else if(location.equals("Brussels")) {
        	return TimeZone.getTimeZone("Europe/Brussels");
    	} else if(location.equals("Portland")) {
    		return TimeZone.getTimeZone("America/New_York");
    	} else if(location.equals("Boston")) {
    		return TimeZone.getTimeZone("America/New_York");
    	} 
    	return TimeZone.getDefault();
    }
}
