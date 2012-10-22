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
package org.overlord.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Entity;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

/**
 * This class represents an activity unit, which generally relates
 * to the activities that occur within a transaction scope.
 *
 */
@Entity
public class ActivityUnit implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private static final Logger LOG=Logger.getLogger(ActivityUnit.class.getName());
    
    private String _id=null;
    private Origin _origin=null;
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
        
        for (ActivityType actType : _activityTypes) {
            try {
                Constructor<? extends ActivityType> con=
                            actType.getClass().getConstructor(actType.getClass());
                
                if (con != null) {
                     _activityTypes.add(con.newInstance(actType));
                } else {
                    LOG.severe(MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "activity.Messages").getString("ACTIVITY-4"),
                                actType.getClass().getName()));
                }
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "activity.Messages").getString("ACTIVITY-5"), e);
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
    @Id
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
    @Embedded
    public Origin getOrigin() {
        return (_origin);
    }
    
    /**
     * This method gets all the context instances associated
     * with the activity unit and its contained activity
     * types. Duplicate contexts will be ignored.
     * 
     * @return The complete list of contexts
     */
    public java.util.Set<Context> contexts() {
        java.util.Set<Context> ret=new java.util.HashSet<Context>();
        
        for (ActivityType at : _activityTypes) {
            ret.addAll(at.getContext());
        }
        
        return (ret);
    }
    
    /**
     * This method gets all the properties associated
     * with the activity unit and its contained activity
     * types. Duplicate properties will be ignored.
     * 
     * @return The aggregation of all activity type properties
     */
    public java.util.Map<String,String> properties() {
        java.util.Map<String,String> ret=new java.util.HashMap<String,String>();
        
        for (ActivityType at : _activityTypes) {
            for (String key : at.getProperties().keySet()) {
                ret.put(key, at.getProperties().get(key));
            }
        }
        
        return (ret);
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
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="unitId")
    public java.util.List<ActivityType> getActivityTypes() {
        return (_activityTypes);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("ActivityUnit["+_id+"] origin="+_origin
                    +" activityTypes="+_activityTypes);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_id);
        out.writeObject(_origin);
        
        int len = _activityTypes.size();
        
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
            _activityTypes.add((ActivityType)in.readObject());
        }
    }
    
}
