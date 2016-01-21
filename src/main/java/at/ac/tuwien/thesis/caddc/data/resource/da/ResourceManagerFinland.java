package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeFinlandHTML;

/**
 * 
 */
public class ResourceManagerFinland extends ResourceManager {

	public ResourceManagerFinland() {
		this.addResource(new ResourceTypeFinlandHTML());
	}
}
