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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.overlord.rtgov.reports.AbstractReportContext;

/**
 * This class provides the JEE implementation of the ReportContext
 * interface.
 *
 */
public class JEEReportContext extends AbstractReportContext implements org.overlord.rtgov.reports.ReportContext {
    
    private static final Logger LOG=Logger.getLogger(JEEReportContext.class.getName());
    
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

}
