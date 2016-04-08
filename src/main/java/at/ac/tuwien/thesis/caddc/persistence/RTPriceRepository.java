
package at.ac.tuwien.thesis.caddc.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

import at.ac.tuwien.thesis.caddc.model.RTPrice;

/**
 * <p>
 * Repository for retrieving rt price data from energy markets
 * </p>
 * Retrieves data from the database by querying the 
 * EnergyMarket table. 
 * The ApplicationScoped annotation means that this object 
 * is created only once for the entire duration of the application. 
 * @author Andreas Egger
 */
@ApplicationScoped
public class RTPriceRepository {

    @Inject
    private EntityManager em;

    public RTPrice findById(Long id) {
    	// retrieve energy market by id (primary key)
        return em.find(RTPrice.class, id);
    }

    public List<RTPrice> findAll() {
    	TypedQuery<RTPrice> q = em.createNamedQuery("RTPrice.findAll", RTPrice.class);
    	return q.getResultList();
    }
    
    public List<RTPrice> findByDate(Date startDate, Date endDate) {
    	TypedQuery<RTPrice> q = em.createNamedQuery("RTPrice.findByDate", RTPrice.class);
    	q.setParameter("startDate", startDate);
    	q.setParameter("endDate", endDate);
    	return q.getResultList();
    }
    
    public List<RTPrice> findByDateAndLocation(Date startDate, Date endDate, Long locationId) {
    	TypedQuery<RTPrice> q = em.createNamedQuery("RTPrice.findByDateAndLocation", RTPrice.class);
    	q.setParameter("startDate", startDate);
    	q.setParameter("endDate", endDate);
    	q.setParameter("locationId", locationId);
    	return q.getResultList();
    }
    
    public Date findMaxDate(Long locationId) {
    	TypedQuery<Date> q = em.createNamedQuery("RTPrice.findMaxDate", Date.class);
    	q.setParameter("locationId", locationId);
    	return q.getSingleResult();
    }
    
    
}
