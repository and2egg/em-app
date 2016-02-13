package at.ac.tuwien.thesis.caddc.service;

import java.util.Date;

import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Stateless;
import javax.inject.Inject;

import at.ac.tuwien.thesis.caddc.model.type.EnergyPriceType;
import at.ac.tuwien.thesis.caddc.rest.service.DAPricesResourceRESTService;
import at.ac.tuwien.thesis.caddc.rest.service.RManagerResourceRESTService;

@Stateless
public class Scheduler {
	
	public static final Long LOCATION_HELSINKI = 1L;
	public static final Long LOCATION_STOCKHOLM = 2L;
	
	@Inject
	DAPricesResourceRESTService daPriceService;
	
	@Inject
	RManagerResourceRESTService rManagerService;

	
//	@Schedules ({
////	    @Schedule(second="0", minute="0", hour="*"),
//	    @Schedule(second="0", minute="0", hour="15", timezone="Europe/Helsinki")
//	})
	public void importData() {
		System.out.println("Helsinki price scheduler active on "+new Date());
		
		daPriceService.importMarketDataPerLocation(LOCATION_HELSINKI, 2015, 2015);
		
		rManagerService.generateModels(EnergyPriceType.DA_TYPE, LOCATION_HELSINKI, 14, "2014-07-07", "2014-07-10", "", true, false, false);
		
		rManagerService.generateAllForecasts(EnergyPriceType.DA_TYPE);
	}
	
//	@Schedule(second="0", minute="0", hour="15", timezone="Europe/Stockholm")
	public void importStockholmPriceData() {
		System.out.println("Stockholm price scheduler active on "+new Date());
		
		daPriceService.importMarketDataPerLocation(LOCATION_STOCKHOLM, 2015, 2015);
		
		rManagerService.generateModels(EnergyPriceType.DA_TYPE, LOCATION_STOCKHOLM, 14, "2014-07-07", "2014-07-10", "", true, false, false);
		
		rManagerService.generateAllForecasts(EnergyPriceType.DA_TYPE);
	}
	
}
