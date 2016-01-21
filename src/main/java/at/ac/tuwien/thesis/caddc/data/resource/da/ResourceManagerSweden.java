package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeSwedenHTML;

/**
 * 
 */
public class ResourceManagerSweden extends ResourceManager {

	public ResourceManagerSweden() {
		this.addResource(new ResourceTypeSwedenHTML());
	}
}
