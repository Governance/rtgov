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
