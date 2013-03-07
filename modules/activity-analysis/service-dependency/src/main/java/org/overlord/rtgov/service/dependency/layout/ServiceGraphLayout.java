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
package org.overlord.rtgov.service.dependency.layout;

import org.overlord.rtgov.service.dependency.ServiceGraph;

/**
 * This interface represents the service graph layout
 * algorithm.
 *
 */
public interface ServiceGraphLayout {
    
    /**
     * This property defines the X position.
     */
    public static final String X_POSITION="XPosition";

    /**
     * This property defines the Y position.
     */
    public static final String Y_POSITION="YPosition";

    /**
     * This property defines the width.
     */
    public static final String WIDTH="Width";
    
    /**
     * This property defines the height.
     */
    public static final String HEIGHT="Height";

    /**
     * This method establishes the layout of the
     * nodes and links within the supplied service
     * graph.
     * 
     * @param sg The service graph
     */
    public void layout(ServiceGraph sg);
    
}
