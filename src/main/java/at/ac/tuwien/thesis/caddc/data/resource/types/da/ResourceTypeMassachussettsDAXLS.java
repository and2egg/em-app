package at.ac.tuwien.thesis.caddc.data.resource.types.da;

import at.ac.tuwien.thesis.caddc.data.parse.types.da.ParserMassachussettsDAXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceTypeMassachussettsDAXLS extends ResourceType {

	public ResourceTypeMassachussettsDAXLS() {
		for(Integer year = 2012; year <= 2015; year++) {
			addResourcePath(year, getPath("data", "energydata/ISONE/"+year+"_smd_hourly.xls"));
		}
//		for(Integer year = 2012; year < 2014; year++) {
//			addResourceURL(year, "http://www.iso-ne.com/static-assets/documents/markets/hstdata/znl_info/hourly/"+year+"_smd_hourly.xls");
//		}
//		addResourceURL(2014, "http://www.iso-ne.com/static-assets/documents/2015/05/2014_smd_hourly.xls");
		this.parser = new ParserMassachussettsDAXLS();
	}
}
