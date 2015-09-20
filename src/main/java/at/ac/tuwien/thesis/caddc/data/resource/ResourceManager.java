package at.ac.tuwien.thesis.caddc.data.resource;

import java.util.List;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public class ResourceManager {

	private List<ResourceType> resources;
	
	public void addResource(ResourceType type) {
		this.resources.add(type);
	}
	
	public void removeResource(ResourceType type) {
		this.resources.remove(type);
	}
	
	public ResourceType get() {
		return getResourceAt(0);
	}
	
	public ResourceType getResourceAt(Integer index) {
		return resources.get(index);
	}
}
