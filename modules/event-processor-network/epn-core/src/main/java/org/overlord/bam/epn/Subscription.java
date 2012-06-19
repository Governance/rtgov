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
package org.overlord.bam.epn;

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
