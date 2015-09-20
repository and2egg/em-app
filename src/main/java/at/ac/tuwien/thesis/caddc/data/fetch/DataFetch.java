package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;

import at.ac.tuwien.thesis.caddc.data.format.Resource;

/**
 * 
 */
public interface DataFetch {

	/**
	 * Method to fetch a resource in different formats
	 * @return the resource retrieved by this DataFetch object
	 * @throws FetchDataException is thrown when data could not be retrieved
	 */
	Resource fetch() throws FetchDataException;
}
