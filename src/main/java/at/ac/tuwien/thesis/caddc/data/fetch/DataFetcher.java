package at.ac.tuwien.thesis.caddc.data.fetch;

/**
 * 
 */
public abstract class DataFetcher {

	/**
	 * Method to retrieve a DataFetch object based on the given year
	 * and available resources
	 * @param year the year for which to retrieve price data
	 * @return a DataFetch object constructed from available resources
	 * @throws MissingDataException is thrown if no valid path or URL
	 * 						can be retrieved for the given year
	 */
	public DataFetch getDataFetch(Integer year) throws FetchDataException {
		DataFetch dataFetch;
		try {
			if(getPath(year) != null) {
				String path = getPath(year);
				dataFetch = new FileDataFetch(path);
			}
			else {
				String url = getURL(year);
				dataFetch = new URLDataFetch(url);
			}
		} catch (MissingDataException e) {
			throw new FetchDataException(getClass().getName()+": No resources exist for year "+year);
		}
		return dataFetch;
	}
	
	/**
	 * Method to retrieve the path of a file containing energy data 
	 * based on the year
	 * @param year the year for which to obtain energy prices
	 * @return a String containing the path of the file
	 */
	public abstract String getPath(Integer year);
	
	/**
	 * Method to retrieve a custom price URL based on the year
	 * @param year the year for which to obtain energy prices
	 * @return a url String to get prices from
	 */
	public abstract String getURL(Integer year);
	
	/**
     * Returns the path for a given resource at a resource root
     * @param resourceRoot the root folder of the resource
     * @param resourcePath the path relative to the root folder
     * @return the complete real resource path
     */
    protected String getResourcePath(String resourceRoot, String resourcePath) {
    	String path = getClass().getClassLoader().getResource(resourceRoot).getPath();
		path = path + resourcePath;
	    path = path.substring(1); // remove leading slash
	    return path;
	}
}
