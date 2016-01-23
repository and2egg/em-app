package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerDA;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeFinlandDAHTML;

/**
 * 
 */
public class ResourceManagerFinlandDA extends ResourceManagerDA {

	public ResourceManagerFinlandDA() {
		super(new ResourceTypeFinlandDAHTML());
	}
}
