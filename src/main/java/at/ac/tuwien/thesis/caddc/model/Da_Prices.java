package at.ac.tuwien.thesis.caddc.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import at.ac.tuwien.thesis.caddc.model.key.Da_Prices_Key;

/**
 * 
 * @author Andreas Egger
 */
@SuppressWarnings("serial")
@Entity
@IdClass(Da_Prices_Key.class)
public class Da_Prices implements Serializable {

//	@EmbeddedId
//	private Da_Prices_Key pKey;
	
//	@Id
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date date;
//
//	@Id
////	@ManyToOne
//	private String location;
	
//	@Id
//	private Integer id;

	
	@Id
	@Column(name="bid_date")
	private Date biddingDate;
	@Id
	@ManyToOne
	@Column(name="location")
	private Location location;
	
	/**
	 * price is given multiplied by 100 (no fraction)
	 */
	private Integer price;
	
	@Size(min=1,max=99)
	private Integer interval;
	
	@Size(min=1, max=10)
	@Column(name="interval_unit")
	private String intervalUnit;
	
	@Size(min=-12, max=12)
	@Column(name="time_lag")
	private Integer timelag;
}
