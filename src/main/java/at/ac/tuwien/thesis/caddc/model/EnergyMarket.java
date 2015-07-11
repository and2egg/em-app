/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.ac.tuwien.thesis.caddc.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Digits;
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
																"WHERE e.name LIKE :name"),
//		@NamedQuery(name = "EnergyMarket.findLocationsByName", query = "SELECT l " +
//																"FROM EnergyMarket e JOIN e.locations l " +
//																"WHERE l.name = :name"),
//		@NamedQuery(name = "EnergyMarket.findLocationByMarketandName", query = "SELECT l " +
//																"FROM EnergyMarket e JOIN e.locations l " +
//																"WHERE e.name = :marketName "
//																+ "AND l.name = :locationName")
		
//			    "FROM Organization o, User u " +
//			    "JOIN o.roles oRole " +
//			    "JOIN u.roles uRole " +
//			    "WHERE oRole.id = uRole.id AND u.id = :uId")
//		@NamedQuery(name = "Location.findLocationByMarketandName", 
//		query = "SELECT l "
//				+ "FROM EnergyMarket e JOIN Location l "
//				+ "WHERE l.em.id = e.id "
//				+ "AND e.name = :marketName "
//				+ "AND l.name = :locationName")
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
    
//    @OneToMany(fetch=FetchType.EAGER, mappedBy="em")
//    private List<Location> locations;
    

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
    
//   	public List<Location> getLocations() {
//   		return locations;
//   	}
//
//   	public void setLocations(List<Location> locations) {
//   		this.locations = locations;
//   	}
}
