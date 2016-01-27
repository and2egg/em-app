package at.ac.tuwien.thesis.caddc.data.resource.types.da;

import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserGermanyDAXLS;
import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserMaineDAXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeGermanyDAXLS extends ResourceType {

	public ResourceTypeGermanyDAXLS() {
		for(Integer year = 2012; year <= 2014; year++) {
			addResourcePath(year, getPath("data", "energydata/EPEXSpot/energy_spot_historie_"+year+".xls"));
		}
		this.parser = new ParserGermanyDAXLS();
	}
}
