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
package org.overlord.rtgov.internal.epn.jee;

import javax.jms.Message;

import org.overlord.rtgov.epn.EPNManager;

/**
 * This interface provides the JMS extension to
 * the EPN Manager.
 *
 */
public interface JEEEPNManager extends EPNManager {
    
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
