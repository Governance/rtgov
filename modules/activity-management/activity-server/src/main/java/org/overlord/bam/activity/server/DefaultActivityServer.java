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
package org.overlord.bam.activity.server;

import java.util.UUID;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;
import org.overlord.bam.activity.model.bpm.BPMActivityType;
import org.overlord.bam.activity.model.soa.RPCActivityType;
import org.overlord.bam.activity.server.spi.ActivityNotifier;
import org.overlord.bam.activity.server.spi.ActivityStore;

/**
 * This class represents the default implementation of the activity server.
 *
 */
public class DefaultActivityServer implements ActivityServer {

    @Inject
    private ActivityStore _store=null;
    
    private java.util.List<ActivityNotifier> _notifiers=new java.util.Vector<ActivityNotifier>();
    
    private @Inject @Any Instance<ActivityNotifier> _injectedNotifiers=null;
    
    private boolean _handleDuplicateIds=true;
    
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
     * This method sets whether duplicate ids should be handled.
     * 
     * @param b Whether duplicates should be handled
     */
    public void setHandleDuplicateIds(boolean b) {
        _handleDuplicateIds = b;
    }
    
    /**
     * This method returns whether duplicate ids should be handled.
     * 
     * @return Whether duplicates should be handled
     */
    public boolean getHandleDuplicateIds() {
        return (_handleDuplicateIds);
    }
    
    /**
     * This method stores the supplied activity events.
     * 
     * @param activities The activity events
     * @throws Exception Failed to store the activities
     */
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
        
        int index=0;
        java.util.Set<String> ids=(_handleDuplicateIds ? new java.util.HashSet<String>() : null);
        
        for (ActivityType at : au.getActivityTypes()) {
            
            at.setActivityUnitId(au.getId());
            at.setActivityUnitIndex(index++);
            
            // Check if RPC activity type
            if (at instanceof RPCActivityType) {
 
                if (!_handleDuplicateIds
                        || !ids.contains(((RPCActivityType)at).getMessageId())) {
                    
                    if (_handleDuplicateIds) {
                        ids.add(((RPCActivityType)at).getMessageId());
                    }
                    
                    // Copy message id to context for activity unit
                    au.getContext().add(new Context(Context.MESSAGE_ID, null,
                            ((RPCActivityType)at).getMessageId()));
                }
            } else if (at instanceof BPMActivityType) {
                
                if (!_handleDuplicateIds
                        || !ids.contains(((BPMActivityType)at).getInstanceId())) {
                    
                    if (_handleDuplicateIds) {
                        ids.add(((BPMActivityType)at).getInstanceId());
                    }
                   
                    // Copy instance id to context for activity unit
                    au.getContext().add(new Context(Context.INSTANCE_ID,
                            ((BPMActivityType)at).getProcessType(),
                            ((BPMActivityType)at).getInstanceId()));                   
                }
            }
        }
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
     * This method retrieves a set of activity events associated
     * with the supplied query.
     * 
     * @param query The query
     * @return The list of activities
     * @throws Exception Failed to query the activities
     */
    public java.util.List<ActivityUnit> query(ActivityQuery query) throws Exception {
        
        if (_store == null) {
            throw new Exception("Activity Store is unavailable");
        }
        
        return (_store.query(query));
    }
    
}