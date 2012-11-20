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
package org.overlord.bam.call.trace.model;

/**
 * This class represents a general task in the
 * call trace tree.
 *
 */
public class Task extends TraceNode {

    private String _description=null;
    private java.util.Map<String,String> _properties=new java.util.HashMap<String, String>();
    
    /**
     * This method returns the description of the task.
     *
     * @return The description
     */
    public String getDescription() {
        return (_description);
    }
    
    /**
     * This method sets the description of the task.
     * 
     * @param description The description
     */
    public void setDescription(String description) {
        _description = description;
    }

    /**
     * This method returns the properties of the task.
     *
     * @return The properties
     */
    public java.util.Map<String,String> getProperties() {
        return (_properties);
    }
    
    /**
     * This method sets the properties of the task.
     * 
     * @param properties The properties
     */
    public void setProperties(java.util.Map<String,String> properties) {
        _properties = properties;
    }

}
