package at.ac.tuwien.thesis.caddc.model.handler;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.type.EnergyMarketType;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType;
import at.ac.tuwien.thesis.caddc.model.type.LocationType;
import at.ac.tuwien.thesis.caddc.model.type.LocationTypeFactory;
import at.ac.tuwien.thesis.caddc.model.type.LocationTypeFinland;
import at.ac.tuwien.thesis.caddc.util.DateParser;

/**
 * 
 */
public abstract class EnergyPriceHandler {
	
	
	public abstract List<EnergyPriceType> parseEnergyPriceData(List<String> priceData, Location location, Date lastDate, boolean debug);
	
	
	protected Integer parsePrice(String price) {
    	Integer finalPrice;
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
		return finalPrice;
    }
	
	/**
	 * Checks whether the given date is the date where DST time starts, at the given location
	 * @param date
	 * @param location
	 * @param year
	 * @return
	 */
	protected boolean isDSTDateOn(Date date, Location location) {
		return date.compareTo(TimeZoneDSTHandler.getDSTDateOn(location.getId(), 
								LocationType.getYear(date))) == 0;
	}
	
	/**
	 * Checks whether the given date is the date where DST time ends, at the given location
	 * @param date
	 * @param location
	 * @param year
	 * @return
	 */
	protected boolean isDSTDateOff(Date date, Location location) {
		return date.compareTo(TimeZoneDSTHandler.getDSTDateOff(location.getId(), 
								LocationType.getYear(date))) == 0;
	}
}
