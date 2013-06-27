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
package org.overlord.rtgov.activity.collector;

/**
 * This interface defines the managed attributes and operations
 * for the BatchedActivityLogger.
 *
 */
public interface BatchedActivityUnitLoggerMBean {

    /**
     * This method sets the maximum time interval
     * that should be logged within a single batch.
     * 
     * @param max The maximum number of messages
     */
    public void setMaxTimeInterval(long max);
    
    /**
     * This method returns the maximum time interval
     * that should be logged within a single batch.
     * 
     * @return The maximum number of messages
     */
    public long getMaxTimeInterval();
    
    /**
     * This method sets the maximum number of messages
     * that should be logged within a single batch.
     * 
     * @param max The maximum number of messages
     */
    public void setMaxUnitCount(int max);
    
    /**
     * This method returns the maximum number of messages
     * that should be logged within a single batch.
     * 
     * @return The maximum number of messages
     */
    public int getMaxUnitCount();
    
}
