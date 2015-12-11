package at.ac.tuwien.thesis.caddc.rest.service;

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

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.service.RManager;
import at.ac.tuwien.thesis.caddc.util.DateParser;

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
    private DAPricesResourceRESTService pricesRestService;
    
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
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to generate ARIMA models for the given location, with the given training period,
     * and for all dates between the given start and end date strings
     * @param locationId the location id of the location for which to generate the models
     * @param trainingsPeriod the training period for the model in days, e.g. 14 days for 2 weeks
     * @param startDateString the starting date for which to generate models
     * @param endDateString the end date for which to generate models
     * @return a Response indicating the status (success/failure) of the calculation
     */
    @GET
    @Path("/generatemodels/{loc_id}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateModels(@PathParam("loc_id") Long locationId, @PathParam("training_period") Integer trainingsPeriod, 
    						@PathParam("startDate") String startDateString, @PathParam("endDate") String endDateString, 
    						@DefaultValue("false") @QueryParam("transformPrice") Boolean transformPrice) {
    	Date start = DateParser.parseDate(startDateString);
    	Date end = DateParser.parseDate(endDateString);
    	
    	Calendar calStart = Calendar.getInstance();
    	Calendar calEnd = Calendar.getInstance();
    	calStart.setTime(start);
    	calEnd.setTime(end);
    	
    	calStart.set(calStart.get(Calendar.YEAR), calStart.get(Calendar.MONTH), calStart.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    	calStart.set(Calendar.MILLISECOND, 0);
    	calEnd.set(calEnd.get(Calendar.YEAR), calEnd.get(Calendar.MONTH), calEnd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    	calEnd.set(Calendar.MILLISECOND, 0);
    	
    	String format = "yyyy-MM-dd";
    	
    	int models = 0;
    	while(calStart.getTimeInMillis() <= calEnd.getTimeInMillis()) {
    		Calendar e = Calendar.getInstance();
    		Calendar s = Calendar.getInstance();
    		s.setTimeInMillis(calStart.getTimeInMillis());
    		e.setTimeInMillis(calStart.getTimeInMillis());
    		s.add(Calendar.DATE, -trainingsPeriod); // go back a defined number of days
    												// for model trainingsdata
    		
    		SimpleDateFormat sdf = new SimpleDateFormat(format);
    		String dStart = sdf.format(s.getTime());
    		String dEnd = sdf.format(e.getTime());
    		
    		System.out.println("startdate "+dStart);
    		System.out.println("enddate "+dEnd);
    		
    		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    		String modelDate = sdf2.format(e.getTime());
    		
    		// Each modelname is created by giving the type (da), the location id, 
    		// the training period in days and the date at the end of the trainingsperiod
    		String modelName = "da_model" + "_" + locationId.intValue() + "_" + trainingsPeriod + "d_" + modelDate;
    		System.out.println("modelName "+modelName);
    		generateModel(modelName, locationId, dStart, dEnd, transformPrice);
    		calStart.add(Calendar.DATE, 1);
    		models++;
    	}
    	String output = "Successfully generated "+models+" models";
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Generate an ARIMA model given a modelName, locationId, and start and enddates
     * for the trainingsdata
     * @param modelName the name of the model which should be unique and recognizable
     * @param locationId the id of the location for which to generate the model
     * @param startTraining the startdate of the trainingsdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the trainingsdata (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response to indicate whether or not the model generation was successful
     */
    @GET
    @Path("/generatemodel/{modelName}/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateModel(@PathParam("modelName") String modelName, @PathParam("loc_id") Long locationId, @PathParam("startDate") String startTraining, 
    								@PathParam("endDate") String endTraining, @DefaultValue("false") @QueryParam("transformPrice") Boolean transformPrice) {
    	String result;
    	Response priceResponse = pricesRestService.retrieveDAPricesCSV(locationId, startTraining, endTraining, transformPrice);
    	String csvData = priceResponse.getEntity().toString();
    	
    	try {
    		result = rManager.generateModel(modelName, csvData);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
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
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to retrieve a list of forecasts for a given location
     * example: http://localhost:8081/em-app/rest/r/forecast/1/14/2014-07-07/2014-07-10
     * @param locationId the locationId for which to get forecasts
     * @param trainingsPeriod only include the models generated with this training period
     * @param startDate the start date for which to get forecasts
     * @param endDate the end date for which to get forecasts
     * @return a String containing the retrieved forecasts
     */
    @GET
    @Path("/forecast/{loc_id}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getForecasts(@PathParam("loc_id") Long locationId, @PathParam("training_period") Integer trainingsPeriod, 
    							@PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	String result = "";
    	String[] fcValues = null;
    	try {
    		fcValues = rManager.getForecasts(locationId, trainingsPeriod, startDate, endDate);
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			result = e.getMessage();
		}
    	String output = "Forecast result: "+result+"\n"+Arrays.toString(fcValues);
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Generate forecast data from all currently existing models (files listed in data/models)
     * @return a Response indicating success or failure of forecast generation
     */
    @GET
    @Path("/forecast/generateAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateForecasts() {
    	String result = "";
    	try {
			result = rManager.generateForecastsAllModels();
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			result = e.getMessage();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Method to retrieve a CSV list of forecasts for possible multiple locations at once
     * This format is perfectly suitable to be read by a csv parser
     * example: http://localhost:8081/em-app/rest/r/forecastAll/1,3,4/14/2014-07-07/2014-07-10
     * @param locationIds the locationIds to include in the output
     * @param trainingsPeriod only include the models generated with this training period
     * @param startDate the start date for which to get forecasts
     * @param endDate the end date for which to get forecasts
     * @return a String in csv format containing all of the retrieved forecasts
     */
    @GET
    @Path("/forecastAll/{loc_ids}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMultipleForecastsCsv(@PathParam("loc_ids") String locationIds, @PathParam("training_period") Integer trainingsPeriod,  
    										@PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	String result = "";
    	String csvData = "";
    	String values[] = null;
    	List<Date> dates = getDates(startDate, endDate);
    	try {
    		String[] locs = locationIds.split(",");
    		String[] locationNames = new String[locs.length];
    		List<String[]> fcList = new ArrayList<String[]>();
    		for(int i = 0; i < locs.length; i++) {
    			Location loc = locationRepository.findById(Long.valueOf(locs[i]));
    			locationNames[i] = loc.getName();
    			values = rManager.getForecasts(Long.valueOf(locs[i]), trainingsPeriod, startDate, endDate);
    			fcList.add(values);
    		}
    		
    		StringBuilder builder = new StringBuilder();
    		
    		for(int l = 0; l < locationNames.length; l++) {
    			builder.append(",");
    			builder.append(locationNames[l]);
    		}
    		builder.append(System.lineSeparator());
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		// add dummy values for the first line (date is not forecasted)
    		Date date = dates.get(0);
			String dateString = sdf.format(date);
			builder.append(dateString);
    		for(String[] meanValues : fcList) {
	    		builder.append(",");
	    		builder.append(meanValues[0]);
	    	}
    		builder.append(System.lineSeparator());
    		
    		int rows = fcList.get(0).length;
    		for(int i = 0; i < rows; i++) {
    			date = dates.get(i+1);
    			dateString = sdf.format(date);
    			builder.append(dateString);
        		
    	    	for(String[] meanValues : fcList) {
    	    		builder.append(",");
    	    		builder.append(meanValues[i]);
    	    	}
    	    	builder.append(System.lineSeparator());
    		}
    		csvData = builder.toString();
		} catch (RserveException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			e.printStackTrace();
			result = e.getMessage();
		} catch (REngineException e) {
			e.printStackTrace();
			result = e.getMessage();
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
}
