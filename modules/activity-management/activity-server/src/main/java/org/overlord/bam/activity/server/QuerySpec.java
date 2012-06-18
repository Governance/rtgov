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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;

/**
 * This class represents a query specification.
 *
 */
public class QuerySpec implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private String _id=null;
    private long _fromTimestamp=0;
    private long _toTimestamp=0;
    private java.util.List<Context> _contexts=new java.util.ArrayList<Context>();
    private boolean _contextAND=true;
    
    /**
     * This is the default constructor.
     */
    public QuerySpec() {
    }
    
    /**
     * This method sets the activity unit id to retrieve.
     * 
     * @param id The activity unit id
     * @return The query spec
     */
    public QuerySpec setId(String id) {
        _id = id;
        return (this);
    }
    
    /**
     * This method returns the activity unit id to retrieve.
     * 
     * @return The activity unit id, or null if not relevant
     */
    public String getId() {
        return (_id);
    }
    
    /**
     * This method sets the 'from' timestamp. If
     * set to 0, then query will start from the
     * oldest activity event.
     * 
     * @param from The 'from' timetamp
     * @return The query spec
     */
    public QuerySpec setFromTimestamp(long from) {
        _fromTimestamp = from;       
        return (this);
    }
    
    /**
     * This method gets the 'from' timestamp. If
     * value is 0, then query will start from the
     * oldest activity event.
     * 
     * @return The 'from' timetamp
     */
    public long getFromTimestamp() {
        return (_fromTimestamp);
    }

    /**
     * This method sets the 'to' timestamp. If
     * set to 0, then query will end with the
     * newest activity event.
     * 
     * @param to The 'to' timetamp
     * @return The query spec
     */
    public QuerySpec setToTimestamp(long to) {
        _toTimestamp = to;
        return (this);
    }
    
    /**
     * This method gets the 'to' timestamp. If
     * value is 0, then query will start with the
     * newest activity event.
     * 
     * @return The 'to' timetamp
     */
    public long getToTimestamp() {
        return (_toTimestamp);
    }
    
    /**
     * This method adds a context to query.
     * 
     * @param context The context
     * @return The query spec
     */
    public QuerySpec addContext(Context context) {
        _contexts.add(context);
        return (this);
    }

    /**
     * This method removes a context from the query.
     * 
     * @param context The context
     * @return The query spec
     */
    public QuerySpec removeContext(Context context) {
        _contexts.remove(context);
        return (this);
    }
    
    /**
     * This method returns the list of contexts to be
     * matched within the retrieved 
     * @return
     */
    public java.util.List<Context> getContexts() {
        return (_contexts);
    }
    
    /**
     * This method determines whether the list of
     * contexts to be matched should be treated as
     * a logical conjunction (i.e. AND).
     *
     * @param b Whether query should treat contexts as a logical AND
     * @return The query spec
     */
    public QuerySpec setContextAND(boolean b) {
        _contextAND = b;
        return (this);
    }

    /**
     * This method returns whether the list of contexts
     * is to be matched as a logical conjunction (i.e. AND).
     * 
     * @return Whether contexts are a logical conjunction
     */
    public boolean isContextAND() {
        return (_contextAND);
    }
    
    /**
     * This method applies the query spec to the supplied list of activity units, returning
     * the subset that pass the query criteria.
     * 
     * @param activities The list of activity units to evaluate
     * @return The list of activity units that pass the query criteria, or null if none
     */
    public java.util.List<ActivityUnit> evaluate(java.util.List<ActivityUnit> activities) {
        java.util.List<ActivityUnit> ret=null;
        
        for (ActivityUnit au : activities) {
            if (evaluate(au)) {
                if (ret == null) {
                    ret = new java.util.ArrayList<ActivityUnit>();
                }
                ret.add(au);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method evaluates the supplied activity unit against the
     * criteria defined by this query spec.
     * 
     * @param au The activity unit
     * @return Whether the activity unit matches the query spec
     */
    protected boolean evaluate(ActivityUnit au) {
        boolean ret=true;
        
        if (_id != null && !_id.equals(au.getId())) {
            ret = false;
        } else if (_fromTimestamp != 0 && au.getActivityTypes().size() > 0
                && _fromTimestamp > au.getActivityTypes().get(au.getActivityTypes().size()-1).getTimestamp()) {
            ret = false;
        } else if (_toTimestamp != 0 && au.getActivityTypes().size() > 0
                && _toTimestamp < au.getActivityTypes().get(0).getTimestamp()) {
            ret = false;
        }
        
        // Evaluate the context details
        if (ret && _contexts.size() > 0) {
            boolean onematch=false;
            
            for (Context c : _contexts) {
                if (au.getContext().contains(c)) {
                    onematch = true;
                    if (!_contextAND) {
                        break;
                    }
                } else if (_contextAND) {
                    ret = false;
                    break;
                }
            }
            
            if (!_contextAND && !onematch) {
                ret = false;
            }
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_id);
        out.writeLong(_fromTimestamp);
        out.writeLong(_toTimestamp);
        
        out.writeInt(_contexts.size());
        
        for (int i=0; i < _contexts.size(); i++) {
            out.writeObject(_contexts.get(i));
        }
        
        out.writeBoolean(_contextAND);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version number - not required at the moment
        
        _id = (String)in.readObject();
        _fromTimestamp = in.readLong();
        _toTimestamp = in.readLong();
        
        int num=in.readInt();
        
        for (int i=0; i < num; i++) {
            _contexts.add((Context)in.readObject());
        }
        
        _contextAND = in.readBoolean();
    }
    
}
