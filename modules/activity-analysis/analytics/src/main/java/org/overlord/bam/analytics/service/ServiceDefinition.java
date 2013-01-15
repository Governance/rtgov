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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.activity.model.Context;
import org.overlord.bam.analytics.service.util.ServiceDefinitionUtil;

/**
 * This class represents the service contract details associated
 * with a service type.
 *
 */
public class ServiceDefinition implements java.io.Externalizable {
    
    private static final Logger LOG=Logger.getLogger(ServiceDefinition.class.getName());

    private static final int VERSION = 1;

    private String _serviceType=null;
    private java.util.List<OperationDefinition> _operations=
                    new java.util.ArrayList<OperationDefinition>();
    private java.util.List<Context> _contexts=new java.util.Vector<Context>();
    
    /**
     * Default constructor.
     */
    public ServiceDefinition() {
    }

    /**
     * Copy constructor.
     * 
     * @param sd The source to copy
     */
    public ServiceDefinition(ServiceDefinition sd) {
        _serviceType = sd.getServiceType();
        
        for (OperationDefinition op : sd.getOperations()) {
            _operations.add(new OperationDefinition(op));
        }
        
        // Copy contexts
        for (Context c : sd.getContext()) {
            _contexts.add(new Context(c));
        }
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
        for (int i=0; i < sd.getOperations().size(); i++) {
            OperationDefinition opdef=sd.getOperations().get(i);
            
            OperationDefinition cur=getOperation(opdef.getName());
            
            if (cur != null) {
                cur.merge(opdef);
            } else {
                getOperations().add(new OperationDefinition(opdef));
            }
        }
        
        if (retainContexts) {
            for (Context c : sd.getContext()) {
                if (!_contexts.contains(c)) {
                    _contexts.add(c);
                }
            }
        }

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
        
        out.writeInt(_operations.size());
        for (int i=0; i < _operations.size(); i++) {
            out.writeObject(_operations.get(i));
        }
        
        out.writeInt(_contexts.size());
        for (int i=0; i < _contexts.size(); i++) {
            out.writeObject(_contexts.get(i));
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
            _operations.add((OperationDefinition)in.readObject());
        }
        
        len = in.readInt();
        for (int i=0; i < len; i++) {
            _contexts.add((Context)in.readObject());
        }
    }
}
