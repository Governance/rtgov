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
package org.overlord.rtgov.activity.model.bpm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.Context;

/**
 * This activity type represents a BPM actvity.
 *
 */
@Entity
public abstract class BPMActivityType extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _processType=null;
    private String _instanceId=null;

    /**
     * The default constructor.
     */
    public BPMActivityType() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param ba The bpm activity to copy
     */
    public BPMActivityType(BPMActivityType ba) {
        super(ba);
        _processType = ba._processType;
        _instanceId = ba._instanceId;
    }
    
    /**
     * This method sets the process type.
     * 
     * @param processType The process type
     */
    public void setProcessType(String processType) {
        _processType = processType;
        
        updateEndpointContext();
    }
    
    /**
     * This method gets the process type.
     * 
     * @return The process type
     */
    public String getProcessType() {
        return (_processType);
    }
   
    /**
     * This method sets the instance id. The information is
     * actually stored as a context entry for the Endpoint type.
     * 
     * @param instanceId The instance id
     */
    public void setInstanceId(String instanceId) {
        _instanceId = instanceId;
        
        updateEndpointContext();
    }
    
    /**
     * This method gets the instance id.
     * 
     * @return The instance id
     */
    public String getInstanceId() {
        return (_instanceId);
    }

    /**
     * This method updates the endpoint context value
     * when the process type and/or instance id are
     * changed.
     */
    protected void updateEndpointContext() {
        Context current=null;
        
        try {
            for (Context context : getContext()) {
                if (context.getType() == Context.Type.Endpoint) {
                    current = context;
                    break;
                }
            }
            
            if (current == null) {
                current = new Context();
                current.setType(Context.Type.Endpoint);
                getContext().add(current);
            }
            
            String endpoint="";
            
            if (_processType != null) {
                endpoint = _processType;
                
                if (_instanceId != null) {
                    endpoint += ":";
                }
            }
            
            if (_instanceId != null) {
                endpoint += _instanceId;
            }
            
            current.setValue(endpoint);
        } catch (Throwable t) {
            // RTGOV-278 NPE exception occurs when de-serializing
            // BPM activity type events with Hibernate, where the
            // query accesses the context information.
            current = null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return (getClass().getSimpleName()+":"
                +" processType="+_processType
                +" instanceId="+_instanceId);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_processType);
        out.writeObject(_instanceId);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _processType = (String)in.readObject();
        _instanceId = (String)in.readObject();
    }
}
