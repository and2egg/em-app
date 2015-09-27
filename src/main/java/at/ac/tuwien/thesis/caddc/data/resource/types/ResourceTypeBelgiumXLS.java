package at.ac.tuwien.thesis.caddc.data.resource.types;

import at.ac.tuwien.thesis.caddc.data.parse.types.ParserBelgiumXLS;

/**
 * 
 */
public class ResourceTypeBelgiumXLS extends ResourceType {

	public ResourceTypeBelgiumXLS() {
		for(Integer year = 2012; year < 2015; year++) {
			addResourceURL(year, "energydata/BELPEX/spotmarket_data_"+year+".xls");
		}
		this.parser = new ParserBelgiumXLS();
	}
}
