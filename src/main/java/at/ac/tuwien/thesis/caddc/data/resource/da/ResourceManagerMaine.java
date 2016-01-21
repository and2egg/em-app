package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeMaineXLS;

/**
 * 
 */
public class ResourceManagerMaine extends ResourceManager {

	public ResourceManagerMaine() {
		this.addResource(new ResourceTypeMaineXLS());
	}
}
