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
