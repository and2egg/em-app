package at.ac.tuwien.thesis.caddc.data.resource;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceTypeSwedenHTML;

/**
 * 
 */
public class ResourceManagerSweden extends ResourceManager {

	public ResourceManagerSweden() {
		this.addResource(new ResourceTypeSwedenHTML());
	}
}
