/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This activity type represents a message exchange.
 *
 */
public class MessageExchange extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _serviceType=null;
    private String _operation=null;
    private String _fault=null;

    /**
     * The invocation type.
     *
     */
    public enum InvocationType {
        /** Undefined. **/
        Undefined,
        /** Invocation is a request. **/
        Request,
        /** Invocatoin is a response. **/
        Response
    }
    
    private InvocationType _invocationType=InvocationType.Undefined;
    
    /**
     * The direction of the message.
     *
     */
    public enum Direction {
        /** Message is inbound. **/
        Inbound,
        /** Message is outbound. **/
        Outbound
    }
    
    private Direction _direction=Direction.Inbound;
    
    // GPB? should we support multipart messages, where on the wire we have the diff parts?
    private String _messageType=null;
    private String _content=null;
    
    private String _correlation=null;
    
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
        _serviceType = mex._serviceType;
        _operation = mex._operation;
        _fault = mex._fault;
        _invocationType = mex._invocationType;
        _direction = mex._direction;
        _messageType = mex._messageType;
        _content = mex._content;
        _correlation = mex._correlation;
    }
    
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
     * This method sets the invocation type.
     * 
     * @param itype The invocation type
     */
    public void setInvocationType(InvocationType itype) {
        _invocationType = itype;
    }
    
    /**
     * This method gets the invocation type.
     * 
     * @return The invocation type
     */
    public InvocationType getInvocationType() {
        return (_invocationType);
    }
    
    /**
     * This method sets the direction.
     * 
     * @param direction The direction
     */
    public void setDirection(Direction direction) {
        _direction = direction;
    }
    
    /**
     * This method gets the direction.
     * 
     * @return The direction
     */
    public Direction getDirection() {
        return (_direction);
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
     * This method sets the correlation.
     * 
     * @param correlation The correlation
     */
    public void setCorrelation(String correlation) {
        _correlation = correlation;
    }
    
    /**
     * This method gets the correlation.
     * 
     * @return The correlation
     */
    public String getCorrelation() {
        return (_correlation);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeUTF(_serviceType);
        out.writeUTF(_operation);
        out.writeUTF(_fault);
        out.writeObject(_invocationType);
        out.writeObject(_direction);
        out.writeUTF(_messageType);
        out.writeUTF(_content);
        out.writeUTF(_correlation);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _serviceType = in.readUTF();
        _operation = in.readUTF();
        _fault = in.readUTF();
        _invocationType = (InvocationType)in.readObject();
        _direction = (Direction)in.readObject();
        _messageType = in.readUTF();
        _content = in.readUTF();
        _correlation = in.readUTF();
    }
}
