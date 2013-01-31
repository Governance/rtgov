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
package org.overlord.bam.activity.store.jpa;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.ActivityStore;
import org.overlord.bam.activity.server.QuerySpec;
import org.overlord.bam.common.util.BAMPropertiesProvider;
//import org.overlord.bam.activity.server.ActivityStore;
//import org.overlord.bam.activity.server.QuerySpec;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * This class provides the JPA implementation of the Activity Store.
 *
 */
public class JPAActivityStore implements ActivityStore {

    private static final String EMF_NAME = "overlord-bam-activity";

    private static final Logger LOG=Logger.getLogger(JPAActivityStore.class.getName());
    
    private EntityManager _entityManager=null;
    private EntityManagerFactory _emf=null;
    private String _entityManagerName=EMF_NAME;
    
    @Inject
    private BAMPropertiesProvider _properties=null;
    
    /**
     * This is the default constructor for the JPA activity store.
     */
    public JPAActivityStore() {
    }
    
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
     * This method initializes the activity store.
     * 
     */
    @PostConstruct
    public void init() {
        java.util.Properties props=null;
        
        try {
            if (_properties != null) {
                props = _properties.getProperties();
            }
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Properties passed to entity manager factory creation: "+props);                
            }
            
            _emf = Persistence.createEntityManagerFactory(_entityManagerName, props);
    
            _entityManager = _emf.createEntityManager();
            
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, "Failed to create entity manager", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store="+activities);
        }
        
        boolean localtxn=false;
        
        if (!_entityManager.getTransaction().isActive()) {
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Beginning a local transaction");
            }
           
            _entityManager.getTransaction().begin();
            
            localtxn = true;
        }
        
        for (ActivityUnit au : activities) {
            _entityManager.persist(au);
        }
        
        _entityManager.flush();
        
        if (localtxn) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Committing a local transaction");
            }
            _entityManager.getTransaction().commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {  
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get Activity Unit="+id);
        }

        ActivityUnit ret=(ActivityUnit)
                _entityManager.createQuery("SELECT au FROM ActivityUnit au")
                .getSingleResult();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ActivityUnit id="+id+" Result="+ret);
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {  
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query="+query);
        }

        return (query(query.getExpression()));
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(String context) throws Exception {
        
        @SuppressWarnings("unchecked")
        List<ActivityType> ret=(List<ActivityType>)
                _entityManager.createQuery("SELECT at from ActivityType at "
                        +"JOIN at.context ctx "
                        +"WHERE ctx.value = '"+context+"'")
                .getResultList();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ActivityTypes context '"+context+"' Result="+ret);
        }

        return (ret);
    }
    
    /**
     * This method performs the query associated with the supplied
     * query expression, returning the results as a list of activity
     * types.
     * 
     * @param query The query expression
     * @return The list of activity types
     * @throws Exception Failed to perform query
     */
    public List<ActivityType> query(String query) throws Exception {
        
        @SuppressWarnings("unchecked")
        List<ActivityType> ret=(List<ActivityType>)
                _entityManager.createQuery(query)
                .getResultList();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query="+query+" Result="+ret);
        }

        return (ret);
    }
    
    /**
     * This method removes the supplied activity unit.
     * 
     * @param au The activity unit
     * @throws Exception Failed to remove activity unit
     */
    public void remove(ActivityUnit au) throws Exception {
        _entityManager.remove(au);
    }
    
    /**
     * This method closes the entity manager.
     */
    @PreDestroy
    public void close() {
        if (_entityManager != null) {
            _entityManager.close();
        }
        if (_emf != null) {
            _emf.close();
        }
    }
}
