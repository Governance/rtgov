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
