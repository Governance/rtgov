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
package org.overlord.rtgov.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.InheritanceType;
import javax.persistence.DiscriminatorType;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.overlord.rtgov.activity.model.app.CustomActivity;
import org.overlord.rtgov.activity.model.app.LogMessage;
import org.overlord.rtgov.activity.model.bpm.ProcessCompleted;
import org.overlord.rtgov.activity.model.bpm.ProcessStarted;
import org.overlord.rtgov.activity.model.mom.MessageReceived;
import org.overlord.rtgov.activity.model.mom.MessageSent;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;

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
    @Type(value=ProcessStarted.class),
    @Type(value=CustomActivity.class),
    @Type(value=LogMessage.class) })
@Entity
@IdClass(value=ActivityTypeId.class)
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="type",
    discriminatorType=DiscriminatorType.STRING
)
@Table(name="RTGOV_ACTIVITIES")
public abstract class ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _unitId=null;
    private int _unitIndex=0;
    
    private long _timestamp=0;
    
    private String _principal=null;
    
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
        _unitId = act._unitId;
        _unitIndex = act._unitIndex;
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
    public void setUnitId(String id) {
        _unitId = id;
    }
    
    /**
     * This method gets the activity unit id.
     * 
     * @return The activity unit id
     */
    @Id
    @Column(name="unitId")
    public String getUnitId() {
        return (_unitId);
    }
    
    /**
     * This method sets the index of the activity
     * type within the activity unit.
     * 
     * @param index The index
     */
    public void setUnitIndex(int index) {
        _unitIndex = index;
    }
    
    /**
     * This method sets the index of the activity
     * type within the activity unit.
     * 
     * @return The index
     */
    @Id
    @Column(name="unitIndex")
    public int getUnitIndex() {
        return (_unitIndex);
    }
    
    /**
     * This method sets the principal associated with
     * the activity event.
     * 
     * @param principal The principal
     */
    public void setPrincipal(String principal) {
    	_principal = principal;
    }
    
    /**
     * This method gets the principal associated with
     * the activity event.
     * 
     * @return The principal
     */
    public String getPrincipal() {
    	return (_principal);
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
    @ElementCollection()
    @CollectionTable(name="RTGOV_ACTIVITY_CONTEXT")
    public java.util.List<Context> getContext() {
        return (_contexts);
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
    @ElementCollection(targetClass=String.class)
    @MapKeyColumn(name="name")
    @Column(name="value")
    @CollectionTable(name="RTGOV_ACTIVITY_PROPERTIES",joinColumns={
            @JoinColumn(name="unit_id",referencedColumnName="unitId"),
            @JoinColumn(name="unit_index",referencedColumnName="unitIndex")})
    public java.util.Map<String,String> getProperties() {
        return (_properties);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_unitId);
        out.writeInt(_unitIndex);
        out.writeLong(_timestamp);
        out.writeObject(_principal);
        
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
        
        _unitId = (String)in.readObject();
        _unitIndex = in.readInt();
        _timestamp = in.readLong();
        _principal = (String)in.readObject();
        
        int len=in.readInt();
        
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
        
        _properties = (java.util.Map<String, String>)in.readObject();
    }
    
}
