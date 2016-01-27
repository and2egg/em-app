package at.ac.tuwien.thesis.caddc.model.handler;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    	Integer finalPrice = null;
		Double pr = parsePriceFormat(price);
	    if(pr != null) {
	    	finalPrice = (int)Math.round(pr * 100);
	    }
		return finalPrice;
    }
	
	
	private Double parsePriceFormat(String price) {
		NumberFormat format = NumberFormat.getInstance(getLocale());
		try {
			return format.parse(price).doubleValue();
		} catch (ParseException e) {
			System.err.println(getClass().getSimpleName()+": Could not parse price "+price);
		}
		return null;
	}
	
	
	protected abstract Locale getLocale();
	
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
