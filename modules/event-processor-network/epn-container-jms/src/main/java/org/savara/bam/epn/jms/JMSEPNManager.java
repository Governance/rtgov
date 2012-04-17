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
package org.savara.bam.epn.jms;

import javax.jms.Message;

import org.savara.bam.epn.EPNManager;

/**
 * This interface provides the JMS extension to
 * the EPN Manager.
 *
 */
public interface JMSEPNManager extends EPNManager {
    
    /**
     * This method handles an events message received via JMS.
     * 
     * @param message The JMS message carrying the events
     * @throws Exception Failed to handle events
     */
    public void handleEventsMessage(Message message) throws Exception;
    
    /**
     * This method handles a notification message received via JMS.
     * 
     * @param message The JMS message carrying the notification
     * @throws Exception Failed to handle notification
     */
    public void handleNotificationsMessage(Message message) throws Exception;

}
