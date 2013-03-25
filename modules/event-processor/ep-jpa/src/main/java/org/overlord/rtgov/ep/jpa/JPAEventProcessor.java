/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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

    private EntityManager _entityManager=null;
    private EntityManagerFactory _emf=null;
    private String _entityManagerName=null;
    
    @Inject
    private RTGovPropertiesProvider _properties=null;
    
    /**
     * This method returns the entity manager name.
     * 
     * @return The entity manager name
     */
    public String getEntityManagerName() {
        return (_entityManagerName);
    }
    
    /**
     * This method sets the entity manager name.
     * 
     * @param name The entity manager name
     */
    public void setEntityManagerName(String name) {
        _entityManagerName = name;
    }
    
    /**
     * This method returns the entity manager.
     * 
     * @return The entity manager
     */
    protected EntityManager getEntityManager() {
    	return (_entityManager);
    }
    
    /**
     * {@inheritDoc}
     * 
     */
    public void init() {
        
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
                            
                            _emf = Persistence.createEntityManagerFactory(getEntityManagerName(), props);
                    
                        } catch (Throwable e) {
                            LOG.log(Level.SEVERE, MessageFormat.format(
                                    java.util.PropertyResourceBundle.getBundle(
                                            "ep-jpa.Messages").getString("EP-JPA-1"),
                                            getEntityManagerName()), e);
                        }
                    }
                }).get(5000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                                "ep-jpa.Messages").getString("EP-JPA-2"),
                                getEntityManagerName()), e);
            }
        }
            
        if (_entityManager == null) {
            try {
                _entityManager = _emf.createEntityManager();
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("EntityManager '"+getEntityManagerName()+"' created");                
                }
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                                "ep-jpa.Messages").getString("EP-JPA-3"),
                                getEntityManagerName()), e);
            }
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
                    +"' on JPA Event Processor '"+getEntityManagerName()
                    +"'");
        }

        _entityManager.persist(event);

        return (ret);
    }

}
