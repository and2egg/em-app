package at.ac.tuwien.thesis.caddc.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.thesis.caddc.model.Location;

/**
 * 
 */
public class LocationTypeFactory {

	
	private static Map<Long, LocationType> types = new HashMap<Long, LocationType>();
	
	
	static {
		
		types.put(LocationType.LOCATION_HAMINA, new LocationTypeFinland());
		types.put(LocationType.LOCATION_PORTLAND, new LocationTypeISONE(LocationType.LOCATION_PORTLAND));
		types.put(LocationType.LOCATION_BOSTON, new LocationTypeISONE(LocationType.LOCATION_BOSTON));
		
	}
	
	
	public static LocationType getLocationType(Location location) {
		if(types.containsKey(location.getId())) {
			return types.get(location.getId());
		}
		return null;
	}
	
}
