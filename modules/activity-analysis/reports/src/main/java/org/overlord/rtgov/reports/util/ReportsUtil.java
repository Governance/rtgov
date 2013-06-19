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
package org.overlord.rtgov.reports.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
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
        SerializationConfig config=MAPPER.getSerializationConfig()
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                .withSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
        
        MAPPER.setSerializationConfig(config);
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
