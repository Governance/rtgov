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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.analytics.util.ServiceDefinitionUtil;

/**
 * This class represents the service contract details associated
 * with a service type.
 *
 */
public class ServiceDefinition implements java.io.Externalizable {
    
    private static final Logger LOG=Logger.getLogger(ServiceDefinition.class.getName());

    private static final int VERSION = 1;

    private String _serviceType=null;
    private java.util.List<InterfaceDefinition> _interfaces=
                    new java.util.ArrayList<InterfaceDefinition>();
    private java.util.List<Context> _contexts=new java.util.ArrayList<Context>();
    private java.util.List<InvocationMetric> _history=new java.util.ArrayList<InvocationMetric>();
    
    /**
     * Default constructor.
     */
    public ServiceDefinition() {
    }

    /**
     * This method creates a shallow copy.
     * 
     * @return The shallow copy
     */
    public ServiceDefinition shallowCopy() {
        ServiceDefinition ret=new ServiceDefinition();
        
        ret.setServiceType(_serviceType);
        
        // Copy contexts
        for (Context c : _contexts) {
            ret.getContext().add(new Context(c));
        }
        
        return (ret);
    }

    /**
     * This method sets the service type.
     * 
     * @param st The service type
     */
    public void setServiceType(String st) {
        _serviceType = st;
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
     * This method sets the list of interfaces associated
     * with the service.
     * 
     * @param interfaces The interfaces
     */
    public void setInterfaces(java.util.List<InterfaceDefinition> interfaces) {
        _interfaces = interfaces;
    }
    
    /**
     * This method returns the list of interfaces associated
     * with the service.
     * 
     * @return The interfaces
     */
    public java.util.List<InterfaceDefinition> getInterfaces() {
        return (_interfaces);
    }
    
    /**
     * This method returns the interface associated with the supplied
     * name, if defined within the service definition.
     * 
     * @param name The interface name
     * @return The interface, or null if not found
     */
    public InterfaceDefinition getInterface(String name) {
        InterfaceDefinition ret=null;
        
        for (int i=0; i < _interfaces.size(); i++) {
            if (_interfaces.get(i).getInterface().equals(name)) {
                ret = _interfaces.get(i);
                break;
            }
        }
        
        return (ret);
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
     * This method returns the aggregated invocation metric information
     * from the interfaces.
     * 
     * @return The invocation metric
     */
    public InvocationMetric getMetrics() {
        java.util.List<InvocationMetric> metrics=
                        new java.util.ArrayList<InvocationMetric>();
        
        for (InterfaceDefinition id : getInterfaces()) {
            metrics.add(id.getMetrics());
        }
        
        return (new InvocationMetric(metrics));
    }
    
    /**
     * This method returns the historic list of invocation
     * metrics merged into the current service definition.
     * 
     * @return The invocation metrics history
     */
    public java.util.List<InvocationMetric> getHistory() {
        return (Collections.unmodifiableList(_history));
    }
    
    /**
     * This method merges the supplied definition with this
     * service definition.
     * 
     * @param sd The service definition to merge
     * @throws Exception Failed to merge
     */
    public void merge(ServiceDefinition sd) throws Exception {
        merge(sd, false);
    }
        
    /**
     * This method merges the supplied definition with this
     * service definition.
     * 
     * @param sd The service definition to merge
     * @param retainContexts Whether to merge context information
     * @throws Exception Failed to merge
     */
    public void merge(ServiceDefinition sd, boolean retainContexts) throws Exception {
            
        if (sd == null || !sd.getServiceType().equals(getServiceType())) {
            throw new IllegalArgumentException("Invalid service definition");
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Pre-merge this=["+this+"] with=["+sd+"]");
        }

        // Examine operation definitions - merge existing and
        // transfer undefined
        for (int i=0; i < sd.getInterfaces().size(); i++) {
            InterfaceDefinition idef=sd.getInterfaces().get(i);
            
            InterfaceDefinition cur=getInterface(idef.getInterface());
            
            if (cur == null) {
                cur = idef.shallowCopy();
                getInterfaces().add(cur);
            }

            cur.merge(idef);
        }
        
        if (retainContexts) {
            for (Context c : sd.getContext()) {
                if (!_contexts.contains(c)) {
                    _contexts.add(c);
                }
            }
        }
        
        _history.add(sd.getMetrics());

        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Post-merge this=["+this+"]");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_serviceType.hashCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        
        if (obj instanceof ServiceDefinition
                  && ((ServiceDefinition)obj).getServiceType().equals(_serviceType)) {
            return (true);
        }
        
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        String ret=null;
        
        try {
            ret = new String(ServiceDefinitionUtil.serializeServiceDefinition(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_serviceType);
        
        out.writeInt(_interfaces.size());
        for (int i=0; i < _interfaces.size(); i++) {
            out.writeObject(_interfaces.get(i));
        }
        
        out.writeInt(_contexts.size());
        for (int i=0; i < _contexts.size(); i++) {
            out.writeObject(_contexts.get(i));
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
        
        _serviceType = (String)in.readObject();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _interfaces.add((InterfaceDefinition)in.readObject());
        }
        
        len = in.readInt();
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
        
        len = in.readInt();
        for (int i=0; i < len; i++) {
            _history.add((InvocationMetric)in.readObject());
        }
    }
}
