package at.ac.tuwien.thesis.caddc.data.resource.types.rt;

import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserMaineDAXLS;
import at.ac.tuwien.thesis.caddc.data.parse.types.rt.ParserMaineRTXLS;
import at.ac.tuwien.thesis.caddc.data.parse.types.rt.ParserPJMRTCSV;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeVirginiaRTXLS extends ResourceType {

	public ResourceTypeVirginiaRTXLS() {
		for(Integer year = 2012; year <= 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/PJM/richmond_"+year+".csv"));
		}
		
		this.parser = new ParserPJMRTCSV();
	}
}
