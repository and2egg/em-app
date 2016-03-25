package at.ac.tuwien.thesis.caddc.data.resource.types.rt;

import at.ac.tuwien.thesis.caddc.data.parse.types.rt.ParserPJMRTCSV;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeMichiganRTXLS extends ResourceType {

	public ResourceTypeMichiganRTXLS() {
		for(Integer year = 2012; year <= 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/PJM/brighton_"+year+".csv"));
		}
		
		this.parser = new ParserPJMRTCSV();
	}
}
