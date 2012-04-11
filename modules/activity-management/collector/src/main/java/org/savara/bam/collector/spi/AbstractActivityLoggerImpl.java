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
package org.savara.bam.collector.spi;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.savara.bam.activity.model.Activity;
import org.savara.bam.collector.spi.ActivityLogger;

public abstract class AbstractActivityLoggerImpl implements ActivityLogger {

	private static final Logger LOG=Logger.getLogger(AbstractActivityLoggerImpl.class.getName());
	
	private int _messageCounter=0;
	private java.util.Timer _timer;
	private java.util.TimerTask _timerTask;
	
	@PostConstruct
	public void init() {
		_timer = new java.util.Timer();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void log(Activity act) {
		
		try {
			 synchronized(this) {
				 
				 // Cancel any current scheduled task
				 if (_timerTask == null) {
					 _timerTask = new TimerTask() {
							public void run() {
								try {
									sendMessage();
								} catch(Exception e) {
									LOG.log(Level.SEVERE, "Failed to send activity event", e);
								}
							}						 
						 };
						 
					 // Schedule send
					 _timer.schedule(_timerTask, 500);
				 }

				 appendActivity(act);
				 
				 _messageCounter++;

				 if (_messageCounter > 1000) {
					 sendMessage();
				 }
			 }
			 
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to send activity event", e);
		}
	}

	protected abstract void appendActivity(Activity act) throws Exception;
	
	protected synchronized void sendMessage() throws Exception {
		_messageCounter = 0;
		 
		if (_timerTask != null) {
			_timerTask.cancel();
			_timerTask = null;
		}
	}

	@PreDestroy
	public void close() {
		_timer.cancel();
	}
}
