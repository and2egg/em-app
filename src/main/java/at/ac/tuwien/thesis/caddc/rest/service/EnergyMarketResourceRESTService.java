package at.ac.tuwien.thesis.caddc.rest.service;

import java.util.Calendar;
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
import org.rosuda.REngine.Rserve.RserveException;

import at.ac.tuwien.thesis.caddc.model.DAPrice;
import at.ac.tuwien.thesis.caddc.model.EnergyMarket;
import at.ac.tuwien.thesis.caddc.model.Location;
import at.ac.tuwien.thesis.caddc.persistence.DAPriceRepository;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketPersistence;
import at.ac.tuwien.thesis.caddc.persistence.EnergyMarketRepository;
import at.ac.tuwien.thesis.caddc.persistence.LocationRepository;
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
