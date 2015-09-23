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
 * This class represents the invocation details associated
 * with a called service.
 *
 */
public class InvocationDefinition implements java.io.Externalizable {

    private static final int VERSION = 3;

    private String _interface=null;
    private String _serviceType=null;
    private String _operation=null;
    private String _fault=null;
    private boolean _internal=false;
    private java.util.Map<String,String> _properties=new java.util.HashMap<String,String>();

    private InvocationMetric _metrics=new InvocationMetric();
    private java.util.List<InvocationMetric> _history=
            new java.util.ArrayList<InvocationMetric>();

    /**
     * Default constructor.
     */
    public InvocationDefinition() {
    }

    /**
     * This method creates a shallow copy.
     * 
     * @return The shallow copy
     */
    protected InvocationDefinition shallowCopy() {
        InvocationDefinition ret=new InvocationDefinition();
        
        ret.setInterface(_interface);
        ret.setServiceType(_serviceType);
        ret.setOperation(_operation);
        ret.setFault(_fault);
        
        return (ret);
    }

    /**
     * This method sets the interface.
     * 
     * @param intf The interface
     */
    public void setInterface(String intf) {
        _interface = intf;
    }
    
    /**
     * This method gets the interface.
     * 
     * @return The interface
     */
    public String getInterface() {
        return (_interface);
    }
    
    /**
     * This method sets the optional service type.
     * 
     * @param stype The service type
     */
    public void setServiceType(String stype) {
        _serviceType = stype;
    }
    
    /**
     * This method gets the optional service type.
     * 
     * @return The optional service type
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
     * This method sets whether the service is internal (i.e. not publicly
     * available).
     * 
     * @param b Whether the service is internal
     */
    public void setInternal(boolean b) {
        _internal = b;
    }
    
    /**
     * This method identifies whether the service is internal.
     * 
     * @return Whether the service is internal
     */
    public boolean getInternal() {
        return (_internal);
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
     * This method returns the metrics for the invocation
     * definition.
     * 
     * @return The invocation definition metrics
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
     * This method merges the supplied invocation
     * definition.
     * 
     * @param id The invocation definition to merge
     */
    public void merge(InvocationDefinition id) {
        
        getMetrics().merge(id.getMetrics());
        
        _history.add(id.getMetrics());
    }
    
    /**
     * This method returns the historic list of invocation
     * metrics merged into the current invocation definition.
     * 
     * @return The invocation metrics history
     */
    public java.util.List<InvocationMetric> getHistory() {
        return (Collections.unmodifiableList(_history));
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_interface);
        out.writeObject(_serviceType);
        out.writeObject(_operation);
        out.writeObject(_fault);
        out.writeObject(_metrics);
        
        out.writeInt(_history.size());
        for (int i=0; i < _history.size(); i++) {
            out.writeObject(_history.get(i));
        }
        
        // Version 3
        out.writeBoolean(_internal);
        out.writeObject(_properties);    
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        int version=in.readInt();
        
        _interface = (String)in.readObject();
        
        if (version > 1) {
            _serviceType = (String)in.readObject();
        }
        
        _operation = (String)in.readObject();
        _fault = (String)in.readObject();
        _metrics = (InvocationMetric)in.readObject();
        
        int len = in.readInt();
        for (int i=0; i < len; i++) {
            _history.add((InvocationMetric)in.readObject());
        }
        
        // Version 3
        if (version > 2) {
            _internal = in.readBoolean();
            _properties = (java.util.Map<String, String>)in.readObject();
        }
    }
}
