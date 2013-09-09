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
package org.overlord.rtgov.activity.collector.activity.server;

import org.overlord.rtgov.activity.collector.BatchedActivityUnitLoggerMBean;

/**
 * This interface defines the addition operations and attributes
 * for the Activity Server logger.
 *
 */
public interface ActivityServerLoggerMBean extends BatchedActivityUnitLoggerMBean {

    /**
     * This method returns the size of the queue containing activity units
     * pending storage.
     * 
     * @return The number of pending activity units
     */
    public int getPendingActivityUnits();
    
    /**
     * This method returns the number of failures that have occurred since the
     * last successful activity information was logged.
     * 
     * @return The number of failures since last success
     */
    public int getFailuresSinceLastSuccess();
    
}
