package at.ac.tuwien.thesis.caddc.persistence;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import at.ac.tuwien.thesis.caddc.model.EnergyMarket;

/**
 * 
 * @author Andreas Egger
 */
@Stateless
public class EnergyMarketPersistence {

	@Inject
	private EntityManager em;
	
	public void saveEnergyMarketToDB(EnergyMarket energyMarket) {
    	em.persist(energyMarket);
    }
    
    public void saveEnergyMarketListToDB(List<EnergyMarket> energyMarketList) {
    	for(EnergyMarket energyMarket : energyMarketList)
    		saveEnergyMarketToDB(energyMarket);
    }
}
