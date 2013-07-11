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

import javax.persistence.Embeddable;

/**
 * This class represents a reference to an ActivityType contained within an
 * ActivityUnit.
 *
 */
@Embeddable
public class ActivityTypeId implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _unitId=null;
    private int _unitIndex=0;

    /**
     * The default constructor.
     */
    public ActivityTypeId() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param act The activity to copy.
     */
    public ActivityTypeId(ActivityTypeId act) {
        _unitId = act._unitId;
        _unitIndex = act._unitIndex;
    }
    
    /**
     * This constructor initializes the id and index
     * for the reference.
     * 
     * @param id The activity unit id
     * @param index The activity type index within the unit
     */
    public ActivityTypeId(String id, int index) {
        _unitId = id;
        _unitIndex = index;
    }
    
    /**
     * This method sets the activity unit id.
     * 
     * @param id The activity unit id
     */
    public void setUnitId(String id) {
        _unitId = id;
    }
    
    /**
     * This method gets the activity unit id.
     * 
     * @return The activity unit id
     */
    public String getUnitId() {
        return (_unitId);
    }
    
    /**
     * This method sets the index of the activity
     * type within the activity unit.
     * 
     * @param index The index
     */
    public void setUnitIndex(int index) {
        _unitIndex = index;
    }
    
    /**
     * This method sets the index of the activity
     * type within the activity unit.
     * 
     * @return The index
     */
    public int getUnitIndex() {
        return (_unitIndex);
    }
    
    /**
     * This method creates an id associated with the
     * supplied activity type.
     * 
     * @param at The activity type
     * @return The id for the activity type
     */
    public static ActivityTypeId createId(ActivityType at) {
        return (new ActivityTypeId(at.getUnitId(), at.getUnitIndex()));
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj instanceof ActivityTypeId) {
            return (((ActivityTypeId)obj)._unitId.equals(_unitId)
                    && ((ActivityTypeId)obj)._unitIndex == _unitIndex);
        }
        
        return (false);
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (_unitId.hashCode());
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_unitId);
        out.writeInt(_unitIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _unitId = (String)in.readObject();
        _unitIndex = in.readInt();
    }
    
}
