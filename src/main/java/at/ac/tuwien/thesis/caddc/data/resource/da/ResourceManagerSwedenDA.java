package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerDA;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeSwedenDAHTML;

/**
 * 
 */
public class ResourceManagerSwedenDA extends ResourceManagerDA {

	public ResourceManagerSwedenDA() {
		super(new ResourceTypeSwedenDAHTML());
	}
}
