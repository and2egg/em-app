package at.ac.tuwien.thesis.caddc.service;

import java.io.InputStream;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.util.FileResources;
import at.ac.tuwien.thesis.caddc.util.RUtils;

/**
 * Bean to handle calls to and from R
 */
@Stateless
public class RManager {
	
	private String server;
	private Integer port;
	
	@PostConstruct
	private void init() {
		server = "localhost";
		port = 6311;
		
		System.out.println("server "+server+", port "+port);
	}

	public String testR() throws RserveException, REXPMismatchException {
		RConnection c = new RConnection(server, port);
	    REXP x = c.eval("R.version.string");

	    String path = getClass().getClassLoader().getResource("rscripts").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    
	    REXP wd = c.eval("getwd()");
	    c.eval("source(\"functions.R\")");
	    
	    REXP hello = c.eval("hello_world(\"Achiever !!\")");
	    
	    System.out.println("PALINDROME OUTPUT ---");
	    // call the function. Return true
        REXP is_aba_palindrome = c.eval("palindrome('aba')");
        System.out.println(is_aba_palindrome.asString()); // prints 1 => true
 
        // call the function. return false
        REXP is_abc_palindrome = c.eval("palindrome('abc')");
        System.out.println(is_abc_palindrome.asString()); // prints 0 => false
	    
	    String server_str = server + ":" + port;
	    c.close();
	    return x.asString() + 
	    		"\n wd: "+wd.asString()+
	    		"\n "+hello.asString()+
	    		"\n palindrome 1 : "+is_aba_palindrome.asString()+
	    		"\n palindrome 2 : "+is_abc_palindrome.asString()+
	    		"\n running @ " + server_str;
	}
	
	public String rTestReadData(String csvData) throws RserveException, REXPMismatchException {
		RConnection c = new RConnection(server, port);

	    String path = getClass().getClassLoader().getResource("rscripts").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    
	    REXP wd = c.eval("getwd()");
	    c.eval("source(\"NPSForecast.R\")");
	    
	    path = getClass().getClassLoader().getResource("energydata").getPath(); // folder "energydata" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    
	    c.eval("prices <- read.csv(\"US/prices.csv\")"); // \"~/R DA/energy data/US States (Drazen)/prices.csv\"
	    
	    c.eval("all_prices <- ts(prices[,-1], start=c(2010, 48), frequency=365*24)"); 
	    
	    c.close();
	    return "";
	}
}
