package at.ac.tuwien.thesis.caddc.rest.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;

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
    @Path("/tztest")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testTZ(@QueryParam("dateFrom") String dateFrom, @QueryParam("tz") String timeZone) {
    
		String output;
	
		if(dateFrom == null) {
			output = "Please provide a dateFrom parameter in format \"yyyy-MM-dd\" \n";
		}
		else if(timeZone == null) {
    		output = "Please provide a query parameter \"tz\" for the time zone (e.g. tz=Europe/Helsinki) \n";
    	}
    	else {
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    		try {
				Date date = sdf.parse(dateFrom);
				Calendar cal = new GregorianCalendar();
	        	cal.setTime(date);
	        	cal.set(Calendar.HOUR_OF_DAY, 0);
	        	cal.set(Calendar.MINUTE, 0);
	        	cal.set(Calendar.SECOND, 0);
	        	cal.set(Calendar.MILLISECOND, 0);
	    		output = checkForDSTShift(cal, timeZone);
			} catch (ParseException e) {
				output = "Please provide a date in format \"yyyy-MM-dd\"";
			}
    	}
    	return Response.status(200).entity(output).build();
    }
    
    
    private String checkForDSTShift(Calendar cal, String timeZone) {
    	
    	String result = "";
    	
    	TimeZone tz = TimeZone.getTimeZone(timeZone);
    	
    	DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    	formatter.setTimeZone(tz);
    	
    	cal.setTimeZone(tz);
    	
    	result += "TIME: "+cal.getTimeInMillis()+", "+formatter.format(cal.getTime())+", "+tz.getOffset(cal.getTimeInMillis()) + "\n";
    	cal.add(Calendar.HOUR_OF_DAY, 1);
    	result += "TIME: "+cal.getTimeInMillis()+", "+formatter.format(cal.getTime())+", "+tz.getOffset(cal.getTimeInMillis()) + "\n";
    	cal.add(Calendar.HOUR_OF_DAY, 1);
    	result += "TIME: "+cal.getTimeInMillis()+", "+formatter.format(cal.getTime())+", "+tz.getOffset(cal.getTimeInMillis()) + "\n";
    	cal.add(Calendar.HOUR_OF_DAY, 1);
    	result += "TIME: "+cal.getTimeInMillis()+", "+formatter.format(cal.getTime())+", "+tz.getOffset(cal.getTimeInMillis()) + "\n";
    	cal.add(Calendar.HOUR_OF_DAY, 1);
    	result += "TIME: "+cal.getTimeInMillis()+", "+formatter.format(cal.getTime())+", "+tz.getOffset(cal.getTimeInMillis()) + "\n";
    	cal.add(Calendar.HOUR_OF_DAY, 1);
    	result += "TIME: "+cal.getTimeInMillis()+", "+formatter.format(cal.getTime())+", "+tz.getOffset(cal.getTimeInMillis()) + "\n";
    	cal.add(Calendar.HOUR_OF_DAY, 1);
    	result += "TIME: "+cal.getTimeInMillis()+", "+formatter.format(cal.getTime())+", "+tz.getOffset(cal.getTimeInMillis()) + "\n";
    	
    	return result;
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
