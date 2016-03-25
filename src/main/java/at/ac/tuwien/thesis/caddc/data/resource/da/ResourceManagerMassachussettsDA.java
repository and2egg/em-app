package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerDA;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeMassachussettsDAXLS;

/**
 * 
 */
public class ResourceManagerMassachussettsDA extends ResourceManagerDA {

	public ResourceManagerMassachussettsDA() {
		super(new ResourceTypeMassachussettsDAXLS());
	}
}
