package at.ac.tuwien.thesis.caddc.data.resource.types.rt;

import at.ac.tuwien.thesis.caddc.data.parse.types.rt.ParserMaineRTXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeMaineRTXLS extends ResourceType {

	public ResourceTypeMaineRTXLS() {
		for(Integer year = 2012; year <= 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/ISONE/"+year+"_smd_hourly.xls"));
		}
		
		this.parser = new ParserMaineRTXLS();
	}
}
