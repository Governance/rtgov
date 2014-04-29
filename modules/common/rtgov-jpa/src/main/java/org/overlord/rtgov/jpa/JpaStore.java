/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.jpa;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * Provides common JPA functionality to all data stores.
 * 
 * @author Brett Meyer
 */
public class JpaStore {
	private static final String JPA_DATASOURCE = "javax.persistence.jtaDataSource";
	
	private String _persistenceUnit;
	
	private String _jndiProperty;
    
    private EntityManagerFactory _entityManagerFactory = null;
    
    public JpaStore(String persistenceUnit, String jndiProperty) {
    	_persistenceUnit = persistenceUnit;
    	_jndiProperty = jndiProperty;
    }
    
    public JpaStore(EntityManagerFactory entityManagerFactory) {
    	_entityManagerFactory = entityManagerFactory;
    }
    
    /**
     * This method returns an entity manager.
     * 
     * @return The entity manager
     */
    private EntityManager getEntityManager() {
        if (_entityManagerFactory == null) {
        	final Properties properies = RTGovProperties.getProperties();
        	properies.setProperty(JPA_DATASOURCE, RTGovProperties.getProperty(_jndiProperty));
            _entityManagerFactory = Persistence.createEntityManagerFactory(_persistenceUnit, properies);
        }

        return _entityManagerFactory.createEntityManager();
    }
    
    public <T> T withJpa(JpaWork<T> work) {
		final EntityManager em = getEntityManager();
    	try {
        	em.getTransaction().begin();
            final T result = work.perform(em);
            em.getTransaction().commit();
            return result;
        } finally {
            em.close();
        }
    }
    
    /**
     * Perform work using a provided JPA EntityManager
     * 
     * @param <T>
     */
    public static interface JpaWork<T> {
		/**
		 * The work to be performed using the EntityManager
		 *
		 * @return The result of the work
		 */
		public T perform(EntityManager em);
	}
}
