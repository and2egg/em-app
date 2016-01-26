package at.ac.tuwien.thesis.caddc.model.type;

import java.util.Calendar;
import java.util.Date;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.handler.TimeZoneDSTHandler;

/**
 * 
 */
public class LocationTypeISONE extends LocationType {

	/**
	 * @param location
	 */
	public LocationTypeISONE(Long locationId) {
		this.locationId = locationId;
		this.energyMarketId = EnergyMarketType.EMARKET_ISO_NE;
	}

	/**
	 * @param cal
	 * @param date
	 * @param hour
	 * @return
	 */
	public Integer handleDST(Calendar cal, Date d, Integer hour) {
//		if(d.compareTo(TimeZoneDSTHandler.getDSTDateOn(locationId, cal.get(Calendar.YEAR))) == 0) {
//			if(dstOn  &&  hour == 2) {
//				cal.set(Calendar.HOUR_OF_DAY, hour-1);
//				dstOn = false;
//			}
//			// skip hour 1 because of missing price
//			else if(hour == 1) {
//				dstOn = true;
//				continue;
//			}
//		}
//		if(d.compareTo(TimeZoneDSTHandler.getDSTDateOff(locationId, cal.get(Calendar.YEAR))) == 0) {
//			if(dstOff  &&  hour == 1) {
//				finalPrice /= 2;
//				dstOff = false;
//			}
//			else if(hour == 1) {
//				finalPrice /= 2;
//				cal.set(Calendar.HOUR_OF_DAY, hour-1);
//				cal.add(Calendar.HOUR_OF_DAY, 1);
//				dstOff = true;
//				i--;
//			}
//		}
		return hour;
	}

	
	
	
}
