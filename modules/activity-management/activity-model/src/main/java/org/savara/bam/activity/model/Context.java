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
package org.savara.bam.activity.model;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents context information that can be used to
 * correlate this (set of) activity with other activities related
 * to the same business/service transaction.
 *
 */
public class Context implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private ContextType _type=ContextType.Identifier;
    private String _name=null;
    private String _value=null;

    /**
     * The default constructor.
     */
    public Context() {
    }
 
    /**
     * The copy constructor.
     * 
     * @param context The context to copy
     */
    public Context(Context context) {
        _type = context._type;
        _name = context._name;
        _value = context._value;
    }
    
    /**
     * This method returns the type.
     * 
     * @return The type
     */
    public ContextType getType() {
        return (_type);
    }
    
    /**
     * This method sets the type.
     * 
     * @param type The type
     */
    public void setType(ContextType type) {
        _type = type;
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
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_type);
        out.writeUTF(_name);
        out.writeUTF(_value);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _type = (ContextType)in.readObject();
        _name = in.readUTF();
        _value = in.readUTF();
    }

}
