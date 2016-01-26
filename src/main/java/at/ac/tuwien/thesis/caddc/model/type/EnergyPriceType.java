package at.ac.tuwien.thesis.caddc.model.type;

import java.util.Date;

/**
 * 
 */
public class EnergyPriceType {
	
	/**
	 * IntervalType to specify different data sampling intervals
	 * e.g. hourly (1 hour), every 5 minutes (5 minute) ...
	 */
	public enum IntervalType {
		
		HOURLY(1, "hour"),
		FIVE_MINUTE(5, "minute");
		
		private final Integer interval;
		private final String intervalUnit;
		
		IntervalType(Integer interval, String intervalUnit) {
			this.interval = interval;
			this.intervalUnit = intervalUnit;
		}
		
		public Integer interval() { return interval; }
		public String intervalUnit() { return intervalUnit; }
		
	}
	

	private Date date;
	private Integer price;
	private Integer timeLag;


	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}


	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}


	/**
	 * @return the price
	 */
	public Integer getPrice() {
		return price;
	}


	/**
	 * @param price the price to set
	 */
	public void setPrice(Integer price) {
		this.price = price;
	}


	/**
	 * @return the timeLag
	 */
	public Integer getTimeLag() {
		return timeLag;
	}


	/**
	 * @param timeLag the timeLag to set
	 */
	public void setTimeLag(Integer timeLag) {
		this.timeLag = timeLag;
	}
	
}
