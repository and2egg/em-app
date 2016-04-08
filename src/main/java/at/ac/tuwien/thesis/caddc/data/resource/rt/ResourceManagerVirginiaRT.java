package at.ac.tuwien.thesis.caddc.data.resource.rt;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypeVirginiaRTXLS;

/**
 * 
 */
public class ResourceManagerVirginiaRT extends ResourceManagerRT {

	public ResourceManagerVirginiaRT() {
		super(new ResourceTypeVirginiaRTXLS());
	}
}
