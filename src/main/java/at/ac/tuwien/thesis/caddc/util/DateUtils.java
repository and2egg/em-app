package at.ac.tuwien.thesis.caddc.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 */
public class DateUtils {
	
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	
	
	public static String formatDate(Date date) {
		return df.format(date);
	}
	
	
	public static Date getCurrentDateWithTimeZone(String timeZone) {
		
		Calendar start = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		
		start.set(Calendar.HOUR_OF_DAY, 12);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
    	start.set(Calendar.MILLISECOND, 0);
    	
    	Date d = start.getTime();
    	
    	return d;
	}
}
