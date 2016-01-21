package at.ac.tuwien.thesis.caddc.data.resource.types.da;

import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserBelgiumXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeBelgiumXLS extends ResourceType {

	public ResourceTypeBelgiumXLS() {
		for(Integer year = 2012; year < 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/BELPEX/spotmarket_data_"+year+".xls"));
		}
		this.parser = new ParserBelgiumXLS();
	}
}
