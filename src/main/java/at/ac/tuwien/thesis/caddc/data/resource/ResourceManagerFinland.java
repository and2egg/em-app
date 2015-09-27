package at.ac.tuwien.thesis.caddc.data.resource;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceTypeFinlandHTML;

/**
 * 
 */
public class ResourceManagerFinland extends ResourceManager {

	public ResourceManagerFinland() {
		this.addResource(new ResourceTypeFinlandHTML());
	}
}
