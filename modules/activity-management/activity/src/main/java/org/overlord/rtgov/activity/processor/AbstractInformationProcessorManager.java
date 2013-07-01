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
import org.overlord.rtgov.common.util.VersionUtil;

/**
 * This class manages a set of InformationProcessor
 * implementations.
 *
 */
public abstract class AbstractInformationProcessorManager implements InformationProcessorManager {
    
    private static final Logger LOG=Logger.getLogger(AbstractInformationProcessorManager.class.getName());

    private java.util.Map<String,InformationProcessor> _informationProcessorIndex=
            new java.util.HashMap<String,InformationProcessor>();
    private java.util.List<InformationProcessor> _informationProcessors=
                new java.util.ArrayList<InformationProcessor>();
    
    /**
     * The default constructor.
     */
    public AbstractInformationProcessorManager() {
        InformationProcessorManagerAccessor.setInformationProcessorManager(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void register(InformationProcessor ip) throws Exception {
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register: information processor name="
                        +ip.getName()+" version="
                        +ip.getVersion()+" ip="+ip);
        }
        
        // Initialize the information processor
        ip.init();
        
        synchronized (_informationProcessorIndex) {
            boolean f_add=false;
            
            // Check if information processor for same name already exists
            InformationProcessor existing=_informationProcessorIndex.get(ip.getName());
            
            if (existing != null) {
                
                // Check whether newer version
                if (VersionUtil.isNewerVersion(existing.getVersion(),
                        ip.getVersion())) {
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Replace existing information processor version="
                                    +existing.getVersion());
                    }
                    
                    // Unregister old version
                    unregister(existing);
                    
                    // Add new version
                    f_add = true;                  
                } else {
                                      
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Newer version '"+existing.getVersion()
                                +"' already registered");
                    }
                }
            } else {
                f_add = true;
            }
            
            if (f_add) {
                _informationProcessorIndex.put(ip.getName(), ip);
                _informationProcessors.add(ip);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public InformationProcessor getInformationProcessor(String name) {
        synchronized (_informationProcessorIndex) {
            return (_informationProcessorIndex.get(name));
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String process(String processor, String type,
                    Object info, java.util.Map<String, Object> headers, ActivityType actType) {
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process: processor="+processor
                        +" type="+type+" info="+info
                        +" actType="+actType);
        }

        synchronized (_informationProcessorIndex) {
            
            if (processor != null) {
                InformationProcessor ip=_informationProcessorIndex.get(processor);
                
                if (ip.isSupported(type)) {
                    return (ip.process(type, info, headers, actType));
                }
            } else {
                for (int i=0; i < _informationProcessors.size(); i++) {
                    if (_informationProcessors.get(i).isSupported(type)) {
                        return (_informationProcessors.get(i).process(type, info, headers, actType));
                    }
                }
            }
        }        
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process: processor="+processor+" type="+type+" not supported");
        }

        return (null);
    }
    
    /**
     * {@inheritDoc}
     */
    public void unregister(InformationProcessor ip) throws Exception {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister: information processor name="
                        +ip.getName()+" version="
                        +ip.getVersion()+" ip="+ip);
        }
        
        synchronized (_informationProcessorIndex) {
            
            if (_informationProcessors.contains(ip)) {
                InformationProcessor removed=
                            _informationProcessorIndex.remove(ip.getName());
                _informationProcessors.remove(removed);
                
                removed.close();
                
            } else if (_informationProcessorIndex.containsKey(ip.getName())) {
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Another version of information processor name="
                            +ip.getName()+" is currently registered: existing version ="
                            +_informationProcessorIndex.get(ip.getName()).getVersion());
                }
            }
        }
    }
    
}
