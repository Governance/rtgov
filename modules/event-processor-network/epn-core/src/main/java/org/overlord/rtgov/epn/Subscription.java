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
 * This class represents a subscription used to define a route for events
 * from a subject to a node.
 *
 */
public class Subscription {

    private String _subject=null;
    private String _nodeName=null;
    
    /**
     * The default constructor for the subscription.
     * 
     */
    public Subscription() {
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
     * This method sets the node name.
     * 
     * @param nodeName The node name
     */
    public void setNodeName(String nodeName) {
        _nodeName = nodeName;
    }
    
    /**
     * This method gets the node name.
     * 
     * @return The node name
     */
    public String getNodeName() {
        return (_nodeName);
    }
}
