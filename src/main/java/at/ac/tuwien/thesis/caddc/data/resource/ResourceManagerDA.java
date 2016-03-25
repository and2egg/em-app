package at.ac.tuwien.thesis.caddc.data.resource;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.thesis.caddc.data.resource.types.ResourceType;

/**
 * Resource Manager for resources of DA (day ahead) prices
 */
public abstract class ResourceManagerDA extends ResourceManager {

	private List<ResourceType> daResources = new ArrayList<ResourceType>();
	
	public ResourceManagerDA(ResourceType resourceType) {
		this.addDAResource(resourceType);
	}
	
	public void addDAResource(ResourceType type) {
		this.daResources.add(type);
	}
	
	public void removeDAResource(ResourceType type) {
		this.daResources.remove(type);
	}
	
	public ResourceType getFirstDAResource() {
		return getDAResource(0);
	}
	
	public ResourceType getDAResource(Integer index) {
		return daResources.get(index);
	}
	
}
