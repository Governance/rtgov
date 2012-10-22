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
package org.overlord.bam.activity.store.mem;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.mvel2.MVEL;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.ActivityStore;
import org.overlord.bam.activity.server.QuerySpec;

/**
 * This class provides the in-memory implementation of the Activity Store.
 *
 */
@Singleton
public class MemActivityStore implements ActivityStore {
    
    private static final Logger LOG=Logger.getLogger(MemActivityStore.class.getName());

    private List<ActivityUnit> _activities=new java.util.ArrayList<ActivityUnit>();
    
    private static final int MAX_ITEMS=1000;
    
    /**
     * This method clears the activity store.
     */
    public void clear() {
        _activities.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store ("+this+") = "+activities);
        }
        
        _activities.addAll(activities);
        
        while (_activities.size() > MAX_ITEMS) {
            _activities.remove(0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {
        for (ActivityUnit au : _activities) {
            if (au.getId().equals(id)) {
                return (au);
            }
        }
        
        return (null);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {
        List<ActivityType> ret=new java.util.ArrayList<ActivityType>();
        
        // Check if query format is supported
        if (!isFormatSupported(query)) {
            throw new java.lang.IllegalArgumentException("Unknown query format");
        }
        
        for (ActivityUnit unit : _activities) {
            for (ActivityType activity : unit.getActivityTypes()) {
                if (evaluate(activity, query)) {
                    ret.add(activity);
                }
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query ("+this+") "+query+" = "+ret);
        }
        
        return (ret);
    }
    
    /**
     * This method determines whether the query format is supported
     * by the activity store.
     * 
     * @param query The query
     * @return Whether the format is supported
     */
    protected static boolean isFormatSupported(QuerySpec query) {
        return (query.getFormat() != null
                && query.getFormat().equalsIgnoreCase("mvel"));
    }

    /**
     * This method evaluates whether the supplied activity
     * passes the supplied predicate.
     * 
     * @param activity The activity
     * @param query The query/predicate
     * @return Whether the activity passes the predicate
     */
    protected static boolean evaluate(ActivityType activity, QuerySpec query) {
        boolean ret=false;
        
        if (query.getFormat() != null
                && query.getFormat().equalsIgnoreCase("mvel")) {
            
            Object result=MVEL.eval(query.getExpression(), activity);
            
            if (result instanceof Boolean) {
                ret = ((Boolean)result).booleanValue();
            }
        }
        
        return (ret);
    }
}
