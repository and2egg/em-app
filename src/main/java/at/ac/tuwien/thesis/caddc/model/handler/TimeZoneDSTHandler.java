package at.ac.tuwien.thesis.caddc.model.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.type.LocationType;
import at.ac.tuwien.thesis.caddc.util.DateParser;

/**
 * Class for mapping dst dates to locations
 */
public class TimeZoneDSTHandler {

	
	private static final Map<Integer, Map<Long, Date[]>> dstMap = new HashMap<Integer, Map<Long, Date[]>>();
	
	
	static {
		Map<Long, Date[]> dstDates2012 = new HashMap<Long, Date[]>();
		Map<Long, Date[]> dstDates2013 = new HashMap<Long, Date[]>();
		Map<Long, Date[]> dstDates2014 = new HashMap<Long, Date[]>();
		Map<Long, Date[]> dstDates2015 = new HashMap<Long, Date[]>();
		
		Date[] europe2012 = new Date[] { DateParser.parseDate("2012-03-25"), DateParser.parseDate("2012-10-28") };
		Date[] europe2013 = new Date[] { DateParser.parseDate("2013-03-31"), DateParser.parseDate("2013-10-27") };
		Date[] europe2014 = new Date[] { DateParser.parseDate("2014-03-30"), DateParser.parseDate("2014-10-26") };
		Date[] europe2015 = new Date[] { DateParser.parseDate("2015-03-29"), DateParser.parseDate("2015-10-25") };
		
		Long[] euLocations = new Long[] {	LocationType.LOCATION_HAMINA, 
											LocationType.LOCATION_ST_GHISLAIN, LocationType.LOCATION_POTSDAM,
//											LocationType.LOCATION_STOCKHOLM 
											};
		
		// put all eu locations into the map of the respective year, mapping IDs to Date arrays
		putLocationsAtDST(dstDates2012, euLocations, europe2012);
		putLocationsAtDST(dstDates2013, euLocations, europe2013);
		putLocationsAtDST(dstDates2014, euLocations, europe2014);
		putLocationsAtDST(dstDates2015, euLocations, europe2015);
		
		Date[] usa2012 = new Date[] { DateParser.parseDate("2012-03-11"), DateParser.parseDate("2012-11-04") };
		Date[] usa2013 = new Date[] { DateParser.parseDate("2013-03-10"), DateParser.parseDate("2013-11-03") };
		Date[] usa2014 = new Date[] { DateParser.parseDate("2014-03-09"), DateParser.parseDate("2014-11-02") };
		Date[] usa2015 = new Date[] { DateParser.parseDate("2015-03-08"), DateParser.parseDate("2015-11-01") };
		
		Long[] usLocations = new Long[] {	LocationType.LOCATION_PORTLAND, LocationType.LOCATION_BOSTON, 
				LocationType.LOCATION_RICHMOND, LocationType.LOCATION_BRIGHTON, LocationType.LOCATION_HATFIELD, 
				LocationType.LOCATION_MADISON, LocationType.LOCATION_GEORGETOWN };
		
		// put all us locations into the map of the respective year, mapping IDs to Date arrays
		putLocationsAtDST(dstDates2012, usLocations, usa2012);
		putLocationsAtDST(dstDates2013, usLocations, usa2013);
		putLocationsAtDST(dstDates2014, usLocations, usa2014);
		putLocationsAtDST(dstDates2015, usLocations, usa2015);
		
		dstMap.put(2012, dstDates2012);
		dstMap.put(2013, dstDates2013);
		dstMap.put(2014, dstDates2014);
		dstMap.put(2015, dstDates2015);
	}
	
	
	private static void putLocationsAtDST(Map<Long, Date[]> dstDates, Long[] locationIds, Date[] dateRange) {
		
		for(Long id : locationIds) {
			dstDates.put(id, dateRange);
		}
	}
	
	
	public static Date getDSTDateOn(Long locationId, Integer year) {
		Map<Long, Date[]> dstDates = dstMap.get(year);
		if(dstDates.containsKey(locationId)) {
			return dstDates.get(locationId)[0];
		}
		return null;
	}
	
	
	public static Date getDSTDateOff(Long locationId, Integer year) {
		Map<Long, Date[]> dstDates = dstMap.get(year);
		if(dstDates.containsKey(locationId)) {
			return dstDates.get(locationId)[1];
		}
		return null;
	}
	
	
	public static TimeZone getTimeZone(Location location) {
		String tz = location.getTimeZone();
		TimeZone timeZone;
		if(!tz.isEmpty()) {
			timeZone = TimeZone.getTimeZone(tz);
		}
		else {
			timeZone = TimeZone.getDefault();
		}
		return timeZone;
	}
	
	
}
