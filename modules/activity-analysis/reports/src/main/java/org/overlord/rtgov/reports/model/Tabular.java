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
 * This class represents a tabular section within the report.
 *
 */
public class Tabular extends Section {
    
    private Header _header=null;
    private java.util.List<Row> _rows=new java.util.ArrayList<Row>();
    private Summary _summary=null;
    
    /**
     * This method returns the header.
     * 
     * @return The header
     */
    public Header getHeader() {
        return (_header);
    }
    
    /**
     * This method sets the header.
     * 
     * @param header The header
     * @return The section
     */
    public Tabular setHeader(Header header) {
        _header = header;
        return (this);
    }
    
    /**
     * This method returns the rows.
     * 
     * @return The rows
     */
    public java.util.List<Row> getRows() {
        return (_rows);
    }
    
    /**
     * This method sets the rows.
     * 
     * @param rows The rows
     * @return The section
     */
    public Tabular setRows(java.util.List<Row> rows) {
        _rows = rows;
        return (this);
    }
    
    /**
     * This method returns the summary.
     * 
     * @return The summary
     */
    public Summary getSummary() {
        return (_summary);
    }
    
    /**
     * This method sets the summary.
     * 
     * @param summary The summary
     * @return The section
     */
    public Tabular setSummary(Summary summary) {
        _summary = summary;
        return (this);
    }
    
    /**
     * This class represents the table header.
      *
     */
    public static class Header {
        
        private java.util.List<String> _columnNames=new java.util.ArrayList<String>();

        /**
         * This method returns the column names.
         * 
         * @return The column names
         */
        public java.util.List<String> getColumnNames() {
            return (_columnNames);
        }
        
        /**
         * This method sets the column names.
         * 
         * @param cols The column names
         * @return The header
         */
        public Header setColumnNames(java.util.List<String> cols) {
            _columnNames = cols;
            return (this);
        }
        
    }
    
    /**
     * This class represents a row in the table.
     *
     */
    public static class Row {
        
        private java.util.List<Object> _values=new java.util.ArrayList<Object>();

        /**
         * This method returns the values.
         * 
         * @return The values
         */
        public java.util.List<Object> getValues() {
            return (_values);
        }
        
        /**
         * This method sets the values.
         * 
         * @param values The values
         * @return The row
         */
        public Row setValues(java.util.List<Object> values) {
            _values = values;
            return (this);
        }
        
    }
    
    /**
     * This class represents the summary for a table of information.
     *
     */
    public static class Summary {
        
        private java.util.List<Object> _values=new java.util.ArrayList<Object>();
        private java.util.Map<String,Object> _properties=new java.util.HashMap<String,Object>();

        /**
         * This method returns the values.
         * 
         * @return The values
         */
        public java.util.List<Object> getValues() {
            return (_values);
        }
        
        /**
         * This method sets the values.
         * 
         * @param values The values
         * @return The row
         */
        public Summary setValues(java.util.List<Object> values) {
            _values = values;
            return (this);
        }

        /**
         * This method returns the properties.
         * 
         * @return The properties
         */
        public java.util.Map<String,Object> getProperties() {
            return (_properties);
        }
        
        /**
         * This method sets the properties.
         * 
         * @param props The properties
         * @return The summary
         */
        public Summary setProperties(java.util.Map<String,Object> props) {
            _properties = props;
            return (this);
        }
        
    }
}
