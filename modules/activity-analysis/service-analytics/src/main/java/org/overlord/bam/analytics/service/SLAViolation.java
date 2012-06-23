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
package org.overlord.bam.analytics.service;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.bam.activity.model.ActivityTypeRef;

/**
 * This class represents the occurrence of an SLA violation.
 *
 */
public class SLAViolation implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _serviceType=null;
    private String _operation=null;
    private String _violation=null;
    private long _timestamp=0;
    private java.util.List<ActivityTypeRef> _activityTypeRefs=
                        new java.util.ArrayList<ActivityTypeRef>();

    /**
     * This method sets the service type.
     * 
     * @param serviceType The service type
     */
    public void setServiceType(String serviceType) {
        _serviceType = serviceType;
    }
    
    /**
     * This method gets the service type.
     * 
     * @return The service type
     */
    public String getServiceType() {
        return (_serviceType);
    }
    
    /**
     * This method sets the operation.
     * 
     * @param operation The operation
     */
    public void setOperation(String operation) {
        _operation = operation;
    }
    
    /**
     * This method gets the operation.
     * 
     * @return The operation
     */
    public String getOperation() {
        return (_operation);
    }
    
    /**
     * This method sets the violation.
     * 
     * @param violation The violation
     */
    public void setViolation(String violation) {
        _violation = violation;
    }
    
    /**
     * This method gets the violation.
     * 
     * @return The violation
     */
    public String getViolation() {
        return (_violation);
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
     * This method returns the timestamp.
     * 
     * @return The timestamp
     */
    public long getTimestamp() {
        return (_timestamp);
    }
    
    /**
     * This method returns the list of activity type references
     * associated with the violation.
     * 
     * @return The related activity type refs
     */
    public java.util.List<ActivityTypeRef> getActivityTypeRefs() {
        return (_activityTypeRefs);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("SLA violation '"+_violation+"' on service="+_serviceType+" operation="
                    +_operation+" "+new java.util.Date(_timestamp));
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_serviceType);
        out.writeObject(_operation);
        out.writeObject(_violation);
        out.writeLong(_timestamp);
        
        out.writeInt(_activityTypeRefs.size());
        for (int i=0; i < _activityTypeRefs.size(); i++) {
            out.writeObject(_activityTypeRefs.get(i));            
        }
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _serviceType = (String)in.readObject();
        _operation = (String)in.readObject();
        _violation = (String)in.readObject();
        _timestamp = in.readLong();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _activityTypeRefs.add((ActivityTypeRef)in.readObject());
        }
    }
}
