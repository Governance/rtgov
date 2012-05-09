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
package org.savara.bam.activity.model.bpm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.savara.bam.activity.model.ActivityType;

/**
 * This activity type represents a BPM actvity.
 *
 */
public abstract class BPMActivityType extends ActivityType implements java.io.Externalizable {

    private static final int VERSION = 1;

    private String _processType=null;
    private String _instanceId=null;

    /**
     * The default constructor.
     */
    public BPMActivityType() {
    }
    
    /**
     * The copy constructor.
     * 
     * @param ba The bpm activity to copy
     */
    public BPMActivityType(BPMActivityType ba) {
        _processType = ba._processType;
        _instanceId = ba._instanceId;
    }
    
    /**
     * This method sets the process type.
     * 
     * @param processType The process type
     */
    public void setProcessType(String processType) {
        _processType = processType;
    }
    
    /**
     * This method gets the process type.
     * 
     * @return The process type
     */
    public String getProcessType() {
        return (_processType);
    }
    
    /**
     * This method sets the instance id.
     * 
     * @param instanceId The instance id
     */
    public void setInstanceId(String instanceId) {
        _instanceId = instanceId;
    }
    
    /**
     * This method gets the instance id.
     * 
     * @return The instance id
     */
    public String getInstanceId() {
        return (_instanceId);
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_processType);
        out.writeObject(_instanceId);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version, as not required for now
        
        _processType = (String)in.readObject();
        _instanceId = (String)in.readObject();
    }
}
