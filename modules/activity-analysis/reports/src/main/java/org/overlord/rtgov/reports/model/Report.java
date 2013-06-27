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
package org.overlord.rtgov.reports.model;

/**
 * This class represents a default implementation of the Report interface.
 *
 */
public class Report {
    
    private String _title=null;
    private String _created=null;
    
    private java.util.List<Section> _sections=new java.util.ArrayList<Section>();

    /**
     * This method returns the title of the report.
     * 
     * @return The title
     */
    public String getTitle() {
        return (_title);
    }
    
    /**
     * This method sets the title.
     * 
     * @param title The title
     * @return The report
     */
    public Report setTitle(String title) {
        _title = title;
        return (this);
    }
    
    /**
     * This method returns the date/time when the report
     * was created.
     * 
     * @return The creation date/time
     */
    public String getCreated() {
        return (_created);
    }
    
    /**
     * This method sets the created date/time.
     * 
     * @param created The created date/time
     * @return The report
     */
    public Report setCreated(String created) {
        _created = created;
        return (this);
    }
    
    /**
     * This method returns the sections of the report.
     * 
     * @return The sections
     */
    public java.util.List<Section> getSections() {
        return (_sections);
    }
    
    /**
     * This method sets the sections of the report.
     * 
     * @param sections The sections
     * @return The report
     */
    public Report setSections(java.util.List<Section> sections) {
        _sections = sections;
        return (this);
    }
}
