package at.ac.tuwien.thesis.caddc.data.resource;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public abstract class ResourceManagerRT extends ResourceManager {

	private List<ResourceType> rtResources = new ArrayList<ResourceType>();
	
	public ResourceManagerRT(ResourceType resourceType) {
		this.addRTResource(resourceType);
	}
	
	public void addRTResource(ResourceType type) {
		this.rtResources.add(type);
	}
	
	public void removeRTResource(ResourceType type) {
		this.rtResources.remove(type);
	}
	
	public ResourceType getFirstRTResource() {
		return getRTResource(0);
	}
	
	public ResourceType getRTResource(Integer index) {
		return rtResources.get(index);
	}
}
