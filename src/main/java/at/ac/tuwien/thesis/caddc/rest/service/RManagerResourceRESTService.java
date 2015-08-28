/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.thesis.caddc.rest.service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
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

import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.EnergyMarket;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketPersistence;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketRepository;
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
    private Validator validator;

    @Inject
    private DAPricesResourceRESTService pricesRestService;
    
    @Inject
    private RManager rManager;


    
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
    
    
    @GET
    @Path("/forecast/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getForecasts(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	String result = "";
    	String[] fcValues = null;
    	try {
    		System.out.println("before getForecasts");
    		fcValues = rManager.getForecasts(locationId, startDate, endDate);
    		System.out.println("after getForecasts");
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
    
    
    @GET
    @Path("/generatemodels/{loc_id}/{training_period}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateModels(@PathParam("loc_id") Long locationId, @PathParam("training_period") Integer trainingPeriod, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	Date start = DateParser.parseDate(startDate);
    	Date end = DateParser.parseDate(endDate);
    	
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
    		s.add(Calendar.DATE, -trainingPeriod); // go back a defined number of days
    												// for model trainingsdata
    		
    		SimpleDateFormat sdf = new SimpleDateFormat(format);
    		String dStart = sdf.format(s.getTime());
    		String dEnd = sdf.format(e.getTime());
    		
    		System.out.println("startdate "+dStart);
    		System.out.println("enddate "+dEnd);
    		
    		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    		String d = sdf2.format(e.getTime());
    		
    		String modelName = "da_model" + "_" + locationId.intValue() + "_" + d;
    		System.out.println("modelName "+modelName);
    		generateModel(modelName, locationId, dStart, dEnd);
    		calStart.add(Calendar.DATE, 1);
    		models++;
    	}
    	String output = "Successfully generated "+models+" models";
    	return Response.status(200).entity(output).build();
    }
    
    
    /**
     * Retrieve day ahead prices in csv format from the location with the given id
     * and within a start- and enddate, based on current local time
     * example: http://localhost:8081/em-app/rest/r/generatemodel/1/2014-07-07 00:00:00/2014-07-20 23:00:00
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/generatemodel/{modelName}/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateModel(@PathParam("modelName") String modelName, @PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	String result;
    	Response priceResponse = pricesRestService.retrieveDAPricesCSV(locationId, startDate, endDate);
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
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     * 
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<String, String>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }
}
