package at.ac.tuwien.thesis.caddc.data.resource.types;

import at.ac.tuwien.thesis.caddc.data.parse.types.ParserMassachussettsXLS;

/**
 * 
 */
public class ResourceTypeMassachussettsXLS extends ResourceType {

	public ResourceTypeMassachussettsXLS() {
		for(Integer year = 2012; year < 2014; year++) {
			addResourceURL(year, "http://www.iso-ne.com/static-assets/documents/markets/hstdata/znl_info/hourly/"+year+"_smd_hourly.xls");
		}
		addResourceURL(2014, "http://www.iso-ne.com/static-assets/documents/2015/05/2014_smd_hourly.xls");
		this.parser = new ParserMassachussettsXLS();
	}
}
