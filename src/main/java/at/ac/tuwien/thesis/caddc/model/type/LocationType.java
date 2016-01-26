package at.ac.tuwien.thesis.caddc.model.type;

import java.util.Calendar;
import java.util.Date;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.util.DateParser;

/**
 * 
 */
public abstract class LocationType {

	public static final Long LOCATION_HAMINA = 1L;
	public static final Long LOCATION_STOCKHOLM = 2L;
	public static final Long LOCATION_PORTLAND = 3L;
	public static final Long LOCATION_BOSTON = 4L;
	public static final Long LOCATION_ST_GHISLAIN = 5L;
	public static final Long LOCATION_POTSDAM = 6L;
	public static final Long LOCATION_RICHMOND = 7L;
	public static final Long LOCATION_BRIGHTON = 8L;
	public static final Long LOCATION_HATFIELD = 9L;
	public static final Long LOCATION_MADISON = 10L;
	public static final Long LOCATION_GEORGETOWN = 11L;
	
	
	protected Long locationId;
	protected Long energyMarketId;
	protected boolean dstOn;	
	protected boolean dstOff;
	
	
	
	/**
	 * @return the locationId
	 */
	public Long getLocationId() {
		return locationId;
	}


	/**
	 * @param locationId the locationId to set
	 */
	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
	
	
	/**
	 * @return the energyMarketId
	 */
	public Long getEnergyMarketId() {
		return energyMarketId;
	}


	/**
	 * @param energyMarketId the energyMarketId to set
	 */
	public void setEnergyMarketId(Long energyMarketId) {
		this.energyMarketId = energyMarketId;
	}
	
	
	/**
	 * @return the dstOn
	 */
	public boolean isDstOn() {
		return dstOn;
	}


	/**
	 * @param dstOn the dstOn to set
	 */
	public void setDstOn(boolean dstOn) {
		this.dstOn = dstOn;
	}


	/**
	 * @return the dstOff
	 */
	public boolean isDstOff() {
		return dstOff;
	}


	/**
	 * @param dstOff the dstOff to set
	 */
	public void setDstOff(boolean dstOff) {
		this.dstOff = dstOff;
	}
	
	
	public static Integer getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}
	
	public static Integer getYear(String dateString) {
		Date d = DateParser.parseDate(dateString);
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.YEAR);
	}

}
