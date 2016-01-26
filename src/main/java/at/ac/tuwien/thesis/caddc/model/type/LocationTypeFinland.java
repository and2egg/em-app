package at.ac.tuwien.thesis.caddc.model.type;

import java.util.Calendar;
import java.util.Date;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.handler.TimeZoneDSTHandler;

/**
 * 
 */
public class LocationTypeFinland extends LocationType {

	/**
	 * @param location
	 */
	public LocationTypeFinland() {
		this.locationId = LocationType.LOCATION_HAMINA;
		this.energyMarketId = EnergyMarketType.EMARKET_NORD_POOL;
	}

	/**
	 * @param cal
	 * @param date
	 * @param hour
	 * @return
	 */
	public Integer handleDST(Calendar cal, Date d, Integer hour) {
		if(d.compareTo(TimeZoneDSTHandler.getDSTDateOn(locationId, cal.get(Calendar.YEAR))) == 0) {
			// mapping the gap from 1-3 to 2-4 (correct DST jump for Finland)
			if(hour == 3) {
				cal.add(Calendar.HOUR_OF_DAY, -1);
				hour = 2;
			}
		}
		if(d.compareTo(TimeZoneDSTHandler.getDSTDateOff(locationId, cal.get(Calendar.YEAR))) == 0) {
			if(dstOff  &&  hour == 2) {
				cal.add(Calendar.HOUR_OF_DAY, 1);
				hour = 3;
				dstOff = false;
			}
			else if(hour == 2) {
				dstOff = true;
			}
		}
		return hour;
	}

	
	
	
}
