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
package org.overlord.rtgov.reports.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;

import org.overlord.rtgov.reports.ReportDefinition;
import org.overlord.rtgov.reports.model.Calendar;
import org.overlord.rtgov.reports.model.Report;

/**
 * This class provides utility functions for the reporting capability.
 *
 */
public final class ReportsUtil {

    private static final ObjectMapper MAPPER=new ObjectMapper();

    private static final TypeReference<java.util.List<ReportDefinition>> REPORT_DEFN_LIST=
            new TypeReference<java.util.List<ReportDefinition>>() { };

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
    }

    /**
     * The default constructor.
     */
    private ReportsUtil() {
    }
    
    /**
     * This method deserializes the report definition based on the contents.
     * 
     * @param content The content
     * @return The report definition, or null if not able to deserialize
     * @throws Exception Failed to deserialize
     */
    public static ReportDefinition deserializeReportDefinition(byte[] content) throws Exception {
        ReportDefinition ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(content);
        
        ret = MAPPER.readValue(bais, ReportDefinition.class);
        
        bais.close();
        
        return (ret);
    }
    
    /**
     * This method deserializes the report definition list based on the contents.
     * 
     * @param content The content
     * @return The report definition list, or null if not able to deserialize
     * @throws Exception Failed to deserialize
     */
    public static java.util.List<ReportDefinition> deserializeReportDefinitionList(byte[] content) throws Exception {
        java.util.List<ReportDefinition> ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(content);
        
        ret = MAPPER.readValue(bais, REPORT_DEFN_LIST);
        
        bais.close();
        
        return (ret);
    }
    
    /**
     * This method serializes a report definition into a JSON representation.
     * 
     * @param defn The report definition
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeReportDefinition(ReportDefinition defn) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, defn);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }
    
   /**
     * This method deserializes the report from a JSON representation.
     * 
     * @param content The JSON representation of the report
     * @return The report, or null if not able to deserialize
     * @throws Exception Failed to deserialize
     */
    public static Report deserializeReport(byte[] content) throws Exception {
        Report ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(content);
        
        ret = MAPPER.readValue(bais, Report.class);
        
        bais.close();
        
        return (ret);
    }
    
    /**
     * This method serializes a report into a JSON representation.
     * 
     * @param report The report
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeReport(Report report) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, report);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }
    
    /**
     * This method deserializes the calendar from a JSON representation.
     * 
     * @param content The JSON representation of the calendar
     * @return The calendar, or null if not able to deserialize
     * @throws Exception Failed to deserialize
     */
    public static Calendar deserializeCalendar(byte[] content) throws Exception {
        Calendar ret=null;
        
        java.io.ByteArrayInputStream bais=new java.io.ByteArrayInputStream(content);
        
        ret = MAPPER.readValue(bais, Calendar.class);
        
        bais.close();
        
        return (ret);
    }
    
    /**
     * This method serializes a calendar into a JSON representation.
     * 
     * @param cal The calendar
     * @return The JSON serialized representation
     * @throws Exception Failed to serialize
     */
    public static byte[] serializeCalendar(Calendar cal) throws Exception {
        byte[] ret=null;
        
        java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
        
        MAPPER.writeValue(baos, cal);
        
        ret = baos.toByteArray();
        
        baos.close();
        
        return (ret);
    }
}
