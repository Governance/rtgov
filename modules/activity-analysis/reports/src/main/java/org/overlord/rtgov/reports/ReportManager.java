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
package org.overlord.rtgov.reports;

import org.overlord.rtgov.reports.model.Report;

/**
 * This interface represents the component for managing reports.
 *
 */
public interface ReportManager {
    
    /**
     * This method registers the supplied report definition.
     * 
     * @param rd The report definition
     * @throws Exception Failed to register report
     */
    public void register(ReportDefinition rd) throws Exception;

    /**
     * This method unregisters the supplied report definition.
     * 
     * @param rd The report definition
     * @throws Exception Failed to unregister report
     */
    public void unregister(ReportDefinition rd) throws Exception;

    /**
     * This method returns a list of registered report definitions.
     * 
     * @return The report definitions
     */
    public java.util.List<ReportDefinition> getReportDefinitions();
    
    /**
     * This method returns the report associated with the supplied name and parameters.
     * 
     * @param reportName The report name
     * @param params The report parameters
     * @return The report, or null if no generator is available
     * @throws Exception Failed to generate the report
     */
    public Report generate(String reportName, java.util.Map<String,Object> params) throws Exception;
    
}
