package at.ac.tuwien.thesis.caddc.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 
 * @author Andreas Egger
 */
@SuppressWarnings("serial")
@Entity
public class Location implements Serializable {

	@Id
    private Long id;

    @NotNull
    @Size(min = 2, max = 30)
    @Pattern(regexp = "[^0-9](\\w+)", message = "Must not use number as first character")
    private String name;

    @ManyToOne
    private EnergyMarket em;
}
