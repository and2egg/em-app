
package at.ac.tuwien.thesis.caddc.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

import at.ac.tuwien.thesis.caddc.model.DAPrice;

/**
 * <p>
 * Repository for retrieving da price data from energy markets
 * </p>
 * Retrieves data from the database by querying the 
 * EnergyMarket table. 
 * The ApplicationScoped annotation means that this object 
 * is created only once for the entire duration of the application. 
 * @author Andreas Egger
 */
@ApplicationScoped
public class DAPriceRepository {

    @Inject
    private EntityManager em;

    public DAPrice findById(Long id) {
    	// retrieve energy market by id (primary key)
        return em.find(DAPrice.class, id);
    }

    public List<DAPrice> findAll() {
    	TypedQuery<DAPrice> q = em.createNamedQuery("DAPrice.findAll", DAPrice.class);
    	return q.getResultList();
    }
    
    public List<DAPrice> findByDate(Date startDate, Date endDate) {
    	TypedQuery<DAPrice> q = em.createNamedQuery("DAPrice.findByDate", DAPrice.class);
    	q.setParameter("startDate", startDate);
    	q.setParameter("endDate", endDate);
    	return q.getResultList();
    }
    
    public List<DAPrice> findByDateAndLocation(Date startDate, Date endDate, Long locationId) {
    	TypedQuery<DAPrice> q = em.createNamedQuery("DAPrice.findByDateAndLocation", DAPrice.class);
    	q.setParameter("startDate", startDate);
    	q.setParameter("endDate", endDate);
    	q.setParameter("locationId", locationId);
    	return q.getResultList();
    }
    
    public Date findMaxDate(Long locationId) {
    	TypedQuery<Date> q = em.createNamedQuery("DAPrice.findMaxDate", Date.class);
    	q.setParameter("locationId", locationId);
    	return q.getSingleResult();
    }
    
    
}
