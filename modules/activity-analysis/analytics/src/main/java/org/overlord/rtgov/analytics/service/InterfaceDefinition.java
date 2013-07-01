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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the details associated
 * with an interface type.
 *
 */
public class InterfaceDefinition implements java.io.Externalizable {
    
    private static final Logger LOG=Logger.getLogger(InterfaceDefinition.class.getName());

    private static final int VERSION = 1;

    private String _interface=null;
    private java.util.List<OperationDefinition> _operations=
                    new java.util.ArrayList<OperationDefinition>();
    
    /**
     * Default constructor.
     */
    public InterfaceDefinition() {
    }

    /**
     * This method creates a shallow copy.
     * 
     * @return The shallow copy
     */
    public InterfaceDefinition shallowCopy() {
        InterfaceDefinition ret=new InterfaceDefinition();
        
        ret.setInterface(_interface);
        
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
     * with the interface.
     * 
     * @param operations The operations
     */
    public void setOperations(java.util.List<OperationDefinition> operations) {
        _operations = operations;
    }
    
    /**
     * This method returns the list of operations associated
     * with the interface.
     * 
     * @return The operations
     */
    public java.util.List<OperationDefinition> getOperations() {
        return (_operations);
    }
    
    /**
     * This method returns the operation associated with the supplied
     * name, if defined within the interface definition.
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
     * This method merges the supplied definition with this
     * interface definition.
     * 
     * @param id The interface definition to merge
     * @throws Exception Failed to merge
     */
    public void merge(InterfaceDefinition id) throws Exception {
            
        if (id == null || !id.getInterface().equals(getInterface())) {
            throw new IllegalArgumentException("Invalid interface definition");
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Pre-merge this=["+this+"] with=["+id+"]");
        }

        // Examine operation definitions - merge existing and
        // transfer undefined
        for (int i=0; i < id.getOperations().size(); i++) {
            OperationDefinition opdef=id.getOperations().get(i);
            
            OperationDefinition cur=getOperation(opdef.getName());
            
            if (cur == null) {
                cur = opdef.shallowCopy();
                getOperations().add(cur);
            }

            cur.merge(opdef);
        }
        
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
        
        if (obj instanceof InterfaceDefinition
                  && ((InterfaceDefinition)obj).getInterface().equals(_interface)) {
            return (true);
        }
        
        return (false);
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
    }
}
