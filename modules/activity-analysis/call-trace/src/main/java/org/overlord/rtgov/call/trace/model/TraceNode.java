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
@org.codehaus.enunciate.json.JsonType
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
