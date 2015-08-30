package at.ac.tuwien.thesis.caddc.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.util.DateParser;

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

	    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    
	    REXP wd = c.eval("getwd()");
	    c.eval("source(\"rscripts/functions.R\")");
	    
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
	
	
	public String[] getForecasts(Long locationId, Integer trainingsPeriod, String startDateString, String endDateString) throws REXPMismatchException, REngineException {
		RConnection c = new RConnection(server, port);
	    
		String[] modelNames = getModelList(c, "forecast", locationId, trainingsPeriod, startDateString, endDateString);
		System.out.println("model list : "+modelNames.length+", names: "+Arrays.toString(modelNames));
		String[] meanValues = new String[0];
	    
	    for(String modelName : modelNames) {
			c.eval("load(\"forecast/"+modelName+"\")");
			String name = modelName.substring(0, modelName.lastIndexOf("."));
			String[] values = c.eval(name+"$mean").asStrings();
			meanValues = ArrayUtils.addAll(meanValues, values);
		}
	    c.close();
	    
	    return meanValues;
	}
	
	
	public String generateForecastsAllModels() throws REXPMismatchException, REngineException {
		RConnection c = new RConnection(server, port);

	    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    c.eval("library(\"forecast\")");
	    
	    String[] modelNames = c.eval("list.files(path=\"models\", recursive=FALSE, include.dirs=FALSE)").asStrings();
	    
	    for(String modelName : modelNames) {
			c.eval("load(\"models/"+modelName+"\")");
			String name = modelName.substring(0, modelName.lastIndexOf("."));
			String fcName = "fc_"+name;
			c.eval(fcName+" <- forecast("+name+",h=24)");
			c.eval("save("+fcName+", file=\"forecast/"+fcName+".RData\")");
			System.out.println("Forecast for model "+name+" finished");
		}
	    c.close();
	    return "forecasts generated for "+modelNames.length+" models";
	}
	
	
	public String generateForecasts(Long locationId, Integer trainingsPeriod, String startDateString, String endDateString) throws REXPMismatchException, REngineException {
		RConnection c = new RConnection(server, port);

	    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    c.eval("source(\"rscripts/functions.R\")");
	    c.eval("library(\"forecast\")");
	    
	    String[] modelNames = getModelList(c, "models", locationId, trainingsPeriod, startDateString, endDateString);
	    for(String modelName : modelNames) {
			c.eval("load(\"models/"+modelName+"\")");
			String name = modelName.substring(0, modelName.lastIndexOf("."));
			String fcName = "fc_"+name;
			c.eval(fcName+" <- forecast("+name+",h=24)");
			c.eval("save("+fcName+", file=\"forecast/"+fcName+".RData\")");
		}
	    c.close();
	    return "forecasts generated for "+modelNames.length+" models";
	}
	
	
	private String[] getModelList(RConnection c, String modelPath, Long locationId, Integer trainingsPeriod, 
									String startDateString, String endDateString) throws REXPMismatchException, REngineException {
	    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
		
		Date startDate = DateParser.parseDate(startDateString);
	    Date endDate = DateParser.parseDate(endDateString);
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String startStr = sdf.format(startDate);
	    String endStr = sdf.format(endDate);
	    
	    Integer locId = locationId.intValue();
	    String pattern = ".*_"+locId+"_"+trainingsPeriod+"d_.*"; // get modelnames containing the given location id
	    
	    String[] names = c.eval("list.files(path=\""+modelPath+"\", pattern=\""+pattern+"\")").asStrings();
	    Arrays.sort(names);
	    System.out.println("names len = "+names.length);
	    
	    boolean add = false;
	    List<String> result = new ArrayList<String>();
	    for (int i = 0; i < names.length; i++) {
	    	String dateStr = names[i].substring(names[i].lastIndexOf("_")+1, names[i].lastIndexOf("."));
	    	if(dateStr.equals(startStr))
	    		add = true;
	    	if(add) {
	    		result.add(names[i]);
	    		System.out.println("add model "+names[i]);
	    	}
	    	if(dateStr.equals(endStr))
	    		add = false;
	    }
	    
	    // Save the modelnames in a String array
	    String[] modelNames = result.toArray(new String[0]);
		
		return modelNames;
	}
	
	
	public String generateModel(String modelName, String csvData, int targetPeriod, 
						boolean approximation, boolean stepwise, boolean output, boolean plot) throws RserveException, REXPMismatchException {
		RConnection c = new RConnection(server, port);

	    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    c.eval("source(\"rscripts/functions.R\")");
	    c.eval("loadLibraries()");
	   
	    c.assign("csv_input", csvData);
	    c.eval("con <- textConnection(csv_input)");
		c.eval("prices <- read.csv(con, header = FALSE, strip.white = TRUE, blank.lines.skip = TRUE, " +
                "quote = \"\\\"\", dec = \".\", sep = \",\", comment.char = \"\", "+
                "col.names=c(\"times\",\"prices\"))");
	    c.eval("close(con)");
	    c.eval("dates_training <- prices$times");
	    c.eval("prices_training <- prices$prices");
	    
	    c.eval(modelName+" <- generate_model(prices_training, target_period="+targetPeriod+", "
	    		+ "approximation="+String.valueOf(approximation).toUpperCase()+", "
	    		+ "stepwise="+String.valueOf(stepwise).toUpperCase()+", "
	    		+ "output="+String.valueOf(output).toUpperCase()+", "
	    		+ "plot="+String.valueOf(plot).toUpperCase()+")");
	    
	    c.eval("save("+modelName+", file=\"models/"+modelName+".RData\")");
	    
	    c.close();
	    
	    return "Model saved";
	}
	
	
	public String generateModel(String modelName, String csvData) throws RserveException, REXPMismatchException {
		return generateModel(modelName, csvData, 24, true, true, true, false);
	}
	
	
	public String loadModels(String[] modelNames) throws RserveException, REXPMismatchException {
		RConnection c = new RConnection(server, port);

	    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    
		for(String modelName : modelNames) {
			REXP name = c.eval("load(\"models/"+modelName+".RData\")");
		}
		
		c.close();
	    
	    return "loaded models";
	}
	
	public String loadModel(String modelName) throws RserveException, REXPMismatchException  {
		RConnection c = new RConnection(server, port);

	    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
	    path = path.substring(1); // remove leading slash
	    
	    c.eval("setwd(\""+path+"\")");
	    
		REXP name = c.eval("load(\"models/"+modelName+".RData\")");
	    
	    c.close();
	    
	    return "loaded model "+name.asString();
	}
	
}
