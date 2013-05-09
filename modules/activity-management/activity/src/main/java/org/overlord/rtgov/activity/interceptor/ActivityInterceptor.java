/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.interceptor;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.ep.Predicate;

/**
 * This class represents an activity interceptor, associated with the
 * collector mechanism, to process the observed activities.
 *
 */
public class ActivityInterceptor {

    private static final String ACTIVITY_SOURCE = "collector";
    
    private String _name=null;
    private String _version=null;

    private Predicate _predicate=null;
    private EventProcessor _eventProcessor=null;
    
    private boolean _initialized=false;
    
    /**
     * Default constructor.
     */
    public ActivityInterceptor() {
    }
    
    /**
     * This method returns the name of the activity interceptor.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the activity interceptor.
     * 
     * @param name The name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method returns the version of the activity interceptor.
     * 
     * @return The version
     */
    public String getVersion() {
        return (_version);
    }

    /**
     * This method sets the version of the activity interceptor.
     * 
     * @param version The version
     */
    public void setVersion(String version) {
        _version = version;
    }
    
    /**
     * This method sets the optional predicate that can be
     * used to assess whether a supplied activity is
     * applicable to the interceptor.
     * 
     * @param predicate The predicate
     */
    public void setPredicate(Predicate predicate) {
        _predicate = predicate;
    }
    
    /**
     * This method gets the optional predicate that can be
     * used to assess whether a supplied activity is
     * applicable to the interceptor.
     * 
     * @return The predicate, or null if not relevant
     */
    public Predicate getPredicate() {
        return (_predicate);
    }
    
    /**
     * This method sets the event processor used to
     * intercept and analyse the activity event.
     * 
     * @param ep The event processor
     */
    public void setEventProcessor(EventProcessor ep) {
        _eventProcessor = ep;
    }
    
    /**
     * This method gets the event processor used to
     * intercept and analyse the activity event.
     * 
     * @return The event processor
     */
    public EventProcessor getEventProcessor() {
        return (_eventProcessor);
    }
    
    /**
     * Initialize the activity interceptor.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        
        if (!_initialized) {
            _initialized = true;
            
            if (_predicate != null) {
                _predicate.init();
            }
            if (_eventProcessor != null) {
                _eventProcessor.init();
            }
        }
    }

    /**
     * This method processes the intercepted activity event.
     * 
     * @param actType The activity type
     * @throws Exception Failed to process activity
     */
    public void process(ActivityType actType) throws Exception {
        
        // Check if predicate defined and if so, the event is applicable
        if (_predicate == null || _predicate.evaluate(actType)) {
            
            // Process the event
            _eventProcessor.process(ACTIVITY_SOURCE, actType, 0);
        }
    }
    
}
