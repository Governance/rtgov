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

/**
 * This class represents an operation within a service interface.
 *
 */
public class OperationDefinition implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _name=null;
    private java.util.List<OperationImplDefinition> _implementations=
            new java.util.ArrayList<OperationImplDefinition>();

    /**
     * Default constructor.
     */
    public OperationDefinition() {
    }

    /**
     * Copy constructor.
     * 
     * @param od The source to copy
     */
    public OperationDefinition(OperationDefinition od) {
        _name = od.getName();
        
        for (OperationImplDefinition sto : od.getImplementations()) {
            _implementations.add(new OperationImplDefinition(sto));
        }
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
     * This method sets the list of implementations associated
     * with the operation.
     * 
     * @param impls The operation implementations
     */
    public void setImplementations(java.util.List<OperationImplDefinition> impls) {
        _implementations = impls;
    }
    
    /**
     * This method returns the list of implementations associated
     * with the operation.
     * 
     * @return The operation implementations
     */
    public java.util.List<OperationImplDefinition> getImplementations() {
        return (_implementations);
    }
    
    /**
     * This method returns the specific operation information associated with the supplied
     * service type, if defined within the operation definition. If the supplied
     * service type is null, it can only be matched against an existing service type
     * op definition with a null service type (i.e. unknown service type).
     * 
     * @param serviceType The service type
     * @return The service type's operation definition, or null if not found
     */
    public OperationImplDefinition getServiceTypeOperation(String serviceType) {
        OperationImplDefinition ret=null;
        
        for (int i=0; i < _implementations.size(); i++) {
            if ((_implementations.get(i).getServiceType() == null
                    && serviceType == null)
                    || (_implementations.get(i).getServiceType() != null
                    && _implementations.get(i).getServiceType().equals(serviceType))) {
                ret = _implementations.get(i);
                break;
            }
        }
        
        return (ret);
    }
    
    /**
     * This method returns the aggregated invocation metric information
     * from the service type specific operation definitions.
     * 
     * @return The invocation metric
     */
    public InvocationMetric getMetrics() {
        java.util.List<InvocationMetric> metrics=
                new java.util.ArrayList<InvocationMetric>();
        
        for (OperationImplDefinition stod : getImplementations()) {
            metrics.add(stod.getMetrics());
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
        
        for (int i=0; i < opdef.getImplementations().size(); i++) {
            OperationImplDefinition stod=opdef.getImplementations().get(i);
            
            OperationImplDefinition cur=getServiceTypeOperation(stod.getServiceType());
             
            if (cur != null) {
                cur.merge(stod);
            } else {
                getImplementations().add(new OperationImplDefinition(stod));
            }
        }
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
        
        out.writeInt(_implementations.size());
        for (int i=0; i < _implementations.size(); i++) {
            out.writeObject(_implementations.get(i));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _name = (String)in.readObject();
        
        int len=in.readInt();
        for (int i=0; i < len; i++) {
            _implementations.add((OperationImplDefinition)in.readObject());
        }
    }
}
