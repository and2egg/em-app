package at.ac.tuwien.thesis.caddc.data.resource;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceTypeBelgiumXLS;

/**
 * 
 */
public class ResourceManagerBelgium extends ResourceManager {

	public ResourceManagerBelgium() {
		this.addResource(new ResourceTypeBelgiumXLS());
	}
}
