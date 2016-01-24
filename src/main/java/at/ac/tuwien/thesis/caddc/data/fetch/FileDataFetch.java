package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import at.ac.tuwien.thesis.caddc.data.fetch.exception.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.exception.MissingDataException;
import at.ac.tuwien.thesis.caddc.data.format.Resource;

/**
 * Class for fetching data from a file
 */
public class FileDataFetch implements DataFetch {

	private String path;
	private File file;
	
	
	public FileDataFetch(String path) throws MissingDataException {
		if(path == null || path.length() == 0)
			throw new MissingDataException("Invalid Path");
		this.path = path;
		this.file = new File(path);
	}
	
	public FileDataFetch(File file) throws MissingDataException {
		if(file == null || !file.isFile())
			throw new MissingDataException("Invalid File");
		this.path = file.getAbsolutePath();
		this.file = file;
	}


	/**
	 * @return
	 * @throws FetchDataException
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetch()
	 */
	@Override
	public Resource fetch() throws FetchDataException {
		String content;
		try {
			 content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			throw new FetchDataException("IOException: "+e.getLocalizedMessage());
		}
		return new Resource(file, content);
	}

}
