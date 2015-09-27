package at.ac.tuwien.thesis.caddc.data.resource.types;

import java.util.Map;

import at.ac.tuwien.thesis.caddc.data.fetch.DataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.FileDataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.URLDataFetch;
import at.ac.tuwien.thesis.caddc.data.fetch.exception.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.exception.MissingDataException;
import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;

/**
 * 
 */
public abstract class ResourceType {
	
	protected Map<Integer, String> resourcePathMap;
	protected Map<Integer, String> resourceURLMap;
	
	protected Parser parser;
	

	/**
	 * Method to retrieve a Resource object based on the given year
	 * and available resources
	 * @param year the year for which to retrieve price data
	 * @return a Resource object constructed from available resources
	 * @throws MissingDataException is thrown if no valid path or URL
	 * 						can be retrieved for the given year
	 */
	public Resource fetchData(Integer year) throws FetchDataException {
		DataFetch dataFetch;
		try {
			if(this.resourcePathMap.containsKey(year)) {
				String path = getResourcePath(year);
				dataFetch = new FileDataFetch(path);
			}
			else if(this.resourceURLMap.containsKey(year)) {
				String url = getResourceURL(year);
				dataFetch = new URLDataFetch(url);
			}
			else {
				throw new FetchDataException(getClass().getName()+": No resources exist for year "+year);
			}
		} catch (MissingDataException e) {
			throw new FetchDataException(getClass().getName()+": No resources exist for year "+year);
		}
		return dataFetch.fetch();
	}
	
	/**
	 * Method to get the parser associated with this Resource Type
	 * @return the parser for this Resource Type
	 */
	public Parser getParser() {
		return parser;
	}
	
	/**
	 * Method to retrieve the path of a file containing energy data 
	 * based on the year
	 * @param year the year for which to obtain energy prices
	 * @return a String containing the path of the file
	 */
	public String getResourcePath(Integer year) {
		if(this.resourcePathMap.containsKey(year))
			return this.resourcePathMap.get(year);
		return null;
	}
	
	/**
	 * Method to retrieve a custom price URL based on the year
	 * @param year the year for which to obtain energy prices
	 * @return a url String to get prices from
	 */
	public String getResourceURL(Integer year) {
		if(this.resourceURLMap.containsKey(year))
			return this.resourceURLMap.get(year);
		return null;
	}

	/**
	 * Method to add a path resource for the given year
	 * @param year the year for which to add a resource
	 * @param path the path to add to resources
	 */
	public void addResourcePath(Integer year, String path) {
		this.resourcePathMap.put(year, path);
	}

	/**
	 * Method to add a URL resource for the given year
	 * @param year the year for which to add a resource
	 * @param url the url to add to resources
	 */
	public void addResourceURL(Integer year, String url) {
		this.resourceURLMap.put(year, url);
	}
	
	/**
     * Returns the path for a given resource at a resource root
     * @param resourceRoot the root folder of the resource
     * @param resourcePath the path relative to the root folder
     * @return the complete real resource path
     */
    protected String getPath(String resourceRoot, String resourcePath) {
    	String path = getClass().getClassLoader().getResource(resourceRoot).getPath();
		path = path + resourcePath;
	    path = path.substring(1); // remove leading slash
	    return path;
	}
}
