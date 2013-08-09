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
package org.overlord.rtgov.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * This class represents context information that can be used to
 * correlate this (set of) activity with other activities related
 * to the same business/service transaction.
 *
 */
@Embeddable
public class Context implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private Type _type=Type.Conversation;
    private String _value=null;
    private long _timeframe=0;

    /**
     * The default constructor.
     */
    public Context() {
    }
 
    /**
     * This constructor sets the fields for the context.
     * 
     * @param type The context type
     * @param value The value
     */
    public Context(Type type, String value) {
        _type = type;
        _value = value;
    }
    
    /**
     * The copy constructor.
     * 
     * @param context The context to copy
     */
    public Context(Context context) {
        _type = context._type;
        _value = context._value;
    }
    
    /**
     * This method returns the type.
     * 
     * @return The type
     */
    @Column(name="type")
    @Enumerated(EnumType.STRING)
    public Type getType() {
        return (_type);
    }
    
    /**
     * This method sets the type.
     * 
     * @param type The type
     */
    public void setType(Type type) {
        _type = type;
    }
    
    /**
     * This method returns the value.
     * 
     * @return The value
     */
    @Column(name="value")
    public String getValue() {
        return (_value);
    }
    
    /**
     * This method sets the value.
     * 
     * @param value The value
     */
    public void setValue(String value) {
        _value = value;
    }
    
    /**
     * This method indicates whether the context represents a link source.
     * 
     * @return Whether a link source
     */
    public boolean linkSource() {
        return (_type == Type.Link && _timeframe != 0);
    }
    
    /**
     * This method indicates whether the context represents a link target.
     * 
     * @return Whether a link target
     */
    public boolean linkTarget() {
        return (_type == Type.Link && _timeframe == 0);
    }
    
    /**
     * This method returns the timeframe.
     * 
     * @return The timeframe
     */
    @Column(name="timeframe")
    public long getTimeframe() {
        return (_timeframe);
    }
    
    /**
     * This method sets the timeframe. This property
     * is used in conjunction with the 'Link' context type.
     * If this value is 0, then it means the activity
     * associated with this context is the link target. The
     * link source must identify the timeframe appropriate
     * for the link.
     * 
     * @param timeframe The timeframe
     */
    public void setTimeframe(long timeframe) {
        _timeframe = timeframe;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_value == null ? 0 : _value.hashCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof Context) {
            Context ctx=(Context)obj;
            
            if (ctx._type == _type
                    && ((ctx._value != null && ctx._value.equals(_value))
                        || (ctx._value == null && _value == null))) {
                return (true);
            }
        }
        
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("Context["+_type+":"+_value+":"+_timeframe+"]");
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_type);
        out.writeObject(_value);
        out.writeLong(_timeframe);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _type = (Type)in.readObject();
        _value = (String)in.readObject();
        _timeframe = in.readLong();
    }

    /**
     * This enumerated type represents the type of the context.
     *
     */
    public enum Type {
        
        /**
         * A 'conversation id' represents a value that can be used to correlate
         * activities across distributed services. These context types
         * will be globally unique, and may refer to values that are
         * carried in the application message contents.
         */
        Conversation,
        
        /**
         * The 'endpoint id' type represents an id that may be associated
         * with the executable unit enacting the service/process being monitored,
         * and can therefore be used to correlate local activities as being
         * part of the same executable unit.
         */
        Endpoint,
        
        /**
         * This context type represents an id associated with a particular message
         * being exchanged between distributed participants.
         */
        Message,
        
        /**
         * This context type represents an association between two or more activities
         * that will usually be constrained to a specified timeframe.
         */
        Link
        
    }
}
