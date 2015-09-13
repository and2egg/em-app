package at.ac.tuwien.thesis.caddc.data.fetch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import sun.misc.BASE64Encoder;

/**
 * 
 */
public class URLDataFetchSecure implements DataFetch {

	private String url;
	private String username;
	private String password;
	private String requestMethod;
	private String acceptProperty;
	
	
	public static class Builder {
		// Required properties
		private String url;
		private String username;
		private String password;
		
		// Optional properties
		private String requestMethod;
		private String acceptProperty;
		
		public Builder(String url) {
			this.url = url;
		}
		
		public Builder userName(String username) {
			this.username = username;
			return this;
		}
		
		public Builder password(String password) {
			this.password = password;
			return this;
		}
		
		public Builder requestMethod(String requestMethod) {
			this.requestMethod = requestMethod;
			return this;
		}
		
		public Builder acceptProperty(String acceptProperty) {
			this.acceptProperty = acceptProperty;
			return this;
		}
		
		public URLDataFetchSecure build() throws MissingArgumentException {
			return new URLDataFetchSecure(this);
		}
	}
	
	private URLDataFetchSecure(Builder builder) throws MissingArgumentException {
		this.url = builder.url;
		this.username = builder.username;
		this.password = builder.password;
		
		this.requestMethod = builder.requestMethod;
		this.acceptProperty = builder.acceptProperty;
		
		if(this.username == null || this.password == null) {
			throw new MissingArgumentException("URLDataFetchSecure: Please provide username and password");
		}
		
		if(this.requestMethod == null) {
			this.requestMethod = "GET";
		}
		if(this.acceptProperty == null) {
			this.acceptProperty = "application/xml";
		}
	}

	
	/**
	 * @return
	 * @see at.ac.tuwien.thesis.caddc.data.fetch.DataFetch#fetchToFile()
	 */
	@Override
	public File fetchToFile() throws FetchDataException {
		
		URL urlObj = null;
		HttpsURLConnection conn = null;
		OutputStream out = null;
		File file = null;
		
		try {
			 
			urlObj = new URL(this.url);
			conn = (HttpsURLConnection) urlObj.openConnection();
			conn.setRequestMethod(this.requestMethod);
			conn.setRequestProperty("Accept", this.acceptProperty);
			conn.setRequestProperty("Authorization", getBasicAuthorizationString(
										this.username, 
										this.password));
	 
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
//			BufferedReader br = new BufferedReader(new InputStreamReader(
//				(conn.getInputStream())));
//	 
//			String output;
//			System.out.println("Output from Server .... \n");
//			while ((output = br.readLine()) != null) {
//				System.out.println(output);
//			}
	 
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
		HttpsURLConnection conn = null;
		InputStream in = null;
		String result = null;
		
		try {
			 
			urlObj = new URL(this.url);
			conn = (HttpsURLConnection) urlObj.openConnection();
			conn.setRequestMethod(this.requestMethod);
			conn.setRequestProperty("Accept", this.acceptProperty);
			conn.setRequestProperty("Authorization", getBasicAuthorizationString(
										this.username, 
										this.password));
	 
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
			in = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			result = writer.toString();
		} catch (MalformedURLException e) {
			throw new FetchDataException("MalformedURLException: "+e.getLocalizedMessage());
		} catch (IOException e) {
			throw new FetchDataException("IOException: "+e.getLocalizedMessage());
		} finally {
			conn.disconnect();
		}
		return result;
	}
	
	
	
	private String getBasicAuthorizationString(String username, String password) {
		BASE64Encoder enc = new sun.misc.BASE64Encoder();
		String userpassword = username + ":" + password;
		String encodedAuthorization = enc.encode( userpassword.getBytes() );
		return "Basic "+encodedAuthorization;
	}
	

}
