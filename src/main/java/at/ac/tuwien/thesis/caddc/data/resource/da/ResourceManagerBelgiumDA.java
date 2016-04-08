package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerDA;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeBelgiumDAXLS;

/**
 * 
 */
public class ResourceManagerBelgiumDA extends ResourceManagerDA {

	public ResourceManagerBelgiumDA() {
		super(new ResourceTypeBelgiumDAXLS());
	}
}
