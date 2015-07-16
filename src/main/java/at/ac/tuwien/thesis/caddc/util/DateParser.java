package at.ac.tuwien.thesis.caddc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Andreas Egger
 */
public class DateParser {

	
	private static final HashMap<String, String> validDateFormats = new HashMap<String, String>() {{
	    put("^\\d{1,2}-.{3}-\\d{4}$", "dd-MMM-yyyy");
	    put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
	    put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
	    put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
	    put("^\\d{8}$", "yyyyMMdd");
	}};
	
	public static String checkDatePattern(String dateString) {
	    for (String regexp : validDateFormats.keySet()) {
	        if (dateString.toLowerCase().matches(regexp)) {
	            return validDateFormats.get(regexp);
	        }
	    }
	    return null; // Unknown format.
	}
	
	public static Date parseDate(String dateString) {
		String pattern = checkDatePattern(dateString);
		if(pattern == null) {
			System.err.println("Pattern not recognized");
			return null;
		}
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date result = sdf.parse(dateString);
			return result;
		} catch (ParseException e) {
			System.err.println("Could not parse date: "+e.getLocalizedMessage());
		}
		return null;
	}
}
