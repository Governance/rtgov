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
package org.overlord.rtgov.reports.model;

/**
 * This class represents a default implementation of the Report interface.
 *
 */
public class Report {
    
    private String _title=null;
    private java.util.Date _created=new java.util.Date();
    
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
    public java.util.Date getCreated() {
        return (_created);
    }
    
    /**
     * This method sets the created date/time.
     * 
     * @param created The created date/time
     * @return The report
     */
    public Report setCreated(java.util.Date created) {
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
