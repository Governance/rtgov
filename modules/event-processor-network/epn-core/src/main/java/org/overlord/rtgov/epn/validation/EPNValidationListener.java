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
package org.overlord.rtgov.epn.validation;

import org.overlord.rtgov.epn.Network;

/**
 * This interface is used to report validation issues
 * for an Event Processor Network.
 *
 */
public interface EPNValidationListener {

	/**
	 * This method identifies an issue with a part of
	 * an Event Processor Network.
	 * 
	 * @param epn The network
	 * @param target The object resulting in the error
	 * @param issue The description of the issue
	 */
	public void error(Network epn, Object target, String issue);
	
}
