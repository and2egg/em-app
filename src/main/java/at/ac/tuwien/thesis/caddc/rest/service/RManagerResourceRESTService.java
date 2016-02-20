package at.ac.tuwien.thesis.caddc.rest.service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.model.type.TimeSeriesType;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.type.LocationType;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.service.RManager;
import at.ac.tuwien.thesis.caddc.util.DateParser;
import at.ac.tuwien.thesis.caddc.util.DateUtils;

/**
 * R Manager REST Service
 * <p/>
 * REST Service specifications for interactions with the R interface
 * @author Andreas Egger
 */
@Path("/r")
@RequestScoped
public class RManagerResourceRESTService {

    @Inject
    private Logger log;

    @Inject
    private LocationRepository locationRepository;
    
    @Inject
    private LocationResourceRESTService locationService;

    @Inject
    private DAPricesResourceRESTService daPriceService;
    
    @Inject
    private RTPricesResourceRESTService rtPriceService;
    
    @Inject
    private RManager rManager;


    /**
     * A simple R test to show that Rserve is up and running
     * @return a Response containing the result of the test queries
     */
    @GET
    @Path("/rtest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testR() {
    	String result = "";
    	try {
			result = rManager.testR();
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to generate ARIMA models for multiple locations, with the given training period,
     * and for all dates between the given start and end date strings
     * example: http://localhost:8081/em-app/rest/r/generatemodelsbatch/da/1,2,4/14/2014-07-07/2014-07-10?debugOutput=true
     * @param priceType the type of energy prices to evaluate ("da" or "rt")
     * @param locationIds the location ids of the locations for which to generate the models
     * 						if -1 then all locations of the given priceType are queried
     * @param trainingsPeriod the training period for the model in days, e.g. 14 days for 2 weeks
     * @param startDateString the starting date for which to generate models (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDateString the end date for which to generate models (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @param enforceTarget a boolean value to indicate whether setting the target period for price time series
     * 			should be enforced (target period is 24 by default)
     * @param debugOutput boolean value to indicate whether debug outputs should be printed
     * @return a Response indicating the status (success/failure) of the calculation 
     */
    @GET
    @Path("/generatemodelsbatch/{type}/{loc_ids}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateModelsBatch(@PathParam("type") String priceType, @PathParam("loc_ids") String locationIds, 
    						@PathParam("training_period") Integer trainingsPeriod, 
    						@PathParam("startDate") String startDateString, @PathParam("endDate") String endDateString, 
    						@DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice,
    						@DefaultValue("false") @QueryParam("enforceTarget") Boolean enforceTarget,
    						@DefaultValue("false") @QueryParam("debugOutput") Boolean debugOutput) {
    	
		if(locationIds.equals("-1")) {
			if(priceType.equals(EnergyPriceType.DA_TYPE)) {
				locationIds = "1,2,3,4,5";
			}
			else if(priceType.equals(EnergyPriceType.RT_TYPE)) {
				locationIds = "4,5,6,7,8,9,10";
			}
		}
    	
    	String[] locSplit = locationIds.split(",");
    	
    	for(String locationId : locSplit) {
    		
    		if(!(priceType.equals(EnergyPriceType.DA_TYPE) && LocationType.isDayAheadLocation(Long.valueOf(locationId))) &&
	    		!(priceType.equals(EnergyPriceType.RT_TYPE) && LocationType.isRealTimeLocation(Long.valueOf(locationId)))) {
	    		System.err.println("generateModelsBatch: priceType ("+priceType+") and locationId ("+locationId+") do not match");
	    		continue;
	    	}
    	    
    	    Date startDate = DateParser.parseDate(startDateString);
    	    Date endDate = DateParser.parseDate(endDateString);
    	    String sDate = DateUtils.formatDate(startDate, DateUtils.DATE_FORMAT_COMPACT);
    	    String eDate = DateUtils.formatDate(endDate, DateUtils.DATE_FORMAT_COMPACT);
    	    String modelBasePath = "models_"+priceType;
    	    String modelSubDir = priceType + "_model_"+trainingsPeriod+"d_"+sDate+"_"+eDate;
    	    String modelPath = modelBasePath + "/" + modelSubDir;
    		
    		generateModels(priceType, Long.valueOf(locationId), trainingsPeriod, startDateString, endDateString, 
    					modelPath, transformPrice, enforceTarget, debugOutput);
    		
    	}
    	
    	String output = "Successfully generated models for "+priceType+" locations "+locationIds+" for dates "+startDateString+" to "
    			+ endDateString +" with trainings period of "+trainingsPeriod+" days";
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to generate ARIMA models for the given location, with the given training period,
     * and for all dates between the given start and end date strings
     * example: http://localhost:8081/em-app/rest/r/generatemodels/da/1/14/2014-07-07/2014-07-10?debugOutput=true
     * @param priceType the type of energy prices to evaluate ("da" or "rt")
     * @param locationId the location id of the location for which to generate the models
     * @param trainingsPeriod the training period for the model in days, e.g. 14 days for 2 weeks
     * @param startDateString the starting date for which to generate models (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDateString the end date for which to generate models (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param modelPath the path where the models in this run should be stored (set to default if not set)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @param enforceTarget a boolean value to indicate whether setting the target period for price time series
     * 			should be enforced (target period is 24 by default)
     * @param debugOutput boolean value to indicate whether debug outputs should be printed
     * @return a Response indicating the status (success/failure) of the calculation 
     */
    @GET
    @Path("/generatemodels/{type}/{loc_id}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateModels(@PathParam("type") String priceType, @PathParam("loc_id") Long locationId, 
    						@PathParam("training_period") Integer trainingsPeriod, 
    						@PathParam("startDate") String startDateString, @PathParam("endDate") String endDateString, 
    						@DefaultValue("") @QueryParam("modelPath") String modelPath,
    						@DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice,
    						@DefaultValue("false") @QueryParam("enforceTarget") Boolean enforceTarget,
    						@DefaultValue("false") @QueryParam("debugOutput") Boolean debugOutput) {
    	
    	if(!(priceType.equals(EnergyPriceType.DA_TYPE) && LocationType.isDayAheadLocation(locationId)) &&
    		!(priceType.equals(EnergyPriceType.RT_TYPE) && LocationType.isRealTimeLocation(locationId))) {
    		return Response.status(Status.BAD_REQUEST).entity("generateModel: priceType ("+priceType+") and locationId ("+locationId+") do not match").build();
    	}
    	
    	Date start = DateParser.parseDate(startDateString);
    	Date end = DateParser.parseDate(endDateString);
    	
    	Calendar eLocal = Calendar.getInstance();
		Calendar sLocal = Calendar.getInstance();
		
		sLocal.setTime(start);
		eLocal.setTime(end);

    	TimeZone tz = TimeZone.getTimeZone("UTC");
		
		DateFormat formatter = new SimpleDateFormat(DateUtils.DATE_FORMAT);
    	formatter.setTimeZone(tz);
    	
    	Calendar calStart = Calendar.getInstance(tz);
    	Calendar calEnd = Calendar.getInstance(tz);
    	calStart.set(sLocal.get(Calendar.YEAR), sLocal.get(Calendar.MONTH), sLocal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    	calStart.set(Calendar.MILLISECOND, 0);
    	calEnd.set(eLocal.get(Calendar.YEAR), eLocal.get(Calendar.MONTH), eLocal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    	calEnd.set(Calendar.MILLISECOND, 0);
    	
    	int models = 0;
    	while(calStart.getTimeInMillis() <= calEnd.getTimeInMillis()) {
    		Calendar e = (Calendar) calStart.clone();
    		Calendar s = (Calendar) calStart.clone();
    		s.add(Calendar.DATE, -trainingsPeriod); // go back a defined number of days
    												// for model trainingsdata
    		
    		String dStart = formatter.format(s.getTime());
    		String dEnd = formatter.format(e.getTime());
    		
    		System.out.println("startdate "+dStart);
    		System.out.println("enddate "+dEnd);
    		
    		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    		String modelDate = sdf2.format(e.getTime());
    		
    		String modelPrefix = "";
        	if(priceType.equals(EnergyPriceType.DA_TYPE) && LocationType.isDayAheadLocation(locationId)) {
        		modelPrefix = "da_model";
        	}
        	else if(priceType.equals(EnergyPriceType.RT_TYPE) && LocationType.isRealTimeLocation(locationId)) {
        		modelPrefix = "rt_model";
        	}
        	else {
        		return Response.status(Status.BAD_REQUEST).entity("generateModels: priceType ("+priceType+") and locationId ("+locationId+") do not match").build();
        	}
    		// Each modelname is created by giving the type (da), the location id, 
    		// the training period in days and the date at the end of the trainingsperiod
    		String modelName = modelPrefix + "_" + locationId.intValue() + "_" + trainingsPeriod + "d_" + modelDate;
    		System.out.println("modelName "+modelName);
    		
    		generateModel(priceType, modelName, locationId, dStart, dEnd, modelPath, transformPrice, enforceTarget, debugOutput);
    		calStart.add(Calendar.DATE, 1);
    		models++;
    	}
    	String output = "Successfully generated "+models+" "+priceType+" models for dates "+startDateString+" to "
    			+ endDateString +" with trainings period of "+trainingsPeriod+" days";
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Generate an ARIMA model given a modelName, locationId, and start and enddates for the trainingsdata
     * example: http://localhost:8081/em-app/rest/r/generatemodel/da/da_model_1_14d_20140720/1/2014-07-07/2014-07-20
     * @param priceType the type of energy prices to evaluate ("da" or "rt")
     * @param modelName the name of the model which should be unique and recognizable
     * @param modelPath the path where the models in this run should be stored
     * @param locationId the id of the location for which to generate the model
     * @param startTraining the startdate of the trainingsdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endTraining the enddate of the trainingsdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param modelPath the path where the models in this run should be stored (set to default if not set)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @param enforceTarget a boolean value to indicate whether setting the target period for price time series
     * 			should be enforced (target period is 24 by default)
     * @param debugOutput boolean value to indicate whether debug outputs should be printed
     * @return Response to indicate whether or not the model generation was successful
     */
    @GET
    @Path("/generatemodel/{type}/{modelName}/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateModel(@PathParam("type") String priceType, @PathParam("modelName") String modelName, 
    								@PathParam("loc_id") Long locationId, 
    								@PathParam("startDate") String startTraining, @PathParam("endDate") String endTraining,
    								@DefaultValue("") @QueryParam("modelPath") String modelPath,
    								@DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice,
    								@DefaultValue("false") @QueryParam("enforceTarget") Boolean enforceTarget,
    								@DefaultValue("false") @QueryParam("debugOutput") Boolean debugOutput) {
    	String result;
    	Response priceResponse;
    	if(priceType.equals(EnergyPriceType.DA_TYPE) && LocationType.isDayAheadLocation(locationId)) {
    		priceResponse = daPriceService.retrieveDAPricesCSV(String.valueOf(locationId), startTraining, endTraining, transformPrice);
    	}
    	else if(priceType.equals(EnergyPriceType.RT_TYPE) && LocationType.isRealTimeLocation(locationId)) {
    		priceResponse = rtPriceService.retrieveRTPricesCSV(String.valueOf(locationId), startTraining, endTraining, transformPrice);
    	}
    	else {
    		return Response.status(Status.BAD_REQUEST).entity("generateModel: priceType ("+priceType+") and locationId ("+locationId+") do not match").build();
    	}
    	String csvData = priceResponse.getEntity().toString();
    	
    	try {
    		result = rManager.generateArimaModel(priceType, modelName, modelPath, csvData, enforceTarget, debugOutput);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Get simulation results for a previously run simulation
     * example: http://localhost:8081/em-app/rest/r/simulationresults/da_sim_1_2w_1w_1w?aggregated=false
     * @param simulationName the name of the simulation run which should be unique and recognizable
     * 			it is generic and should consist of the priceType, locationId, trainingsPeriod, testPeriod and intervalPeriod
     * 			example: da_sim_1_2w_1w_1w (see method runSimulation)
     * @param aggregated a boolean value to indicate whether the data should be returned in 
 * 					aggregated form (by calculating the mean)
     * @return Response to indicate whether or not the model generation was successful
     */
    @GET
    @Path("/simulationresults/{simulationName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSimulationResults(@PathParam("simulationName") String simulationName,
    						@DefaultValue("true") @QueryParam("aggregated") Boolean aggregated) {
    	
    	String result;

    	try {
    		result = rManager.getSimulationResults(simulationName, aggregated);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Get simulation results for a previously run simulation
     * example: http://localhost:8081/em-app/rest/r/runsimulations/2014-07-07 00:00/2014-08-08/da_sim_1_2w_1w_1w,rt_sim_6_4w_1w_1w,rt_sim_4_3w_1w_2w
     * @param simulationStart the common start date of the simulations (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param simulationEnd the common end date of the simulations (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param simulationNames a comma separated list of simulation names with the predefined format
     * 			<priceType>_sim_<locationId>_<trainingsPeriod>_<testPeriod>_<intervalPeriod>
     * 			example: da_sim_1_2w_1w_1w (see also method runSimulation)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @param debugOutput boolean value to indicate whether debug outputs should be printed
     * @return Response to indicate whether or not the model generation was successful
     */
    @GET
    @Path("/runsimulations/{simulationStart}/{simulationEnd}/{simulationNames}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runSimulations(@PathParam("simulationStart") String simulationStart, @PathParam("simulationEnd") String simulationEnd,
    					@PathParam("simulationNames") String simulationNames, 
						@DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice,
						@DefaultValue("false") @QueryParam("debugOutput") Boolean debugOutput) {
    	
    	String[] splitNames = simulationNames.split(",");
    	
    	System.out.println("Run simulations for simulation names "+simulationNames);
    	
    	for(String simulationName : splitNames) {
    		
    		String[] s = simulationName.split("_");
    		
    		if(s.length != 6) {
    			System.out.println("runSimulations: ParserError: simulationName "+simulationName+" is invalid");
    			continue;
    		}
    		
    		String priceType = s[0];
    		Long locationId = Long.valueOf(s[2]);
    		String trainingsPeriod = s[3];
    		String testPeriod = s[4];
    		String intervalPeriod = s[5];
    		
    		runSimulation(priceType, locationId, simulationStart, simulationEnd, 
    				trainingsPeriod, testPeriod, intervalPeriod, transformPrice, debugOutput);
    		
    	}
    	
    	String output = "Successfully completed simulations: "+ System.lineSeparator();
    	for(String simName: splitNames) {
    		output += simName + System.lineSeparator();
    	}
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Run a simulation calculating accuracy measures for models over a defined time range
     * example: http://localhost:8081/em-app/rest/r/simulation/da/1/2014-07-07 00:00/2014-08-08/2w/1w/1w
     * @param priceType the type of energy prices to evaluate ("da" or "rt")
     * @param locationId the id of the location for which to evaluate models
     * @param simulationStart the start date of the simulation (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param simulationEnd the end date of the simulation (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param trainingsPeriod the trainingsperiod for model evaluation (e.g. "14d", "2w")
     * @param testPeriod the testperiod for model evaluation (e.g. "4d", "1w")
     * @param intervalPeriod the interval between simulation time stamps (e.g. "1d", "1w")
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @param debugOutput boolean value to indicate whether debug outputs should be printed
     * @return Response to indicate whether the simulation could be completed successfully
     */
    @GET
    @Path("/simulation/{type}/{loc_id}/{simulationStart}/{simulationEnd}/{trainingsPeriod}/{testPeriod}/{intervalPeriod}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runSimulation(@PathParam("type") String priceType, @PathParam("loc_id") Long locationId, 
    								@PathParam("simulationStart") String simulationStart, @PathParam("simulationEnd") String simulationEnd,
    								@PathParam("trainingsPeriod") String trainingsPeriod, @PathParam("testPeriod") String testPeriod, 
    								@PathParam("intervalPeriod") String intervalPeriod, 
    								@DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice,
    								@DefaultValue("false") @QueryParam("debugOutput") Boolean debugOutput) {
    	
    	TimeSeriesType typeTraining = TimeSeriesType.convertTimePeriod(trainingsPeriod);
    	TimeSeriesType typeTest= TimeSeriesType.convertTimePeriod(testPeriod);
    	TimeSeriesType typeInterval= TimeSeriesType.convertTimePeriod(intervalPeriod);
    	
    	TimeZone tz = TimeZone.getTimeZone("UTC");
		
		DateFormat formatter = new SimpleDateFormat(DateUtils.DATE_FORMAT_NO_SECONDS);
		DateFormat compactFormatter = new SimpleDateFormat(DateUtils.DATE_FORMAT_COMPACT);
    	formatter.setTimeZone(tz);
		
		Calendar cal = Calendar.getInstance(tz);
		Calendar calIterator = Calendar.getInstance(tz);
		Calendar calSimulationEnd = Calendar.getInstance(tz);
    	Date s = DateParser.parseDate(simulationStart);
    	Date e = DateParser.parseDate(simulationEnd);
    	Calendar start = Calendar.getInstance();
    	Calendar end = Calendar.getInstance();
    	start.setTime(s);
    	end.setTime(e);
    	
		cal.set(start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH), 
				start.get(Calendar.HOUR_OF_DAY), start.get(Calendar.MINUTE), start.get(Calendar.SECOND));
    	cal.set(Calendar.MILLISECOND, 0);
    	
    	calSimulationEnd.set(end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH), 
				end.get(Calendar.HOUR_OF_DAY), end.get(Calendar.MINUTE), end.get(Calendar.SECOND));
    	calSimulationEnd.set(Calendar.MILLISECOND, 0);
    	
    	calIterator = (Calendar) cal.clone();
    	calIterator.add(typeTraining.getTimeUnit(), typeTraining.getTimeInterval());
    	calIterator.add(typeTest.getTimeUnit(), typeTest.getTimeInterval());
    	calIterator.add(Calendar.HOUR_OF_DAY, -1);
    	
    	String trainingStart, trainingEnd, testStart, testEnd;
    	
    	String simulationName = priceType+"_sim_"+locationId+"_"+trainingsPeriod+"_"+testPeriod+"_"+intervalPeriod;
    	createFolder("simulation", simulationName);
    	
    	int iterationCount = 0;
    	
    	while ( calIterator.before(calSimulationEnd) ) {
    		
    		trainingStart = formatter.format(cal.getTime());
    		String tStart = compactFormatter.format(cal.getTime());
    		
    		Calendar sTraining = (Calendar) cal.clone();
        	
        	cal.add(typeTraining.getTimeUnit(), typeTraining.getTimeInterval());
        	testStart = formatter.format(cal.getTime());
        	
        	cal.add(Calendar.HOUR_OF_DAY, -1);
        	trainingEnd = formatter.format(cal.getTime());
        	
        	cal.add(typeTest.getTimeUnit(), typeTest.getTimeInterval());
        	testEnd = formatter.format(cal.getTime());
        	
        	String instanceName = simulationName+"_"+tStart;
        	
        	System.out.println("Starting simulation instance "+instanceName);
        	
        	evaluateModels(instanceName, priceType, locationId, 
        					trainingStart, trainingEnd, testStart, testEnd, transformPrice, debugOutput);
        	
        	cal = sTraining;
        	cal.add(typeInterval.getTimeUnit(), typeInterval.getTimeInterval());
        	calIterator.add(typeInterval.getTimeUnit(), typeInterval.getTimeInterval());
        	
        	iterationCount++;
    	}
	    
    	String output = "Simulation finished successfully for location id "+locationId+", simulation start: "+simulationStart
    				+", simulation end: "+simulationEnd+", trainings period: "+trainingsPeriod+", test period: "+testPeriod
    				+", interval period: "+intervalPeriod+", total iteration count: "+iterationCount;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Evaluate forecasting model accuracy for the given training and test time ranges
     * example: http://localhost:8081/em-app/rest/r/evaluatemodels/da_sim_1_2w_1w_1w/da/1/2014-07-07/2014-07-20%2023:00/2014-07-21/2014-07-27%2023:00
     * @param simulationName the name of the simulation run which should be unique and recognizable
     * 			it is generic and should consist of the priceType, locationId, trainingsPeriod, testPeriod and intervalPeriod
     * 			example: da_sim_1_2w_1w_1w (see method runSimulation)
     * @param priceType the type of energy prices to evaluate ("da" or "rt")
     * @param locationId the id of the location for which to evaluate models
     * @param startTraining the startdate of the trainingsdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param endTraining the enddate of the trainingsdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param startTest the startdate of the testdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param endTest the enddate of the testdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @param debugOutput boolean value to indicate whether debug outputs should be printed
     * @return Response to indicate whether or not the model generation was successful
     */
    @GET
    @Path("/evaluatemodels/{simulationName}/{type}/{loc_id}/{startTraining}/{endTraining}/{startTest}/{endTest}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response evaluateModels(@PathParam("simulationName") String simulationName, 
    								@PathParam("type") String priceType, @PathParam("loc_id") Long locationId, 
    								@PathParam("startTraining") String startTraining, @PathParam("endTraining") String endTraining,
    								@PathParam("startTest") String startTest, @PathParam("endTest") String endTest, 
    								@DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice,
    								@DefaultValue("false") @QueryParam("debugOutput") Boolean debugOutput) {
    	
    	String result;
    	Response pricesTraining = null;
    	Response pricesTest = null;
    	
    	if(priceType.equals(EnergyPriceType.DA_TYPE) && LocationType.isDayAheadLocation(locationId)) {
    		pricesTraining = daPriceService.retrieveDAPricesCSVLocalTZ(String.valueOf(locationId), startTraining, endTraining, transformPrice);
        	pricesTest = daPriceService.retrieveDAPricesCSVLocalTZ(String.valueOf(locationId), startTest, endTest, transformPrice);
    	}
    	else if(priceType.equals(EnergyPriceType.RT_TYPE) && LocationType.isRealTimeLocation(locationId)) {
    		pricesTraining = rtPriceService.retrieveRTPricesCSVLocalTZ(String.valueOf(locationId), startTraining, endTraining, transformPrice);
        	pricesTest = rtPriceService.retrieveRTPricesCSVLocalTZ(String.valueOf(locationId), startTest, endTest, transformPrice);
    	}
    	else {
    		return Response.status(Status.BAD_REQUEST).entity("evaluateModels: priceType ("+priceType+") and locationId ("+locationId+") do not match").build();
    	}
    	String csvTraining = pricesTraining.getEntity().toString();
    	String csvTest = pricesTest.getEntity().toString();

    	try {
    		result = rManager.evaluateModels(simulationName, csvTraining, csvTest, debugOutput);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getLocalizedMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getLocalizedMessage();
		} catch(Exception e) {
			e.printStackTrace();
			result = e.getLocalizedMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Load a model with the given name
     * @param name the name of the model to load
     * @return Response to indicate whether or not the model loading was successful
     */
    @GET
    @Path("/loadmodel/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadModel(@PathParam("name") String name) {
    	String result;
    	try {
    		result = rManager.loadModel(name);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Generate forecast data from all currently existing models (files listed in data/models)
     * @return a Response indicating success or failure of forecast generation
     */
    @GET
    @Path("/forecast/generateAll/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateAllForecasts(@PathParam("type") String priceType) {
    	String result = "";
    	try {
			result = rManager.generateForecastsAllModels(priceType);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Generate forecast data from all models meeting the specified criteria
     * example: http://localhost:8081/em-app/rest/r/forecast/generate/da/1,2/14/2014-06-20/2014-07-28/
     * @param priceType the energy price type for which to generate forecasts
     * @param locationIds the locationIds for which to generate forecasts
	 * 					if -1 then forecasts are generated for all locations
	 * 						satisfying the other parameters
     * @param trainingsPeriod the model trainingsperiod to generate forecasts from
     * @param startDate the start date of the previous batch generation of models
     * @param endDate the end date of the previous batch generation of models
     * @return a Response indicating success or failure of forecast generation
     */
    @GET
    @Path("/forecast/generate/{type}/{loc_ids}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateForecasts(@PathParam("type") String priceType, @PathParam("loc_ids") String locationIds, 
										@PathParam("training_period") Integer trainingsPeriod,
										@PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	String result = "";
    	try {
			result = rManager.generateForecasts(priceType, locationIds, trainingsPeriod, startDate, endDate);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to retrieve a list of forecasts for a given location
     * example: http://localhost:8081/em-app/rest/r/forecast/da/1/14/2014-07-07/2014-07-10
     * @param locationId the locationId for which to get forecasts
     * @param trainingsPeriod only include the models generated with this training period
     * @param startDate the start date for which to get forecasts
     * @param endDate the end date for which to get forecasts
     * @return a String containing the retrieved forecasts
     */
    @GET
    @Path("/forecast/{type}/{loc_id}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getForecasts(@PathParam("type") String priceType, @PathParam("loc_id") Long locationId, 
    							@PathParam("training_period") Integer trainingsPeriod, 
    							@PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	String result = "";
    	String[] fcValues = null;
    	try {
    		fcValues = rManager.getForecasts(priceType, String.valueOf(locationId), trainingsPeriod, startDate, endDate);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			result = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	String output = "Forecast result: "+result+"\n"+Arrays.toString(fcValues);
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to retrieve a CSV list of forecasts for possible multiple locations at once
     * This format is perfectly suitable to be read by a csv parser
     * example: http://localhost:8081/em-app/rest/r/forecastAll/da/1,3,4/14/2014-07-07/2014-07-10
     * @param locationIds the locationIds to include in the output
     * @param trainingsPeriod only include the models generated with this training period
     * @param startDate the start date for which to get forecasts
     * @param endDate the end date for which to get forecasts
     * @return a String in csv format containing all of the retrieved forecasts
     */
    @GET
    @Path("/forecastAll/{type}/{loc_ids}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMultipleForecastsCsv(@PathParam("type") String priceType, @PathParam("loc_ids") String locationIds, 
    										@PathParam("training_period") Integer trainingsPeriod,  
    										@PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	String error = null;
    	String csvData = "";
    	String values[] = null;
    	List<Date> dates = getDates(startDate, endDate);
    	
		List<Location> locations = null;
    	
    	if(priceType.equals(EnergyPriceType.DA_TYPE)) {
    		locations = (List<Location>) locationService.getDALocations(locationIds).getEntity();
    	}
    	else if(priceType.equals(EnergyPriceType.RT_TYPE)) {
    		locations = (List<Location>) locationService.getRTLocations(locationIds).getEntity();
    	}
    	
    	
    	try {
    		
    		boolean anyResults = false;
    		
    		List<String[]> fcList = new ArrayList<String[]>();
    		StringBuilder builder = new StringBuilder();
    		for(Location loc : locations) {
    			values = rManager.getForecasts(priceType, String.valueOf(loc.getId()), trainingsPeriod, startDate, endDate);
    			if(values.length > 0) {
    				fcList.add(values);
        			// setting up CSV header
        			builder.append(",");
        			builder.append(loc.getName());
        			anyResults = true;
    			}
    		}
    		builder.append(System.lineSeparator());
    		
    		if(!anyResults) {
    			String msg = "No forecasts available for location ids "+locationIds+" from "+priceType+" markets, "
    					+ "start date "+startDate+", end date "+endDate+" and trainingsPeriod "+trainingsPeriod;
    			System.err.println(msg);
    			return Response.status(Status.BAD_REQUEST).entity(msg).build();
    		}
    		
    		// add dummy values for the first line (date is not forecasted)
    		Date date = dates.get(0);
			builder.append(DateUtils.formatDate(date));
    		for(String[] meanValues : fcList) {
	    		builder.append(",");
	    		builder.append(meanValues[0]);
	    	}
    		builder.append(System.lineSeparator());
    		
    		int rows = fcList.get(0).length;
    		for(int i = 0; i < rows-2; i++) {
    			date = dates.get(i+1);
    			builder.append(DateUtils.formatDate(date));
        		
    	    	for(String[] meanValues : fcList) {
    	    		builder.append(",");
    	    		builder.append(meanValues[i]);
    	    	}
    	    	builder.append(System.lineSeparator());
    		}
    		csvData = builder.toString();
		} catch (RserveException e) {
			e.printStackTrace();
			error = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			error = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			error = e.getMessage();
		} finally {
			rManager.closeRConnection();
		}
    	if(error != null) {
        	return Response.serverError().entity(error).build();
    	}
    	String output = csvData;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to retrieve all dates in one hour steps between a defined start and end date
     * @param startDateString the start date to begin with
     * @param endDateString the end date to end with
     * @return a list of dates in one hour steps between start and end date
     */
    private List<Date> getDates(String startDateString, String endDateString) {
		Date startDate = DateParser.parseDate(startDateString);
	    Date endDate = DateParser.parseDate(endDateString);
	    
//	    // start one hour after specified start date
//	    startDate.setTime(startDate.getTime() + 3600*1000);
	    // add another day to the end of the forecast dates
	    endDate.setTime(endDate.getTime() + 24*3600*1000);
	    
	    List<Date> dates = new ArrayList<Date>();
	    Calendar calendar = new GregorianCalendar();
	    calendar.setTime(startDate);
	    
	    while (calendar.getTime().before(endDate))
	    {
	        Date result = calendar.getTime();
	        dates.add(result);
	        calendar.add(Calendar.HOUR, 1);
	    }
	    // adding end date
	    dates.add(calendar.getTime());
	    return dates;
	}
    
    
    /**
     * Create a new folder if it doesn't exist within a base directory
     * @param baseDirectory a base directory relative to the "root" data directory
     * @param name the name of the directory to create
     */
    private void createFolder(String baseDirectory, String name) {
    	String path = getClass().getClassLoader().getResource("data").getPath(); // folder "rscripts" in resource directory
    	path = path + "/" + baseDirectory + "/" + name;
	    path = path.substring(1); // remove leading slash
	    File dir = new File(path);
	    if(!dir.exists()) {
	    	dir.mkdir();
	    }
    }
}
