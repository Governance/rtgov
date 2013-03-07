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
package org.overlord.rtgov.call.trace.descriptors;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.app.LogMessage;
import org.overlord.rtgov.call.trace.util.CallTraceUtil;

/**
 * This class provides a default descriptor for activities that may be
 * presented within the call trace.
 *
 */
public class DefaultTaskDescriptor implements TaskDescriptor {

    private static final Logger LOG=Logger.getLogger(DefaultTaskDescriptor.class.getName());

    /**
     * {@inheritDoc}
     */
    public boolean isSupported(ActivityType at) {
        return (at instanceof LogMessage);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDescription(ActivityType at) {
        StringBuffer buf=new StringBuffer();
        
        buf.append(at.getClass().getSimpleName());
        
        try {
            BeanInfo bi=java.beans.Introspector.getBeanInfo(at.getClass());
            
            for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
                
                if (CallTraceUtil.shouldIncludeProperty(pd)) {
                    buf.append(" "+pd.getDisplayName());
                    
                    try {
                        Object value=pd.getReadMethod().invoke(at);
                        buf.append("="+value);
                        
                    } catch (Exception ex) {
                        buf.append("=<unavailable>");
                    }
                }
            }
        } catch (IntrospectionException e) {
            LOG.log(Level.SEVERE, MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "call-trace.Messages").getString("CALL-TRACE-2"),
                        at.getClass().getName()), e);
        }
        
        return (buf.toString());
    }
    
}
