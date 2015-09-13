package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;

/**
 * 
 */
public interface DataFetch {

	File fetchToFile() throws FetchDataException;
	String fetchToString() throws FetchDataException;
}
