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
package org.overlord.rtgov.activity.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.dom.DOMSource;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.util.ActivityUtil;

/**
 * This class represents a serialization based information transformer.
 *
 */
public class SerializeInformationTransformer extends InformationTransformer {

    private static final Logger LOG=Logger.getLogger(SerializeInformationTransformer.class.getName());
    
    private boolean _includeHeaders=false;
    
    /**
     * This method sets whether headers should be included with the
     * serialized representation.
     * 
     * @param b Whether headers should be included
     */
    public void setIncludeHeaders(boolean b) {
        _includeHeaders = b;
    }
    
    /**
     * This method indicates whether headers will be included
     * with the serialized representation.
     * 
     * @return Whether headers will be included
     */
    public boolean getIncludeHeaders() {
        return (_includeHeaders);
    }
    
    /**
     * {@inheritDoc}
     */
    public String transform(Object information, java.util.Map<String, Object> headers, ActivityType activityType) {
        String ret=transformToString(information, false);
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Transform headers? '"+getIncludeHeaders());
        }
        
        if (getIncludeHeaders()) {
            
            if (headers == null) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Configuration indicates to include headers, but no headers supplied");
                }
            } else if (activityType == null) {
                LOG.severe("Cannot include headers as no activity type supplied");
            } else {
                java.util.Iterator<String> iter=headers.keySet().iterator();
                
                while (iter.hasNext()) {
                    String headerName=iter.next();
                    Object headerValue=headers.get(headerName);
                    
                    // Get transformed value
                    String transformed=transformToString(headerValue, true);
                    
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.finest("Transformed header='"+headerName+"' value='"+headerValue+"' into='"+transformed+"'");
                    }
                    
                    if (transformed != null) {
                        // Determine format if need to reconstruct message
                        String encoding="text";
                        
                        if (headerValue instanceof org.w3c.dom.Node || headerValue instanceof DOMSource) {
                            encoding = "dom";
                        }
                        
                        // Store properties
                        activityType.getProperties().put(headerName, transformed);
                        activityType.getProperties().put(getHeaderFormatPropertyName(headerName), encoding);
                    }
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This method determines the property name to use to store the
     * header value's original format.
     * 
     * @param headerName The header name
     * @return The format property name
     */
    protected String getHeaderFormatPropertyName(String headerName) {
        return (ActivityType.HEADER_FORMAT_PROPERTY_PREFIX+headerName);
    }
    
    /**
     * This method converts the supplied information to a string representation.
     * 
     * @param information The information
     * @param header Whether the information is a header value
     * @return The string representation
     */
    protected String transformToString(Object information, boolean header) {
        if (information instanceof String) {
            return ((String)information);
            
        } else {
            if (information instanceof DOMSource) {
                information = ((DOMSource)information).getNode();
            }
            
            if (information instanceof org.w3c.dom.Node) {
                try {
                    java.io.ByteArrayOutputStream baos=new java.io.ByteArrayOutputStream();
                    
                    javax.xml.transform.dom.DOMSource source=
                            new javax.xml.transform.dom.DOMSource((org.w3c.dom.Node)information);
                    javax.xml.transform.stream.StreamResult result=
                            new javax.xml.transform.stream.StreamResult(baos);
                    
                    javax.xml.transform.Transformer transformer=
                            javax.xml.transform.TransformerFactory.newInstance().newTransformer();
                        
                    transformer.transform(source, result);
                    
                    return (baos.toString());
                    
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to transformer DOM information '"+information+"'", e);
                }           
    
            } else if (!header) {
                try {
                    return (ActivityUtil.objectToJSONString(information));
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to transformer information '"+information+"' to JSON", e);
                }
            }
        }
        
        return null;
    }
    
}
