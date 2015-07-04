package at.ac.tuwien.thesis.caddc.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import at.ac.tuwien.thesis.caddc.model.key.Da_Prices_Key;
import at.ac.tuwien.thesis.caddc.model.key.Rt_Prices_Key;

/**
 * 
 * @author Andreas Egger
 */
@SuppressWarnings("serial")
@Entity
@Table(
    uniqueConstraints=
        @UniqueConstraint(columnNames={"bid_date", "location_id"})
)
public class Rt_Prices implements Serializable {

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
	
	@Size(min=1,max=99)
	private Integer interval;
	
	@Size(min=1, max=10)
	@Column(name="interval_unit")
	private String intervalUnit;
	
	@Size(min=-12, max=12)
	@Column(name="time_lag")
	private Integer timelag;
}
