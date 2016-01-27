package at.ac.tuwien.thesis.caddc.data.resource.rt;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeMaineDAXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypeMaineRTXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypePennsylvaniaRTXLS;

/**
 * 
 */
public class ResourceManagerPennsylvaniaRT extends ResourceManagerRT {

	public ResourceManagerPennsylvaniaRT() {
		super(new ResourceTypePennsylvaniaRTXLS());
	}
}
