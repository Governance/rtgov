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

/**
 * This class represents a report definition used to produce a report.
 *
 */
public class ReportDefinition {

    private String _name=null;
    private String _version=null;
    private ReportGenerator _generator=null;
    
    private boolean _initialized=false;
    
    /**
     * This method returns the name of the report.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the report.
     * 
     * @param name The name
     * @return The report definition
     */
    public ReportDefinition setName(String name) {
        _name = name;
        return (this);
    }
    
    /**
     * This method returns the version of the report.
     * 
     * @return The version
     */
    public String getVersion() {
        return (_version);
    }
    
    /**
     * This method sets the version of the report.
     * 
     * @param version The version
     * @return The report definition
     */
    public ReportDefinition setVersion(String version) {
        _version = version;
        return (this);
    }
    
    /**
     * This method returns the report generator.
     * 
     * @return The report generator
     */
    public ReportGenerator getGenerator() {
        return (_generator);
    }
    
    /**
     * This method sets the report generator.
     * 
     * @param gen The report generator
     * @return The report definition
     */
    public ReportDefinition setGenerator(ReportGenerator gen) {
        _generator = gen;
        return (this);
    }

    /**
     * This method initializes the report definition.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        preInit();
    }
    
    /**
     * This method pre-initializes the report definition.
     * 
     * @throws Exception Failed to pre-initialize
     */
    protected void preInit() throws Exception {
        
        synchronized (this) {
            if (_generator != null && !_initialized) {
                _generator.init();
                
                _initialized = true;
            }
        }
    }
    
    /**
     * This method closes the report definition.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
        if (_generator != null) {
            _generator.close();
        }
    }
}
