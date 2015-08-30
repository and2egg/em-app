package at.ac.tuwien.thesis.caddc.util;

/**
 * 
 * @author Andreas Egger
 */
public class Currency {

	// Value at 2015-08-30
	private static final Double EURO_TO_DOLLAR = 1.11800;
	
	public static Integer convertToDollar(Integer euro) {
		return new Integer((int)(euro * EURO_TO_DOLLAR));
	}
	
	
	public static boolean isInDollar(Long locationId) {
		if(locationId == 1 || locationId == 2 || locationId == 5) {
			return false;
		} else {
			return true;
		}
	}
}
