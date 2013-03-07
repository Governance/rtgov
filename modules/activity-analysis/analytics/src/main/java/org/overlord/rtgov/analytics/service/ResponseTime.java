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
package org.overlord.rtgov.analytics.service;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;

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
    
    private ActivityTypeId _requestId=null;
    private ActivityTypeId _responseId=null;
    private java.util.Map<String,String> _properties=new java.util.HashMap<String,String>();
    
    private java.util.List<Context> _contexts=
                    new java.util.ArrayList<Context>();


    /**
     * Default constructor.
     */
    public ResponseTime() {
    }
    
    /**
     * Copy constructor.
     * 
     * @param rt The source to copy
     */
    public ResponseTime(ResponseTime rt) {
        _serviceType = rt.getServiceType();
        _operation = rt.getOperation();
        _fault = rt.getFault();
        _avg = rt.getAverage();
        _max = rt.getMax();
        _min = rt.getMin();
        _timestamp = rt.getTimestamp();
        
        _requestId = rt.getRequestId();
        _responseId = rt.getResponseId();
        _properties.putAll(rt.getProperties());

        _contexts.addAll(rt.getContext());
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
     * This method sets the request activity type id.
     * 
     * @param id The request activity type id
     */
    public void setRequestId(ActivityTypeId id) {
        _requestId = id;
    }
    
    /**
     * This method sets the request activity type id.
     * 
     * @return The request activity type id
     */
    public ActivityTypeId getRequestId() {
        return (_requestId);
    }
    
    /**
     * This method sets the response activity type id.
     * 
     * @param id The response activity type id
     */
    public void setResponseId(ActivityTypeId id) {
        _responseId = id;
    }
    
    /**
     * This method sets the response activity type id.
     * 
     * @return The response activity type id
     */
    public ActivityTypeId getResponseId() {
        return (_responseId);
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
        
        out.writeObject(_requestId);
        out.writeObject(_responseId);
        out.writeObject(_properties);    

        out.writeInt(_contexts.size());
        for (int i=0; i < _contexts.size(); i++) {
            out.writeObject(_contexts.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
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
        
        _requestId = (ActivityTypeId)in.readObject();
        _responseId = (ActivityTypeId)in.readObject();
        _properties = (java.util.Map<String, String>)in.readObject();

        int len = in.readInt();
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
    }
}
