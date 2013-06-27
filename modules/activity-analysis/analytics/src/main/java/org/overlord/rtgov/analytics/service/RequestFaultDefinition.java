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

/**
 * This class represents a request/fault MEP associated
 * with a service operation.
 *
 */
public class RequestFaultDefinition extends MEPDefinition implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _fault=null;

    /**
     * Default constructor.
     */
    public RequestFaultDefinition() {
    }

    /**
     * This method creates a shallow copy.
     * 
     * @return The shallow copy
     */
    protected RequestFaultDefinition shallowCopy() {
        RequestFaultDefinition ret=new RequestFaultDefinition();
        
        initCopy(ret);
        
        ret.setFault(_fault);
        
        return (ret);
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
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_fault.hashCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        
        if (obj instanceof RequestFaultDefinition
                  && ((RequestFaultDefinition)obj).getFault().equals(_fault)) {
            return (true);
        }
        
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_fault);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _fault = (String)in.readObject();
    }
}
