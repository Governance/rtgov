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
package org.savara.bam.epn.cep;

/**
 * This interface provides services to the CEP rules that process
 * notifications regarding event processors.
 *
 */
public interface CEPServices {

    /**
     * This method is used to set the result obtained
     * from the evaluation of a CEP rule.
     * 
     * @param result The result
     */
    public void setResult(Object result);

    /**
     * This method returns the result.
     * 
     * @return The result
     */
    public Object getResult();

    /**
     * This method logs information.
     * 
     * @param info The information
     */
    public void logInfo(String info);

    /**
     * This method logs an error.
     * 
     * @param error The error
     */
    public void logError(String error);

    /**
     * This method logs debug information.
     * 
     * @param debug The debug information
     */
    public void logDebug(String debug);

}
