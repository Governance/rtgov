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
package org.overlord.bam.analytics.principal;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents a change notification related to a principal.
 *
 */
public class PrincipalChangeNotification implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    /**
     * This enumeration type represents the severity of
     * the situation.
     *
     */
    public enum Type {
        
        /**
         * Set the properties on the principal.
         */
        Set
    }
    
    private String _principal=null;
    private Type _type=Type.Set;
    private java.util.Map<String,java.io.Serializable> _properties=
                        new java.util.HashMap<String,java.io.Serializable>();

    /**
     * This method sets the principal.
     * 
     * @param principal The principal
     * @return The notification
     */
    public PrincipalChangeNotification setPrincipal(String principal) {
        _principal = principal;
        return (this);
    }
    
    /**
     * This method gets the principal.
     * 
     * @return The principal
     */
    public String getPrincipal() {
        return (_principal);
    }
    
    /**
     * This method returns the change type.
     * 
     * @return The change type
     */
    public Type getType() {
        return _type;
    }

    /**
     * This method sets the change type.
     * 
     * @param type The change type
     */
    public void setType(Type type) {
        _type = type;
    }

    /**
     * This method sets the property value associated with the
     * supplied name.
     * 
     * @param name The name
     * @param value The value
     * @return The notification
     */
    public PrincipalChangeNotification setProperty(String name, java.io.Serializable value) {
        _properties.put(name, value);
        
        return (this);
    }
    
    /**
     * This method returns the properties associated with the
     * notification.
     * 
     * @return The properties
     */
    public java.util.Map<String,java.io.Serializable> getProperties() {
        return (_properties);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_principal);
        out.writeObject(_type);
        out.writeObject(_properties);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _principal = (String)in.readObject();
        _type = (Type)in.readObject();
        _properties = (java.util.Map<String,java.io.Serializable>)in.readObject();
    }
}
