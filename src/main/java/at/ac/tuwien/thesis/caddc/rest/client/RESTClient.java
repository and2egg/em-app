package at.ac.tuwien.thesis.caddc.rest.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import sun.misc.BASE64Encoder;

/**
 * 
 */
public class RESTClient {

	
	
	public void fetchURL(String urlString) {
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
			  System.err.println("MalformedURLException: "+e.getLocalizedMessage());
		  } catch (IOException e) {
			  System.err.println("IOException: "+e.getLocalizedMessage());
		  }
	}
}
