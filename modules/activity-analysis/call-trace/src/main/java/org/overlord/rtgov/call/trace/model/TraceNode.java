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
package org.overlord.rtgov.call.trace.model;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;

/**
 * This abstract base class represents a node in the call
 * trace tree.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({@Type(value=Call.class),
    @Type(value=Task.class) })
public abstract class TraceNode {

    private long _duration=0;
    private int _percentage=0;
    private Status _status=Status.Success;
    private java.util.Map<String,String> _properties=new java.util.HashMap<String, String>();
    
    /**
     * This method returns the duration of the task.
     *
     * @return The duration
     */
    public long getDuration() {
        return (_duration);
    }
    
    /**
     * This method sets the duration of the task.
     * 
     * @param duration The duration
     */
    public void setDuration(long duration) {
        _duration = duration;
    }
    
    /**
     * This method returns the percentage of time
     * taken by this task within a parent call scope.
     * 
     * @return The percentage
     */
    public int getPercentage() {
        return (_percentage);
    }
    
    /**
     * This method sets the percentage of time
     * taken by this task within a parent call scope.
     * 
     * @param percentage The percentage
     */
    public void setPercentage(int percentage) {
        _percentage = percentage;
    }

    /**
     * This method returns the status.
     * 
     * @return The status
     */
    public Status getStatus() {
        return (_status);
    }
    
    /**
     * This method sets the status.
     * 
     * @param status The status
     */
    public void setStatus(Status status) {
        _status = status;
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

    /**
     * This enumerated type represents the completion status
     * of the call.
     *
     */
    public static enum Status {
        
        /**
         * Completed successfully.
         */
        Success,
        
        /**
         * A problem occurred within the scope of the node.
         */
        Warning,

        /**
         * Completed unsuccessfully.
         */
        Fail        
    }
}
