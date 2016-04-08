package at.ac.tuwien.thesis.caddc.data.resource.types.da;

import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserFinlandDAHTML;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeFinlandDAHTML extends ResourceType {

	public ResourceTypeFinlandDAHTML() {
		
		for(Integer year = 2012; year <= 2014; year++) {
			addResourcePath(year, getPath("data", "energydata/NordPool/elspot-prices_"+year+"_hourly_eur.xls"));
		}
		
//		for(Integer year = 2013; year <= 2015; year++) {
//			addResourceURL(year, "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_"+year.toString()+"_hourly_eur.xls");
//		}
		
		this.parser = new ParserFinlandDAHTML();
	}
}
