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
package org.overlord.rtgov.ep;

import org.overlord.rtgov.common.service.Service;

/**
 * This interface provides services to the EventProcessor
 * implementations that process the events.
 *
 */
public interface EPContext {

    /**
     * This method is used to pass a result, obtained
     * from processing an event, back to the environment
     * managing the event processing.
     * 
     * @param result The result
     */
    public void handle(Object result);

    /**
     * This method logs information.
     * 
     * @param info The information
     */
    public void logInfo(String info);

    /**
     * This method logs an error.
     * 
     * @param error The error
     */
    public void logError(String error);

    /**
     * This method logs debug information.
     * 
     * @param debug The debug information
     */
    public void logDebug(String debug);

    /**
     * This method returns the named service if available.
     * 
     * @param name The service name
     * @return The service, or null if not found
     */
    public Service getService(String name);
    
}
