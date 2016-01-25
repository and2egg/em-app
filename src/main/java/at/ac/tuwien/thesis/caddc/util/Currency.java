package at.ac.tuwien.thesis.caddc.util;

/**
 * 
 * @author Andreas Egger
 */
public class Currency {

	// Value at 2015-08-30: 1.11800
	// Value at 2016-01-24: 1.07985
	private static final Double EURO_TO_DOLLAR = 1.07985;
	
	public static Integer convertToDollar(Integer euro) {
		return new Integer((int)(euro * EURO_TO_DOLLAR));
	}
	
	
	public static boolean isInDollar(Long locationId) {
		if(locationId == 1 || locationId == 2 || locationId == 5 || locationId == 6) {
			return false;
		} else {
			return true;
		}
	}
}
