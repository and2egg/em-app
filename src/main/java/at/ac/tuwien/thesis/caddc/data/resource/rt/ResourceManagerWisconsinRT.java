package at.ac.tuwien.thesis.caddc.data.resource.rt;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypeWisconsinRTXLS;

/**
 * 
 */
public class ResourceManagerWisconsinRT extends ResourceManagerRT {

	public ResourceManagerWisconsinRT() {
		super(new ResourceTypeWisconsinRTXLS());
	}
}
