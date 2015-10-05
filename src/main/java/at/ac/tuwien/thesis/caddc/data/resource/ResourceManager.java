package at.ac.tuwien.thesis.caddc.data.resource;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * 
 */
public abstract class ResourceManager {

	private List<ResourceType> resources = new ArrayList<ResourceType>();
	
	public void addResource(ResourceType type) {
		this.resources.add(type);
	}
	
	public void removeResource(ResourceType type) {
		this.resources.remove(type);
	}
	
	public ResourceType get() {
		return get(0);
	}
	
	public ResourceType get(Integer index) {
		return resources.get(index);
	}
}
