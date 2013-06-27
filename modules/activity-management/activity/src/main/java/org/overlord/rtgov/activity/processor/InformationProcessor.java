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

import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This class provides an information processor that is
 * based on a set of expressions used to extract relevant data from
 * the supplied information.
 *
 */
public class InformationProcessor {
    
    private static final Logger LOG=Logger.getLogger(InformationProcessor.class.getName());

    private String _name=null;
    private String _version=null;
    private java.util.Map<String,TypeProcessor> _typeProcessors=
            new java.util.HashMap<String,TypeProcessor>();
    
    private boolean _initialized=false;
    
    /**
     * This method returns the name of the information processor.
     * 
     * @return The name
     */
    public String getName() {
        return (_name);
    }
    
    /**
     * This method sets the name of the information processor.
     * 
     * @param name The name
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * This method returns the version of the information processor.
     * 
     * @return The version
     */
    public String getVersion() {
        return (_version);
    }

    /**
     * This method sets the version of the information processor.
     * 
     * @param version The version
     */
    public void setVersion(String version) {
        _version = version;
    }
    
    /**
     * The map of types to processors.
     * 
     * @return The type to processor map
     */
    public java.util.Map<String, TypeProcessor> getTypeProcessors() {
        return (_typeProcessors);
    }

    /**
     * Initialize the information processor.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        
        if (!_initialized) {
            _initialized = true;

            for (TypeProcessor tp : _typeProcessors.values()) {
                tp.init();
            }
        }
    }

    /**
     * This method determines whether the specified type
     * is handled by the information processor.
     * 
     * @param type The type
     * @return Whether the information processor handles the type
     */
    public boolean isSupported(String type) {
        return (_typeProcessors.containsKey(type));
    }

    /**
     * This method processes supplied information to
     * extract relevant details, and then return an
     * appropriate representation of that information
     * for public distribution.
     * 
     * @param type The information type
     * @param info The information to be processed
     * @param headers The optional header information
     * @param actType The activity type to be annotated with
     *              details extracted from the information
     * @return The public representation of the information
     */
    public String process(String type, Object info,
                java.util.Map<String, Object> headers, ActivityType actType) {
        TypeProcessor processor=_typeProcessors.get(type);
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process type="+type+" info="
                    +info+" actType="+actType
                    +" with processor="+processor);
        }

        if (processor != null) {
            // Process the context and property details
            return (processor.process(info, headers, actType));
        }
        
        return null;
    }

    /**
     * Close the information processor.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
        for (TypeProcessor tp : _typeProcessors.values()) {
            tp.close();
        }
    }

}
