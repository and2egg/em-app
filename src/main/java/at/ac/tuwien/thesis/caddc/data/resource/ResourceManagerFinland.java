package at.ac.tuwien.thesis.caddc.data.resource;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceFinlandHTML;

/**
 * 
 */
public class ResourceManagerFinland extends ResourceManager {

	public ResourceManagerFinland() {
		this.addResource(new ResourceFinlandHTML());
	}
}
