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

import org.codehaus.jackson.annotate.JsonTypeInfo;
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
