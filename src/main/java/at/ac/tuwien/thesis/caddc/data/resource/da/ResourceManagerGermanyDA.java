package at.ac.tuwien.thesis.caddc.data.resource.da;

import at.ac.tuwien.thesis.caddc.data.resource.ResourceManager;
import at.ac.tuwien.thesis.caddc.data.resource.ResourceManagerDA;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeFinlandDAHTML;
import at.ac.tuwien.thesis.caddc.data.resource.types.da.ResourceTypeGermanyDAXLS;

/**
 * 
 */
public class ResourceManagerGermanyDA extends ResourceManagerDA {

	public ResourceManagerGermanyDA() {
		super(new ResourceTypeGermanyDAXLS());
	}
}
