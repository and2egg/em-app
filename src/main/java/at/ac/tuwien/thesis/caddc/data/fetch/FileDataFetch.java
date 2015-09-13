package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 
 */
public class FileDataFetch implements DataFetch {

	private String path;
	
	
	public FileDataFetch(String path) {
		this.path = path;
	}
	
	
	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetchToFile()
	 */
	@Override
	public File fetchToFile() {
		return new File(path);
	}

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetchToString()
	 */
	@Override
	public String fetchToString() throws FetchDataException {
		try {
			return new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			throw new FetchDataException("IOException: "+e.getLocalizedMessage());
		}	
	}

}