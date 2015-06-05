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
package org.jboss.tools.examples.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

import org.jboss.tools.examples.model.EnergyMarket;
import org.jboss.tools.examples.model.Member;

/**
 * Repository for retrieving data from energy markets
 * 
 * Retrieves data from the database by querying the 
 * EnergyMarket table. 
 * The ApplicationScoped annotation means that this object 
 * is created only once for the duration of the application. 
 *
 */
@ApplicationScoped
public class EnergyMarketRepository {

    @Inject
    private EntityManager em;

    public EnergyMarket findById(Long id) {
        return em.find(EnergyMarket.class, id);
    }

    public EnergyMarket findByLocation(String location) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EnergyMarket> criteria = cb.createQuery(EnergyMarket.class);
        Root<EnergyMarket> energyMarket = criteria.from(EnergyMarket.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(member).where(cb.equal(member.get(Member_.email), email));
        criteria.select(energyMarket).where(cb.equal(energyMarket.get("location"), location));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<EnergyMarket> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EnergyMarket> criteria = cb.createQuery(EnergyMarket.class);
        Root<EnergyMarket> energyMarket = criteria.from(EnergyMarket.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(energyMarket).orderBy(cb.asc(energyMarket.get(EnergyMarket_.name)));
        criteria.select(energyMarket).orderBy(cb.asc(energyMarket.get("name")));
        return em.createQuery(criteria).getResultList();
    }
}
