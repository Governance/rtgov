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
package org.overlord.rtgov.activity.processor;

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This interface manages a set of InformationProcessor
 * implementations.
 *
 */
public interface InformationProcessorManager {
    
    /**
     * This method registers the information processor.
     * 
     * @param ip The information processor
     * @throws Exception Failed to register
     */
    public void register(InformationProcessor ip) throws Exception;
    
    /**
     * This method returns the information processor associated
     * with the supplied name.
     * 
     * @param name The name
     * @return The information processor, or null if not found
     */
    public InformationProcessor getInformationProcessor(String name);
    
    /**
     * This method processes supplied information to
     * extract relevant details, and then return an
     * appropriate representation of that information
     * for public distribution.
     * 
     * @param processor The optional information processor to use
     * @param type The information type
     * @param info The information to be processed
     * @param headers The optional header information
     * @param actType The activity type to be annotated with
     *              details extracted from the information
     * @return The public representation of the information
     */
    public String process(String processor, String type,
                    Object info, java.util.Map<String, Object> headers, ActivityType actType);
    
    /**
     * This method registers the information processor.
     * 
     * @param ip The information processor
     * @throws Exception Failed to unregister
     */
    public void unregister(InformationProcessor ip) throws Exception;
    
}
