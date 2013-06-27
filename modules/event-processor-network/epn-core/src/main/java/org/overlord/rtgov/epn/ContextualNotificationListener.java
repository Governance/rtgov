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
package org.overlord.rtgov.epn;

/**
 * This class represents an abstract listener interested in information
 * about an Event Processor Network notification subject.
 *
 */
public abstract class ContextualNotificationListener implements NotificationListener {

    /**
     * This method notifies the listener when a situation occurs
     * on the identified network/version/node concerning a list
     * of events.
     * 
     * @param subject The subject
     * @param events The events that have been processed
     */
    public final void notify(String subject, EventList events) {
        // Reset to make sure context class loader related classes are used
        events.reset();
        
        // Resolve the events in the list, in the context of the supplied
        // classloader
        events.resolve(getContextClassLoader());
        
        // Get the client to handle the events
        handleEvents(subject, events);
    }
    
    /**
     * This method returns the context classloader to use when
     * resolving the events.
     * 
     * @return The context classloader
     */
    public abstract ClassLoader getContextClassLoader();
   
    /**
     * This method notifies the listener when a situation occurs
     * on the subject for the identified network/version/node
     * concerning a list of events.
     * 
     * @param subject The subject
     * @param events The events that have been processed
     */
    public abstract void handleEvents(String subject, EventList events);
    
}
