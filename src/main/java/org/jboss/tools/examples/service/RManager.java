package org.jboss.tools.examples.service;

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
import org.jboss.tools.examples.util.RUtils;

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
	    path = path.substring(1); // remove trailing slash
	    
	    c.eval("setwd(\""+path+"\")");
	    
	    REXP wd = c.eval("getwd()");
	    c.eval("source(\"functions.R\")");
	    
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
	    		"\n palindrome 1 : "+is_aba_palindrome.asString()+
	    		"\n palindrome 2 : "+is_abc_palindrome.asString()+
	    		"\n running @ " + server_str;
	}
}
