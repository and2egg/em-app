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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.ac.tuwien.thesis.caddc.data.market.MarketData;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataBelgium;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataFinland;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataMaine;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataMassachussetts;
import at.ac.tuwien.thesis.caddc.data.market.MarketDataSweden;
import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.ImportDataException;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.util.Currency;

/**
 * Price Resource REST Service
 * <p/>
 * REST Service specifications for the import and retrieval of day ahead energy prices
 * @author Andreas Egger
 */
@Path("/daprices")
@RequestScoped
public class DAPricesResourceRESTService {

    @Inject
    private Logger log;
    
    @Inject
    private LocationRepository locationRepository;
    
    @Inject
    private DAPriceRepository daPriceRepository;
    
    
    private List<MarketData> marketList;
    
    
    /**
     * Add all currently available market data locations to the list
     */
    @PostConstruct
    public void init() {
    	marketList.add(new MarketDataFinland(locationRepository.findByName("Helsinki")));
    	marketList.add(new MarketDataSweden(locationRepository.findByName("Stockholm")));
    	marketList.add(new MarketDataMaine(locationRepository.findByName("Portland")));
    	marketList.add(new MarketDataMassachussetts(locationRepository.findByName("Boston")));
    	marketList.add(new MarketDataBelgium(locationRepository.findByName("Brussels")));
    }
    
    
    /**
     * Import prices for all locations for the given time range in years
     * @param yearFrom the year from which to import energy prices
     * @param yearTo the year up to which to import energy prices
     * @return a Response object indicating if the request has been successful
     */
    @GET
    @Path("/importall/{yearfrom}/{yearto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response importAllLocationsForYears(@PathParam("yearfrom") Integer yearFrom, @PathParam("yearto") Integer yearTo) {
    	try {
	    	for(int year = yearFrom; year <= yearTo; year++) {
	    		for(MarketData market : marketList) {
					market.importPrices(year);
	    		}
	    	}
    	} catch (ImportDataException e) {
    		return Response.status(503).entity("Request failed: Retrieving data for all locations for years "+yearFrom+" to "+yearTo).build();
		}
    	return Response.status(200).entity("Successfully imported data for all locations for years "+yearFrom+" to "+yearTo).build();
    }
    
    
    /**
     * Import energy market data per location and time range
     * @param locationId the location Id for which to retrieve energy prices
     * @param yearFrom the year from which to import energy prices
     * @param yearTo the year up to which to import energy prices
     * @return a Response object indicating if the request has been successful
     */
    @GET
    @Path("/import/{id:[0-9]+}/{yearfrom}/{yearto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response importMarketDataPerLocation(@PathParam("id") Long locationId, @PathParam("yearfrom") Integer yearFrom, @PathParam("yearto") Integer yearTo) {
    	List<MarketData> list = new ArrayList<MarketData>();
    	// if all locations should be queried, assign whole list
    	if(locationId.equals(Long.valueOf(-1L))) {
    		list = marketList;
    	}
    	else {
    		for(MarketData m : marketList) {
    			if(m.getLocation().getId().equals(locationId)) {
    				list.add(m);
    			}
    		}
    	}
    	
    	try {
	    	for(int year = yearFrom; year <= yearTo; year++) {
	    		for(MarketData market : list) {
					market.importPrices(year);
	    		}
	    	}
    	} catch (ImportDataException e) {
    		if(locationId.equals(Long.valueOf(-1L))) {
    			return Response.status(503).entity("Request failed: Retrieving data for all locations for years "+yearFrom+" to "+yearTo).build();
    		}
    		else {
    			return Response.status(503).entity("Request failed: Retrieving data for years "+yearFrom+" to "+yearTo+" for location with ID "+locationId).build();
    		}
		}
    	if(locationId.equals(Long.valueOf(-1L))) {
    		return Response.status(200).entity("Successfully imported data for all locations for years "+yearFrom+" to "+yearTo).build();
    	}
    	else {
    		return Response.status(200).entity("Successfully imported data for years "+yearFrom+" to "+yearTo+" for location with ID "+locationId).build();
    	}
    }
  
    
    /**
     * Retrieve day ahead prices from the location with the given id
     * and within a start- and enddate, based on the local timezone
     * example: http://localhost:8081/em-app/rest/daprices/price/1/2014-07-11/2014-07-12/localTZ
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/{loc_id}/{startDate}/{endDate}/{transformPrice}/localTZ")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesLocalTZ(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, 
    										@PathParam("endDate") String endDate, @PathParam("transformPrice") String transformPrice) {
    	Location location = locationRepository.findById(locationId);
    	if(location == null) 
    		return Response.status(Response.Status.BAD_REQUEST).entity("DA Price save: Invalid location").build();

    	System.out.println("Location = "+location.toString());
    	System.out.println("startDate = "+startDate.toString());
    	System.out.println("endDate = "+endDate.toString());
    	
    	Calendar start = Calendar.getInstance(TimeZone.getTimeZone(location.getTimeZone()));
    	Calendar end = Calendar.getInstance(TimeZone.getTimeZone(location.getTimeZone()));
    	
    	// Check date formats
    	String format = "";
    	if(startDate.length() == 10) {
    		format = "yyyy-MM-dd";
    	} else if(startDate.length() == 19) {
    		format = "yyyy-MM-dd HH:mm:ss";
    	}
    	SimpleDateFormat sdfStart = new SimpleDateFormat(format);
    	
    	if(endDate.length() == 10) {
    		format = "yyyy-MM-dd";
    	} else if(endDate.length() == 19) {
    		format = "yyyy-MM-dd HH:mm:ss";
    	}
    	SimpleDateFormat sdfEnd = new SimpleDateFormat(format);
    	try {
    		// Parse dates
    		sdfStart.parse(startDate);
    		sdfEnd.parse(endDate);
			
			Calendar s = sdfStart.getCalendar();
			Calendar e = sdfEnd.getCalendar();
			System.out.println("S DATE = "+s.getTime());
			
			start.set(s.get(Calendar.YEAR), s.get(Calendar.MONTH), s.get(Calendar.DAY_OF_MONTH), 
					s.get(Calendar.HOUR_OF_DAY), s.get(Calendar.MINUTE), s.get(Calendar.SECOND));
	    	start.set(Calendar.MILLISECOND, 0);
	    	
	    	end.set(e.get(Calendar.YEAR), e.get(Calendar.MONTH), e.get(Calendar.DAY_OF_MONTH), 
					e.get(Calendar.HOUR_OF_DAY), e.get(Calendar.MINUTE), e.get(Calendar.SECOND));
	    	end.set(Calendar.MILLISECOND, 0);
	    	
	    	System.out.println("START DATE = "+start.getTime());
		} catch (ParseException e) {
			System.err.println("Error parsing date: "+e.getLocalizedMessage());
			return Response.status(Response.Status.CONFLICT).entity("Error parsing date: "+e.getLocalizedMessage()).build();
		}
    	
    	System.out.println("start time = "+start.getTime());
    	System.out.println("end time = "+end.getTime());
    	
    	List<DAPrice> prices = null;
    	String output = "";
    	
    	if(locationId == -1) {
    		prices = daPriceRepository.findByDate(start.getTime(), end.getTime());
    		output = "Retrieved da prices in local TZ from all locations "
    				+ "from "+startDate+" to "+endDate+", dataset length: "+prices.size();
    	}
    	else {
    		prices = daPriceRepository.findByDateAndLocation(start.getTime(), end.getTime(), locationId);
    		output = "Retrieved da prices in local TZ from location id "+locationId+" "
    				+ "from "+startDate+" to "+endDate+", dataset length: "+prices.size();
    	}
    	if(!Currency.isInDollar(locationId) && Boolean.valueOf(transformPrice)) {
    		for(DAPrice price: prices) {
    			price.setPrice(Currency.convertToDollar(price.getPrice()));
    		}
    	}
    	System.out.println(output);
		return Response.status(200).entity(prices).build();
    }
    
    
    /**
     * Retrieve day ahead prices from the location with the given id
     * and within a start- and enddate, based on current local time
     * example: http://localhost:8081/em-app/rest/daprices/price/1/2014-07-11/2014-07-12
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDateString the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDateString the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/{loc_id}/{startDate}/{endDate}/{transformPrice}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPrices(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDateString, 
    								@PathParam("endDate") String endDateString, @PathParam("transformPrice") String transformPrice) {
    	Location location = null;
    	if(locationId != -1) {
    		location = locationRepository.findById(locationId);
        	if(location == null) 
        		return Response.status(Response.Status.BAD_REQUEST).entity("DA Price save: Invalid location").build();

        	System.out.println("Location = "+location.toString());
    	}
    	
    	Calendar start = Calendar.getInstance();
    	Calendar end = Calendar.getInstance();
    	
    	// Check date formats
    	String format = "";
    	if(startDateString.length() == 10) {
    		format = "yyyy-MM-dd";
    	} else if(startDateString.length() == 19) {
    		format = "yyyy-MM-dd HH:mm:ss";
    	}
    	SimpleDateFormat sdfStart = new SimpleDateFormat(format);
    	
    	if(endDateString.length() == 10) {
    		format = "yyyy-MM-dd";
    	} else if(endDateString.length() == 19) {
    		format = "yyyy-MM-dd HH:mm:ss";
    	}
    	SimpleDateFormat sdfEnd = new SimpleDateFormat(format);
    	try {
    		// Parse dates
    		sdfStart.parse(startDateString);
    		sdfEnd.parse(endDateString);
			
			Calendar s = sdfStart.getCalendar();
			Calendar e = sdfEnd.getCalendar();
			
			start.set(s.get(Calendar.YEAR), s.get(Calendar.MONTH), s.get(Calendar.DAY_OF_MONTH), 
					s.get(Calendar.HOUR_OF_DAY), s.get(Calendar.MINUTE), s.get(Calendar.SECOND));
	    	start.set(Calendar.MILLISECOND, 0);
	    	
	    	end.set(e.get(Calendar.YEAR), e.get(Calendar.MONTH), e.get(Calendar.DAY_OF_MONTH), 
					e.get(Calendar.HOUR_OF_DAY), e.get(Calendar.MINUTE), e.get(Calendar.SECOND));
	    	end.set(Calendar.MILLISECOND, 0);
		} catch (ParseException e) {
			System.err.println("Error parsing date: "+e.getLocalizedMessage());
			return Response.status(Response.Status.CONFLICT).entity("Error parsing date: "+e.getLocalizedMessage()).build();
		}
    	
    	System.out.println("start time = "+start.getTime());
    	System.out.println("end time = "+end.getTime());
    	
    	List<DAPrice> prices = null;
    	String output = "";
    	
    	if(locationId == -1) {
    		prices = daPriceRepository.findByDate(start.getTime(), end.getTime());
    		output = "Retrieved da prices from all locations "
    				+ "from "+startDateString+" to "+endDateString+", dataset length: "+prices.size();
    	}
    	else {
    		prices = daPriceRepository.findByDateAndLocation(start.getTime(), end.getTime(), locationId);
    		output = "Retrieved da prices from location id "+locationId+" "
    				+ "from "+startDateString+" to "+endDateString+", dataset length: "+prices.size();
    	}
    	if(!Currency.isInDollar(locationId) && Boolean.valueOf(transformPrice)) {
    		for(DAPrice price: prices) {
    			price.setPrice(Currency.convertToDollar(price.getPrice()));
    		}
    	}
    	System.out.println(output);
		return Response.status(200).entity(prices).build();
    }
    
    
    /**
     * Retrieve day ahead prices in csv format from the location with the given id
     * and within a start- and enddate, based on the local timezone
     * example: http://localhost:8081/em-app/rest/daprices/price/csv/1/2014-07-11/2014-07-12/true/localTZ
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response to indicate whether or not the query was successful
     */
    @SuppressWarnings("unchecked")
	@GET
    @Path("/price/csv/{loc_id}/{startDate}/{endDate}/{transformPrice}/localTZ")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesCSVLocalTZ(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, 
    										@PathParam("endDate") String endDate, @PathParam("transformPrice") String transformPrice) {
    	Response response = retrieveDAPricesLocalTZ(locationId, startDate, endDate, transformPrice);
    	List<DAPrice> prices = (List<DAPrice>)response.getEntity();
    	StringBuilder builder = new StringBuilder();
    	for(DAPrice price : prices) {
    		builder.append(price.getBiddingDate());
    		builder.append(",");
    		builder.append(price.getPrice().doubleValue() / 100.0);
    		builder.append(System.lineSeparator());
    	}
    	String csv = builder.toString();
    	return Response.status(200).entity(csv).build();
    }
    
    
    /**
     * Retrieve day ahead prices in csv format from the location with the given id
     * and within a start- and enddate, based on current local time
     * example: http://localhost:8081/em-app/rest/daprices/price/csv/1/2014-07-07 00:00:00/2014-07-20 23:00:00/true
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response to indicate whether or not the query was successful
     */
    @SuppressWarnings("unchecked")
	@GET
    @Path("/price/csv/{loc_id}/{startDate}/{endDate}/{transformPrice}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesCSV(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, 
    									@PathParam("endDate") String endDate, @PathParam("transformPrice") String transformPrice) {
    	Response response = retrieveDAPrices(locationId, startDate, endDate, transformPrice);
    	List<DAPrice> prices = (List<DAPrice>)response.getEntity();
    	StringBuilder builder = new StringBuilder();
    	for(DAPrice price : prices) {
    		builder.append(price.getBiddingDate());
    		builder.append(",");
    		builder.append(price.getPrice().doubleValue() / 100.0);
    		builder.append(System.lineSeparator());
    	}
    	String csv = builder.toString();
    	return Response.status(200).entity(csv).build();
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