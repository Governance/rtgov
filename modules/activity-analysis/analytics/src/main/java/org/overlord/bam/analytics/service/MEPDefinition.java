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

import org.overlord.bam.activity.model.ActivityTypeId;

/**
 * This class represents an abstract response within a service operation.
 *
 */
public abstract class MEPDefinition implements java.io.Externalizable {

    private static final int VERSION = 1;

    private ActivityTypeId _requestId=null;
    private ActivityTypeId _responseId=null;
    private java.util.Map<String,String> _properties=new java.util.HashMap<String,String>();
    
    private java.util.List<InvocationDefinition> _invocations=
                new java.util.ArrayList<InvocationDefinition>();
    
    private InvocationMetric _metrics=new InvocationMetric();

    /**
     * Default constructor.
     */
    public MEPDefinition() {
    }

    /**
     * Copy constructor.
     * 
     * @param md The source to copy
     */
    public MEPDefinition(MEPDefinition md) {
         
        _requestId = md.getRequestId();
        _responseId = md.getResponseId();
        _properties.putAll(md.getProperties());

        for (InvocationDefinition id : md.getInvocations()) {
            _invocations.add(new InvocationDefinition(id));
        }
        
        if (md.getMetrics() != null) {
            _metrics = new InvocationMetric(md.getMetrics());
        }
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
     * This method gets the request activity type id.
     * This information will only be available if the
     * definition represents a single MEP exchange,
     * as opposed to the aggregation of multiple
     * exchanges.
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
     * This method gets the response activity type id.
     * This information will only be available if the
     * definition represents a single MEP exchange,
     * as opposed to the aggregation of multiple
     * exchanges.
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
     * This method sets the list of invocations associated
     * with the operation.
     * 
     * @param invocations The invocations
     */
    public void setInvocations(java.util.List<InvocationDefinition> invocations) {
        _invocations = invocations;
    }
    
    /**
     * This method returns the list of invocations associated
     * with the operation.
     * 
     * @return The invocations
     */
    public java.util.List<InvocationDefinition> getInvocations() {
        return (_invocations);
    }
    
    /**
     * This method returns the invocation associated with the supplied
     * service type, operation and optional fault.
     * 
     * @param serviceType The service type
     * @param operation The operation
     * @param fault The optional fault
     * @return The invocation definition, or null if not found
     */
    public InvocationDefinition getInvocation(String serviceType, String operation,
                                String fault) {
        for (int i=0; i < _invocations.size(); i++) {
            InvocationDefinition id=(InvocationDefinition)_invocations.get(i);
            
            if (id.getServiceType().equals(serviceType)
                    && id.getOperation().equals(operation)) {
                
                if (id.getFault() == null) {
                    if (fault == null) {
                        return (id);
                    }
                } else if (fault != null && id.getFault().equals(fault)) {
                    return (id);
                }
            }
        }
        
        return (null);
    }
    
    /**
     * This method returns the invocation metric information.
     * 
     * @return The invocation metric
     */
    public InvocationMetric getMetrics() {
        return (_metrics);
    }
    
    /**
     * This method sets the invocation metric information.
     * 
     * @param im The invocation metric
     */
    protected void setMetrics(InvocationMetric im) {
        _metrics = im;
    }
    
    /**
     * This method merges the information from the supplied
     * MEP definition. Merging will result in any request
     * and response ids being cleared, as the definition
     * no longer relates to a single exchange.
     * 
     * @param mep The MEP definition to merge
     */
    public void merge(MEPDefinition mep) {
        
        // Clear the request/response ids, and properties,
        // as merged results no longer relate to a single exchange
        _requestId = null;
        _responseId = null;
        _properties.clear();
        
        // Merge metrics
        getMetrics().merge(mep.getMetrics());
        
        // Merge invocation information
        for (int i=0; i < mep.getInvocations().size(); i++) {
            InvocationDefinition id=mep.getInvocations().get(i);
            
            InvocationDefinition cur=getInvocation(id.getServiceType(),
                            id.getOperation(), id.getFault());
            
            if (cur != null) {
                cur.merge(id);
            } else {
                getInvocations().add(new InvocationDefinition(id));
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_requestId);
        out.writeObject(_responseId);
        out.writeObject(_properties);    

        out.writeObject(_metrics);
        
        out.writeInt(_invocations.size());
        for (int i=0; i < _invocations.size(); i++) {
            out.writeObject(_invocations.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _requestId = (ActivityTypeId)in.readObject();
        _responseId = (ActivityTypeId)in.readObject();
        _properties = (java.util.Map<String, String>)in.readObject();

        _metrics = (InvocationMetric)in.readObject();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _invocations.add((InvocationDefinition)in.readObject());
        }
    }
}
