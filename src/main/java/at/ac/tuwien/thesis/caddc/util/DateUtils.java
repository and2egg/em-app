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
	public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
	public static final String DATE_FORMAT_NO_SECONDS = "yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_COMPACT = "yyyyMMdd";
	
	private static DateFormat df = new SimpleDateFormat(DATE_FORMAT);
	
	
	public static String formatDate(Date date) {
		return df.format(date);
	}
	
	
	public static String formatDate(Date date, String dateFormat) {
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
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
