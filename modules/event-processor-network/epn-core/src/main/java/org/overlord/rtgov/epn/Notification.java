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
package org.overlord.rtgov.epn;

/**
 * This class represents a notification used to define a subject and type.
 *
 */
public class Notification {

    private String _subject=null;
    private NotificationType _type=null;
    
    /**
     * The default constructor for the notification.
     * 
     */
    public Notification() {
    }

    /**
     * This method sets the subject.
     * 
     * @param subject The subject
     */
    public void setSubject(String subject) {
        _subject = subject;
    }
    
    /**
     * This method gets the subject.
     * 
     * @return The subject
     */
    public String getSubject() {
        return (_subject);
    }

    /**
     * This method sets the type.
     * 
     * @param type The type
     */
    public void setType(NotificationType type) {
        _type = type;
    }
    
    /**
     * This method gets the node name.
     * 
     * @return The node name
     */
    public NotificationType getType() {
        return (_type);
    }
}
