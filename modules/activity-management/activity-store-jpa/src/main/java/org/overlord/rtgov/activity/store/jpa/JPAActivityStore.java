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
package org.overlord.rtgov.activity.store.jpa;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;
import org.overlord.rtgov.common.util.RTGovPropertiesProvider;

/**
 * This class provides the JPA implementation of the Activity Store.
 *
 */
@Singleton
public class JPAActivityStore implements ActivityStore {
    
    @Inject
    private RTGovPropertiesProvider _properties;
    
    private EntityManager _entityManager;
    
    private EntityManagerFactory _entityManagerFactory=null;
        
    private static final Logger LOG=Logger.getLogger(JPAActivityStore.class.getName());
    
    /**
     * Initialize the activity store.
     */
    @PostConstruct
    public void init() {
        _entityManagerFactory = Persistence.createEntityManagerFactory("overlord-rtgov-activity");
    }
    
    /**
     * This method returns an entity manager.
     * 
     * @return The entity manager
     */
    protected EntityManager getEntityManager() {
        return (_entityManager == null ? _entityManagerFactory.createEntityManager() : _entityManager);
    }
    
    /**
     * This method closes the supplied entity manager.
     * 
     * @param em The entity manager
     */
    protected void closeEntityManager(EntityManager em) {
        if (em != _entityManager) {
            em.close();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store="+new String(ActivityUtil.serializeActivityUnitList(activities)));
        }
        
        EntityManager em=getEntityManager();
        
        try {
            for (int i=0; i < activities.size(); i++) {
                em.persist(activities.get(i));
            }
        } finally {
            closeEntityManager(em);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get Activity Unit="+id);
        }

        EntityManager em=getEntityManager();
        
        ActivityUnit ret=null;
        
        try {
            ret=(ActivityUnit)em.createQuery("SELECT au FROM ActivityUnit au "
                                +"WHERE au.id = '"+id+"'")
                                .getSingleResult();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("ActivityUnit id="+id+" Result="
                        +new String(ActivityUtil.serializeActivityUnit(ret)));
            }
        } finally {
            closeEntityManager(em);
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public java.util.List<ActivityType> getActivityTypes(Context context,
                    long from, long to) throws Exception {
        List<ActivityType> ret=null;
        
        EntityManager em=getEntityManager();
        
        try {
            if (from == 0 && to == 0) {
                ret = (List<ActivityType>)
                    em.createQuery("SELECT at from ActivityType at "
                            +"JOIN at.context ctx "
                            +"WHERE ctx.value = '"+context.getValue()+"' "
                            +"AND ctx.type = '"+context.getType().name()+"'")
                            .getResultList();
                
            } else {            
                ret = (List<ActivityType>)em.createQuery("SELECT at from ActivityType at "
                        +"JOIN at.context ctx "
                        +"WHERE ctx.value = '"+context.getValue()+"' "
                        +"AND ctx.type = '"+context.getType().name()+"' "
                        +"AND at.timestamp >= "+from+" "
                        +"AND at.timestamp <= "+to)
                        .getResultList();
            }
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("ActivityTypes context '"+context+"' from="+from+" to="+to+" Result="
                        +new String(ActivityUtil.serializeActivityTypeList(ret)));
            }
        } finally {
            closeEntityManager(em);
        }

        return (ret);        
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        return (getActivityTypes(context, 0, 0));
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query="+query);
        }
        
        if (query.getFormat() == null || !query.getFormat().equalsIgnoreCase("jpql")) {
            throw new IllegalArgumentException(MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "activity-store-jpa.Messages").getString("ACTIVITY-STORE-JPA-1"),
                    (query.getFormat() == null ? "" : query.getFormat())));
        }
        
        if (query.getExpression() == null || !query.getExpression().toLowerCase().startsWith("select ")) {
            throw new IllegalArgumentException(java.util.PropertyResourceBundle.getBundle(
                    "activity-store-jpa.Messages").getString("ACTIVITY-STORE-JPA-2"));
        }

        return (query(query.getExpression()));
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
    @SuppressWarnings("unchecked")
    public List<ActivityType> query(String query) throws Exception {

        EntityManager em=getEntityManager();
        
        List<ActivityType> ret=null;
        
        try {
            ret = (List<ActivityType>)
                    em.createQuery(query).getResultList();
        
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Query="+query+" Result="
                        +new String(ActivityUtil.serializeActivityTypeList(ret)));
            }
        } finally {
            closeEntityManager(em);
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
        EntityManager em=getEntityManager();
        
        try {
            // Cascading delete is not working from activity unit to activity types,
            // so resorting to native SQL for now to delete an activity unit and its
            // associated components
            em.createNativeQuery("DELETE FROM RTGOV_ACTIVITY_CONTEXT WHERE unitId = '"
                            +au.getId()+"'").executeUpdate();
    
            em.createNativeQuery("DELETE FROM RTGOV_ACTIVITY_PROPERTIES WHERE unitId = '"
                            +au.getId()+"'").executeUpdate();
    
            em.createNativeQuery("DELETE FROM RTGOV_ACTIVITIES WHERE unitId = '"
                            +au.getId()+"'").executeUpdate();
            
            em.createNativeQuery("DELETE FROM RTGOV_ACTIVITY_UNITS WHERE id = '"
                            +au.getId()+"'").executeUpdate();
            
            em.flush();
            em.clear();
        } finally {
            closeEntityManager(em);
        }
    }
    
    /**
     * This method sets the entity manager.
     * 
     * @param entityManager The entity manager
     */
    public void setEntityManager(EntityManager entityManager) {
        _entityManager = entityManager;
    }
   
}
