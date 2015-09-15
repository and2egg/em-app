package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

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
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetchToFile()
	 */
	@Override
	public File fetchToFile() throws FetchDataException {
		URL urlObj = null;
		HttpURLConnection conn = null;
		OutputStream out = null;
		File file = null;
		
		try {
			urlObj = new URL(this.url);
			conn = (HttpURLConnection) urlObj.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new FetchDataException("Connection failed: HTTP error code = "
						+ conn.getResponseCode());
			}
			
			file = new File("tempFile");
			out = new FileOutputStream(file);
			out.write(IOUtils.toByteArray(conn.getInputStream()));
			out.flush();
			
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
		return file;
	}
	

	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetchToString()
	 */
	@Override
	public String fetchToString() throws FetchDataException {
		URL urlObj = null;
		HttpURLConnection conn = null;
		InputStream in = null;
		String result = null;
		
		try {
			urlObj = new URL(this.url);
			conn = (HttpURLConnection) urlObj.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new FetchDataException("Connection failed: HTTP error code = "
						+ conn.getResponseCode());
			}
			in = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			result = writer.toString();
		} catch (MalformedURLException e) {
			throw new FetchDataException("MalformedURLException: "+e.getLocalizedMessage());
		} catch (SocketException e) {
			throw new FetchDataException("SocketException: "+e.getLocalizedMessage()+"\n"+e.getMessage());
		} catch (IOException e) {
			throw new FetchDataException("IOException: "+e.getLocalizedMessage());
		} finally {
			conn.disconnect();
		}
		return result;
	}

}
