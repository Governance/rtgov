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
package org.overlord.rtgov.reports.loader.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.overlord.rtgov.reports.AbstractReportsLoader;
import org.overlord.rtgov.reports.ReportDefinition;
import org.overlord.rtgov.reports.ReportManagerAccessor;
import org.overlord.rtgov.reports.util.ReportsUtil;

/**
 * This class provides the capability to load report definition from a
 * defined file.
 *
 */
public class ReportsActivator extends AbstractReportsLoader implements BundleActivator {
    
    private static final Logger LOG=Logger.getLogger(ReportsActivator.class.getName());
    
    private static final String REPORTS_JSON = "reports.json";

    private org.overlord.rtgov.reports.ReportManager _reportManager=null;
    
    private java.util.List<ReportDefinition> _reportDefinitions=null;
    
    /**
     * The constructor.
     */
    public ReportsActivator() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        init();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        close();
    }

    /**
     * This method initializes the Reports loader.
     */
    public void init() {
        
        _reportManager = ReportManagerAccessor.getReportManager();
        
        if (_reportManager == null) {
            LOG.severe(java.util.PropertyResourceBundle.getBundle(
                    "reports-loader-osgi.Messages").getString("REPORTS-LOADER-OSGI-5"));
        }
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(REPORTS_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "reports-loader-osgi.Messages").getString("REPORTS-LOADER-OSGI-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _reportDefinitions = ReportsUtil.deserializeReportDefinitionList(b);
                
                if (_reportDefinitions == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "reports-loader-osgi.Messages").getString("REPORTS-LOADER-OSGI-2"));
                } else {
                    for (ReportDefinition rd : _reportDefinitions) {
                        // Pre-initialize the report definition to avoid any contextual class
                        // loading issues. Within OSGI, the registration of the definition
                        // will be done in the context of the core war, while as the
                        // report definition requires the classloading context associated
                        // with the ReportDefinition deployment.
                        preInit(rd);
                        
                        _reportManager.register(rd);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "reports-loader-osgi.Messages").getString("REPORTS-LOADER-OSGI-3"), e);
        }
    }
    
    /**
     * This method closes the EPN loader.
     */
    public void close() {
        
        if (_reportManager != null && _reportDefinitions != null) {
            try {
                for (ReportDefinition rd : _reportDefinitions) {
                    _reportManager.unregister(rd);
                }
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                        "reports-loader-osgi.Messages").getString("REPORTS-LOADER-OSGI-4"), t);
                }
            }
        }
    }       
}
