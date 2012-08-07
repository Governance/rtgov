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

/**
 * This class represents response time information associated with
 * the invocation of a service.
 *
 */
public class ResponseTime implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _serviceType=null;
    private String _operation=null;
    private String _fault=null;
    private long _avg=0;
    private long _max=0;
    private long _min=0;
    private long _timestamp=0;

    /**
     * Default constructor.
     */
    public ResponseTime() {
    }
    
    /**
     * Copy constructor.
     */
    public ResponseTime(ResponseTime rt) {
        _serviceType = rt.getServiceType();
        _operation = rt.getOperation();
        _fault = rt.getFault();
        _avg = rt.getAverage();
        _max = rt.getMax();
        _min = rt.getMin();
        _timestamp = rt.getTimestamp();
    }
    
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
     * This method sets the optional fault.
     * 
     * @param fault The fault
     */
    public void setFault(String fault) {
        _fault = fault;
    }
    
    /**
     * This method gets the optional fault.
     * 
     * @return The optional fault
     */
    public String getFault() {
        return (_fault);
    }
    
    /**
     * This method sets the average.
     * 
     * @param average The average
     */
    public void setAverage(long average) {
        _avg = average;
    }
    
    /**
     * This method returns the average.
     * 
     * @return The average
     */
    public long getAverage() {
        return (_avg);
    }
    
    /**
     * This method sets the maximum duration.
     * 
     * @param time The maximum duration
     */
    public void setMax(long time) {
        _max = time;
    }
    
    /**
     * This method returns the maximum duration.
     * 
     * @return The maximum duration
     */
    public long getMax() {
        return (_max);
    }
    
    /**
     * This method sets the minimum duration.
     * 
     * @param time The minimum duration
     */
    public void setMin(long time) {
        _min = time;
    }
    
    /**
     * This method returns the minimum duration.
     * 
     * @return The minimum duration
     */
    public long getMin() {
        return (_min);
    }
    
    /**
     * This method sets the timestamp.
     * 
     * @param time The timestamp
     */
    public void setTimestamp(long time) {
        _timestamp = time;
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
     * {@inheritDoc}
     */
    public String toString() {
        return ("ResponseTime[service="+_serviceType+" op="+_operation+" fault="+_fault
                    +" duration="+_avg+" min="+_min+" max="+_max+" timestamp="
                    +_timestamp+"]");
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_serviceType);
        out.writeObject(_operation);
        out.writeObject(_fault);
        out.writeLong(_avg);
        out.writeLong(_max);
        out.writeLong(_min);
        out.writeLong(_timestamp);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _serviceType = (String)in.readObject();
        _operation = (String)in.readObject();
        _fault = (String)in.readObject();
        _avg = in.readLong();
        _max = in.readLong();
        _min = in.readLong();
        _timestamp = in.readLong();
    }
}
