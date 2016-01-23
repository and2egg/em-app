package at.ac.tuwien.thesis.caddc.data.resource.rt;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerRT;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeMassachussettsDAXLS;
import at.ac.tuwien.thesis.caddc.data.resource.types.rt.ResourceTypeMassachussettsRTXLS;

/**
 * 
 */
public class ResourceManagerMassachussettsRT extends ResourceManagerRT {

	public ResourceManagerMassachussettsRT() {
		super(new ResourceTypeMassachussettsRTXLS());
	}
}
