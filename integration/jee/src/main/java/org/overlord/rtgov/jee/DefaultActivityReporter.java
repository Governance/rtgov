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
package org.overlord.rtgov.jee;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.InitialContext;

import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.app.CustomActivity;
import org.overlord.rtgov.activity.model.app.LogMessage;

/**
 * This interface represents the capability for recording
 * activity information from a switchyard application.
 *
 */
public class DefaultActivityReporter implements ActivityReporter {

    private static final Logger LOG=Logger.getLogger(DefaultActivityReporter.class.getName());
    
    private static final String ACTIVITY_COLLECTOR = "java:global/overlord-rtgov/ActivityCollector";

    @Resource(lookup=ACTIVITY_COLLECTOR)
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
            LOG.fine("*********** Default Activity Reporter initialized with collector="+_activityCollector);
        }
        
        _initialized = true;
    }

    /**
     * {@inheritDoc}
     */
    public void logInfo(String info) {
        LogMessage lm=new LogMessage();
        lm.setMessage(info);
        lm.setLevel(LogMessage.Level.Information);
        
        try {
            report(lm);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to report Info activity: "+info, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void logWarning(String warning) {
        LogMessage lm=new LogMessage();
        lm.setMessage(warning);
        lm.setLevel(LogMessage.Level.Warning);
        
        try {
            report(lm);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to report Warning activity: "+warning, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void logError(String error) {
        LogMessage lm=new LogMessage();
        lm.setMessage(error);
        lm.setLevel(LogMessage.Level.Error);
        
        try {
            report(lm);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to report Error activity: "+error, e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void report(String type, java.util.Map<String,String> props) throws Exception {
        CustomActivity ca=new CustomActivity();
        ca.setCustomType(type);        
        ca.setProperties(props);
        
        report(ca);
    }
    
    /**
     * {@inheritDoc}
     */
    public void report(ActivityType actType) throws Exception {
        if (!_initialized) {
            init();
        }
        
        _activityCollector.record(actType);
    }
    
}
