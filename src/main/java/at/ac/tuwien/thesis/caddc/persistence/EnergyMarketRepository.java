
package at.ac.tuwien.thesis.caddc.persistence;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import at.ac.tuwien.thesis.caddc.model.EnergyMarket;
import at.ac.tuwien.thesis.caddc.model.Member;

/**
 * Repository for retrieving data from energy markets
 * 
 * Retrieves data from the database by querying the 
 * EnergyMarket table. 
 * The ApplicationScoped annotation means that this object 
 * is created only once for the duration of the application. 
 *
 */
@ApplicationScoped
public class EnergyMarketRepository {

    @Inject
    private EntityManager em;

    public EnergyMarket findById(Long id) {
        return em.find(EnergyMarket.class, id);
    }

    public EnergyMarket findByLocation(String location) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EnergyMarket> criteria = cb.createQuery(EnergyMarket.class);
        Root<EnergyMarket> energyMarket = criteria.from(EnergyMarket.class);
        criteria.select(energyMarket).where(cb.equal(energyMarket.get("location"), location));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<EnergyMarket> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EnergyMarket> criteria = cb.createQuery(EnergyMarket.class);
        Root<EnergyMarket> energyMarket = criteria.from(EnergyMarket.class);
        criteria.select(energyMarket).orderBy(cb.asc(energyMarket.get("name")));
        return em.createQuery(criteria).getResultList();
    }
}
