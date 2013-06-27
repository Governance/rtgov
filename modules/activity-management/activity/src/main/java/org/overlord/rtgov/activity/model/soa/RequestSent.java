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
package org.overlord.rtgov.activity.model.soa;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * This activity type represents a sent request.
 *
 */
@Entity
public class RequestSent extends RPCActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    /**
     * The default constructor.
     */
    public RequestSent() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param rpc The rpc activity to copy
     */
    public RequestSent(RequestSent rpc) {
        super(rpc);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isRequest() {
        return (true);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    public boolean isServiceProvider() {
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
    }
}
