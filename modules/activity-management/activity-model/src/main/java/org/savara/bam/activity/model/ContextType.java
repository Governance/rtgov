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
package org.savara.bam.activity.model;

/**
 * This enumeration represents the type of context information that
 * may be recorded against an activity unit.
 *
 */
public enum ContextType {

    /**
     * An conversation id represents a value that can be used to correlate
     * activities across distributed services. These context types
     * will be globally unique, and may refer to values that are
     * carried in the application message contents.
     */
    ConversationId,
    
    /**
     * The 'InstanceId' type represents a local id that may be associated
     * with the executable unit enacting the service/process being monitored,
     * and can therefore be used to correlate local activities as being
     * part of the same executable unit.
     */
    InstanceId,
    
    /**
     * This context represents an id associated with a particular message
     * being exchanged between distributed participants.
     */
    MessageId
    
}
