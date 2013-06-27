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

import org.overlord.rtgov.activity.util.ActivityUtil;

/**
 * This class represents a serialization based information transformer.
 *
 */
public class SerializeInformationTransformer extends InformationTransformer {

    private static final Logger LOG=Logger.getLogger(SerializeInformationTransformer.class.getName());
    
    /**
     * {@inheritDoc}
     */
    public String transform(Object information) {
        
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
    
            } else {
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
