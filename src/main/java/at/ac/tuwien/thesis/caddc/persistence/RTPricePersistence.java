package at.ac.tuwien.thesis.caddc.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.thesis.caddc.model.RTPrice;

/**
 * 
 * @author Andreas Egger
 */
@Stateless
public class RTPricePersistence {

	@Inject
	private EntityManager em;
	
	public void saveRTPrice(RTPrice price) {
    	em.persist(price);
    }
    
    public void saveRTPriceList(List<RTPrice> daPriceList) {
    	for(RTPrice daPrice : daPriceList)
    		saveRTPrice(daPrice);
    }
}
