package at.ac.tuwien.thesis.caddc.data.resource.types.rt;

import at.ac.tuwien.thesis.caddc.data.parse.types.rt.ParserPJMRTCSV;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeOhioRTXLS extends ResourceType {

	public ResourceTypeOhioRTXLS() {
		for(Integer year = 2012; year <= 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/PJM/georgetown_"+year+".csv"));
		}
		
		this.parser = new ParserPJMRTCSV();
	}
}
