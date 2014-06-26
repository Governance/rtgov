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

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * This activity type represents a sent request.
 *
 */
@Entity
public class ResponseSent extends RPCActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _replyToId=null;

    /**
     * The default constructor.
     */
    public ResponseSent() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param rpc The rpc activity to copy
     */
    public ResponseSent(ResponseSent rpc) {
        super(rpc);
        _replyToId = rpc._replyToId;
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    @JsonIgnore
    public boolean isRequest() {
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    @Transient
    @JsonIgnore
    public boolean isServiceProvider() {
        return (true);
    }
    
    /**
     * This method sets the 'reply to' message id.
     * 
     * @param replyToId The 'reply to' message id
     */
    public void setReplyToId(String replyToId) {
        _replyToId = replyToId;
    }
    
    /**
     * This method gets the 'reply to' message id. When used
     * for correlation against a request, it should
     * only be used to correlate against the
     * sending or receiving action performed in the
     * same service - not in the endpoint being
     * communicated with, as the message id may not
     * be carried with the message content, as is
     * therefore only relevant in the local service
     * context.
     * 
     * @return The 'reply to' message id
     */
    public String getReplyToId() {
        return (_replyToId);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return (super.toString()
                +" replyToId="+_replyToId);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);

        out.writeObject(_replyToId);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now

        _replyToId = (String)in.readObject();
    }
}
