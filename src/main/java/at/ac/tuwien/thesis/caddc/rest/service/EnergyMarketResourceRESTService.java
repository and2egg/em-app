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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.data.parse.NordPoolFinlandParser;
import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.EnergyMarket;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.Member;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.DAPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketPersistence;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketRepository;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;
import at.ac.tuwien.thesis.caddc.service.MemberRegistration;
import at.ac.tuwien.thesis.caddc.service.RManager;

/**
 * Energy Market REST Service
 * <p/>
 * REST Service specifications for registering and retrieving energy markets
 * @author Andreas Egger
 */
@Path("/em")
@RequestScoped
public class EnergyMarketResourceRESTService {

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private EnergyMarketRepository marketRepository;
    
    @Inject
    private LocationRepository locationRepository;
    
    @Inject
    private DAPriceRepository daPriceRepository;
    
    @Inject
    private EnergyMarketPersistence energyMarketResource;
    
    @Inject
    private DAPricePersistence daPriceResource;
    
    @Inject
    private RManager rManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllEMs() {
    	List<EnergyMarket> markets = marketRepository.findAllOrderedByName();
        return Response.status(200).entity(markets).build();
    }

    @GET
    @Path("/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupEMById(@PathParam("id") Long id) {
    	EnergyMarket energyMarket = marketRepository.findById(id);
        if (energyMarket == null) {
        	String output = "No energy market with id "+id+" found";
            return Response.status(Response.Status.NOT_FOUND).entity(output).build();
        }
    	return Response.status(200).entity(energyMarket).build();
    }
    
    @GET
    @Path("/rtest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testR() {
    	String result = "";
    	try {
			result = rManager.testR();
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = e.getMessage();
		}
    	String output = "R result: "+result;
    	return Response.status(200).entity(output).build();
    }
    
    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testDataFetch() {
    	String url = "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_2014_hourly_eur.xls";
		String result = RESTClient.fetchDataString(url);
		String output = "DATA FETCH\n"+NordPoolFinlandParser.parsePrices(result);
		return Response.status(200).entity(output).build();
    }
    
    
    @GET
    @Path("/import/{id:[0-9]+}/{year}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response importMarketData(@PathParam("id") Integer locationId, @PathParam("year") Integer year) {
    	if(locationId == 1) {
    		importFinlandMarketData(year);
    	} else if(locationId == 2) {
    		importSwedenMarketData(year);
    	} else if(locationId == 3) {
    		importMaineMarketData(year);
    	} else if(locationId == 4) {
    		importBostonMarketData(year);
    	} else if(locationId == 5) {
    		importBrusselsMarketData(year);
    	}
		return Response.status(200).entity("Successfully imported data for location "+locationId).build();
    }
    
    private void importFinlandMarketData(Integer year) {
    	String result = null;
    	if(year == 2012) {
    	    String path = getResourcePath("energydata", "/NPS/Elspot_Prices_2012_Hourly_EUR.xls");
    	    try {
    	    	result = new String(Files.readAllBytes(Paths.get(path)));
			} catch (IOException e) {
				System.err.println("Could not read resource file: "+path);
			}
    	}
    	else {
    		String url = "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_"+year.toString()+"_hourly_eur.xls";
    		result = RESTClient.fetchDataString(url);
    	}
		
    	if(result != null) 
    		daPriceResource.saveDAPrices(NordPoolFinlandParser.parsePrices(result), "Helsinki");
    }
    
    private void importSwedenMarketData(Integer year) {
    	
    }
    
    private void importMaineMarketData(Integer year) {
    	
    }
    
    private void importBostonMarketData(Integer year) {
    	
    }
    
    private void importBrusselsMarketData(Integer year) {
    	
    }
    
    private String getResourcePath(String resourceRoot, String resourcePath) {
    	String path = getClass().getClassLoader().getResource(resourceRoot).getPath();
		path = path + resourcePath;
	    path = path.substring(1); // remove leading slash
	    return path;
	}
    
    /**
     * Retrieve day ahead prices from the location with the given id
     * and within a start- and enddate, based on the local timezone
     * @param locationId the id of the location where the query should be executed
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/daprice/{loc_id}/{startDate}/{endDate}/localTZ")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesLocal(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	Location location = locationRepository.findById(locationId);
    	if(location == null) 
    		return Response.status(Response.Status.BAD_REQUEST).entity("DA Price save: Invalid location").build();

    	System.out.println("Location = "+location.toString());
    	System.out.println("startDate = "+startDate.toString());
    	System.out.println("endDate = "+endDate.toString());

//    	Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//    	System.out.println("UTC TIME: "+utc.setTime(s.getTime())+", "+e.getTime());
    	
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
    	
    	List<DAPrice> prices = daPriceRepository.findByDate(start.getTime(), end.getTime());
    	
    	for(DAPrice price : prices) {
    		System.out.println("price: "+price);
    	}
		return Response.status(200).entity("Retrieved da prices from location id "+locationId+" "
				+ "from "+startDate+" to "+endDate).build();
    }
    
    
    /**
     * Retrieve day ahead prices from the location with the given id
     * and within a start- and enddate, based on current local time
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/daprice/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPrices(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	Location location = null;
    	if(locationId != -1) {
    		location = locationRepository.findById(locationId);
        	if(location == null) 
        		return Response.status(Response.Status.BAD_REQUEST).entity("DA Price save: Invalid location").build();

        	System.out.println("Location = "+location.toString());
    	}
    	
    	System.out.println("startDate = "+startDate.toString());
    	System.out.println("endDate = "+endDate.toString());
    	
    	Calendar start = Calendar.getInstance();
    	Calendar end = Calendar.getInstance();
    	
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
    	
    	if(locationId == -1) {
    		prices = daPriceRepository.findByDate(start.getTime(), end.getTime());
    	}
    	else {
    		prices = daPriceRepository.findByDateAndLocation(start.getTime(), end.getTime(), locationId);
    	}
    	
    	for(DAPrice price : prices) {
    		System.out.println("price: "+price);
    	}
		return Response.status(200).entity("Retrieved da prices from location id "+locationId+" "
				+ "from "+startDate+" to "+endDate+": "+prices.size()).build();
    }
    
    
    @GET
    @Path("/daprice/save")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testDAPriceSave() {
    	Location location = locationRepository.findById(1L);
    	if(location == null) 
    		return Response.status(Response.Status.BAD_REQUEST).entity("DA Price save: Invalid location").build();

    	System.out.println("Location = "+location.toString());
    	
    	Calendar c = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
    	c.set(2015, 06, 11, 10, 00, 00);
    	c.set(Calendar.MILLISECOND, 0);
    	
    	DAPrice daPrice = new DAPrice();
    	daPrice.setBiddingDate(c.getTime());
    	daPrice.setInterval(new Integer(1));
    	daPrice.setIntervalUnit("hour");
    	daPrice.setLocation(location);
    	daPrice.setPrice(new Integer(3600));
    	daPrice.setTimelag(new Integer(0));
    	System.out.println("DAPrice = "+daPrice.toString());
    	
    	daPriceResource.saveDAPrice(daPrice);
    	
    	String output = "Saved DA Price: "+daPrice.toString()+"\n for location "+location.toString();
    	return Response.status(200).entity(output).build();
    }
    
    
    @GET
    @Path("/tz")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testTZ() {
    	
    	
    	TimeZone UTC = TimeZone.getTimeZone("UTC");
    	TimeZone HLS = TimeZone.getTimeZone("Europe/Helsinki");
    	
    	Calendar c0 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Helsinki"));
    	c0.set(2012, 02, 25, 00, 00, 00);
    	c0.set(Calendar.MILLISECOND, 0);
    	System.out.println("HELSINKI TIME: "+c0.getTimeInMillis()+", "+c0.getTime()+", "+HLS.getOffset(c0.getTimeInMillis()));
    	c0.set(2012, 02, 25, 01, 00, 00);
    	System.out.println("HELSINKI TIME: "+c0.getTimeInMillis()+", "+c0.getTime()+", "+HLS.getOffset(c0.getTimeInMillis()));
    	c0.set(2012, 02, 25, 02, 00, 00);
    	System.out.println("HELSINKI TIME: "+c0.getTimeInMillis()+", "+c0.getTime()+", "+HLS.getOffset(c0.getTimeInMillis()));
    	System.out.println("THIS TIME DOES NOT EXIST !!!!");
    	c0.set(2012, 02, 25, 03, 00, 00);
    	System.out.println("HELSINKI TIME: "+c0.getTimeInMillis()+", "+c0.getTime()+", "+HLS.getOffset(c0.getTimeInMillis()));
    	c0.set(2012, 02, 25, 04, 00, 00);
    	System.out.println("HELSINKI TIME: "+c0.getTimeInMillis()+", "+c0.getTime()+", "+HLS.getOffset(c0.getTimeInMillis()));
    	c0.set(2012, 02, 25, 05, 00, 00);
    	System.out.println("HELSINKI TIME: "+c0.getTimeInMillis()+", "+c0.getTime()+", "+HLS.getOffset(c0.getTimeInMillis()));
    	c0.set(2012, 02, 25, 06, 00, 00);
    	System.out.println("HELSINKI TIME: "+c0.getTimeInMillis()+", "+c0.getTime()+", "+HLS.getOffset(c0.getTimeInMillis()));

    	
    	Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    	c1.set(2012, 02, 25, 00, 00, 00);
    	c1.set(Calendar.MILLISECOND, 0);
    	System.out.println("UTC TIME: "+c1.getTimeInMillis()+", "+c1.getTime()+", "+HLS.getOffset(c1.getTimeInMillis()));
    	c1.set(2012, 02, 25, 01, 00, 00);
    	System.out.println("UTC TIME: "+c1.getTimeInMillis()+", "+c1.getTime()+", "+HLS.getOffset(c1.getTimeInMillis()));
    	c1.set(2012, 02, 25, 02, 00, 00);
    	System.out.println("UTC TIME: "+c1.getTimeInMillis()+", "+c1.getTime()+", "+HLS.getOffset(c1.getTimeInMillis()));
    	c1.set(2012, 02, 25, 03, 00, 00);
    	System.out.println("UTC TIME: "+c1.getTimeInMillis()+", "+c1.getTime()+", "+HLS.getOffset(c1.getTimeInMillis()));
    	c1.set(2012, 02, 25, 04, 00, 00);
    	System.out.println("UTC TIME: "+c1.getTimeInMillis()+", "+c1.getTime()+", "+HLS.getOffset(c1.getTimeInMillis()));

    	
    	String output = "Finished";
    	return Response.status(200).entity(output).build();
    }
    
    
    @GET
    @Path("/register")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerEnergyMarket(@QueryParam("name") String name, @QueryParam("location") String location, @QueryParam("description") String description) {
    	// decode path params URL strings
    	System.out.println("name= "+name+" des "+description);
    	
    	Response.ResponseBuilder builder = null;
    	
    	if(marketRepository.findByName(name) != null) {
//    		locationRepository.findByName(location);
    		Location loc = null;
    		if((loc = locationRepository.findByMarketandName(name, location)) != null) {
    			return Response.status(200).entity("market exists, location = "+loc.getName()).build();
    		}
    		return Response.status(200).entity("market exists, location = null").build();
    	}
    	
		EnergyMarket energyMarket = new EnergyMarket();
    	energyMarket.setName(name);
    	energyMarket.setDescription(description);
    	try {
    		validateMarket(energyMarket);
    		energyMarketResource.saveEnergyMarketToDB(energyMarket);
        	String output = "Successfully saved new energy market "+energyMarket.getName()+", location: "+location+", description: "+energyMarket.getDescription();
        	builder = Response.status(200).entity(output);
    	} catch(ConstraintViolationException cvEx) {
    		builder = createViolationResponse(cvEx.getConstraintViolations());
    	}
    	return builder.build();
    }
    
    
    @GET
    @Path("/findlocation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerEnergyMarket(@QueryParam("market") String market, @QueryParam("location") String location) {
    	Response.ResponseBuilder builder = null;
    	Location loc = locationRepository.findByMarketandName(market, location);
    	if(loc == null) 
    		builder = Response.status(200).entity("market exists, location = "+loc);
    	else 
    		builder = Response.status(200).entity("market exists, location = "+loc.getName());
    	
    	return builder.build();
    }
    
    
    /**
     * <p>
     * Validates the given EnergyMarket and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * 
     * @param market EnergyMarket to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     */
    private void validateMarket(EnergyMarket market) throws ConstraintViolationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<EnergyMarket>> violations = validator.validate(market);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }
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
