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
package org.overlord.rtgov.internal.activity.client.jee;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.processor.InformationProcessorManager;
import org.overlord.rtgov.activity.validator.ActivityValidatorManager;

/**
 * This class provides access to the client side components. When deployed in
 * a CDI container, it pre-loads the client components ready to be accessed
 * through the appropriate components 'accessor' class.
 *
 */
@Singleton
@ApplicationScoped
@Startup
@ConcurrencyManagement(BEAN)
public class JEEClientManager {
    
    private static final Logger LOG=Logger.getLogger(JEEClientManager.class.getName());
    
    @Inject @Dependent
    private ActivityCollector _activityCollector;
    
    @Inject @Dependent
    private InformationProcessorManager _informationProcessorManager;
    
    @Inject @Dependent
    private ActivityValidatorManager _activityValidatorManager;
    
    /**
     * The constructor.
     */
    public JEEClientManager() {
    }
    
    /**
     * The initialize method.
     */
    @PostConstruct
    public void init() {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register client components: collector="+_activityCollector);
        }
    }
    
    /**
     * This method returns the activity collector.
     * 
     * @return The activity collector
     */
    public ActivityCollector getActivityCollector() {
        return (_activityCollector);
    }
    
    /**
     * This method returns the information processor manager.
     * 
     * @return The information processor manager
     */
    public InformationProcessorManager getInformationProcessorManager() {
        return (_informationProcessorManager);
    }
    
    /**
     * This method returns the activity validator manager.
     * 
     * @return The activity validator manager
     */
    public ActivityValidatorManager getActivityValidatorManager() {
        return (_activityValidatorManager);
    }
}
