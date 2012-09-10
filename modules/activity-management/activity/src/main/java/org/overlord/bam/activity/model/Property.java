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
package org.overlord.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents a property that can be used to
 * query an activity unit/type.
 *
 */
public class Property implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private String _name=null;
    private String _value=null;

    /**
     * The default constructor.
     */
    public Property() {
    }
 
    /**
     * This constructor sets the fields for the property.
     * 
     * @param name The name
     * @param value The value
     */
    public Property(String name, String value) {
        _name = name;
        _value = value;
    }
    
    /**
     * The copy constructor.
     * 
     * @param prop The property to copy
     */
    public Property(Property prop) {
        _name = prop._name;
        _value = prop._value;
    }
    
    /**
     * This method returns the name.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name.
     * 
     * @param name The name
     */
    public void setName(String name) {
        _name = name;
    }
    
    /**
     * This method returns the value.
     * 
     * @return The value
     */
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
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_value.hashCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof Property) {
            Property prop=(Property)obj;
            
            if (((prop._value != null && prop._value.equals(_value))
                        || (prop._value == null && _value == null))
                    && ((prop._name != null && prop._name.equals(_name))
                            || (prop._name == null && _name == null))) {
                return (true);
            }
        }
        
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("Property["+_name+"="+_value+"]");
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_name);
        out.writeObject(_value);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _name = (String)in.readObject();
        _value = (String)in.readObject();
    }
}
