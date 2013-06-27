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
package org.overlord.rtgov.activity.server.impl;

import java.util.List;
import java.util.UUID;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityNotifier;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;

/**
 * This class represents the default implementation of the activity server.
 *
 */
@Singleton
@Transactional
public class ActivityServerImpl implements ActivityServer {

    @Inject
    private ActivityStore _store=null;
    
    private java.util.List<ActivityNotifier> _notifiers=new java.util.Vector<ActivityNotifier>();
    
    private @Inject @Any Instance<ActivityNotifier> _injectedNotifiers=null;
    
    /**
     * This method sets the activity store.
     * 
     * @param store The activity store
     */
    public void setActivityStore(ActivityStore store) {
        _store = store;
    }
    
    /**
     * This method gets the activity store.
     * 
     * @return The activity store
     */
    public ActivityStore getActivityStore() {
        return (_store);
    }
    
    /**
     * This method gets the list of activity notifiers.
     * 
     * @return The activity notifiers
     */
    public java.util.List<ActivityNotifier> getActivityNotifiers() {
        return (_notifiers);
    }
    
    /**
     * This method stores the supplied activity events.
     * 
     * @param activities The activity events
     * @throws Exception Failed to store the activities
     */
    @TransactionAttribute(value= TransactionAttributeType.REQUIRED)
    public void store(java.util.List<ActivityUnit> activities) throws Exception {
        
        // Process activity units to establish consistent id info
        for (ActivityUnit au : activities) {
            processActivityUnit(au);
        }
        
        // Store the activities
        if (_store != null) {
            _store.store(activities);
        } else {
            throw new Exception("Activity Store is unavailable");
        }
        
        // Inform registered notifiers
        for (ActivityNotifier notifier : _notifiers) {
            notifier.notify(activities);
        }
        
        if (_injectedNotifiers != null) {
            for (ActivityNotifier notifier : _injectedNotifiers) {
                notifier.notify(activities);
            }
        }
    }
    
    /**
     * This method processes the activity unit to establish consistent
     * context and id information across the activity unit and contained
     * activity types.
     * 
     * @param au The activity unit
     */
    protected void processActivityUnit(ActivityUnit au) {
        
        // Check that activity unit has an id - and if not, create a
        // globally unique id
        if (au.getId() == null) {
            au.setId(createUniqueId());
        }

        au.init();
    }
    
    /**
     * This method creates a globally unique id for an activity unit.
     * 
     * @return The globally unique id
     */
    protected String createUniqueId() {
        return (UUID.randomUUID().toString());
    }
    
    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(value= TransactionAttributeType.REQUIRED)
    public ActivityUnit getActivityUnit(String id) throws Exception {
        
        if (_store == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        return (_store.getActivityUnit(id));
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(value= TransactionAttributeType.REQUIRED)
    public java.util.List<ActivityType> query(QuerySpec query) throws Exception {
        
        if (_store == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        return (_store.query(query));
    }

    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(value= TransactionAttributeType.REQUIRED)
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        
        if (_store == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        return (_store.getActivityTypes(context));
    }
    
    /**
     * {@inheritDoc}
     */
    @TransactionAttribute(value= TransactionAttributeType.REQUIRED)
    public List<ActivityType> getActivityTypes(Context context, long from, long to) throws Exception {
        
        if (_store == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        return (_store.getActivityTypes(context, from, to));
    }
    
}
