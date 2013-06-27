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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.overlord.rtgov.common.util.RTGovPropertiesProvider;
import org.overlord.rtgov.ep.EventProcessor;

/**
 * This class represents the JPA implementation of the Event
 * Processor.
 *
 */
public class JPAEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(JPAEventProcessor.class.getName());

    private EntityManager _em=null;
    private EntityManagerFactory _emf=null;
    private String _entityManager=null;
    
    @Inject
    private RTGovPropertiesProvider _properties=null;
    
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
        return (_em);
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public void init() throws Exception {
        
        if (_emf == null) {
            try {
                // BAM-120 Use separate thread as causes problem when hibernate creates
                // the schema within a transaction scope
                Executors.newSingleThreadExecutor().submit(new Runnable() {
                    public void run() {                    
                        java.util.Properties props=null;
                        
                        try {
                            if (_properties != null) {
                                props = _properties.getProperties();
                            }
                            
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
                    }
                }).get(5000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                                "ep-jpa.Messages").getString("EP-JPA-2"),
                                getEntityManager()), e);
            }
        }
            
        if (_em == null) {
            try {
                _em = _emf.createEntityManager();
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("EntityManager '"+getEntityManager()+"' created");                
                }
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                                "ep-jpa.Messages").getString("EP-JPA-3"),
                                getEntityManager()), e);
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("JPAEventProcessor init: entity manager="+_entityManager+" em="+_em);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public java.io.Serializable process(String source,
                java.io.Serializable event, int retriesLeft) throws Exception {
        java.io.Serializable ret=null;
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '"+event+" from source '"+source
                    +"' on JPA Event Processor '"+getEntityManager()
                    +"'");
        }

        _em.persist(event);

        return (ret);
    }

}
