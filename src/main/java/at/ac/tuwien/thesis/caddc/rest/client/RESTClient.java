package at.ac.tuwien.thesis.caddc.rest.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.thesis.caddc.data.parse.NordPoolFinlandParser;
import at.ac.tuwien.thesis.caddc.data.parse.XLSParser;
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
	
	
	public String fetchNordPoolSpotData(String urlString) {
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader br = null;
		InputStream in = null;
		
		String result = null;
		String prices = null;
		try {
			url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				System.err.println("OTHER RESPONSE CODE: "+conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			
			br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			
			in = conn.getInputStream();
			
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			result = writer.toString();
			prices = NordPoolFinlandParser.parsePrices(result);

		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException: "+e.getLocalizedMessage());
		} catch (SocketException e) {
			System.err.println("SocketException: "+e.getLocalizedMessage()+"\n"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException: "+e.getLocalizedMessage());
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.disconnect();
		}
		return result + "\n"+ prices;
	}
	
	
	public void parseXLS(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
			
			
			XLSParser parser = new XLSParser();
			parser.parse(conn.getInputStream());
			
//			String output;
//			System.out.println("Output from Server .... \n");
//			
//			// create file
//			String path = getClass().getClassLoader().getResource("test").getPath();
//			System.out.println("Path = "+path);
//			PrintWriter writer = new PrintWriter(path+"XLSOutput.txt", "UTF-8");
//			
//			while ((output = br.readLine()) != null) {
//				writer.println(output);
//			}
//			writer.close();
	 
			conn.disconnect();
	 
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException: "+e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("IOException: "+e.getLocalizedMessage());
		}
	}
}
