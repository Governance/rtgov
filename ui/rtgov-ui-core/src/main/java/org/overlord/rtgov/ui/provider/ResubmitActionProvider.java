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
package org.overlord.rtgov.ui.provider;

import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.UiException;

/**
 * This interface represents the action provider for the Resubmit task.
 *
 */
public abstract class ResubmitActionProvider extends ActionProvider {

    private static final String RESUBMIT = "Resubmit";

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return RESUBMIT;
    }
    
    /**
     * This method determines whether this provider support's a resubmit of the
     * supplied service operation.
     * 
     * @param service The service
     * @param operation The operation
     * @return Whether this provider support's a resubmit for the given service
     *         and operation
     * @throws UiException Failed to test if service operation can be resubmitted
     */
    public abstract boolean isResubmitSupported(String service, String operation) throws UiException;

    /**
     * This method resubmits the supplied message to the target service
     * and operation.
     * 
     * @param service The service
     * @param operation The operation
     * @param message The message
     * @throws UiException Failed to resubmit the message
     */
    public abstract void resubmit(String service, String operation, MessageBean message) throws UiException;

}
