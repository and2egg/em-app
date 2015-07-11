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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

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
    public Response importMarketData(@PathParam("id") Integer marketId, @PathParam("year") Integer year) {
    	if(marketId == 1) {
    		importNPSMarketData(year);
    	} else if(marketId == 2) {
    		importISONEMarketData(year);
    	} else if(marketId == 3) {
    		importBelpexMarketData(year);
    	}
		return Response.status(200).entity("Successfully imported data").build();
    }
    
    private void importNPSMarketData(Integer year) {
    	
    }
    
    private void importISONEMarketData(Integer year) {
    	
    }
    
    private void importBelpexMarketData(Integer year) {
    	
    }
    
    
    @GET
    @Path("/daprice")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testDAPriceSave() {
    	Location location = locationRepository.findByMarketandName("Nord Pool Spot", "Finland");
    	if(location == null) 
    		return Response.status(Response.Status.BAD_REQUEST).entity("DA Price save: Invalid location").build();
    	else
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
    	
//    	daPriceResource.saveDAPrice(daPrice);
    	
//    	List<DAPrice> prices = daPriceRepository.findAll();

//    	Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//    	
//    	System.out.println("UTC TIME: "+utc.setTime(s.getTime())+", "+e.getTime());
    	
    	Calendar start = Calendar.getInstance();
    	start.set(2015, 06, 11, 16, 00, 00);
    	start.set(Calendar.MILLISECOND, 0);
    	
    	Calendar end = Calendar.getInstance(); //TimeZone.getTimeZone("America/New_York")
    	end.set(2015, 06, 11, 17, 00, 00);
    	end.set(Calendar.MILLISECOND, 0);
    	
    	List<DAPrice> prices = daPriceRepository.findByDate(start.getTime(), end.getTime());
    	
    	for(DAPrice price : prices) {
    		System.out.println("price: "+price);
    	}
    	
    	String output = "Saved DA Price: "+daPrice.toString()+"\n for location "+location.toString();
		return Response.status(200).entity(output).build();
    }
    
    
    @GET
    @Path("/tz")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testTZ() {
    	Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"));
    	c1.set(2015, 02, 25, 01, 00, 00);
    	c1.set(Calendar.MILLISECOND, 0);
    	
    	Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"));
    	c2.set(2015, 02, 25, 02, 00, 00);
    	c2.set(Calendar.MILLISECOND, 0);
    	
    	Calendar c3 = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"));
    	c3.set(2015, 02, 25, 03, 00, 00);
    	c3.set(Calendar.MILLISECOND, 0);
    	
    	System.out.println("SWEDEN TIME: "+c1.getTime()+", "+c1.getTime());
    	System.out.println("SWEDEN TIME: "+c2.getTime()+", "+c2.getTime());
    	System.out.println("SWEDEN TIME: "+c3.getTime()+", "+c3.getTime());
    	
    	c1.setTimeZone(TimeZone.getTimeZone("UTC"));
    	c2.setTimeZone(TimeZone.getTimeZone("UTC"));
    	c3.setTimeZone(TimeZone.getTimeZone("UTC"));
    	
    	System.out.println("UTC TIME: "+c1.getTime()+", "+c1.getTime());
    	System.out.println("UTC TIME: "+c2.getTime()+", "+c2.getTime());
    	System.out.println("UTC TIME: "+c3.getTime()+", "+c3.getTime());
    	
    	
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
