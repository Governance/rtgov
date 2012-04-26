/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.bam.collector;

import java.util.UUID;

import javax.inject.Inject;

import org.savara.bam.activity.model.ActivityUnit;
import org.savara.bam.activity.model.ActivityType;
import org.savara.bam.collector.spi.ActivityLogger;
import org.savara.bam.collector.spi.OriginFactory;

/**
 * This class provides a default implementation of the activity
 * collector interface.
 *
 */
public class DefaultActivityCollector implements ActivityCollector {

    @Inject
    private OriginFactory _originInitializer=null;
    
    @Inject
    private ActivityLogger _activityLogger=null;

    private java.lang.ThreadLocal<ActivityUnit> _activityUnit=null;
    
    /**
     * This method sets the origin initializer.
     * 
     * @param initializer The initializer
     */
    public void setOriginInitializer(OriginFactory initializer) {
        _originInitializer = initializer;
    }
    
    /**
     * This method gets the origin initializer.
     * 
     * @return The initializer
     */
    public OriginFactory getOriginInitializer() {
        return (_originInitializer);
    }
    
    /**
     * This method sets the activity logger.
     * 
     * @param activityLogger The activity logger
     */
    public void setActivityLogger(ActivityLogger activityLogger) {
        _activityLogger = activityLogger;
    }
    
    /**
     * This method gets the activity logger.
     * 
     * @return The activity logger
     */
    public ActivityLogger getActivityLogger() {
        return (_activityLogger);
    }
    
    /**
     * This method generates a unique transaction id.
     * 
     * @return The transaction id
     */
    protected String createTransactionId() {
        return (UUID.randomUUID().toString());
    }
    
    /**
     * This method returns the current date/time.
     * 
     * @return The timestamp
     */
    protected long getTimestamp() {
        return (System.currentTimeMillis());
    }
    
    /**
     * {@inheritDoc}
     */
    public void startTransaction() {
        _activityUnit.set(createActivityUnit());
    }
    
    /**
     * This method creates a new activity unit and
     * initializes its origin based on the template
     * supplied by the OriginInitializer, if
     * available.
     * 
     * @return The new activity unit
     */
    protected ActivityUnit createActivityUnit() {
        ActivityUnit ret=new ActivityUnit();
        
        ret.setOrigin(_originInitializer.createOrigin());
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public void endTransaction() {
        _activityUnit.remove();
    }

    /**
     * {@inheritDoc}
     */
    public void record(ActivityType actType) {
        
        ActivityUnit au=_activityUnit.get();
        
        // Check if need to create a single event activity unit outside of transaction scope
        boolean transactional=true;
        
        if (au == null) {
            au = createActivityUnit();
            transactional = false;
        }
        
        // Set timestamp
        actType.setTimestamp(getTimestamp());
        
        // TODO: Need to determine how best to collect context
        // information
        
        // TODO: Need to also consider how to deal with filtering
        // mechanism
        
        au.getActivityTypes().add(actType);
        
        if (!transactional) {
            _activityLogger.log(au);
        }
    }

}
