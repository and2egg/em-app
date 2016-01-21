package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeBelgiumXLS;

/**
 * 
 */
public class ResourceManagerBelgium extends ResourceManager {

	public ResourceManagerBelgium() {
		this.addResource(new ResourceTypeBelgiumXLS());
	}
}
