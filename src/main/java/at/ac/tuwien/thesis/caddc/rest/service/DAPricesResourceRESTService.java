package at.ac.tuwien.thesis.caddc.rest.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import at.ac.tuwien.thesis.caddc.data.market.MarketData;
import at.ac.tuwien.thesis.caddc.data.market.da.MarketDataBelgiumDA;
import at.ac.tuwien.thesis.caddc.data.market.da.MarketDataFinlandDA;
import at.ac.tuwien.thesis.caddc.data.market.da.MarketDataGermanyDA;
import at.ac.tuwien.thesis.caddc.data.market.da.MarketDataMaineDA;
import at.ac.tuwien.thesis.caddc.data.market.da.MarketDataMassachussettsDA;
import at.ac.tuwien.thesis.caddc.data.market.da.MarketDataSwedenDA;
import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPrice;
import at.ac.tuwien.thesis.caddc.persistence.DAPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.DAPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.persistence.exception.ImportDataException;
import at.ac.tuwien.thesis.caddc.util.Currency;
import at.ac.tuwien.thesis.caddc.util.DateParser;
import at.ac.tuwien.thesis.caddc.util.DateUtils;
import at.ac.tuwien.thesis.caddc.util.FormatUtils;

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
    
    @Inject
    private LocationResourceRESTService locationService;
    
    
    private List<MarketData> marketList = new ArrayList<MarketData>();
    
    
    /**
     * Add all currently available market data locations to the list
     */
    @PostConstruct
    public void init() {
    	marketList.add(new MarketDataFinlandDA(locationRepository.findByName("Hamina"), daPriceResource));
    	marketList.add(new MarketDataBelgiumDA(locationRepository.findByName("St.Ghislain"), daPriceResource));
    	marketList.add(new MarketDataGermanyDA(locationRepository.findByName("Potsdam"), daPriceResource));
    	marketList.add(new MarketDataMaineDA(locationRepository.findByName("Portland"), daPriceResource));
    	marketList.add(new MarketDataMassachussettsDA(locationRepository.findByName("Boston"), daPriceResource));
    	marketList.add(new MarketDataSwedenDA(locationRepository.findByName("Stockholm"), daPriceResource));
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
	    	for(int year = yearFrom; year <= yearTo; year++) 
	    		for(MarketData market : marketList)
					market.importPrices(year);
    	} catch (ImportDataException e) {
    		return Response.status(503).entity("Request failed: Retrieving data for all locations for years "+yearFrom+" to "+yearTo).build();
		}
    	return Response.status(200).entity("Successfully imported data for all locations for years "+yearFrom+" to "+yearTo).build();
    }
    
    
    /**
     * Import energy market data per location and time range
     * example: http://localhost:8081/em-app/rest/daprices/import/1/2014/2014
     * @param locationId if -1 import data from all registered locations, 
     * 		else the location Id denotes for which location to retrieve energy prices
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
    	if(locationId.equals(Long.valueOf(-1L)))
    		list = marketList;
    	else
    		for(MarketData m : marketList)
    			if(m.getLocation().getId().equals(locationId))
    				list.add(m);
    	try {
	    	for(int year = yearFrom; year <= yearTo; year++)
	    		for(MarketData market : list)
	    			market.importPrices(year);
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
     * and within a start- and enddate, based on the local timezone in JSON format
     * example: http://localhost:8081/em-app/rest/daprices/price/localTZ/1/2014-07-11/2014-07-12
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/localTZ/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesLocalTZ(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, 
    										@PathParam("endDate") String endDate, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	Location location = locationRepository.findById(locationId);
    	if(location == null) 
    		return Response.status(Response.Status.BAD_REQUEST).entity("DA Price save: Invalid location").build();

    	System.out.println("Location = "+location.toString());
    	System.out.println("startDate = "+startDate.toString());
    	System.out.println("endDate = "+endDate.toString());
    	
    	TimeZone tz = TimeZone.getTimeZone(location.getTimeZone());
    	
    	Date sDate = DateParser.parseDate(startDate);
    	Date eDate = DateParser.parseDate(endDate);
    	
    	if(sDate == null || eDate == null) {
    		String errMsg = "One or both of dates "+startDate+" and "+endDate+" could not be parsed";
    		System.err.println(errMsg);
			return Response.status(Response.Status.CONFLICT).entity(errMsg).build();
    	}
    	
    	Calendar s = Calendar.getInstance();
		Calendar e = Calendar.getInstance();
		s.setTime(sDate);
		e.setTime(eDate);
		System.out.println("S DATE = "+s.getTime());
		
		Calendar start = Calendar.getInstance(tz);
    	Calendar end = Calendar.getInstance(tz);
		
		start.set(s.get(Calendar.YEAR), s.get(Calendar.MONTH), s.get(Calendar.DAY_OF_MONTH), 
				s.get(Calendar.HOUR_OF_DAY), s.get(Calendar.MINUTE), s.get(Calendar.SECOND));
    	start.set(Calendar.MILLISECOND, 0);
    	
    	end.set(e.get(Calendar.YEAR), e.get(Calendar.MONTH), e.get(Calendar.DAY_OF_MONTH), 
				e.get(Calendar.HOUR_OF_DAY), e.get(Calendar.MINUTE), e.get(Calendar.SECOND));
    	end.set(Calendar.MILLISECOND, 0);
    	
    	System.out.println("start time (localTZ) = "+start.getTime());
    	System.out.println("end time (localTZ) = "+end.getTime());
    	
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
    	
    	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	formatter.setTimeZone(tz);
    	
    	List<EnergyPrice> finalPrices = new ArrayList<EnergyPrice>();
    	for(DAPrice price: prices) {
    		finalPrices.add(new EnergyPrice(formatter.format(price.getBiddingDate()), price.getFinalPrice()));
    	}
    	System.out.println(output);
		return Response.status(200).entity(finalPrices).build();
    }
    
    
    /**
     * Retrieve day ahead prices from possibly multiple locations with the given ids
     * within a start- and enddate, based on current local time in JSON format
     * example: http://localhost:8081/em-app/rest/daprices/price/1,2,4/2014-07-11/2014-07-12?transformPrice=true
     * @param locationIds the ids of the location where the query should be executed
     * 					if -1 then all locations containing day ahead data are queried
     * @param startDateString the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDateString the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response to indicate whether or not the query was successful
     */
    @GET
    @Path("/price/{loc_ids}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPrices(@PathParam("loc_ids") String locationIds, @PathParam("startDate") String startDateString, 
    								@PathParam("endDate") String endDateString, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	
    	@SuppressWarnings("unchecked")
		List<Location> locations = (List<Location>) locationService.getDALocations(locationIds).getEntity();
    	
    	if(locations.size() == 0) {
    		return Response.status(Response.Status.BAD_REQUEST).entity("retrieveDAPrices: Invalid location ids").build();
    	}
    	
    	Date sDate = DateParser.parseDate(startDateString);
    	Date eDate = DateParser.parseDate(endDateString);
    	
    	if(sDate == null || eDate == null) {
    		String errMsg = "One or both of dates "+startDateString+" and "+endDateString+" could not be parsed";
    		System.err.println(errMsg);
			return Response.status(Response.Status.CONFLICT).entity(errMsg).build();
    	}
    	
    	Calendar s = Calendar.getInstance();
		Calendar e = Calendar.getInstance();
		s.setTime(sDate);
		e.setTime(eDate);
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		
		DateFormat formatter = new SimpleDateFormat(DateUtils.DATE_FORMAT);
    	formatter.setTimeZone(tz);
		
		Calendar start = Calendar.getInstance(tz);
    	Calendar end = Calendar.getInstance(tz);
			
		start.set(s.get(Calendar.YEAR), s.get(Calendar.MONTH), s.get(Calendar.DAY_OF_MONTH), 
				s.get(Calendar.HOUR_OF_DAY), s.get(Calendar.MINUTE), s.get(Calendar.SECOND));
    	start.set(Calendar.MILLISECOND, 0);
    	
    	end.set(e.get(Calendar.YEAR), e.get(Calendar.MONTH), e.get(Calendar.DAY_OF_MONTH), 
				e.get(Calendar.HOUR_OF_DAY), e.get(Calendar.MINUTE), e.get(Calendar.SECOND));
    	end.set(Calendar.MILLISECOND, 0);
    	
    	System.out.println("start time = "+formatter.format(start.getTime()));
    	System.out.println("end time = "+formatter.format(end.getTime()));
    	
    	String output = "";
    	
    	List<List<DAPrice>> listOfPrices = new ArrayList<List<DAPrice>>();
    	
    	for(Location loc : locations) {
    		List<DAPrice> prices = daPriceRepository.findByDateAndLocation(start.getTime(), end.getTime(), loc.getId());
    		listOfPrices.add(prices);
    	}
    	
    	// check if time series have the same length
    	if(!checkLengthOfPriceTimeSeries(listOfPrices)) {
    		return Response.status(Status.PRECONDITION_FAILED).entity("Precondition failed: Length of price time"
    																	+ " series is not equal").build();
    	}

    	Map<String, List<EnergyPrice>> mapLocationToPrices = new LinkedHashMap<String, List<EnergyPrice>>();
    	
    	for(List<DAPrice> prices : listOfPrices) {
    		List<EnergyPrice> finalPrices = new ArrayList<EnergyPrice>();
        	for(DAPrice price: prices) {
        		if(!Currency.isInDollar(price.getLocation().getId()) && Boolean.valueOf(transformPrice)) {
        			price.setPrice(Currency.convertToDollar(price.getPrice()));
        		}
        		finalPrices.add(new EnergyPrice(formatter.format(price.getBiddingDate()), price.getFinalPrice()));
        	}
        	mapLocationToPrices.put(prices.get(0).getLocation().getName(), finalPrices);
    	}
    	
    	output = "Retrieved da prices from location with ids "+locationIds+" "
				+ "from "+startDateString+" to "+endDateString+", dataset length: "+listOfPrices.get(0).size();
    	
    	System.out.println(output);
		return Response.status(200).entity(mapLocationToPrices).build();
    }
    
    
    /**
     * Retrieve day ahead prices in csv format from the location with the given id
     * and within a start- and enddate, based on the local timezone
     * example: http://localhost:8081/em-app/rest/daprices/price/csv/localTZ/1/2014-07-11/2014-07-12?transformPrice=true
     * @param locationId the id of the location where the query should be executed
     * 					if -1 then all stored locations are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response containing a string representation of CSV data
     */
    @GET
    @Path("/price/csv/localTZ/{loc_id}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesCSVLocalTZ(@PathParam("loc_id") Long locationId, @PathParam("startDate") String startDate, 
    										@PathParam("endDate") String endDate, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	Response response = retrieveDAPricesLocalTZ(locationId, startDate, endDate, transformPrice);
    	return Response.status(200).entity(retrieveCSV(response)).build();
    }
    
    
    /**
     * Retrieve day ahead prices from possibly multiple locations with the given ids
     * within a start- and enddate, based on current local time in CSV format
     * example: http://localhost:8081/em-app/rest/daprices/price/csv/1,2,4/2014-07-11/2014-07-12?transformPrice=true
     * @param locationIds the ids of the location where the query should be executed
     * 					if -1 then all locations containing day ahead data are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response containing a string representation of CSV data
     */
    @GET
    @Path("/price/csv/{loc_ids}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveDAPricesCSV(@PathParam("loc_ids") String locationIds, @PathParam("startDate") String startDate, 
    									@PathParam("endDate") String endDate, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	Response response = retrieveDAPrices(locationIds, startDate, endDate, transformPrice);
    	return Response.status(200).entity(retrieveCSV(response)).build();
    }
    
    
    /**
     * Retrieve a string of prices in CSV format from the given reponse
     * containing a list of EnergyPrice objects
     * @param response the response to parse
     * @return a string representation of a CSV file for the given response
     */
	@SuppressWarnings("unchecked")
	private String retrieveCSV(Response response) {
		Map<String, List<EnergyPrice>> mapLocationToPrices = (Map<String, List<EnergyPrice>>) response.getEntity();
    	StringBuilder builder = new StringBuilder();
    	
    	// Generate CSV header
    	builder.append("Date");
    	for(String location : mapLocationToPrices.keySet()) {
    		builder.append(FormatUtils.CSV_SEPARATOR);
    		builder.append(location);
    	}
    	builder.append(System.lineSeparator());
    	
    	List<List<EnergyPrice>> listOfPrices = new ArrayList<List<EnergyPrice>>();
    	Iterator<List<EnergyPrice>> it = mapLocationToPrices.values().iterator();
    	while(it.hasNext()) {
    		listOfPrices.add(it.next());
    	}
    	
    	// take first price series and iterate over list
    	// (length of time series should be the same for each series)
    	List<EnergyPrice> series = listOfPrices.get(0);
    	
    	// Generate CSV
    	for(int i = 0; i < series.size(); i++) {
    		builder.append(series.get(i).getDate());
    		
    		for(List<EnergyPrice> prices : listOfPrices) {
        		builder.append(FormatUtils.CSV_SEPARATOR);
        		builder.append(prices.get(i).getPrice());
        	}    		
    		builder.append(System.lineSeparator());
    	}
    	
    	return builder.toString();
    }
    
    
	/**
	 * Check if all contained lists of energy prices are of same length
	 * @param listOfPrices
	 * @return true if each price series is of same length, false otherwise
	 */
    private boolean checkLengthOfPriceTimeSeries(List<List<DAPrice>> listOfPrices) {
    	int priceLength = listOfPrices.get(0).size();
    	for(List<DAPrice> prices : listOfPrices) {
    		if(prices.size() != priceLength) {
    			System.err.println("checkLengthOfPriceTimeSeries: the length of price time series ("
    					+prices.get(0).getLocation().getName()+","+prices.size()+") is different from ("
    					+listOfPrices.get(0).get(0).getLocation().getName()+","+listOfPrices.get(0).size()+")");
    			return false;
    		}
    	}
    	return true;
    }
    
}
