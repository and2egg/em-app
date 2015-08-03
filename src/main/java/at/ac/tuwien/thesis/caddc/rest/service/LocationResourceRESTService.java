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
import java.util.ArrayList;
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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.data.parse.NordPoolHTMLParser;
import at.ac.tuwien.thesis.caddc.data.parse.HTMLParser;
import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.EnergyMarket;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.DAPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketPersistence;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketRepository;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;
import at.ac.tuwien.thesis.caddc.service.RManager;

/**
 * Energy Market REST Service
 * <p/>
 * REST Service specifications for registering and retrieving energy markets
 * @author Andreas Egger
 */
@Path("/location")
@RequestScoped
public class LocationResourceRESTService {

    @Inject
    private Logger log;
    
    @Inject
    private LocationRepository locationRepository;
    
    
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
    @Path("/findlocation")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findLocation(@QueryParam("market") String market, @QueryParam("location") String location) {
    	Response.ResponseBuilder builder = null;
    	Location loc = locationRepository.findByMarketandName(market, location);
    	if(loc == null) 
    		builder = Response.status(200).entity("market "+market+" exists, location = "+loc);
    	else 
    		builder = Response.status(200).entity("market "+market+" exists, location = "+loc.getName());
    	
    	return builder.build();
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
