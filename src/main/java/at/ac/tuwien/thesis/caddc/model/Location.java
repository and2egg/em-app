package at.ac.tuwien.thesis.caddc.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 
 * @author Andreas Egger
 */
@NamedQueries({
@NamedQuery(name = "Location.findLocationByMarketandName", 
			query = "SELECT l " +
					"FROM Location l JOIN l.em e " +
					"WHERE e.name LIKE :marketName AND " +
					"l.name LIKE :locationName")
})
@SuppressWarnings("serial")
@Entity
public class Location implements Serializable {

	@Id
	@GeneratedValue
    private Long id;

    @NotNull
    @Column(unique=true)
    @Size(min = 2, max = 40, message = "Please provide a name of length between 2 and 40 characters")
    @Pattern(regexp = "[^0-9](\\w+)", message = "Must not use number as first character")
    private String name;

    @ManyToOne
    private EnergyMarket em;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the em
	 */
	public EnergyMarket getEm() {
		return em;
	}

	/**
	 * @param em the em to set
	 */
	public void setEm(EnergyMarket em) {
		this.em = em;
	}
	
	@Override
	public String toString() {
		return "Location: "+
				"id="+this.getId()+
				", name="+this.getName()+
				", Energy Market="+this.getEm().getName();
	}
}
