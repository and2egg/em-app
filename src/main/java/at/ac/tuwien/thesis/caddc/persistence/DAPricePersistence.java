package at.ac.tuwien.thesis.caddc.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.thesis.caddc.model.DAPrice;

/**
 * 
 * @author Andreas Egger
 */
@Stateless
public class DAPricePersistence {

	@Inject
	private EntityManager em;
	
	public void saveDAPrice(DAPrice price) {
    	em.persist(price);
    }
    
    public void saveDAPriceList(List<DAPrice> daPriceList) {
    	for(DAPrice daPrice : daPriceList)
    		saveDAPrice(daPrice);
    }
}
