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

import javax.annotation.PostConstruct;

import org.savara.bam.activity.model.Activity;
import org.savara.bam.activity.model.ActivityType;
import org.savara.bam.activity.model.Context;
import org.savara.bam.collector.spi.ActivityLogger;
import org.savara.bam.collector.spi.ContextInitializer;

/**
 * This class provides a default implementation of the activity
 * collector interface.
 *
 */
public class DefaultActivityCollector implements ActivityCollector {

	private ContextInitializer _contextInitializer=null;
	private ActivityLogger _activityLogger=null;

	private Context _context=null;
	
	@PostConstruct
	protected void init() {
		if (_contextInitializer != null) {
			_context = _contextInitializer.getContext();
		}
		
		if (_context == null) {
		    _context = new Context();
		}
	}
	
	/**
	 * This method sets the context initializer.
	 * 
	 * @param initializer The initializer
	 */
	public void setContextInitializer(ContextInitializer initializer) {
	    _contextInitializer = initializer;
	}
	
    /**
     * This method gets the context initializer.
     * 
     * @return The initializer
     */
	public ContextInitializer getContextInitializer() {
	    return (_contextInitializer);
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
		_context.setTransaction(createTransactionId());
	}

	/**
	 * {@inheritDoc}
	 */
	public void endTransaction() {
		_context.setTransaction(null);
	}

	/**
	 * {@inheritDoc}
	 */
	public void record(ActivityType actType) {
		Activity act=new Activity();
		act.setTimestamp(getTimestamp());
		
		act.setContext(new Context(_context));
		
		act.setActivityType(actType);
		
		_activityLogger.log(act);
	}

}
