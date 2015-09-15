package at.ac.tuwien.thesis.caddc.util;

/**
 * 
 */
public class ExceptionFormatter {

	public String getMessage(Exception e) {
		return e.getClass().getSimpleName()+": "+e.getLocalizedMessage();
	}
}
