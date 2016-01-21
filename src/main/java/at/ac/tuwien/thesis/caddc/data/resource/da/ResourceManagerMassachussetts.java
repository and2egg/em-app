package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeMassachussettsXLS;

/**
 * 
 */
public class ResourceManagerMassachussetts extends ResourceManager {

	public ResourceManagerMassachussetts() {
		this.addResource(new ResourceTypeMassachussettsXLS());
	}
}
