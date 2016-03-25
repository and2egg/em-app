package at.ac.tuwien.thesis.caddc.data.resource.types.rt;

import at.ac.tuwien.thesis.caddc.data.parse.types.rt.ParserPJMRTCSV;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypePennsylvaniaRTXLS extends ResourceType {

	public ResourceTypePennsylvaniaRTXLS() {
		for(Integer year = 2012; year <= 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/PJM/hatfield_"+year+".csv"));
		}
		
		this.parser = new ParserPJMRTCSV();
	}
}
