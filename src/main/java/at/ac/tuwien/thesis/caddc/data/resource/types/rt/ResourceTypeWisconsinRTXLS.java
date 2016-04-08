package at.ac.tuwien.thesis.caddc.data.resource.types.rt;

import at.ac.tuwien.thesis.caddc.data.parse.types.rt.ParserPJMRTCSV;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeWisconsinRTXLS extends ResourceType {

	public ResourceTypeWisconsinRTXLS() {
		for(Integer year = 2012; year <= 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/PJM/madison_"+year+".csv"));
		}
		
		this.parser = new ParserPJMRTCSV();
	}
}
