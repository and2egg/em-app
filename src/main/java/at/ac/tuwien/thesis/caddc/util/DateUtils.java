package at.ac.tuwien.thesis.caddc.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 */
public class DateUtils {

	
	
	public static Date getCurrentDateWithTimeZone(String timeZone) {
		
		Calendar start = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		

		start.set(Calendar.HOUR_OF_DAY, 12);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
    	start.set(Calendar.MILLISECOND, 0);
    	
    	Date d = start.getTime();
    	
    	return d;
    	
//    	// Check date formats
//    	String format = "";
//    	if(startDate.length() == 10) {
//    		format = "yyyy-MM-dd";
//    	} else if(startDate.length() == 19) {
//    		format = "yyyy-MM-dd HH:mm:ss";
//    	}
//    	SimpleDateFormat sdfStart = new SimpleDateFormat(format);
//    	
//    	if(endDate.length() == 10) {
//    		format = "yyyy-MM-dd";
//    	} else if(endDate.length() == 19) {
//    		format = "yyyy-MM-dd HH:mm:ss";
//    	}
//    	SimpleDateFormat sdfEnd = new SimpleDateFormat(format);
//    	try {
//    		
//    		d.getTime();
//    	}
//    		// Parse dates
//    		sdfStart.parse(startDate);
//    		sdfEnd.parse(endDate);
//			
//			Calendar s = sdfStart.getCalendar();
//			Calendar e = sdfEnd.getCalendar();
//			System.out.println("S DATE = "+s.getTime());
//			
//			start.set(s.get(Calendar.YEAR), s.get(Calendar.MONTH), s.get(Calendar.DAY_OF_MONTH), 
//					s.get(Calendar.HOUR_OF_DAY), s.get(Calendar.MINUTE), s.get(Calendar.SECOND));
//	    	start.set(Calendar.MILLISECOND, 0);
	}
	
	
}
