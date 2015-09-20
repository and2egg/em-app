package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import at.ac.tuwien.thesis.caddc.data.format.Resource;

/**
 * 
 */
public class FileDataFetch implements DataFetch {

	private String path;
	
	
	public FileDataFetch(String path) throws MissingDataException {
		if(path == null || path.length() == 0)
			throw new MissingDataException("Invalid Path");
		this.path = path;
	}
	
	public FileDataFetch(File file) throws MissingDataException {
		if(file == null || !file.isFile())
			throw new MissingDataException("Invalid File");
		this.path = file.getAbsolutePath();
	}


	/**
	 * @return
	 * @throws FetchDataException
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetch()
	 */
	@Override
	public Resource fetch() throws FetchDataException {
		File file;
		String content;
		
		file = new File(path);
		try {
			 content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			throw new FetchDataException("IOException: "+e.getLocalizedMessage());
		}
		return new Resource(file, content);
	}

}
