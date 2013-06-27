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
package org.overlord.rtgov.activity.model.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This activity type represents a message exchange.
 *
 */
@Entity
public abstract class MessageExchange extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _messageType=null;
    private String _content=null;
    
    /**
     * The default constructor.
     */
    public MessageExchange() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param mex The message exchange to copy
     */
    public MessageExchange(MessageExchange mex) {
        super(mex);
        _messageType = mex._messageType;
        _content = mex._content;
    }
    
    /**
     * This method sets the message type.
     * 
     * @param mtype The message type
     */
    public void setMessageType(String mtype) {
        _messageType = mtype;
    }
    
    /**
     * This method gets the message type.
     * 
     * @return The message type
     */
    public String getMessageType() {
        return (_messageType);
    }
    
    /**
     * This method sets the content.
     * 
     * @param content The content
     */
    public void setContent(String content) {
        _content = content;
    }
    
    /**
     * This method gets the content.
     * 
     * @return The content
     */
    @Column(length=10240)
    @Lob
    public String getContent() {
        return (_content);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_messageType);
        out.writeObject(_content);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _messageType = (String)in.readObject();
        _content = (String)in.readObject();
    }
}
