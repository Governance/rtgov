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

/**
 * This class represents an operation within a service type's
 * interface.
 *
 */
public class OperationDefinition implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _name=null;
    private RequestResponseDefinition _requestResponse=null;
    private java.util.List<RequestFaultDefinition> _requestFaults=
            new java.util.ArrayList<RequestFaultDefinition>();

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
            if (getRequestResponse() != null) {
                getRequestResponse().merge(opdef.getRequestResponse());
            } else {
                setRequestResponse(opdef.getRequestResponse());
            }
        }
        
        for (int i=0; i < opdef.getRequestFaults().size(); i++) {
            RequestFaultDefinition rfd=opdef.getRequestFaults().get(i);
            
            RequestFaultDefinition cur=getRequestFault(rfd.getFault());
            
            if (cur != null) {
                cur.merge(rfd);
            } else {
                getRequestFaults().add(rfd);
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
        out.writeObject(_requestResponse);
        
        out.writeInt(_requestFaults.size());
        for (int i=0; i < _requestFaults.size(); i++) {
            out.writeObject(_requestFaults.get(i));
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
    }
}
