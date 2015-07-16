package at.ac.tuwien.thesis.caddc.rest.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import at.ac.tuwien.thesis.caddc.data.parse.XLSParser;
import sun.misc.BASE64Encoder;

/**
 * 
 */
public class RESTClient {

	public static String fetchData(String urlString) {
		URL url = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;

		try {
			url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new SocketException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			inputStream = conn.getInputStream();
			conn.disconnect();
			return null;
			
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException: "+e.getLocalizedMessage());
		} catch (SocketException e) {
			System.err.println("SocketException: "+e.getLocalizedMessage()+"\n"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException: "+e.getLocalizedMessage());
		} finally {
			conn.disconnect();
		}
		return null;
	}
	
	public static InputStream secureFetchData(String urlString) {
		URL url = null;
		HttpsURLConnection conn = null;
		InputStream inputStream = null;

		try {
			url = new URL(urlString);
			conn = (HttpsURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new SocketException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			inputStream = conn.getInputStream();
			conn.disconnect();
			return inputStream;
			
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException: "+e.getLocalizedMessage());
		} catch (SocketException e) {
			System.err.println("SocketException: "+e.getLocalizedMessage()+"\n"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException: "+e.getLocalizedMessage());
		} finally {
			conn.disconnect();
		}
		return inputStream;
	}
	
	public static void fetchURL(String urlString) {
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
	
	
	public static String fetchDataString(String urlString) {
		URL url = null;
		HttpURLConnection conn = null;
		InputStream in = null;
		String result = null;
		
		System.out.println("URL = "+urlString);
		try {
			url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				System.err.println("OTHER RESPONSE CODE: "+conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			in = conn.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			result = writer.toString();
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException: "+e.getLocalizedMessage());
		} catch (SocketException e) {
			System.err.println("SocketException: "+e.getLocalizedMessage()+"\n"+e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException: "+e.getLocalizedMessage());
		} finally {
			conn.disconnect();
		}
		return result;
	}
	
	
	public static List<String> fetchAndParseXLS(String urlString, Integer sheetNumber, int rowOffset, int[] colIndices) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
			List<String> result;
			XLSParser parser = new XLSParser();
			result = parser.parse(conn.getInputStream(),sheetNumber,rowOffset,colIndices);
				 
			conn.disconnect();
			return result;
	 
		} catch (MalformedURLException e) {
			System.err.println("MalformedURLException: "+e.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println("IOException: "+e.getLocalizedMessage());
		}
		return null;
	}
	
	public static List<String> fileFetchAndParseXLS(String path, Integer sheetNumber, int rowOffset, int[] colIndices) {
		try {
			InputStream in = new FileInputStream(path);
			List<String> result;
			XLSParser parser = new XLSParser();
			result = parser.parse(in,sheetNumber,rowOffset,colIndices);
			return result;
		} catch (IOException e) {
			System.err.println("IOException: "+e.getLocalizedMessage());
		}
		return null;
	}
}
