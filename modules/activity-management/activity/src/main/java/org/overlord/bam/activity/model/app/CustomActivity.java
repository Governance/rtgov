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
