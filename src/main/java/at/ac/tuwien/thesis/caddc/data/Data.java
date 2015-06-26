package at.ac.tuwien.thesis.caddc.data;

import at.ac.tuwien.thesis.caddc.data.format.Format;

/**
 * 
 */
public class Data<T extends Format> {

	public T getData() {
		return T;
	}
}
