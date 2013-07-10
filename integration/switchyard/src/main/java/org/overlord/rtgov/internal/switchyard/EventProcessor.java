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
package org.overlord.rtgov.internal.switchyard;

import org.overlord.rtgov.activity.collector.ActivityCollector;

/**
 * This interface represents an event processor that is used to process
 * events observed from applications running on SwitchYard.
 *
 */
public interface EventProcessor {

    /**
     * This method initializes the event processor, supplying
     * the activity collector to use for reporting activities.
     * 
     * @param collector The activity collector
     */
    public void init(ActivityCollector collector);
    
    /**
     * This method returns the event type that can be handled
     * by this event processor.
     * 
     * @return The event type associated with the processor
     */
    public Class<?> getEventType();
    
}
