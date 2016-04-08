package at.ac.tuwien.thesis.caddc.rest.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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
import at.ac.tuwien.thesis.caddc.data.market.rt.MarketDataMaineRT;
import at.ac.tuwien.thesis.caddc.data.market.rt.MarketDataMassachussettsRT;
import at.ac.tuwien.thesis.caddc.data.market.rt.MarketDataMichiganRT;
import at.ac.tuwien.thesis.caddc.data.market.rt.MarketDataOhioRT;
import at.ac.tuwien.thesis.caddc.data.market.rt.MarketDataPennsylvaniaRT;
import at.ac.tuwien.thesis.caddc.data.market.rt.MarketDataVirginiaRT;
import at.ac.tuwien.thesis.caddc.data.market.rt.MarketDataWisconsinRT;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.model.RTPrice;
import at.ac.tuwien.thesis.caddc.model.type.EnergyPrice;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
import at.ac.tuwien.thesis.caddc.persistence.RTPricePersistence;
import at.ac.tuwien.thesis.caddc.persistence.RTPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.exception.ImportDataException;
import at.ac.tuwien.thesis.caddc.util.Currency;
import at.ac.tuwien.thesis.caddc.util.DateParser;
import at.ac.tuwien.thesis.caddc.util.DateUtils;
import at.ac.tuwien.thesis.caddc.util.FormatUtils;

/**
 * Price Resource REST Service
 * <p/>
 * REST Service specifications for the import and retrieval of real time energy prices
 * @author Andreas Egger
 */
@Path("/rtprices")
@RequestScoped
public class RTPricesResourceRESTService {

    @Inject
    private Logger log;
    
    @Inject
    private LocationRepository locationRepository;
    
    @Inject
    private RTPriceRepository rtPriceRepository;
    
    @Inject
    private RTPricePersistence rtPriceResource;
    
    @Inject
    private LocationResourceRESTService locationService;
    
    
    private List<MarketData> marketList = new ArrayList<MarketData>();
    
    
    /**
     * Add all currently available market data locations to the list
     */
    @PostConstruct
    public void init() {
    	marketList.add(new MarketDataMaineRT(locationRepository.findByName("Portland"), rtPriceResource));
    	marketList.add(new MarketDataMassachussettsRT(locationRepository.findByName("Boston"), rtPriceResource));
    	marketList.add(new MarketDataVirginiaRT(locationRepository.findByName("Richmond"), rtPriceResource));
    	marketList.add(new MarketDataMichiganRT(locationRepository.findByName("Brighton"), rtPriceResource));
    	marketList.add(new MarketDataPennsylvaniaRT(locationRepository.findByName("Hatfield"), rtPriceResource));
    	marketList.add(new MarketDataWisconsinRT(locationRepository.findByName("Madison"), rtPriceResource));
    	marketList.add(new MarketDataOhioRT(locationRepository.findByName("Georgetown"), rtPriceResource));
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
     * example: http://localhost:8081/em-app/rest/rtprices/import/1/2014/2014
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
     * Retrieve real time prices from possibly multiple locations with the given ids
     * within a start- and enddate, based on the respective local timezone in JSON format
     * example: http://localhost:8081/em-app/rest/rtprices/price/localTZ/6,7,9/2014-07-11/2014-07-12?transformPrice=true
     * @param locationIds the ids of the location where the query should be executed
     * 					if -1 then all locations containing real time data are queried
     * @param startDateString the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDateString the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response containing a map of locations to list of EnergyPrices
     */
    @GET
    @Path("/price/localTZ/{loc_ids}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveRTPricesLocalTZ(@PathParam("loc_ids") String locationIds, @PathParam("startDate") String startDate, 
    										@PathParam("endDate") String endDate, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	
    	@SuppressWarnings("unchecked")
		List<Location> locations = (List<Location>) locationService.getRTLocations(locationIds).getEntity();
    	
    	if(locations.size() == 0) {
    		return Response.status(Response.Status.BAD_REQUEST).entity("retrieveRTPrices: Invalid location ids").build();
    	}
    	
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
		
		List<List<RTPrice>> listOfPrices = processLocalTimeZones(locations, s, e);

		// check if time series have the same length
    	if(!checkLengthOfPriceTimeSeries(listOfPrices)) {
    		return Response.status(Status.PRECONDITION_FAILED).entity("Precondition failed: Length of price time"
    																	+ " series is not equal").build();
    	}
    	
    	Map<String, List<EnergyPrice>> mapLocationToPrices = new LinkedHashMap<String, List<EnergyPrice>>();
    	
    	DateFormat formatter = new SimpleDateFormat(DateUtils.DATE_FORMAT);
    	
    	for(List<RTPrice> prices : listOfPrices) {
    		// set the right local timezone for the current location
    		TimeZone tz = TimeZone.getTimeZone(prices.get(0).getLocation().getTimeZone());
    		formatter.setTimeZone(tz);
    		
    		// the final formatted prices for this price time series
    		List<EnergyPrice> finalPrices = new ArrayList<EnergyPrice>();
        	for(RTPrice price: prices) {
        		if(!Currency.isInDollar(price.getLocation().getId()) && Boolean.valueOf(transformPrice)) {
        			price.setPrice(Currency.convertToDollar(price.getPrice()));
        		}
        		finalPrices.add(new EnergyPrice(formatter.format(price.getBiddingDate()), price.getFinalPrice()));
        	}
        	mapLocationToPrices.put(prices.get(0).getLocation().getName(), finalPrices);
    	}
    	
    	String output = "Retrieved rt prices in local TZ from location with ids "+locationIds+" "
				+ "from "+startDate+" to "+endDate+", dataset length: "+listOfPrices.get(0).size();
    	
    	System.out.println(output);
		return Response.status(200).entity(mapLocationToPrices).build();
    }
    
    
    /**
     * Retrieve real time prices from possibly multiple locations with the given ids
     * within a start- and enddate, based on UTC time in JSON format
     * example: http://localhost:8081/em-app/rest/rtprices/price/6,7,9/2014-07-11/2014-07-12?transformPrice=true
     * @param locationIds the ids of the location where the query should be executed
     * 					if -1 then all locations containing real time data are queried
     * @param startDateString the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDateString the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response containing a map of locations to list of EnergyPrices
     */
    @GET
    @Path("/price/{loc_ids}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveRTPrices(@PathParam("loc_ids") String locationIds, @PathParam("startDate") String startDateString, 
    								@PathParam("endDate") String endDateString, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	
    	@SuppressWarnings("unchecked")
		List<Location> locations = (List<Location>) locationService.getRTLocations(locationIds).getEntity();
    	
    	if(locations.size() == 0) {
    		return Response.status(Response.Status.BAD_REQUEST).entity("retrieveRTPrices: Invalid location ids").build();
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
    	
    	List<List<RTPrice>> listOfPrices = new ArrayList<List<RTPrice>>();
    	
    	for(Location loc : locations) {
    		List<RTPrice> prices = rtPriceRepository.findByDateAndLocation(start.getTime(), end.getTime(), loc.getId());
    		listOfPrices.add(prices);
    	}
    	
    	// check if time series have the same length
    	if(!checkLengthOfPriceTimeSeries(listOfPrices)) {
    		return Response.status(Status.PRECONDITION_FAILED).entity("Precondition failed: Length of price time"
    																	+ " series is not equal").build();
    	}
    	
    	Map<String, List<EnergyPrice>> mapLocationToPrices = new LinkedHashMap<String, List<EnergyPrice>>();
    	
    	for(List<RTPrice> prices : listOfPrices) {
    		List<EnergyPrice> finalPrices = new ArrayList<EnergyPrice>();
        	for(RTPrice price: prices) {
        		if(!Currency.isInDollar(price.getLocation().getId()) && Boolean.valueOf(transformPrice)) {
        			price.setPrice(Currency.convertToDollar(price.getPrice()));
        		}
        		finalPrices.add(new EnergyPrice(formatter.format(price.getBiddingDate()), price.getFinalPrice()));
        	}
        	mapLocationToPrices.put(prices.get(0).getLocation().getName(), finalPrices);
    	}
    	
    	output = "Retrieved rt prices from location with ids "+locationIds+" "
				+ "from "+startDateString+" to "+endDateString+", dataset length: "+listOfPrices.get(0).size();
    	
    	System.out.println(output);
		return Response.status(200).entity(mapLocationToPrices).build();
    }
    
    
    /**
     * Retrieve real time prices from possibly multiple locations with the given ids
     * within a start- and enddate, based on the respective local time zone in CSV format
     * example: http://localhost:8081/em-app/rest/rtprices/price/csv/localTZ/6,7,9/2014-07-11/2014-07-12?transformPrice=true
     * @param locationIds the ids of the location where the query should be executed
     * 					if -1 then all locations containing real time data are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response containing a string representation of CSV data
     */
    @SuppressWarnings("unchecked")
	@GET
    @Path("/price/csv/localTZ/{loc_ids}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveRTPricesCSVLocalTZ(@PathParam("loc_ids") String locationIds, @PathParam("startDate") String startDate, 
    										@PathParam("endDate") String endDate, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	Response response = retrieveRTPricesLocalTZ(locationIds, startDate, endDate, transformPrice);
    	return Response.status(200).entity(retrieveCSV(response)).build();
    }
    
    
    /**
     * Retrieve real time prices from possibly multiple locations with the given ids
     * within a start- and enddate, based on current local time in CSV format
     * example: http://localhost:8081/em-app/rest/rtprices/price/csv/6,7,9/2014-07-11/2014-07-12?transformPrice=true
     * @param locationIds the ids of the location where the query should be executed
     * 					if -1 then all locations containing real time data are queried
     * @param startDate the startdate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param endDate the enddate of the query (Dateformat: yyyy-MM-dd or yyyy-MM-dd HH:mm:ss)
     * @param transformPrice a boolean value to indicate whether the prices should be transformed
     * 				(converted to a unified currency, e.g. dollars)
     * @return Response containing a string representation of CSV data
     */
	@GET
    @Path("/price/csv/{loc_ids}/{startDate}/{endDate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveRTPricesCSV(@PathParam("loc_ids") String locationIds, @PathParam("startDate") String startDate, 
    									@PathParam("endDate") String endDate, @DefaultValue("true") @QueryParam("transformPrice") Boolean transformPrice) {
    	Response response = retrieveRTPrices(locationIds, startDate, endDate, transformPrice);
    	return Response.status(200).entity(retrieveCSV(response)).build();
    }
    
    
    
    /**
     * Get energy prices for locations at local time zones (e.g. each series starting locally at midnight)
     * @param locations the locations to process
     * @param startDate the start date of the query
     * @param endDate the end date of the query
     * @return a list of lists of DA Prices, one for each location
     */
    private List<List<RTPrice>> processLocalTimeZones(List<Location> locations, Calendar startDate, Calendar endDate) {
    	
    	List<List<RTPrice>> listOfPrices = new ArrayList<List<RTPrice>>();
    	
    	for(Location loc : locations) {
    		TimeZone tz = TimeZone.getTimeZone(loc.getTimeZone());
    		
    		DateFormat formatter = new SimpleDateFormat(DateUtils.DATE_FORMAT);
        	formatter.setTimeZone(tz);
    		
    		Calendar start = Calendar.getInstance(tz);
        	Calendar end = Calendar.getInstance(tz);
    		
    		start.set(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH), 
    				startDate.get(Calendar.HOUR_OF_DAY), startDate.get(Calendar.MINUTE), startDate.get(Calendar.SECOND));
        	start.set(Calendar.MILLISECOND, 0);
        	
        	end.set(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH), 
    				endDate.get(Calendar.HOUR_OF_DAY), endDate.get(Calendar.MINUTE), endDate.get(Calendar.SECOND));
        	end.set(Calendar.MILLISECOND, 0);
        	
        	System.out.println("start time (localTZ) = "+start.getTime());
        	System.out.println("end time (localTZ) = "+end.getTime());
        	
        	List<RTPrice> prices = rtPriceRepository.findByDateAndLocation(start.getTime(), end.getTime(), loc.getId());
    		listOfPrices.add(prices);
    	}
    	
    	return listOfPrices;
    }
    
    
    /**
     * Retrieve a string of prices in CSV format from the given reponse
     * containing a list of EnergyPrice objects
     * @param response the response to parse
     * @return a string representation of a CSV file for the given response
     */
	@SuppressWarnings("unchecked")
	private String retrieveCSV(Response response) {
		
		if(response.getEntity() instanceof String) {
			return response.getEntity().toString();
		}
		if(!(response.getEntity() instanceof Map)) {
			return "Not parseable response: "+response.getEntity().toString();
		}
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
    private boolean checkLengthOfPriceTimeSeries(List<List<RTPrice>> listOfPrices) {
    	int priceLength = listOfPrices.get(0).size();
    	for(List<RTPrice> prices : listOfPrices) {
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
