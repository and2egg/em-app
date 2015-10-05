
package at.ac.tuwien.thesis.caddc.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

import at.ac.tuwien.thesis.caddc.model.EnergyMarket;
import at.ac.tuwien.thesis.caddc.model.Location;

/**
 * <p>
 * Repository for retrieving data from energy markets
 * </p>
 * Retrieves data from the database by querying the 
 * EnergyMarket table. 
 * The ApplicationScoped annotation means that this object 
 * is created only once for the entire duration of the application. 
 */
@ApplicationScoped
public class EnergyMarketRepository {

    @Inject
    private EntityManager em;

    public EnergyMarket findById(Long id) {
    	// retrieve energy market by id (primary key)
        return em.find(EnergyMarket.class, id);
    }
    
    public EnergyMarket findByName(String name) {
    	TypedQuery<EnergyMarket> q = em.createNamedQuery("EnergyMarket.findByName", EnergyMarket.class);
    	q.setParameter("name", name);
    	EnergyMarket eMarket = null;
		try {
		  	eMarket = q.getSingleResult();
		} catch(NoResultException nrEx) {
		  	// eMarket = null;
		}
    	return eMarket;
    }
    
    public List<Location> findLocationsByName(String name) {
    	TypedQuery<Location> q = em.createNamedQuery("EnergyMarket.findLocationsByName", Location.class);
    	return q.getResultList();
    }

    public List<EnergyMarket> findAllOrderedByName() {
    	TypedQuery<EnergyMarket> q = em.createNamedQuery("EnergyMarket.findAll", EnergyMarket.class);
    	return q.getResultList();
    }
    
}
