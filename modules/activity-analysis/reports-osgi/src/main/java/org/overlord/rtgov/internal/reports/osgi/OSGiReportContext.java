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
package org.overlord.rtgov.internal.reports.osgi;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.overlord.rtgov.reports.AbstractReportContext;

/**
 * This class provides the OSGi implementation of the ReportContext
 * interface.
 *
 */
public class OSGiReportContext extends AbstractReportContext implements org.overlord.rtgov.reports.ReportContext {
    
    private static final Logger LOG=Logger.getLogger(OSGiReportContext.class.getName());
    
    private static BundleContext _context=null;
    
    /**
     * This method sets the bundle context.
     * 
     * @param context The bundle context
     */
    public static void setBundleContext(BundleContext context) {
        _context = context;
    }

    /**
     * {@inheritDoc}
     */
    public Object getService(String name) {
        Object ret=null;
        
        try {
            ServiceReference ref=_context.getServiceReference(name);
            
            if (ref != null) {
                ret = _context.getService(ref);
            }
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "reports-osgi.Messages").getString("REPORTS-OSGI-1"),
                    name), e);
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Get service for class name="+name+" ret="+ret);
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public Object getService(Class<?> cls) {
        return (getService(cls.getName()));
    }

}
