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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.overlord.rtgov.common.util.VersionUtil;
import org.overlord.rtgov.reports.model.Report;

/**
 * This class represents the default implementation of the ReportManager
 * interface.
 *
 */
@Deprecated
public abstract class AbstractReportManager implements ReportManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractReportManager.class.getName());

    private java.util.Map<String,ReportDefinition> _reportDefinitionIndex=
            new java.util.HashMap<String,ReportDefinition>();
    private java.util.List<ReportDefinition> _reportDefinitions=
                new java.util.ArrayList<ReportDefinition>();
    
    @Inject
    private ReportContext _context=null;

    /**
     * The default constructor.
     */
    public AbstractReportManager() {
        ReportManagerAccessor.setReportManager(this);
    }
    
    /**
     * This method initializes the report manager.
     */
    @PostConstruct
    protected void init() {
    }
    
    /**
     * This method sets the report context.
     * 
     * @param context The context
     */
    public void setContext(ReportContext context) {
        _context = context;
    }
    
    /**
     * This method returns the report context.
     * 
     * @return The report context
     */
    public ReportContext getContext() {
        return (_context);
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(ReportDefinition rd) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register: report definitoin name="
                        +rd.getName()+" version="
                        +rd.getVersion()+" definition="+rd);
        }
        
        // Initialize the report definition
        rd.init();
        
        synchronized (_reportDefinitionIndex) {
            boolean f_add=false;
            
            // Check if report definition for same name already exists
            ReportDefinition existing=_reportDefinitionIndex.get(rd.getName());
            
            if (existing != null) {
                
                // Check whether newer version
                if (VersionUtil.isNewerVersion(existing.getVersion(),
                        rd.getVersion())) {
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Replace existing report definition version="
                                    +existing.getVersion());
                    }
                    
                    // Unregister old version
                    unregister(existing);
                    
                    // Add new version
                    f_add = true;                  
                } else {
                                      
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Newer version '"+existing.getVersion()
                                +"' already registered");
                    }
                }
            } else {
                f_add = true;
            }
            
            if (f_add) {
                _reportDefinitionIndex.put(rd.getName(), rd);
                _reportDefinitions.add(rd);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unregister(ReportDefinition rd) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister: report manager name="
                        +rd.getName()+" version="
                        +rd.getVersion()+" definition="+rd);
        }
        
        synchronized (_reportDefinitionIndex) {
            
            if (_reportDefinitions.contains(rd)) {
                ReportDefinition removed=
                        _reportDefinitionIndex.remove(rd.getName());
                _reportDefinitions.remove(removed);
                
                removed.close();
                
            } else if (_reportDefinitionIndex.containsKey(rd.getName())) {
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Another version of report definition name="
                            +rd.getName()+" is currently registered: existing version ="
                            +_reportDefinitionIndex.get(rd.getName()).getVersion());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ReportDefinition> getReportDefinitions() {
        return (java.util.Collections.unmodifiableList(_reportDefinitions));
    }

    /**
     * {@inheritDoc}
     */
    public Report generate(String reportName, Map<String, Object> params)
            throws Exception {
        
        ReportDefinition rd=_reportDefinitionIndex.get(reportName);
        
        if (rd != null && rd.getGenerator() != null) {
            return (rd.getGenerator().generate(_context, params));
        }
        
        return null;
    }

}
