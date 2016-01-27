package at.ac.tuwien.thesis.caddc.data.resource.rt;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeMaineDAXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypeMaineRTXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypeMichiganRTXLS;

/**
 * 
 */
public class ResourceManagerMichiganRT extends ResourceManagerRT {

	public ResourceManagerMichiganRT() {
		super(new ResourceTypeMichiganRTXLS());
	}
}
