package at.ac.tuwien.thesis.caddc.data.resource.rt;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypeMichiganRTXLS;

/**
 * 
 */
public class ResourceManagerMichiganRT extends ResourceManagerRT {

	public ResourceManagerMichiganRT() {
		super(new ResourceTypeMichiganRTXLS());
	}
}
