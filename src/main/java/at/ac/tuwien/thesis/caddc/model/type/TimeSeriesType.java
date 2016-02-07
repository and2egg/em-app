package at.ac.tuwien.thesis.caddc.model.type;

import java.util.Calendar;

/**
 * 
 */
public class TimeSeriesType {
	
	public static final String HOUR = "h";
	public static final String DAY 	= "d";
	public static final String WEEK = "w";

	private Integer timeInterval;
	private Integer timeUnit;
	
	
	public TimeSeriesType() {
		
	}
	
	public TimeSeriesType(Integer timeInterval, int timeUnit) {
		this.timeInterval = timeInterval;
		this.timeUnit = timeUnit;
	}
	
	
	/**
	 * Convert given time Period as String to a TimeSeriesType 
	 * containing the suitable interval and time unit
	 * Attention: only supported time units! (see static fields)
	 * @param timePeriod the time period to parse (example: "14d", "1w")
	 * @return a TimeSeriesType object containing the respective interval and time unit
	 */
    public static TimeSeriesType convertTimePeriod(String timePeriod) {
    	String timeInterval = timePeriod.substring(0, timePeriod.length()-1);
    	String timeUnit = timePeriod.substring(timePeriod.length()-1);
    	
    	Integer interval = Integer.valueOf(timeInterval);
    	Integer unit = null;
    	if(timeUnit.equals(HOUR)) {
    		unit = Calendar.HOUR_OF_DAY;
    	}
    	else if(timeUnit.equals(DAY)) {
    		unit = Calendar.DATE;
    	}
    	else if(timeUnit.equals(WEEK)) {
    		unit = Calendar.WEEK_OF_YEAR;
    	}
    	
    	if(interval != null && unit != null) {
    		return new TimeSeriesType(interval, unit);
    	}
    	
    	return null;
    }
    
    
	/**
	 * @return the timeInterval
	 */
	public Integer getTimeInterval() {
		return timeInterval;
	}

	/**
	 * @param timeInterval the timeInterval to set
	 */
	public void setTimeInterval(Integer timeInterval) {
		this.timeInterval = timeInterval;
	}

	/**
	 * @return the timeUnit
	 */
	public Integer getTimeUnit() {
		return timeUnit;
	}

	/**
	 * @param timeUnit the timeUnit to set
	 */
	public void setTimeUnit(Integer timeUnit) {
		this.timeUnit = timeUnit;
	}
	
}
