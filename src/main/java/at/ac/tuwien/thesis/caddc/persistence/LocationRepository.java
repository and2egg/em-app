
package at.ac.tuwien.thesis.caddc.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import at.ac.tuwien.thesis.caddc.model.Location;

/**
 * <p>
 * Repository for retrieving data from registered locations 
 * within energy markets
 * </p>
 * @author Andreas Egger
 */
@ApplicationScoped
public class LocationRepository {

	@Inject
    private EntityManager em;

    public Location findById(Long id) {
        return em.find(Location.class, id);
    }
    
    public Location findByMarketandName(String marketName, String locationName) {
    	TypedQuery<Location> q = em.createNamedQuery("Location.findLocationByMarketandName", Location.class);
    	q.setParameter("marketName", marketName);
    	q.setParameter("locationName", locationName);
    	Location loc = null;
		try {
			loc = q.getSingleResult();
		} catch(NoResultException nrEx) {
		  	// eMarket = null;
		}
    	return loc;
    }
    
    public Location findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> criteria = cb.createQuery(Location.class);
        Root<Location> location = criteria.from(Location.class);
        criteria.select(location).where(cb.equal(location.get("name"), name));
        Location loc = null;
        try {
        	loc = em.createQuery(criteria).getSingleResult();
        	return loc;
        } catch(NoResultException nrEx) {
        	return null;
        }
    }

    public List<Location> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Location> criteria = cb.createQuery(Location.class);
        Root<Location> location = criteria.from(Location.class);
        criteria.select(location).orderBy(cb.asc(location.get("name")));
        return em.createQuery(criteria).getResultList();
    }
    
}
