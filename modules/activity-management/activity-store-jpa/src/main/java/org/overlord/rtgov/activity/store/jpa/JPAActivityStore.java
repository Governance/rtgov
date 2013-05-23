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
package org.overlord.rtgov.activity.store.jpa;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
    @PersistenceContext(unitName="overlord-rtgov-activity")
    private EntityManager _entityManager;
        
    private static final Logger LOG=Logger.getLogger(JPAActivityStore.class.getName());
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store="+new String(ActivityUtil.serializeActivityUnitList(activities)));
        }
        for (ActivityUnit au : activities) {
            _entityManager.persist(au);
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
                _entityManager.createQuery("SELECT au FROM ActivityUnit au "
                            +"WHERE au.id = '"+id+"'")
                            .getSingleResult();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ActivityUnit id="+id+" Result="
                    +new String(ActivityUtil.serializeActivityUnit(ret)));
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
        
        if (from == 0 && to == 0) {
            ret = (List<ActivityType>)
                _entityManager.createQuery("SELECT at from ActivityType at "
                        +"JOIN at.context ctx "
                        +"WHERE ctx.value = '"+context.getValue()+"' "
                        +"AND ctx.type = '"+context.getType().name()+"'")
                        .getResultList();
            
        } else {            
            ret = (List<ActivityType>)_entityManager.createQuery("SELECT at from ActivityType at "
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
    public List<ActivityType> query(String query) throws Exception {

        @SuppressWarnings("unchecked")
        List<ActivityType> ret=(List<ActivityType>)
            _entityManager.createQuery(query)
                .getResultList();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query="+query+" Result="
                    +new String(ActivityUtil.serializeActivityTypeList(ret)));
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
     * This method sets the entity manager.
     * 
     * @param entityManager The entity manager
     */
    public void setEntityManager(EntityManager entityManager) {
        _entityManager = entityManager;
    }
   
}
