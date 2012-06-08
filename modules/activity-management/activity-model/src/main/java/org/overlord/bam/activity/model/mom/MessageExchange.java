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
package org.overlord.bam.activity.model.mom;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.bam.activity.model.ActivityType;

/**
 * This activity type represents a message exchange.
 *
 */
public abstract class MessageExchange extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _messageType=null;
    private String _content=null;
    
    private String _destination=null;

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
        _messageType = mex._messageType;
        _content = mex._content;
        _destination = mex._destination;
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
    public String getContent() {
        return (_content);
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
        
        out.writeObject(_messageType);
        out.writeObject(_content);
        out.writeObject(_destination);
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
        _destination = (String)in.readObject();
    }
}
