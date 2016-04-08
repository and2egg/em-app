package at.ac.tuwien.thesis.caddc.data.format;

import java.io.File;

/**
 * A wrapper for resources represented as both
 * a file and a String (content of the file) 
 */
public class Resource {

	private File file;
	private String content;
	
	public Resource(File file, String content) {
		this.file = file;
		this.content = content;
	}
	
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}	
}
