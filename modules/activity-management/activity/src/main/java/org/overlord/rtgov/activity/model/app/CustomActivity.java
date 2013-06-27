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
package org.overlord.rtgov.activity.model.app;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.persistence.Entity;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This activity type represents a custom activity.
 *
 */
@Entity
public class CustomActivity extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _customType=null;

    /**
     * The default constructor.
     */
    public CustomActivity() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param ca The custom activity to copy
     */
    public CustomActivity(CustomActivity ca) {
        super(ca);
        _customType = ca._customType;
    }
    
    /**
     * This method sets the custom activity type.
     * 
     * @param type The custom activity type
     */
    public void setCustomType(String type) {
        _customType = type;
    }
    
    /**
     * This method gets the custom activity type.
     * 
     * @return The custom activity type
     */
    public String getCustomType() {
        return (_customType);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        
        out.writeInt(VERSION);
        
        out.writeObject(_customType);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        
        in.readInt(); // Consume version, as not required for now
        
        _customType = (String)in.readObject();
    }
}
