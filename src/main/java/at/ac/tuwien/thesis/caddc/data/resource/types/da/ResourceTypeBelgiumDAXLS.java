package at.ac.tuwien.thesis.caddc.data.resource.types.da;

import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserBelgiumDAXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeBelgiumDAXLS extends ResourceType {

	public ResourceTypeBelgiumDAXLS() {
		for(Integer year = 2012; year < 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/Belpex/spotmarket_data_"+year+".xls"));
		}
		this.parser = new ParserBelgiumDAXLS();
	}
}
