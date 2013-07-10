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
package org.overlord.rtgov.internal.reports.jee;

import java.text.MessageFormat;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.overlord.rtgov.common.util.RTGovPropertiesProvider;
import org.overlord.rtgov.reports.model.Calendar;
import org.overlord.rtgov.reports.util.ReportsUtil;

/**
 * This class provides the JEE implementation of the ReportContext
 * interface.
 *
 */
public class JEEReportContext implements org.overlord.rtgov.reports.ReportContext {
    
    private static final Logger LOG=Logger.getLogger(JEEReportContext.class.getName());
    
    @Inject
    private RTGovPropertiesProvider _propertiesProvider=null;
    
    /**
     * This method is used to set the properties provider.
     * 
     * @param properties The properties provider
     */
    public void setPropertiesProvider(RTGovPropertiesProvider properties) {
        _propertiesProvider = properties;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getService(String name) {
        Object ret=null;
        
        try {
            InitialContext ctx=new InitialContext();
            
            ret = ctx.lookup(name);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "reports-jee.Messages").getString("REPORTS-JEE-1"),
                    name), e);
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Get service for name="+name+" ret="+ret);
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object getService(Class<?> cls) {
        Object ret=null;
        
        try {
            BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
            
            java.util.Set<Bean<?>> beans=bm.getBeans(cls);
            
            for (Bean<?> b : beans) {                
                CreationalContext<Object> cc=new CreationalContext<Object>() {
                    public void push(Object arg0) {
                    }
                    public void release() {
                    }                   
                };
                
                ret = ((Bean<Object>)b).create(cc);
                
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Get service for class="+cls+" for bean="+b+" ret="+ret);
                }
                
                if (ret != null) {
                    break;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                    "reports-jee.Messages").getString("REPORTS-JEE-2"),
                    cls.getName()), e);
        }
        
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Get service for class="+cls+" ret="+ret);
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public void logError(String mesg) {
        LOG.severe(mesg);
    }

    /**
     * {@inheritDoc}
     */
    public void logWarning(String mesg) {
        LOG.warning(mesg);
    }

    /**
     * {@inheritDoc}
     */
    public void logInfo(String mesg) {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(mesg);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void logDebug(String mesg) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(mesg);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Calendar getCalendar(String name, String timezone) {
        Calendar ret=null;
        
        if (name != null) {
            
            // Attempt to load calendar - if no name specified, then use 'default'
            String location=_propertiesProvider.getProperty("calendar."+name);
            
            if (location != null) {
                java.io.File f=new java.io.File(location);
                
                if (f.exists()) {
                    try {
                        java.io.InputStream is=new java.io.FileInputStream(f);
                        
                        byte[] b=new byte[is.available()];
                        
                        is.read(b);
                        
                        is.close();
                        
                        ret = ReportsUtil.deserializeCalendar(b);
                        
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                                "reports-jee.Messages").getString("REPORTS-JEE-4"),
                                name, location), e);
                    }
                    
                } else {
                    LOG.severe(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                            "reports-jee.Messages").getString("REPORTS-JEE-3"),
                            name, location));
                }
            }
            
            // If not found, and no name defined, then create default
            if (ret == null && name.equalsIgnoreCase(Calendar.DEFAULT)) {
                ret = new Calendar().setName(Calendar.DEFAULT);
                
                // Define working week
                ret.setMonday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
                ret.setTuesday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
                ret.setWednesday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
                ret.setThursday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
                ret.setFriday(new Calendar.WorkingDay().setStartHour(9).setEndHour(17));
                
                ret.getExcludedDays().add(new Calendar.ExcludedDay().setDay(25).
                            setMonth(12).setReason("Christmas Day"));
            }
            
            if (ret != null && timezone != null) {
                ret.setTimeZone(TimeZone.getTimeZone(timezone));
            }
        }
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Calendar name="+name+" timezone="+timezone+" ret="+ret);
        }
        
        return (ret);
    }

}
