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

    private String _interface=null;
    private java.util.List<OperationDefinition> _operations=
                    new java.util.ArrayList<OperationDefinition>();
    private java.util.List<Context> _contexts=new java.util.ArrayList<Context>();
    private java.util.List<ServiceDefinition> _merged=new java.util.ArrayList<ServiceDefinition>();
    
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
        
        ret.setInterface(_interface);
        
        // Copy contexts
        for (Context c : _contexts) {
            ret.getContext().add(new Context(c));
        }
        
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
     * This method sets the list of operations associated
     * with the service.
     * 
     * @param operations The operations
     */
    public void setOperations(java.util.List<OperationDefinition> operations) {
        _operations = operations;
    }
    
    /**
     * This method returns the list of operations associated
     * with the service.
     * 
     * @return The operations
     */
    public java.util.List<OperationDefinition> getOperations() {
        return (_operations);
    }
    
    /**
     * This method returns the operation associated with the supplied
     * name, if defined within the service definition.
     * 
     * @param name The operation name
     * @return The operation, or null if not found
     */
    public OperationDefinition getOperation(String name) {
        OperationDefinition ret=null;
        
        for (int i=0; i < _operations.size(); i++) {
            if (_operations.get(i).getName().equals(name)) {
                ret = _operations.get(i);
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
     * from the operations.
     * 
     * @return The invocation metric
     */
    public InvocationMetric getMetrics() {
        java.util.List<InvocationMetric> metrics=
                        new java.util.ArrayList<InvocationMetric>();
        
        for (OperationDefinition op : getOperations()) {
            metrics.add(op.getMetrics());
        }
        
        return (new InvocationMetric(metrics));
    }
    
    /**
     * This method returns the list of merged service definitions.
     * 
     * @return The merged list
     */
    public java.util.List<ServiceDefinition> getMerged() {
        return (Collections.unmodifiableList(_merged));
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
            
        if (sd == null || !sd.getInterface().equals(getInterface())) {
            throw new IllegalArgumentException("Invalid service definition");
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Pre-merge this=["+this+"] with=["+sd+"]");
        }

        // Examine operation definitions - merge existing and
        // transfer undefined
        for (int i=0; i < sd.getOperations().size(); i++) {
            OperationDefinition opdef=sd.getOperations().get(i);
            
            OperationDefinition cur=getOperation(opdef.getName());
            
            if (cur == null) {
                cur = opdef.shallowCopy();
                getOperations().add(cur);
            }

            cur.merge(opdef);
        }
        
        if (retainContexts) {
            for (Context c : sd.getContext()) {
                if (!_contexts.contains(c)) {
                    _contexts.add(c);
                }
            }
        }
        
        _merged.add(sd);

        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Post-merge this=["+this+"]");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_interface.hashCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        
        if (obj instanceof ServiceDefinition
                  && ((ServiceDefinition)obj).getInterface().equals(_interface)) {
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
        
        out.writeObject(_interface);
        
        out.writeInt(_operations.size());
        for (int i=0; i < _operations.size(); i++) {
            out.writeObject(_operations.get(i));
        }
        
        out.writeInt(_contexts.size());
        for (int i=0; i < _contexts.size(); i++) {
            out.writeObject(_contexts.get(i));
        }
        
        out.writeInt(_merged.size());
        for (int i=0; i < _merged.size(); i++) {
            out.writeObject(_merged.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _interface = (String)in.readObject();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _operations.add((OperationDefinition)in.readObject());
        }
        
        len = in.readInt();
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
        
        len = in.readInt();
        for (int i=0; i < len; i++) {
            _merged.add((ServiceDefinition)in.readObject());
        }
    }
}
