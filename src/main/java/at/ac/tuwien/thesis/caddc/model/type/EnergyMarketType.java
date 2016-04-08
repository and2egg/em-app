package at.ac.tuwien.thesis.caddc.model.type;

/**
 * 
 */
public class EnergyMarketType {

	public static final Long EMARKET_NORD_POOL = 1L;
	public static final Long EMARKET_BELPEX = 2L;
	public static final Long EMARKET_EPEX_SPOT = 3L;
	public static final Long EMARKET_ISO_NE = 4L;
	public static final Long EMARKET_PJM = 5L;
	
	// 3600000 is the minimum offset for a dst time (one hour in milliseconds)
	public static final Long MIN_DST_TIME = 3600000L;
	
	
	public static boolean isDayAheadMarket(Long id) {
		if(id.equals(EMARKET_NORD_POOL) || id.equals(EMARKET_BELPEX) 
				|| id.equals(EMARKET_EPEX_SPOT) || id.equals(EMARKET_ISO_NE) ) {
			return true;
		}
		return false;
	}
	
	public static boolean isRealTimeMarket(Long id) {
		if(id.equals(EMARKET_ISO_NE) || id.equals(EMARKET_PJM) ) {
			
			return true;
		}
		return false;
	}
	
}
