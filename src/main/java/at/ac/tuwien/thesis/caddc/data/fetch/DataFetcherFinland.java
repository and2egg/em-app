package at.ac.tuwien.thesis.caddc.data.fetch;

/**
 * 
 */
public class DataFetcherFinland extends DataFetcher {

	/**
	 * @param year
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetcher#getPath(java.lang.Integer)
	 */
	@Override
	public String getPath(Integer year) {
		if(year == 2012) {
    		return getResourcePath("data", "energydata/NPS/Elspot_Prices_2012_Hourly_EUR.xls");
    	}
		return null;
	}

	/**
	 * @param year
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetcher#getURL(java.lang.Integer)
	 */
	@Override
	public String getURL(Integer year) {
		if(year >= 2013) {
    		return "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_"+year.toString()+"_hourly_eur.xls";
    	}
    	return null;
	}
}
