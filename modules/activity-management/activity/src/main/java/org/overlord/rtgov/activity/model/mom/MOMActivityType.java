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
package org.overlord.rtgov.activity.model.mom;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

import org.overlord.rtgov.activity.model.common.MessageExchange;

/**
 * This activity type represents a MOM activity type.
 *
 */
@Entity
public abstract class MOMActivityType extends MessageExchange implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _destination=null;

    /**
     * The default constructor.
     */
    public MOMActivityType() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param mat The activity type to copy
     */
    public MOMActivityType(MOMActivityType mat) {
        super(mat);
        _destination = mat._destination;
    }
    
    /**
     * This method sets the destination.
     * 
     * @param destination The destination
     */
    public void setDestination(String destination) {
        _destination = destination;
    }
    
    /**
     * This method gets the destination.
     * 
     * @return The destination
     */
    public String getDestination() {
        return (_destination);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_destination);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _destination = (String)in.readObject();
    }
}
