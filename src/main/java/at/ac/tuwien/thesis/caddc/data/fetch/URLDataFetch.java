package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import at.ac.tuwien.thesis.caddc.data.fetch.exception.FetchDataException;
import at.ac.tuwien.thesis.caddc.data.fetch.exception.MissingDataException;
import at.ac.tuwien.thesis.caddc.data.format.Resource;

/**
 * 
 */
public class URLDataFetch implements DataFetch {

	private String url;
	
	public URLDataFetch(String url) throws MissingDataException {
		if(url == null || url.length() == 0)
			throw new MissingDataException("Invalid URL");
		this.url = url;
	}
	
	/**
	 * @return
	 * @throws FetchDataException
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetch()
	 */
	@Override
	public Resource fetch() throws FetchDataException {
		URL urlObj = null;
		HttpURLConnection conn = null;
		InputStream in = null;
		OutputStream out = null;
		File file = null;
		String content = null;
		
		try {
			urlObj = new URL(this.url);
			conn = (HttpURLConnection) urlObj.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new FetchDataException("Connection failed: HTTP error code = "
						+ conn.getResponseCode());
			}
			
			in = conn.getInputStream();
			// fetch file
			file = new File("tempFile");
			out = new FileOutputStream(file);
			out.write(IOUtils.toByteArray(in));
			out.flush();
			// fetch String
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			content = writer.toString();
		} catch (MalformedURLException e) {
			throw new FetchDataException("MalformedURLException: "+e.getLocalizedMessage());
		} catch (IOException e) {
			throw new FetchDataException("IOException: "+e.getLocalizedMessage());
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					System.err.println("URLDataFetch: Error closing OutputStream");
				}
			}
			conn.disconnect();
		}
		return new Resource(file, content);
	}
}
