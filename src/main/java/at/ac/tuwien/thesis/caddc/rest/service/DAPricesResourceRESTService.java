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
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import at.ac.tuwien.thesis.caddc.data.parse.NordPoolHTMLParser;
import at.ac.tuwien.thesis.caddc.data.parse.HTMLParser;
import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.DAPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.rest.client.RESTClient;

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
    
    @Inject
    private DAPricePersistence daPriceResource;
    
    
    @GET
    @Path("/data")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testDataFetch() {
    	String url = "http://www.nordpoolspot.com/globalassets/marketdata-excel-files/elspot-prices_2014_hourly_eur.xls";
		String result = RESTClient.fetchDataString(url);
		String output = "DATA FETCH\n"+new NordPoolHTMLParser().parsePrices(result, 0, new int[]{5});
		return Response.status(200).entity(output).build();
    }
    
    
    @GET
    @Path("/importall/{yearfrom}/{yearto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response importAllLocationsForYears(@PathParam("yearfrom") Integer yearFrom, @PathParam("yearto") Integer yearTo) {
    	for(int year = yearFrom; year <= yearTo; year++) {
    		importFinlandMarketData(year);
    		importSwedenMarketData(year);
    		importMaineMarketData(year);
    		importMassachusettsMarketData(year);
    		importBelgiumMarketData(year);
    	}
    	return Response.status(200).entity("Successfully imported data for all locations for years "+yearFrom+" to "+yearTo).build();
    }
    
    
    @GET
    @Path("/import/{id:[0-9]+}/{yearfrom}/{yearto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response importMarketDataPerLocation(@PathParam("id") Integer locationId, @PathParam("yearfrom") Integer yearFrom, @PathParam("yearto") Integer yearTo) {
    	for(int year = yearFrom; year <= yearTo; year++) {
    		if(locationId == 1) {
        		importFinlandMarketData(year);
        	} else if(locationId == 2) {
        		importSwedenMarketData(year);
        	} else if(locationId == 3) {
        		importMaineMarketData(year);
        	} else if(locationId == 4) {
        		importMassachusettsMarketData(year);
        	} else if(locationId == 5) {
        		importBelgiumMarketData(year);
        	} else if(locationId == -1) {
        		importFinlandMarketData(year);
        		importSwedenMarketData(year);
        		importMaineMarketData(year);
        		importMassachusettsMarketData(year);
        		importBelgiumMarketData(year);
        	}
    	}
    	return Response.status(200).entity("Successfully imported data for years "+yearFrom+" to "+yearTo+" for location with ID "+locationId).build();
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
    	
    	if(result != null) {
    		HTMLParser htmlParser = new NordPoolHTMLParser();
    		List<String> prices = htmlParser.parsePrices(result, // html content to parse
					0,   // row offset
					new int[]{5} // price column indices
    		);
    		daPriceResource.saveDAPrices(prices, "Helsinki");
    	}
    }
    
    private void importSwedenMarketData(Integer year) {
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
		
    	if(result != null) {
    		HTMLParser htmlParser = new NordPoolHTMLParser();
    		List<String> prices = htmlParser.parsePrices(result, // html content to parse
					0,   // row offset
					new int[]{1} // price column indices
    		);
    		daPriceResource.saveDAPrices(prices, "Stockholm");
    	}
    		
    }
    
    private void importMaineMarketData(Integer year) {
    	String url = "";
    	if(year == 2014) {
    		url = "http://www.iso-ne.com/static-assets/documents/2015/05/2014_smd_hourly.xls";
    	}
    	else {
    		url = "http://www.iso-ne.com/static-assets/documents/markets/hstdata/znl_info/hourly/"+year+"_smd_hourly.xls";
    	}
		int[] colIdx = {0,1,4};  // Date, Hour, DA_LMP
		
		List<String> priceList = RESTClient.fetchAndParseXLS(url, // fetch URL
																2, // sheet number
																1, // row Offset
																colIdx // column indices array
															);
		
		List<String> newList = new ArrayList<String>();
		for(String price : priceList) {
			String[] split = price.split(";");
			int hour = (int)Double.parseDouble(split[1]); // parse hour
			hour --; // reduce hour by one to get hours from 0-23 instead of 1-24
			String result = split[0] + ";" + String.valueOf(hour) + ";" + split[2];
			newList.add(result);
		}
		
		System.out.println("newList (1-10) : ");
		for(int i = 0; i < 10; i++) {
			System.out.println(newList.get(i));
		}
		
		daPriceResource.saveDAPrices(newList, "Portland");
    }
    
    private void importMassachusettsMarketData(Integer year) {
    	String url = "";
    	if(year == 2014) {
    		url = "http://www.iso-ne.com/static-assets/documents/2015/05/2014_smd_hourly.xls";
    	}
    	else {
    		url = "http://www.iso-ne.com/static-assets/documents/markets/hstdata/znl_info/hourly/"+year+"_smd_hourly.xls";
    	}
		int[] colIdx = {0,1,4};
		
		List<String> priceList = RESTClient.fetchAndParseXLS(url, // fetch URL
																9, // sheet number
																1, // row Offset
																colIdx // column indices array
															);
		List<String> newList = new ArrayList<String>();
		for(String price : priceList) {
			String[] split = price.split(";");
			int hour = (int)Double.parseDouble(split[1]); // parse hour
			hour --; // reduce hour by one to get hours from 0-23 instead of 1-24
			String result = split[0] + ";" + String.valueOf(hour) + ";" + split[2];
			newList.add(result);
		}
		
		System.out.println("newList (1-10) : ");
		for(int i = 0; i < 10; i++) {
			System.out.println(newList.get(i));
		}
		
		daPriceResource.saveDAPrices(newList, "Boston");
    }
    
    private void importBelgiumMarketData(Integer year) {
    	String path = getResourcePath("energydata","/BELPEX/spotmarket_data_"+year+".xls");
		int[] colIdx = new int[26];
		for(int i = 0; i < 26; i++) {
			colIdx[i] = i;
		}
		List<String> priceList = RESTClient.fileFetchAndParseXLS(path, // fetch URL
																1, // sheet number
																1, // row Offset
																colIdx // column indices array
															);
		System.out.println("priceList (1-10) : ");
		for(int i = 0; i < 10; i++) {
			System.out.println(priceList.get(i));
		}
		
		List<String> transformedPrices = new ArrayList<String>();
		int count = 0;
		for(String prices: priceList) {
			String[] split = prices.split(";");
			
			for(int i = 1; i < split.length; i++) {
				String result = split[0] + ";"; // date
				String price = split[i].trim();
				if(i == 25 && !price.isEmpty()) {
					result += 1 + ";" + price;
				}
				else {
					result += (i-1) + ";" + price;
				}
				transformedPrices.add(result);
			}
			
			if(count < 10) {
				System.out.println("price: "+transformedPrices.get(count));
				count++;
			}
		}
		
		daPriceResource.saveDAPrices(transformedPrices, "Brussels");
    }

    
    /**
     * Retrieve day ahead prices from the location with the given id
     * and within a start- and enddate, based on the local timezone
     * example: http://localhost:8081/em-app/rest/daprices/price/1/2014-07-11/2014-07-12/localTZ
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/{loc_id}/{startDate}/{endDate}/localTZ")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesLocalTZ(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
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
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPrices(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDateString, @PathParam("endDate") String endDateString) {
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
    	System.out.println(output);
		return Response.status(200).entity(prices).build();
    }
    
    
    /**
     * Retrieve day ahead prices in csv format from the location with the given id
     * and within a start- and enddate, based on the local timezone
     * example: http://localhost:8081/em-app/rest/daprices/price/csv/1/2014-07-11/2014-07-12/localTZ
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/csv/{loc_id}/{startDate}/{endDate}/localTZ")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesCSVLocalTZ(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	Response response = retrieveDAPricesLocalTZ(locationId, startDate, endDate);
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
     * example: http://localhost:8081/em-app/rest/daprices/price/csv/1/2014-07-07 00:00:00/2014-07-20 23:00:00
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/csv/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesCSV(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, @PathParam("endDate") String endDate) {
    	Response response = retrieveDAPrices(locationId, startDate, endDate);
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
    
    
    @GET
    @Path("/price/save")
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
    
    
    /**
     * Returns the path for a given resource at a resource root
     * @param resourceRoot the root folder of the resource
     * @param resourcePath the path relative to the root folder
     * @return the complete real resource path
     */
    private String getResourcePath(String resourceRoot, String resourcePath) {
    	String path = getClass().getClassLoader().getResource(resourceRoot).getPath();
		path = path + resourcePath;
	    path = path.substring(1); // remove leading slash
	    return path;
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
