package at.ac.tuwien.thesis.caddc.model.key;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import at.ac.tuwien.thesis.caddc.model.Location;

/**
 * 
 * @author Andreas Egger
 */
public class Rt_Prices_Key implements Serializable {

	private Date biddingDate;
	private Location location;
}
