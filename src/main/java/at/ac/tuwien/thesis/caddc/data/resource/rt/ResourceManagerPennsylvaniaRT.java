package at.ac.tuwien.thesis.caddc.data.resource.rt;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypePennsylvaniaRTXLS;

/**
 * 
 */
public class ResourceManagerPennsylvaniaRT extends ResourceManagerRT {

	public ResourceManagerPennsylvaniaRT() {
		super(new ResourceTypePennsylvaniaRTXLS());
	}
}
