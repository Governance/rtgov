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
package org.savara.bam.epn.internal;

/**
 * This class represents a list of events that can be serialized.
 *
 * @param <T> The event type
 */
public class EventList extends java.util.Vector<java.io.Serializable> {

    private static final long serialVersionUID = -2940973962085558684L;

    /**
     * This method represents the default constructor.
     */
    public EventList() {
    }

    /**
     * This method represents a constructor to initialize the event list
     * from a standard Java list.
     */
    public EventList(java.util.List<? extends java.io.Serializable> list) {
        super(list);
    }
}
