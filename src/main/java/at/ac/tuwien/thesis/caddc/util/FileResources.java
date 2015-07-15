package at.ac.tuwien.thesis.caddc.util;

/**
 * 
 * @author Andreas Egger
 */
public class FileResources {

	public String getResourcePath(String resourceName) {
		String path = getClass().getClassLoader().getResource(resourceName).getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    return path;
	}
}
