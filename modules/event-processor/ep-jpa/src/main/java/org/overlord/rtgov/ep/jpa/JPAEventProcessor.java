/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ep.jpa;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.jpa.JpaStore;
import org.overlord.rtgov.jpa.JpaStore.JpaWork;

/**
 * This class represents the JPA implementation of the Event
 * Processor.
 *
 */
public class JPAEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(JPAEventProcessor.class.getName());

    private static final String JNDI_PROPERTY = "JPAEventProcessor.jndi.datasource";
	
	private JpaStore _jpaStore;
	
	private String _persistenceUnit = null;
    
    /**
     * This method returns the persistence unit name.
     * 
     * @return The persistence unit name
     */
    public String getPersistenceUnit() {
        return (_persistenceUnit);
    }
    
    /**
     * This method sets the persistence unit name.
     * 
     * @param name The persistence unit name
     */
    public void setPersistenceUnit(String persistenceUnit) {
    	_persistenceUnit = persistenceUnit;
    }
    
    protected void setJpaStore(JpaStore jpaStore) {
    	_jpaStore = jpaStore;
    }
    
    /**
     * {@inheritDoc}
     */
    public java.io.Serializable process(String source,
                final java.io.Serializable event, int retriesLeft) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '"+event+" from source '"+source
                    +"' on JPA Event Processor");
        }
        
        if (_jpaStore == null) {
        	_jpaStore = new JpaStore(_persistenceUnit, JNDI_PROPERTY);
        }

        _jpaStore.withJpa(new JpaWork<Void>() {
			public Void perform(EntityManager em) {
				em.persist(event);
				return null;
			}
		});

        return null;
    }
}
