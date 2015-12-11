package at.ac.tuwien.thesis.caddc.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * 
 * @author Andreas Egger
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
	@NamedQuery(name="DAPrice.findAll", query="SELECT p FROM DAPrice p " +
													"ORDER BY id ASC"),
	@NamedQuery(name="DAPrice.findByDate", query="SELECT p FROM DAPrice p "
									+ "WHERE p.biddingDate BETWEEN :startDate AND :endDate "
									+ "ORDER BY p.biddingDate"),
	@NamedQuery(name="DAPrice.findByDateAndLocation", query="SELECT p FROM DAPrice p "
									+ "WHERE p.biddingDate BETWEEN :startDate AND :endDate "
									+ "AND p.location.id = :locationId "
									+ "ORDER BY p.biddingDate"),
	@NamedQuery(name="DAPrice.findMaxDate", query="SELECT MAX(p.biddingDate) FROM DAPrice p")
})
@Table(
	name="DA_PRICES"
)
public class DAPrice implements Serializable {

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(name="bid_date")
	private Date biddingDate;
	
	@NotNull
	@ManyToOne
	private Location location;
	
	/**
	 * price is given multiplied by 100 (no fraction)
	 */
	@NotNull
	private Integer price;
	
	private Integer interval;
	
	@Size(min=1, max=10)
	@Column(name="interval_unit")
	private String intervalUnit;
	
	@Column(name="time_lag")
	private Integer timelag;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the biddingDate
	 */
	public Date getBiddingDate() {
		return biddingDate;
	}

	/**
	 * @param biddingDate the biddingDate to set
	 */
	public void setBiddingDate(Date biddingDate) {
		this.biddingDate = biddingDate;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
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
	 * @return the interval
	 */
	public Integer getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	/**
	 * @return the intervalUnit
	 */
	public String getIntervalUnit() {
		return intervalUnit;
	}

	/**
	 * @param intervalUnit the intervalUnit to set
	 */
	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}

	/**
	 * @return the timelag
	 */
	public Integer getTimelag() {
		return timelag;
	}

	/**
	 * @param timelag the timelag to set
	 */
	public void setTimelag(Integer timelag) {
		this.timelag = timelag;
	}
	
	@Override
	public String toString() {
		return "DA PRICE: "+
				"id="+this.getId()+", bid date="+this.getBiddingDate()+
				", price="+this.getPrice()+", location="+this.getLocation().getName()+
				", interval="+this.getInterval()+", intervalUnit="+this.getIntervalUnit()+
				", timelag="+this.getTimelag();
	}
	
}
