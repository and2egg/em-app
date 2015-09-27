package at.ac.tuwien.thesis.caddc.data.resource;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceTypeMassachussettsXLS;

/**
 * 
 */
public class ResourceManagerMassachussetts extends ResourceManager {

	public ResourceManagerMassachussetts() {
		this.addResource(new ResourceTypeMassachussettsXLS());
	}
}
