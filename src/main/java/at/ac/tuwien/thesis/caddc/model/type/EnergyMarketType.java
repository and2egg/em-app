package at.ac.tuwien.thesis.caddc.model.type;

/**
 * 
 */
public class EnergyMarketType {

	public static final Long EMARKET_NORD_POOL = 1L;
	public static final Long EMARKET_ISO_NE = 2L;
	public static final Long EMARKET_BELPEX = 3L;
	public static final Long EMARKET_EPEX_SPOT = 4L;
	public static final Long EMARKET_PJM = 5L;
	
	// 3600000 is the minimum offset for a dst time (one hour in milliseconds)
	public static final Long MIN_DST_TIME = 3600000L;
}
