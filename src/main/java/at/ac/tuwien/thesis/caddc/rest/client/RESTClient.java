package at.ac.tuwien.thesis.caddc.rest.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;

import at.ac.tuwien.thesis.caddc.data.parse.types.impl.XLSParserGeneric;
import sun.misc.BASE64Encoder;

/**
 * 
 */
public class RESTClient {

	
	public static void fetchURL(String urlString) throws ConnectException {
		try {
			 
			URL url = new URL(urlString);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");
			
			BASE64Encoder enc = new sun.misc.BASE64Encoder();
			String username = "egger.andreas.1@gmail.com";
			String password = "d3mqua0h";
			String userpassword = username + ":" + password;
			String encodedAuthorization = enc.encode( userpassword.getBytes() );
			conn.setRequestProperty("Authorization", "Basic "+
					encodedAuthorization);
	 
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	 
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
	 
			conn.disconnect();
	 
		  } catch (MalformedURLException e) {
			  throw new ConnectException("MalformedURLException: "+e.getLocalizedMessage());
		  } catch (IOException e) {
			  throw new ConnectException("IOException: "+e.getLocalizedMessage());
		  }
	}
	
	
	public static String urlToString(String url) throws ConnectException {
		URL urlObj = null;
		HttpURLConnection conn = null;
		InputStream in = null;
		String result = null;
		
		try {
			urlObj = new URL(url);
			conn = (HttpURLConnection) urlObj.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new ConnectException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			in = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			result = writer.toString();
		} catch (MalformedURLException e) {
			throw new ConnectException("MalformedURLException: "+e.getLocalizedMessage());
		} catch (SocketException e) {
			throw new ConnectException("SocketException: "+e.getLocalizedMessage()+"\n"+e.getMessage());
		} catch (IOException e) {
			throw new ConnectException("IOException: "+e.getLocalizedMessage());
		} finally {
			conn.disconnect();
		}
		return result;
	}
	
	
	public static File urlToFile(String url) throws ConnectException {
		try {
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new ConnectException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			
			File file = new File("tempFile");
			OutputStream out = new FileOutputStream(file);
			out.write(IOUtils.toByteArray(conn.getInputStream()));
			out.close();
			
			return file;
		} catch (MalformedURLException e) {
			throw new ConnectException("MalformedURLException: "+e.getLocalizedMessage());
		} catch (IOException e) {
			throw new ConnectException("IOException: "+e.getLocalizedMessage());
		}
	}
	
}
