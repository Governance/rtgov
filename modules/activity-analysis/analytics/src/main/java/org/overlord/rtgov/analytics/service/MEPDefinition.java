/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.analytics.service;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.rtgov.activity.model.ActivityTypeId;

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
     * This method initializes a supplied MEP definition.
     * 
     * @param md The MEP definition to initialize
     */
    public void initCopy(MEPDefinition md) {
        md.setRequestId(_requestId);
        md.setResponseId(_responseId);
        md.getProperties().putAll(_properties);
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
     * interface, operation and optional fault.
     * 
     * @param intf The interface
     * @param operation The operation
     * @param fault The optional fault
     * @return The invocation definition, or null if not found
     */
    public InvocationDefinition getInvocation(String intf, String operation,
                                String fault) {
        return (getInvocation(intf, null, operation, fault));
    }
    
    /**
     * This method returns the invocation associated with the supplied
     * interface, service type, operation and optional fault.
     * 
     * @param intf The interface
     * @param serviceType The optional service type
     * @param operation The operation
     * @param fault The optional fault
     * @return The invocation definition, or null if not found
     */
    public InvocationDefinition getInvocation(String intf, String serviceType,
                    String operation, String fault) {
        for (int i=0; i < _invocations.size(); i++) {
            InvocationDefinition id=(InvocationDefinition)_invocations.get(i);
            
            if ((id.getServiceType() == null && serviceType != null)
                    || (id.getServiceType() != null && serviceType == null)) {
                continue;
            } else if (serviceType != null && !serviceType.equals(id.getServiceType())) {
                continue;
            }
            
            if (id.getInterface().equals(intf)
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
            
            InvocationDefinition cur=getInvocation(id.getInterface(),
                    id.getServiceType(), id.getOperation(), id.getFault());
            
            if (cur == null) {
                cur = id.shallowCopy();
                getInvocations().add(cur);
            }

            cur.merge(id);
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
