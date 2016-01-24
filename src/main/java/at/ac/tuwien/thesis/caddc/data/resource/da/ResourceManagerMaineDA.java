package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerDA;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeMaineDAXLS;

/**
 * 
 */
public class ResourceManagerMaineDA extends ResourceManagerDA {

	public ResourceManagerMaineDA() {
		super(new ResourceTypeMaineDAXLS());
	}
}
