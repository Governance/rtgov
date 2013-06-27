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
package org.overlord.rtgov.active.collection.jee;

/**
 * This interface represents the JMX management interface
 * to the Event Processor Network management capability.
 *
 */
public interface ACManagementMBean {
    
    /**
     * This method sets the house keeping interval.
     * 
     * @param interval The interval
     */
    public void setHouseKeepingInterval(long interval);
    
    /**
     * This method gets the house keeping interval.
     * 
     * @return The interval
     */
    public long getHouseKeepingInterval();

}
