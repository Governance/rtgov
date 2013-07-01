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
import java.util.Collections;

/**
 * This class represents an operation within a service interface.
 *
 */
public class OperationDefinition implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _name=null;
    private RequestResponseDefinition _requestResponse=null;
    private java.util.List<RequestFaultDefinition> _requestFaults=
            new java.util.ArrayList<RequestFaultDefinition>();
    private java.util.List<InvocationMetric> _history=
            new java.util.ArrayList<InvocationMetric>();

    /**
     * Default constructor.
     */
    public OperationDefinition() {
    }

    /**
     * This method creates a shallow copy.
     * 
     * @return The shallow copy
     */
    protected OperationDefinition shallowCopy() {
        OperationDefinition ret=new OperationDefinition();
        
        ret.setName(_name);
        
        return (ret);
    }

    /**
     * This method sets the operation name.
     * 
     * @param operation The operation name
     */
    public void setName(String operation) {
        _name = operation;
    }
    
    /**
     * This method gets the operation name.
     * 
     * @return The operation name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the normal response details.
     * 
     * @param response The normal response
     */
    public void setRequestResponse(RequestResponseDefinition response) {
        _requestResponse = response;
    }
    
    /**
     * This method gets the normal response details.
     * 
     * @return The normal response
     */
    public RequestResponseDefinition getRequestResponse() {
        return (_requestResponse);
    }
    
    /**
     * This method sets the list of faults associated
     * with the operation.
     * 
     * @param faults The faults
     */
    public void setRequestFaults(java.util.List<RequestFaultDefinition> faults) {
        _requestFaults = faults;
    }
    
    /**
     * This method returns the list of faults associated
     * with the operation.
     * 
     * @return The faults
     */
    public java.util.List<RequestFaultDefinition> getRequestFaults() {
        return (_requestFaults);
    }
    
    /**
     * This method returns the fault associated with the supplied
     * name, if defined within the operation definition.
     * 
     * @param name The fault name
     * @return The fault, or null if not found
     */
    public RequestFaultDefinition getRequestFault(String name) {
        RequestFaultDefinition ret=null;
        
        for (int i=0; i < _requestFaults.size(); i++) {
            if (_requestFaults.get(i).getFault().equals(name)) {
                ret = _requestFaults.get(i);
                break;
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the aggregated invocation metric information
     * from the normal and fault responses.
     * 
     * @return The invocation metric
     */
    public InvocationMetric getMetrics() {
        java.util.List<InvocationMetric> metrics=
                new java.util.ArrayList<InvocationMetric>();
        
        if (getRequestResponse() != null) {
            metrics.add(getRequestResponse().getMetrics());            
        }

        for (RequestFaultDefinition fault : getRequestFaults()) {
            metrics.add(fault.getMetrics());
        }

        return (new InvocationMetric(metrics));
    }
    
    /**
     * This method merges the supplied operation definition
     * with this.
     * 
     * @param opdef The operation definition to merge
     */
    public void merge(OperationDefinition opdef) {
        
        if (opdef.getRequestResponse() != null) {
            if (getRequestResponse() == null) {
                setRequestResponse(opdef.getRequestResponse().shallowCopy());
            }
                
            getRequestResponse().merge(opdef.getRequestResponse());
        }
        
        for (int i=0; i < opdef.getRequestFaults().size(); i++) {
            RequestFaultDefinition rfd=opdef.getRequestFaults().get(i);
            
            RequestFaultDefinition cur=getRequestFault(rfd.getFault());
            
            if (cur == null) {
                cur = rfd.shallowCopy();
                getRequestFaults().add(cur);
            }

            cur.merge(rfd);
        }
        
        _history.add(opdef.getMetrics());
    }
    
    /**
     * This method returns the historic list of invocation
     * metrics merged into the current operation definition.
     * 
     * @return The invocation metrics history
     */
    public java.util.List<InvocationMetric> getHistory() {
        return (Collections.unmodifiableList(_history));
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_name.hashCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        
        if (obj instanceof OperationDefinition
                  && ((OperationDefinition)obj).getName().equals(_name)) {
            return (true);
        }
        
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_name);
        
        out.writeObject(_requestResponse);
        
        out.writeInt(_requestFaults.size());
        for (int i=0; i < _requestFaults.size(); i++) {
            out.writeObject(_requestFaults.get(i));
        }
        
        out.writeInt(_history.size());
        for (int i=0; i < _history.size(); i++) {
            out.writeObject(_history.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _name = (String)in.readObject();
        
        _requestResponse = (RequestResponseDefinition)in.readObject();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _requestFaults.add((RequestFaultDefinition)in.readObject());
        }
        
        len = in.readInt();
        for (int i=0; i < len; i++) {
            _history.add((InvocationMetric)in.readObject());
        }
    }
}
