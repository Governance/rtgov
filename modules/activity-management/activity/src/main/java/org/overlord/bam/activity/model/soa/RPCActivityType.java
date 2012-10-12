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
package org.overlord.bam.activity.model.soa;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.Context;

/**
 * This activity type represents a RPC activity.
 *
 */
@Entity
public abstract class RPCActivityType extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _serviceType=null;
    private String _operation=null;
    private String _fault=null;

    private String _messageType=null;
    private String _content=null;
    
    private String _messageId=null;
    
    /**
     * The default constructor.
     */
    public RPCActivityType() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param rpc The RPC activity to copy
     */
    public RPCActivityType(RPCActivityType rpc) {
        _serviceType = rpc._serviceType;
        _operation = rpc._operation;
        _fault = rpc._fault;
        _messageType = rpc._messageType;
        _content = rpc._content;
        _messageId = rpc._messageId;
    }
    
    /**
     * This method determines whether the RPC activity is
     * associated with the service provider.
     * 
     * @return Whether associated with the service provider
     */
    public abstract boolean isServiceProvider();
    
    /**
     * This method determines whether the RPC activity is
     * a request.
     * 
     * @return Whether a request
     */
    public abstract boolean isRequest();
    
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
     * This method sets the fault.
     * 
     * @param fault The fault
     */
    public void setFault(String fault) {
        _fault = fault;
    }
    
    /**
     * This method gets the fault.
     * 
     * @return The fault
     */
    public String getFault() {
        return (_fault);
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
     * This method sets the message id.
     * 
     * @param messageId The message id
     */
    public void setMessageId(String messageId) {
        _messageId = messageId;
    }
    
    /**
     * This method gets the message id. When used
     * for correlation against a response, it should
     * only be used to correlate against the
     * sending or receiving action performed in the
     * same service - not in the endpoint being
     * communicated with, as the message id may not
     * be carried with the message content, as is
     * therefore only relevant in the local service
     * context.
     * 
     * @return The message id
     */
    public String getMessageId() {
        return (_messageId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.List<Context> deriveContexts() {
        java.util.List<Context> ret=super.deriveContexts();
        
        if (_messageId != null) {
            ret.add(new Context(Context.Type.Message, _messageId));
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return (getClass().getSimpleName()+":"
                +" serviceType="+_serviceType
                +" operation="+_operation
                +" fault="+_fault
                +" messageType="+_messageType
                +" content="+_content
                +" messageId="+_messageId);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_serviceType);
        out.writeObject(_operation);
        out.writeObject(_fault);
        out.writeObject(_messageType);
        out.writeObject(_content);
        out.writeObject(_messageId);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _serviceType = (String)in.readObject();
        _operation = (String)in.readObject();
        _fault = (String)in.readObject();
        _messageType = (String)in.readObject();
        _content = (String)in.readObject();
        _messageId = (String)in.readObject();
    }
}
