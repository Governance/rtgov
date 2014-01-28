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
package org.overlord.rtgov.activity.store.mem;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Alternative;
import javax.inject.Singleton;

import org.mvel2.MVEL;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;

/**
 * This class provides the in-memory implementation of the Activity Store.
 *
 */
@Singleton
@Alternative
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
            LOG.finest("Store ("+this+") = "
                    +new String(ActivityUtil.serializeActivityUnitList(activities)));
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
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityUnit[id="+id+"] ("+this+")");
        }

        for (ActivityUnit au : _activities) {
            if (au.getId().equals(id)) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("getActivityUnit[id="+id+"] ("+this+") ret="+au);
                }
                return (au);
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityUnit[id="+id+"] ("+this
                    +") Did not find unit for id="+id);
        }

        return (null);
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
    public List<ActivityType> getActivityTypes(Context context, long from,
            long to) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityTypes[context="+context+"] ("+this+",from="+from+",to="+to+")");
        }

        List<ActivityType> ret=new java.util.ArrayList<ActivityType>();
        
        for (ActivityUnit unit : _activities) {
            for (ActivityType activity : unit.getActivityTypes()) {
                for (Context c : activity.getContext()) {
                    if (c.equals(context) && (from == 0
                            || activity.getTimestamp() >= from)
                            && (to == 0 || activity.getTimestamp() <= to)) {
                        ret.add(activity);
                    }
                }
            }
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("getActivityTypes context["+context+"] ("+this+") ret="
                        +new String(ActivityUtil.serializeActivityTypeList(ret)));
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("query[spec="+query+"] ("+this+")");
        }

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
            LOG.finest("query[spec="+query+"] ("+this+") ret="
                    +new String(ActivityUtil.serializeActivityTypeList(ret)));
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
