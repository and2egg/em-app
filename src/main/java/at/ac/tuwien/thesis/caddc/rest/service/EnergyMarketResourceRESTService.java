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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.data.parse.NordPoolFinlandParser;
import at.ac.tuwien.thesis.caddc.model.EnergyMarket;
import at.ac.tuwien.thesis.caddc.model.Member;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketPersistence;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketRepository;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;
import at.ac.tuwien.thesis.caddc.service.MemberRegistration;
import at.ac.tuwien.thesis.caddc.service.RManager;

/**
 * JAX-RS Example
 * <p/>
 * This class produces a RESTful service to read/write the contents of the members table.
 */
@Path("/em")
@RequestScoped
public class EnergyMarketResourceRESTService {

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private EnergyMarketRepository repository;
    
    @Inject
    private EnergyMarketPersistence resource;
    
    @Inject
    private RManager rManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllEMs() {
    	List<EnergyMarket> markets = repository.findAllOrderedByName();
        return Response.status(200).entity(markets).build();
    }

    @GET
    @Path("/{id:[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupEMById(@PathParam("id") Long id) {
    	EnergyMarket energyMarket = repository.findById(id);
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
    @Path("/create/name/{name}/description/{description}")
    @Produces(MediaType.APPLICATION_JSON)
    @Encoded
    public Response createEnergyMarket(@PathParam("name") String name, @PathParam("description") String description) {
    	
    	// decode path params URL strings
    	try {
    		name = URLDecoder.decode(name, "UTF-8");
    		description = URLDecoder.decode(description, "UTF-8");
		} catch (UnsupportedEncodingException e) {}
    	
    	EnergyMarket energyMarket = new EnergyMarket();
    	energyMarket.setName(name);
    	energyMarket.setDescription(description);
    	
    	resource.saveEnergyMarketToDB(energyMarket);
    	String output = "Successfully saved energy market "+energyMarket.getName()+", desc: "+energyMarket.getDescription();
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
