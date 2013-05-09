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
package org.overlord.rtgov.activity.model.bpm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

/**
 * This activity type represents a process variable being set.
 *
 */
@Entity
public class ProcessVariableSet extends BPMActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _variableName=null;
    private String _variableType=null;
    private String _variableValue=null;

    /**
     * The default constructor.
     */
    public ProcessVariableSet() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param ba The bpm activity to copy
     */
    public ProcessVariableSet(ProcessVariableSet ba) {
        super(ba);
        _variableName = ba._variableName;
        _variableType = ba._variableType;
        _variableValue = ba._variableValue;
    }
    
    /**
     * This method sets the variable name.
     * 
     * @param name The name
     */
    public void setVariableName(String name) {
        _variableName = name;
    }
    
    /**
     * This method gets the variable name.
     * 
     * @return The name
     */
    public String getVariableName() {
        return (_variableName);
    }
    
    /**
     * This method sets the variable type.
     * 
     * @param type The type
     */
    public void setVariableType(String type) {
        _variableType = type;
    }
    
    /**
     * This method gets the variable type.
     * 
     * @return The type
     */
    public String getVariableType() {
        return (_variableType);
    }
    
    /**
     * This method sets the variable value.
     * 
     * @param value The value
     */
    public void setVariableValue(String value) {
        _variableValue = value;
    }
    
    /**
     * This method gets the variable value.
     * 
     * @return The value
     */
    public String getVariableValue() {
        return (_variableValue);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_variableName);
        out.writeObject(_variableType);
        out.writeObject(_variableValue);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _variableName = (String)in.readObject();
        _variableType = (String)in.readObject();
        _variableValue = (String)in.readObject();
    }
}
