package at.ac.tuwien.thesis.caddc.data.resource.types.da;

import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserFinlandHTML;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeFinlandHTML extends ResourceType {

	public ResourceTypeFinlandHTML() {
		addResourcePath(2012, getPath("data", "energydata/NPS/Elspot_Prices_2012_Hourly_EUR.xls"));
		
		for(Integer year = 2013; year <= 2015; year++) {
			addResourceURL(year, "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_"+year.toString()+"_hourly_eur.xls");
		}
		
		this.parser = new ParserFinlandHTML();
	}
}
