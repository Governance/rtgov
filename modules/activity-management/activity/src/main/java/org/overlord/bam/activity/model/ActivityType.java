/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.overlord.bam.activity.model.bpm.ProcessCompleted;
import org.overlord.bam.activity.model.bpm.ProcessStarted;
import org.overlord.bam.activity.model.mom.MessageReceived;
import org.overlord.bam.activity.model.mom.MessageSent;
import org.overlord.bam.activity.model.soa.RequestReceived;
import org.overlord.bam.activity.model.soa.RequestSent;
import org.overlord.bam.activity.model.soa.ResponseReceived;
import org.overlord.bam.activity.model.soa.ResponseSent;

/**
 * This abstract class is the super type of all activity type classes.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@Type(value=MessageReceived.class),
    @Type(value=MessageSent.class),
    @Type(value=RequestReceived.class),
    @Type(value=RequestSent.class),
    @Type(value=ResponseReceived.class),
    @Type(value=ResponseSent.class),
    @Type(value=ProcessCompleted.class),
    @Type(value=ProcessStarted.class) })
public abstract class ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _activityUnitId=null;
    private int _activityUnitIndex=0;
    private long _timestamp=0;
    
    private java.util.List<Context> _contexts=new java.util.Vector<Context>();
    private java.util.Map<String,String> _properties=new java.util.HashMap<String,String>();

    /**
     * The default constructor.
     */
    public ActivityType() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param act The activity to copy.
     */
    public ActivityType(ActivityType act) {
        _activityUnitId = act._activityUnitId;
        _activityUnitIndex = act._activityUnitIndex;
        _timestamp = act._timestamp;
        
        for (Context ctx : act._contexts) {
            _contexts.add(new Context(ctx));
        }
        
        _properties = new java.util.HashMap<String, String>(act._properties);
    }
    
    /**
     * This method sets the activity unit id.
     * 
     * @param id The activity unit id
     */
    public void setActivityUnitId(String id) {
        _activityUnitId = id;
    }
    
    /**
     * This method gets the activity unit id.
     * 
     * @return The activity unit id
     */
    public String getActivityUnitId() {
        return (_activityUnitId);
    }
    
    /**
     * This method sets the index of this activity
     * type within the activity unit.
     * 
     * @param index The activity unit index
     */
    public void setActivityUnitIndex(int index) {
        _activityUnitIndex = index;
    }
    
    /**
     * This method sets the index of this activity
     * type within the activity unit.
     * 
     * @return The activity unit index
     */
    public int getActivityUnitIndex() {
        return (_activityUnitIndex);
    }
    
    /**
     * This method sets the timestamp.
     * 
     * @param timestamp The timestamp
     */
    public void setTimestamp(long timestamp) {
        _timestamp = timestamp;
    }
    
    /**
     * This method gets the timestamp.
     * 
     * @return The timestamp
     */
    public long getTimestamp() {
        return (_timestamp);
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
     * This method returns the explicit and derived
     * context information associated with the activity type.
     * 
     * @return The list of all explicit and derived context information
     */
    protected java.util.List<Context> deriveContexts() {
        return (new java.util.ArrayList<Context>(_contexts));
    }
    
    /**
     * This method sets the properties.
     * 
     * @param props The properties
     */
    public void setProperties(java.util.Map<String,String> props) {
        _properties = props;
    }
    
    /**
     * This method gets the properties.
     * 
     * @return The properties
     */
    public java.util.Map<String,String> getProperties() {
        return (_properties);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_activityUnitId);
        out.writeInt(_activityUnitIndex);
        out.writeLong(_timestamp);
        
        int len=_contexts.size();
        
        out.writeInt(len);
        for (int i=0; i < len; i++) {
            out.writeObject(_contexts.get(i));
        }    
        
        out.writeObject(_properties);    
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _activityUnitId = (String)in.readObject();
        _activityUnitIndex = in.readInt();
        _timestamp = in.readLong();
        
        int len=in.readInt();
        
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
        
        _properties = (java.util.Map<String, String>)in.readObject();
    }
    
}
