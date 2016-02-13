package at.ac.tuwien.thesis.caddc.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;

import org.apache.commons.lang3.ArrayUtils;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType;
import at.ac.tuwien.thesis.caddc.model.type.LocationType;
import at.ac.tuwien.thesis.caddc.util.DateParser;

/**
 * Bean to handle calls to and from R
 */
@Stateless
public class RManager {
	
	private String server;
	private Integer port;
	
	private RConnection c;
	private boolean init;
	
	@PostConstruct
	private void init() {
		server = "localhost";
		port = 6311;
		
		System.out.println("server "+server+", port "+port);
	}
	
	
	public void initRConnection() throws RserveException, REXPMismatchException {
		
		if(!init) {
			System.out.println("Initialize Rserve connection...");
			
			c = new RConnection(server, port);

		    String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
		    path = path.substring(1); // remove leading slash
		    
		    c.eval("setwd(\""+path+"\")");
		    c.eval("source(\"rscripts/functions.R\")");
		    
		    init = true;
		}
	}
	
	public void closeRConnection() {
		init = false;
		if(c != null) {
			System.out.println("Closing Rserve connection...");
			c.close();
		}
	}

	/**
	 * A test method to test the connection to R via the set server and port
	 * @return a String indicating the result of test calculations
	 * @throws RserveException is thrown when the connection to the R server has been lost
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 */
	public String testR() throws RserveException, REXPMismatchException {
		
		initRConnection();
		
	    REXP x = c.eval("R.version.string");
	    REXP wd = c.eval("getwd()");
	    REXP hello = c.eval("hello_world(\"Achiever !!\")");
	    
	    c.eval("x <- stats::runif(20)");
	    c.eval("y <- list(a = 1, b = TRUE, c = \"oops\")");
	    c.eval("save(x, y, file = \"xy.RData\")");
	    
	    System.out.println("PALINDROME OUTPUT ---");
	    // call the function. Return true
        REXP is_aba_palindrome = c.eval("palindrome('aba')");
        System.out.println(is_aba_palindrome.asString()); // prints 1 => true
 
        // call the function. return false
        REXP is_abc_palindrome = c.eval("palindrome('abc')");
        System.out.println(is_abc_palindrome.asString()); // prints 0 => false
	    
	    String server_str = server + ":" + port;
	    
	    closeRConnection();
	    
	    return x.asString() + 
	    		"\n wd: "+wd.asString()+
	    		"\n "+hello.asString()+
	    		"\n palindrome 1 : "+is_aba_palindrome.asString()+
	    		"\n palindrome 2 : "+is_abc_palindrome.asString()+
	    		"\n running @ " + server_str;
	}
	
	/**
	 * Get the actual forecast (mean) values from previously calculated forecasts
	 * @param priceType the type of energy price models to retrieve
	 * @param locationIds the locationIds for which to retrieve the saved forecasts (separated by comma)
	 * 					if -1 then forecasts for all models contained within simulation 
	 * 						constrained by trainingsperiod and start/enddates are returned
	 * @param trainingsPeriod the trainingsperiod by which to filter the list of forecasts
	 * @param startDateString a string denoting the start date of the list of forecasts
	 * @param endDateString a string denoting the end date of the list of forecasts
	 * @return a String array containing the concatenated series of mean forecast values
	 * 			of all models considered by the parameter filters (one model has forecasts for
	 * 			exactly 24 hours i.e. one day, therefore a concatenation of values results in a seamless 
	 * 			series of forecast values)
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws REngineException is thrown when something has gone wrong on the R connection
	 */
	public String[] getForecasts(String priceType, String locationIds, Integer trainingsPeriod, 
					String startDateString, String endDateString) throws REXPMismatchException, REngineException {
		
		initRConnection();
	    
		boolean fullNames = true;
		
		String[] fcModelNames = getModelList(priceType, locationIds, trainingsPeriod, 
											startDateString, endDateString, fullNames, true);
		
		System.out.println("model list : "+fcModelNames.length);
		String[] meanValues = new String[0];
		
		if(fullNames) {
			for(String modelName : fcModelNames) {
				c.eval("load(\""+modelName+"\")");
				String name = modelName.substring(modelName.lastIndexOf("/")+1, modelName.lastIndexOf("."));
				String[] values = c.eval(name+"$mean").asStrings();
				meanValues = ArrayUtils.addAll(meanValues, values);
			}
		}
		else {
			for(String modelName : fcModelNames) {
				c.eval("load(\""+modelName+"\")");
				String name = modelName.substring(0, modelName.lastIndexOf("."));
				String[] values = c.eval(name+"$mean").asStrings();
				meanValues = ArrayUtils.addAll(meanValues, values);
			}
		}
	    
	    closeRConnection();
	    
	    return meanValues;
	}
	
	/**
	 * Generate forecasts for all currently saved models
	 * @param priceType the type of energy price models to create forecasts for ("da" or "rt")
	 * @return a String indicating the result of the calculation
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws REngineException is thrown when something has gone wrong on the R connection
	 */
	public String generateForecastsAllModels(String priceType) throws REXPMismatchException, REngineException {
		initRConnection();
		
	    c.eval("library(\"forecast\")");
	    
	    String modelPath = "models_"+priceType;
	    String fcPath = "forecast_"+priceType;
	    
	    String[] modelNames = c.eval("list.files(path=\""+modelPath+"\", recursive=TRUE, include.dirs=FALSE)").asStrings();
	    
	    for(String modelName : modelNames) {
			c.eval("load(\""+modelPath+"/"+modelName+"\")");
			String name = modelName.substring(0, modelName.lastIndexOf("."));
			String fcName = "fc_"+name;
			c.eval(fcName+" <- forecast("+name+",h=24)");
			c.eval("save("+fcName+", file=\""+fcPath+"/"+fcName+".RData\")");
			System.out.println("Forecast for model "+name+" finished");
		}
	    closeRConnection();
	    return "forecasts generated for "+modelNames.length+" models";
	}
	
	/**
	 * Generate forecasts based on previously saved models, filtered by given parameters
	 * @param priceType the type of energy price models to retrieve
	 * @param locationIds the locationIds for which to generate forecasts
	 * 					if -1 then forecasts are generated for all models
	 * 						satisfying the other parameters
	 * @param trainingsPeriod the trainingsperiod by which to filter the model list (given in number of days)
	 * @param startDateString a string denoting the start date of the model list
	 * @param endDateString a string denoting the end date of the model list
	 * @return a String indicating the result of the calculation
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws REngineException is thrown when something has gone wrong on the R connection
	 */
	public String generateForecasts(String priceType, String locationIds, Integer trainingsPeriod, 
					String startDateString, String endDateString) throws REXPMismatchException, REngineException {
		initRConnection();
	    
	    c.eval("library(\"forecast\")");
	    
	    boolean fullNames = true;
	    
	    String[] modelNames = getModelList(priceType, locationIds, trainingsPeriod, startDateString, endDateString, 
	    												fullNames, false);
	    
	    System.out.println("modelnames length = "+modelNames.length);
	    
	    String fcBasePath = "forecast_"+priceType;
	    String path = "";
	    String name = "";
	    
	    for(String modelName : modelNames) {
			c.eval("load(\""+modelName+"\")");
			if(fullNames) {
				String[] dirs = modelName.split("/");
				path = fcBasePath + "/" + "fc_" +dirs[1];
				name = modelName.substring(modelName.lastIndexOf("/")+1, modelName.lastIndexOf("."));
			}
			else {
				name = modelName.substring(0, modelName.lastIndexOf("."));
				path = fcBasePath;
			}
			
			String fcName = "fc_"+name;
			c.eval("dir.create(\""+path+"\", showWarnings = FALSE)");
			c.eval(fcName+" <- forecast("+name+"[[1]],h=24)"); // getting the first list element as the 
																// actual model, the second is the period
																// (frequency) of the time series
			c.eval("save("+fcName+", file=\""+path+"/"+fcName+".RData\")");
		}
	    closeRConnection();
	    return "forecasts generated for "+modelNames.length+" models";
	}
	
	/**
	 * Method to retrieve a list of models based on given parameters, restrict by location, date range and trainings period
	 * @param priceType the type of energy price models to retrieve
	 * @param locationIds the locationIds for which to retrieve the saved models
	 * @param trainingsPeriod the trainingsperiod by which to filter the model list (given in number of days)
	 * @param startDateString a string denoting the minimum start date of the resulting model list
	 * @param endDateString a string denoting the maximum end date of the resulting model list
	 * @param fullNames boolean value to indicate whether to return the fully qualified file names (parent directories)
	 * 			relative to the current working directory (getwd())
	 * @param forecast boolean value to indicate whether to retrieve forecast calculations
	 * 			or actual models
	 * @return a list of strings containing all model names filtered by the given parameters
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws REngineException is thrown when something has gone wrong on the R connection
	 */
	private String[] getModelList(String priceType, String locationIds, Integer trainingsPeriod, 
									String startDateString, String endDateString, boolean fullNames, boolean forecast) throws REXPMismatchException, REngineException {
		
		Date startDate = DateParser.parseDate(startDateString);
	    Date endDate = DateParser.parseDate(endDateString);
	    
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	    String startStr = sdf.format(startDate);
	    String endStr = sdf.format(endDate);

	    String modelPath = "models_"+priceType;
	    if(forecast) {
	    	modelPath = "forecast_"+priceType;
	    }

	    String pattern = ".*_model_";
	    String patternLocIds = "";
	    if(locationIds.equals("-1")) {
	    	patternLocIds = ".*";
	    }
	    else {
	    	String[] locIds = locationIds.split(",");
	    	patternLocIds = "(";
	    	
	    	for(int i = 0; i < locIds.length; i++) {
	    		if(i > 0) {
	    			patternLocIds += "|";
	    		}
	    		patternLocIds += locIds[i];
	    	}
	    	patternLocIds += ")";
	    }
	    pattern += patternLocIds + "_" + trainingsPeriod+"d_.*";
	    
	    String[] names = c.eval("list.files(   path=\""+modelPath+"\", "
	    									+ "pattern=\""+pattern+"\", "
	    									+ "recursive=TRUE, "
	    									+ "full.names="+String.valueOf(fullNames).toUpperCase()
	    									+ ")").asStrings();
	    Arrays.sort(names);
	    System.out.println("names len = "+names.length);
	    
	    boolean add = false;
	    List<String> result = new ArrayList<String>();
	    for (int i = 0; i < names.length; i++) {
	    	String dateStr = names[i].substring(names[i].lastIndexOf("_")+1, names[i].lastIndexOf("."));
	    	if(dateStr.equals(startStr)) {
	    		System.out.println("add models beginning from date "+dateStr);
	    		add = true;
	    	}
	    	if(add) {
	    		result.add(names[i]);
	    	}
	    	if(dateStr.equals(endStr)) {
	    		System.out.println("add models up to date "+dateStr);
	    		add = false;
	    	}
	    }
	    
	    // Save the modelnames in a String array
	    String[] modelNames = result.toArray(new String[0]);
		
		return modelNames;
	}
	
	
	/**
	 * Get simulation results for a simulation, optionally aggregated by forecast error measure
	 * Saves the results in an R data frame
	 * @param simulationName the name of the simulation to get results for
	 * @param aggregated boolean value to indicate whether results should be aggregated (by mean calculation
	 * 			of forecast error measures)
	 * @return a String indicating the status of the results
	 * @throws REXPMismatchException
	 * @throws REngineException
	 */
	public String getSimulationResults(String simulationName, boolean aggregated) throws REXPMismatchException, REngineException {
		
		initRConnection();
		
		String[] names = c.eval("list.files(path=\"simulation/"+simulationName+"\")").asStrings();
		
		if(names.length == 0) {
			closeRConnection();
			return "getSimulationResults: No simulation with simulationName "+simulationName+" exists";
		}
		
	    Arrays.sort(names);
	    System.out.println("list of simulation files, size = "+names.length);
	    
	    String run = names[0];
    	String rName = run.substring(0, run.lastIndexOf("."));
    	
//    	RList l = c.eval("load(\"simulation/"+simulationName+"/"+rName+"\")").asList();
//    	int numberModels = l.at(0).asList().size();
    	int numberModels = 6;
    	
    	System.out.println("number of models = "+numberModels);
    	
    	// load all simulation runs into memory
    	for(int i = 0; i < names.length; i++) {
	    	String r = names[i];
	    	c.eval("load(\"simulation/"+simulationName+"/"+r+"\")");
    	}
    	
    	String[] accMeasures = new String[] { "ME", "RMSE", "MAE", "MPE", "MAPE" };
	    String[] fcHorizons = new String[] { "1","3","6","12","18","24","36","48","96","168" };
	    String trainingSet = "Training set";
	    String testSet = "Test set";
	    String trainPrefix = "train_";
	    String testPrefix = "test_";
    	
    	c.eval("accModelList <- list()");
    	
    	for(int model = 1; model <= numberModels; model++) {
    		
    		System.out.println("Process model "+model);
    		
    		c.eval("fcHorizonList <- list()");
    		
    		for(int h = 1; h <= fcHorizons.length; h++) {
    			
    			System.out.println("Get forecast horizon "+h);
    			
    			// create error measure variables
    		    c.eval("train_ME <- numeric()");
    		    c.eval("train_RMSE <- numeric()");
    		    c.eval("train_MAE <- numeric()");
    		    c.eval("train_MPE <- numeric()");
    		    c.eval("train_MAPE <- numeric()");
    		    
    		    c.eval("test_ME <- numeric()");
    		    c.eval("test_RMSE <- numeric()");
    		    c.eval("test_MAE <- numeric()");
    		    c.eval("test_MPE <- numeric()");
    		    c.eval("test_MAPE <- numeric()");
    		    
    		    
    		    for(int i = 0; i < names.length; i++) {
    		    	String r = names[i];
    		    	String name = r.substring(0, r.lastIndexOf("."));
    		    	c.eval("acc <- "+name+"[[2]][["+model+"]][["+h+"]]");
//    		    	int numAccMeasures = c.eval("length(acc)/2").asInteger();
    		    	int numAccMeasures = 5;
    		    	
			    	String vectorTrain, vectorTest;
			    	int index = i+1;
			    	
			    	
			    	// get accuracy measures
			    	for(int acc = 0; acc < numAccMeasures; acc++) {
			    		
			    		String accMeasure = accMeasures[acc];
			    		vectorTrain = trainPrefix + accMeasure;
				    	c.eval("value <- acc[\""+trainingSet+"\",\""+accMeasure+"\"]");
				    	c.eval(vectorTrain + "["+index+"] <- value");
				    	
				    	vectorTest = testPrefix + accMeasure;	
				    	c.eval("value <- acc[\""+testSet+"\",\""+accMeasure+"\"]");
				    	c.eval(vectorTest + "["+index+"] <- value");
				    	
			    	}
			    	
    		    }
    			
    		    // create data frame
    		    StringBuilder trainingAccuracyList = new StringBuilder();
    		    StringBuilder testAccuracyList = new StringBuilder();
    		    // create list of error measure variables (see above)
    		    for(String accMeasure : accMeasures) {
    		    	trainingAccuracyList.append(trainPrefix);
    		    	trainingAccuracyList.append(accMeasure);
    		    	trainingAccuracyList.append(",");
    		    	
    		    	testAccuracyList.append(testPrefix);
    		    	testAccuracyList.append(accMeasure);
    		    	testAccuracyList.append(",");
    		    }
    		    
    		    String trainingAccVectors = trainingAccuracyList.toString();
    		    String testAccVectors = testAccuracyList.toString();
    		    testAccVectors = testAccVectors.substring(0, testAccVectors.length()-1);
    		    
    		    String createDataFrame = "resultDF <- data.frame(" + trainingAccVectors + testAccVectors + ")";
    		    c.eval(createDataFrame);
    		    c.eval("resultDF[mapply(is.infinite, resultDF)] <- NA");
    		    
    		    if(aggregated) {
        			c.eval("fcHorizonList[["+h+"]] <- colMeans(resultDF, na.rm = TRUE)");
        		}
        		else {
        			c.eval("fcHorizonList[["+h+"]] <- resultDF");
        		}
    			
    		}
    		
    		c.eval("accModelList[["+model+"]] <- fcHorizonList");
    		
    	}
    	
    	c.eval(simulationName + " <- list()");
    	c.eval(simulationName + "[[1]] <- accModelList");

    	if(aggregated) {
    		c.eval("save("+simulationName+", file=\"simulationResults/aggregated/"+simulationName+".RData\")");
    	}
    	else {
    		c.eval("save("+simulationName+", file=\"simulationResults/"+simulationName+".RData\")");
    	}
    	
    	closeRConnection();
    	
    	return "Retrieved simulation results for simulation "+simulationName;
	}
	    
	
	/**
	 * Method to generate an R ARIMA model based on the given data
	 * @param modelName the name under which this model should be saved (without extension)
	 * @param modelPath the path where the models in this run should be stored (set to default when empty)
	 * @param priceType the type of energy prices to evaluate ("da" or "rt")
	 * @param csvData csv data as a single string
	 * @param targetPeriod a specific target period to search for (e.g. 24 to mark a period every 24 hours)
	 * @param topPeriods the number of periods to investigate in case of missing targetPeriod
	 * @param maxLimit if not null each generated period exceeding this limit will be set to NA
	 * @param weightAicc weight for the AICc utility value
	 * @param weightLjung weight for the Ljung box test p utility value
	 * @param approximation boolean value indicating whether the model should be approximated (true=faster)
	 * @param stepwise a boolean value indicating whether a stepwise calculation should be performed (true=faster)
	 * @param enforceTarget a boolean value indicating whether the given target period should be set as period
	 * 				even though it might not be found in the data
	 * @param output a boolean value indicating whether output to the console should be written
	 * @param plot a boolean value indicating whether the R results should be plotted
	 * @return a String indicating the result of the calculation
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws REngineException is thrown when something has gone wrong on the R connection
	 */
	public String generateArimaModel(String priceType, String modelName, String modelPath, String csvData, int targetPeriod, 
									int topPeriods,	Integer maxLimit, double weightAicc, double weightLjung,
									boolean approximation, boolean stepwise, boolean enforceTarget, boolean output, boolean plot) throws RserveException, REXPMismatchException {
		initRConnection();
		
	    c.eval("loadLibraries()");
	   
	    c.assign("csvInput", csvData);
	    c.eval("con <- textConnection(csvInput)");
		c.eval("pricesDF <- read.csv(con, header = TRUE, strip.white = TRUE, blank.lines.skip = TRUE, " +
                "quote = \"\\\"\", dec = \".\", sep = \",\", comment.char = \"\", "+
                "col.names=c(\"times\",\"prices\"))");
	    c.eval("close(con)");
	    c.eval("pricesTraining <- pricesDF$prices");
	    
	    c.eval(modelName+" <- generateARIMAModel(pricesTraining, targetPeriod="+targetPeriod+", "
					    		+ "numTopPeriods="+topPeriods+", "
					    		+ "maxLimit="+maxLimit+", "
					    		+ "wAicc="+weightAicc+", "
					    		+ "wLjung="+weightLjung+", "
					    		+ "approximation="+String.valueOf(approximation).toUpperCase()+", "
					    		+ "stepwise="+String.valueOf(stepwise).toUpperCase()+", "
					    		+ "enforceTarget="+String.valueOf(enforceTarget).toUpperCase()+", "
					    		+ "output="+String.valueOf(output).toUpperCase()+", "
					    		+ "plot="+String.valueOf(plot).toUpperCase()+")");
	    
	    // set to default
	    if(modelPath.isEmpty()) {
	    	modelPath = "models_"+priceType;
	    }
	    
	    c.eval("dir.create(\""+modelPath+"\", showWarnings = FALSE)");
	    c.eval("save("+modelName+", file=\""+modelPath+"/"+modelName+".RData\")");
	    
	    closeRConnection();
	    
	    return "Model saved";
	}
	
	/**
	 * Method to generate an R ARIMA model with default values (for faster and yet accurate model generation)
	 * @param modelName the name under which this model should be saved (without extension)
	 * @param modelPath the path where the models in this run should be stored (set to default when empty)
	 * @param priceType the type of energy prices to evaluate ("da" or "rt")
	 * @param csvData csv data as a single string
	 * @param enforceTarget a boolean value indicating whether the given target period should be set as period
	 * 				even though it might not be found in the data
	 * @param debugOutput a boolean value indicating whether additional outputs during model generation should
	 * 				be printed (R console)
	 * @return a String indicating the result of the calculation
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws RserveException is thrown when something has gone wrong on the R connection
	 */
	public String generateArimaModel(String modelName, String modelPath, String priceType, String csvData, boolean enforceTarget, boolean debugOutput) throws RserveException, REXPMismatchException {
		return generateArimaModel(modelName, modelPath, priceType, csvData, 24, 4, 168, 0.7, 0.3, true, true, enforceTarget, debugOutput, false);
	}
	
	
	/**
	 * Evaluate all models for a specific training and test period and save the result under the 
	 * given simulationName (in "simulation" folder)
	 * @param simulationName the name under which to save the simulation
	 * @param csvTraining csv training data to train models
	 * @param csvTest csv test data for model evaluation on test data
	 * @param debugOutput a boolean value indicating whether additional outputs during model evaluation 
	 * 				should be printed (R console)
	 * @return a String indicating the status of the simulation
	 * @throws RserveException is thrown when something has gone wrong on the R connection
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 */
	@TransactionTimeout(1500)
	public String evaluateModels(String simulationName, String csvTraining, String csvTest, boolean debugOutput) throws RserveException, REXPMismatchException {
		boolean close = false;
		if(!init) {
			initRConnection();
			c.eval("loadLibraries()");
			close = true;
		}
		
		c.eval("print(\"------------------------------------\")");
		c.eval("print(\"Evaluate models for simulation instance: "+simulationName+"\")");
		
	    c.assign("csvTraining", csvTraining);
	    c.eval("pricesTraining <- getCSV(csvTraining, header=TRUE)");
	    
	    c.assign("csvTest", csvTest);
	    c.eval("pricesTest <- getCSV(csvTest, header=TRUE)");
	    
	    c.eval(simulationName+" <- evaluateModels(pricesTraining, pricesTest, output=TRUE, extendedOutput="+String.valueOf(debugOutput).toUpperCase()+")");
	    
	    String folder = simulationName.substring(0, simulationName.lastIndexOf("_"));
	    c.eval("save("+simulationName+", file=\"simulation/"+folder+"/"+simulationName+".RData\")");
	    
	    if(close) {
	    	closeRConnection();
	    }
	    return "Simulation model saved";
	}
	
	/**
	 * Load the given list of model names in the current R session
	 * @param modelNames the list of models to load in R
	 * @return a String indicating the result of the operation
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws REngineException is thrown when something has gone wrong on the R execution
	 */
	public String loadModels(String[] modelNames) throws RserveException, REXPMismatchException {
		initRConnection();
	    
		for(String modelName : modelNames) {
			REXP name = c.eval("load(\"models/"+modelName+".RData\")");
		}
		
		closeRConnection();
	    
	    return "loaded models: "+modelNames.length;
	}
	
	/**
	 * Load a single model based on the given model name in the current R session
	 * @param modelName the model to load in R
	 * @return a String indicating the result of the operation
	 * @throws REXPMismatchException is thrown when a datatype mismatch occurred
	 * @throws REngineException is thrown when something has gone wrong on the R connection
	 */
	public String loadModel(String modelName) throws RserveException, REXPMismatchException  {
		initRConnection();
	    
		REXP name = c.eval("load(\"models/"+modelName+".RData\")");
	    
	    closeRConnection();
	    
	    return "loaded model "+name.asString();
	}
	
}
