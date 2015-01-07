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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.overlord.rtgov.reports.model.Report;

/**
 * This abstract class represents a report generator.
 *
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class ReportGenerator {

    /**
     * This method can be used to initialize the report generator.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
    }
    
    /**
     * This method generates a report, based on the supplied definition,
     * and returns the report contents.
     * 
     * @param context The report generator's context
     * @param properties The properties
     * @return The report contents
     * @throws Exception Failed to generate report
     */
    public abstract Report generate(ReportContext context,
                    java.util.Map<String,Object> properties) throws Exception;
    
    /**
     * This method can be used to close the report generator.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
    }
    
}
