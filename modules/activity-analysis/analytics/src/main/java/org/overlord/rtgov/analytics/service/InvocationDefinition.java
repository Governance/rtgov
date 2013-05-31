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
import java.util.Collections;

/**
 * This class represents the invocation details associated
 * with a called service.
 *
 */
public class InvocationDefinition implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _interface=null;
    private String _operation=null;
    private String _fault=null;
    private InvocationMetric _metrics=new InvocationMetric();
    private java.util.List<InvocationDefinition> _merged=
            new java.util.ArrayList<InvocationDefinition>();

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
     * This method merges the supplied invocation
     * definition.
     * 
     * @param id The invocation definition to merge
     */
    public void merge(InvocationDefinition id) {
        
        getMetrics().merge(id.getMetrics());
        
        _merged.add(id);
    }
    
    /**
     * This method returns the list of merged invocation definitions.
     * 
     * @return The merged list
     */
    public java.util.List<InvocationDefinition> getMerged() {
        return (Collections.unmodifiableList(_merged));
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_interface);
        out.writeObject(_operation);
        out.writeObject(_fault);
        out.writeObject(_metrics);
        
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
        _operation = (String)in.readObject();
        _fault = (String)in.readObject();
        _metrics = (InvocationMetric)in.readObject();
        
        int len = in.readInt();
        for (int i=0; i < len; i++) {
            _merged.add((InvocationDefinition)in.readObject());
        }
    }
}
