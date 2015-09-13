package at.ac.tuwien.thesis.caddc.data.fetch;

import at.ac.tuwien.thesis.caddc.data.format.Format;

/**
 * Interface for fetching data from a website 
 * and return in a specific Format
 */
public interface IDataFetch {

	<T extends Format> T fetch(String url);
}
