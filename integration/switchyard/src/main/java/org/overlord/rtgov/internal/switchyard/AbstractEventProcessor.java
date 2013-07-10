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

import java.util.EventObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.model.ActivityType;
import org.switchyard.event.EventObserver;

/**
 * This interface represents an event processor that is used to process
 * events observed from applications running on SwitchYard.
 *
 */
public abstract class AbstractEventProcessor implements EventObserver, EventProcessor {
    
    private static final Logger LOG=Logger.getLogger(AbstractEventProcessor.class.getName());

    private Class<?> _class=null;
    
    private ActivityCollector _collector=null;
    
    /**
     * This is the constructor for the abstract event processor.
     *  
     * @param cls The class associated with the processor
     */
    public AbstractEventProcessor(Class<?> cls) {
        _class = cls;
    }
    
    /**
     * {@inheritDoc}
     */
    public void init(ActivityCollector collector) {
        _collector = collector;
    }
    
    /**
     * {@inheritDoc}
     */
    public Class<?> getEventType() {
        return (_class);
    }
    
    /**
     * This method returns the activity collector.
     * 
     * @return The activity collector
     */
    protected ActivityCollector getActivityCollector() {
        return (_collector);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void notify(EventObject event) {
        if (isCollectionEnabled()) {
            handleEvent(event);
        }
    }
    
    /**
     * This method handles the event from switchyard.
     * 
     * @param event The event
     */
    protected abstract void handleEvent(EventObject event);
    
    /**
     * This method determines whether collection has been enabled.
     * 
     * @return Whether collection is enabled
     */
    protected boolean isCollectionEnabled() {
        return (_collector == null ? false : _collector.isCollectionEnabled());
    }
    
    /**
     * This method records the activity type.
     * 
     * @param at The activity type
     */
    protected void recordActivity(Object event, ActivityType at) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Record event '"+event+"' as activity type: "+at);
        }
        
        getActivityCollector().record(at);
    }
}
