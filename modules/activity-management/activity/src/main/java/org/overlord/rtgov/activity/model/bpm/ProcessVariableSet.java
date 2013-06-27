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
