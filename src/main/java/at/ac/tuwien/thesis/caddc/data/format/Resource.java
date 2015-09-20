package at.ac.tuwien.thesis.caddc.data.format;

import java.io.File;

/**
 * 
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
