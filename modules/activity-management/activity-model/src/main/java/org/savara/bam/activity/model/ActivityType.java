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
package org.savara.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.savara.bam.activity.model.bpm.ProcessCompleted;
import org.savara.bam.activity.model.bpm.ProcessStarted;
import org.savara.bam.activity.model.mom.MessageReceived;
import org.savara.bam.activity.model.mom.MessageSent;
import org.savara.bam.activity.model.soa.RequestReceived;
import org.savara.bam.activity.model.soa.RequestSent;
import org.savara.bam.activity.model.soa.ResponseReceived;
import org.savara.bam.activity.model.soa.ResponseSent;

/**
 * This abstract class is the super type of all activity type classes.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="typeName")
@JsonSubTypes({@Type(value=MessageReceived.class),
    @Type(value=MessageSent.class),
    @Type(value=RequestReceived.class),
    @Type(value=RequestSent.class),
    @Type(value=ResponseReceived.class),
    @Type(value=ResponseSent.class),
    @Type(value=ProcessCompleted.class),
    @Type(value=ProcessStarted.class)})
public abstract class ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _activityUnitId=null;
    private int _activityUnitIndex=0;
    private long _timestamp=0;

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
     * This method gets the timestamp.
     * 
     * @return The timestamp
     */
    public long getTimestamp() {
        return (_timestamp);
    }

    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_activityUnitId);
        out.writeInt(_activityUnitIndex);
        out.writeLong(_timestamp);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _activityUnitId = (String)in.readObject();
        _activityUnitIndex = in.readInt();
        _timestamp = in.readLong();
    }
    
}
