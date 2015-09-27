package at.ac.tuwien.thesis.caddc.data.resource;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceTypeMaineXLS;

/**
 * 
 */
public class ResourceManagerMaine extends ResourceManager {

	public ResourceManagerMaine() {
		this.addResource(new ResourceTypeMaineXLS());
	}
}
