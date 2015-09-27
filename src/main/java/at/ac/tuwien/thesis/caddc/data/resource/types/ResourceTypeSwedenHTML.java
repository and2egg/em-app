package at.ac.tuwien.thesis.caddc.data.resource.types;

import at.ac.tuwien.thesis.caddc.data.parse.types.ParserSwedenHTML;

/**
 * 
 */
public class ResourceTypeSwedenHTML extends ResourceType {

	public ResourceTypeSwedenHTML() {
		addResourcePath(2012, getPath("data", "energydata/NPS/Elspot_Prices_2012_Hourly_EUR.xls"));
		
		for(Integer year = 2013; year < 2015; year++) {
			addResourceURL(year, "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_"+year.toString()+"_hourly_eur.xls");
		}
		
		this.parser = new ParserSwedenHTML();
	}
}
