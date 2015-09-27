package at.ac.tuwien.thesis.caddc.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


/**
 * Entity wrapper for energy markets
 * @author Andreas
 *
 */
@SuppressWarnings("serial")
@Entity
@NamedQueries({
		@NamedQuery(name="EnergyMarket.findAll", query="SELECT e FROM EnergyMarket e " +
														"ORDER BY name ASC"),
		@NamedQuery(name = "EnergyMarket.findByName", query = "SELECT e FROM EnergyMarket e " +
																"WHERE e.name LIKE :name")
})

public class EnergyMarket implements Serializable {
	
	@Id
	@GeneratedValue
    private Long id;

    @NotNull
    @Column(unique=true)
    @Size(min = 2, max = 40, message = "Please provide a name of length between 2 and 40 characters")
    @Pattern(regexp = "[^0-9][\\s\\w]+", message = "Must not use number as first character")
    private String name;

    private String description;    

	public EnergyMarket() {
    	
    }
    
    public EnergyMarket(Long id, String name, String description) {
    	this.id = id;
    	this.name = name;
    	this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
