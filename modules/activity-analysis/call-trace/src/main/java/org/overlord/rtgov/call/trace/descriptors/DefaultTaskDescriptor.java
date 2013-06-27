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
                    
                    try {
                        Object value=pd.getReadMethod().invoke(at);
                        
                        if (value != null) {
                            buf.append(" "+pd.getDisplayName());
                            buf.append("="+value);
                        }
                        
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, MessageFormat.format(
                                java.util.PropertyResourceBundle.getBundle(
                                "call-trace.Messages").getString("CALL-TRACE-3"),
                                    at.getClass().getName(), pd.getName()), ex);
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
