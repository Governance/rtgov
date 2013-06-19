/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.reports.loader.jee;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.overlord.rtgov.reports.AbstractReportsLoader;
import org.overlord.rtgov.reports.ReportDefinition;
import org.overlord.rtgov.reports.util.ReportsUtil;

/**
 * This class provides the capability to load an Active Collection Source from a
 * defined file.
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class JEEReportsLoader extends AbstractReportsLoader {
    
    private static final Logger LOG=Logger.getLogger(JEEReportsLoader.class.getName());
    
    private static final String REPORTS_JSON = "reports.json";
    private static final String REPORT_MANAGER = "java:global/overlord-rtgov/ReportManager";

    @Resource(lookup=REPORT_MANAGER)
    private org.overlord.rtgov.reports.ReportManager _reportManager=null;
    
    private java.util.List<ReportDefinition> _reportDefinitions=null;
    
    /**
     * The constructor.
     */
    public JEEReportsLoader() {
    }
    
    /**
     * This method initializes the Reports loader.
     */
    @PostConstruct
    public void init() {
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(REPORTS_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "reports-loader-jee.Messages").getString("REPORTS-LOADER-JEE-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _reportDefinitions = ReportsUtil.deserializeReportDefinitionList(b);
                
                if (_reportDefinitions == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "reports-loader-jee.Messages").getString("REPORTS-LOADER-JEE-2"));
                } else {
                    for (ReportDefinition rd : _reportDefinitions) {
                        // Pre-initialize the report definition to avoid any contextual class
                        // loading issues. Within JEE, the registration of the definition
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
                    "reports-loader-jee.Messages").getString("REPORTS-LOADER-JEE-3"), e);
        }
    }
    
    /**
     * This method closes the EPN loader.
     */
    @PreDestroy
    public void close() {
        
        if (_reportManager != null && _reportDefinitions != null) {
            try {
                for (ReportDefinition rd : _reportDefinitions) {
                    _reportManager.unregister(rd);
                }
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                        "reports-loader-jee.Messages").getString("REPORTS-LOADER-JEE-4"), t);
                }
            }
        }
    }       
}
