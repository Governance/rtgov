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

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.ep.EventProcessor;

/**
 * This class represents the JPA implementation of the Event
 * Processor.
 *
 */
public class JPAEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(JPAEventProcessor.class.getName());

    private EntityManagerFactory _emf=null;
    private EntityManager _em=null;
    private String _entityManager=null;
    
    /**
     * This method returns the entity manager name.
     * 
     * @return The entity manager name
     */
    public String getEntityManager() {
        return (_entityManager);
    }
    
    /**
     * This method sets the entity manager name.
     * 
     * @param name The entity manager name
     */
    public void setEntityManager(String name) {
        _entityManager = name;
    }
    
    /**
     * This method returns the entity manager.
     * 
     * @return The entity manager
     */
    protected EntityManager getEntityMgr() {
        return (_em == null ? _emf.createEntityManager() : _em);
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public void init() throws Exception {
        
        if (_emf == null) {
            java.util.Properties props=RTGovProperties.getProperties();
            
            try {
                 if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Properties passed to entity manager factory creation: "+props);                
                }
                
                _emf = Persistence.createEntityManagerFactory(getEntityManager(), props);
        
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                                "ep-jpa.Messages").getString("EP-JPA-1"),
                                getEntityManager()), e);
            }

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("JPAEventProcessor init: entity manager="+_entityManager+" emf="+_emf);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public java.io.Serializable process(String source,
                java.io.Serializable event, int retriesLeft) throws Exception {
        java.io.Serializable ret=null;
        
        EntityManager em=getEntityMgr();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '"+event+" from source '"+source
                    +"' on JPA Event Processor '"+em
                    +"'");
        }

        try {
            em.persist(event);
        } finally {
            closeEntityManager(em);
        }

        return (ret);
    }

    /**
     * This method closes the supplied entity manager.
     * 
     * @param em The entity manager
     */
    protected void closeEntityManager(EntityManager em) {
        if (em != _em) {
            em.close();
        }
    }
    
    /**
     * This method sets the entity manager.
     * 
     * @param em The entity manager
     */
    public void setEntityMgr(EntityManager em) {
        _em = em;
    }
}
