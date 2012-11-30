/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.switchyard.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;

import org.overlord.bam.activity.collector.ActivityCollector;
import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.app.LogMessage;
import org.overlord.bam.switchyard.ActivityReporter;

/**
 * This interface represents the capability for recording
 * activity information from a switchyard application.
 *
 */
public class DefaultActivityReporter implements ActivityReporter {

    private static final Logger LOG=Logger.getLogger(ExchangeInterceptor.class.getName());
    
    private static final String ACTIVITY_COLLECTOR = "java:global/overlord-bam/ActivityCollector";

    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;
    
    /**
     * This method initializes the auditor.
     */
    @PostConstruct
    protected void init() {
        if (_activityCollector == null) {
            try {
                InitialContext ctx=new InitialContext();
                
                _activityCollector = (ActivityCollector)ctx.lookup(ACTIVITY_COLLECTOR);
                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to initialize activity collector", e);
            }
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("*********** Exchange Interceptor Initialized with collector="+_activityCollector);
        }
        
        _initialized = true;
    }

    /**
     * This method can be used to report general information.
     * 
     * @param info The information
     */
    public void logInfo(String info) {
        LogMessage lm=new LogMessage();
        lm.setMessage(info);
        lm.setLevel(LogMessage.Level.Information);
        
        reportActivity(lm);
    }
    
    /**
     * This method can be used to report warning information.
     * 
     * @param warning The warning description
     */
    public void logWarning(String warning) {
        LogMessage lm=new LogMessage();
        lm.setMessage(warning);
        lm.setLevel(LogMessage.Level.Warning);
        
        reportActivity(lm);
    }
    
    /**
     * This method can be used to report error information.
     * 
     * @param error The error description
     */
    public void logError(String error) {
        LogMessage lm=new LogMessage();
        lm.setMessage(error);
        lm.setLevel(LogMessage.Level.Error);
        
        reportActivity(lm);
    }
    
    /**
     * This method reports the activity event to the
     * collector.
     * 
     * @param actType The activity type
     */
    protected void reportActivity(ActivityType actType) {
        if (!_initialized) {
            init();
        }
        
        _activityCollector.record(actType);
    }
    
}
