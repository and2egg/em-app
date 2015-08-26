package at.ac.tuwien.thesis.caddc.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Utilisation functions for R
 */
public class RUtils {
	
	private static String server = "localhost";
	private static Integer port = 6311;
	private static Set<Integer> active_ports = new HashSet<Integer>();
	
	public static String getServer() {
		if(server == null) {
			server = "localhost";
		}
		return server;
	}
	
	public static Integer getPort() {
		if(active_ports.isEmpty()) {
			active_ports.add(port);
			return port;
		}
		while(active_ports.contains(port)) port++;
		return port;
	}
}
