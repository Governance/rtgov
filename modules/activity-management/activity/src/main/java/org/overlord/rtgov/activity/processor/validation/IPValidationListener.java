/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.processor.validation;

import org.overlord.rtgov.activity.processor.InformationProcessor;

/**
 * This interface is used to report validation issues
 * for an Information Processor.
 *
 */
public interface IPValidationListener {

    /**
     * This method identifies an issue with a part of
     * an Information Processor.
     * 
     * @param ip The information processor
     * @param target The object resulting in the error
     * @param issue The description of the issue
     */
    public void error(InformationProcessor ip, Object target, String issue);
    
}
