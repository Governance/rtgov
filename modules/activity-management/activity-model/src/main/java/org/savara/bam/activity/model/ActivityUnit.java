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
package org.savara.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents an activity unit, which generally relates
 * to the activities that occur within a transaction scope.
 *
 */
public class ActivityUnit implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private static final Logger LOG=Logger.getLogger(ActivityUnit.class.getName());
    
    private String _id=null;
    
    private Origin _origin=null;
    private java.util.List<Context> _contexts=new java.util.Vector<Context>();
    private java.util.List<ActivityType> _activityTypes=new java.util.Vector<ActivityType>();
    
    /**
     * The default constructor.
     */
    public ActivityUnit() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param act The activity to copy.
     */
    public ActivityUnit(ActivityUnit act) {
        _id = act._id;
        
        if (act._origin != null) {
            _origin = new Origin(act._origin);
        }
        
        for (Context context : act._contexts) {
            _contexts.add(new Context(context));
        }
        
        for (ActivityType actType : _activityTypes) {
            try {
                Constructor<? extends ActivityType> con=
                            actType.getClass().getConstructor(actType.getClass());
                
                if (con != null) {
                     _activityTypes.add(con.newInstance(actType));
                } else {
                    LOG.severe("Failed to create copy - can't find copy constructor for '"
                                +actType.getClass().getName()+"'");
                }
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to create copy", e);
            }
        }
    }
    
    /**
     * This method sets the activity id.
     * 
     * @param id The id
     */
    public void setId(String id) {
        _id = id;
    }
    
    /**
     * This method gets the activity id.
     * 
     * @return The id
     */
    public String getId() {
        return (_id);
    }
    
    /**
     * This method sets the origin.
     * 
     * @param origin The origin
     */
    public void setOrigin(Origin origin) {
        _origin = origin;
    }
    
    /**
     * This method gets the origin.
     * 
     * @return The origin
     */
    public Origin getOrigin() {
        return (_origin);
    }
    
    /**
     * This method sets the context.
     * 
     * @param context The context
     */
    public void setContext(java.util.List<Context> context) {
        _contexts = context;
    }
    
    /**
     * This method gets the context.
     * 
     * @return The context
     */
    public java.util.List<Context> getContext() {
        return (_contexts);
    }
    
    /**
     * This method sets the activity types.
     * 
     * @param activityTypes The activity types
     */
    public void setActivityTypes(java.util.List<ActivityType> activityTypes) {
        _activityTypes = activityTypes;
    }
    
    /**
     * This method gets the activity types.
     * 
     * @return The activity types
     */
    public java.util.List<ActivityType> getActivityTypes() {
        return (_activityTypes);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("ActivityUnit["+_id+"] origin="+_origin+" contexts="
                    +_contexts+" activityTypes="+_activityTypes);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_id);
        out.writeObject(_origin);
        
        int len=_contexts.size();
        
        out.writeInt(len);
        for (int i=0; i < len; i++) {
            out.writeObject(_contexts.get(i));
        }
        
        len = _activityTypes.size();
        
        out.writeInt(len);
        for (int i=0; i < len; i++) {
            out.writeObject(_activityTypes.get(i));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _id = (String)in.readObject();
        _origin = (Origin)in.readObject();
        
        int len=in.readInt();
        
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
        
        len = in.readInt();
        
        for (int i=0; i < len; i++) {
            _activityTypes.add((ActivityType)in.readObject());
        }
    }
    
}
