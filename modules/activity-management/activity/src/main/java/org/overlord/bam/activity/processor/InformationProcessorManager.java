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
package org.overlord.bam.activity.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.common.util.VersionUtil;

/**
 * This class manages a set of InformationProcessor
 * implementations.
 *
 */
public class InformationProcessorManager {
    
    private static final Logger LOG=Logger.getLogger(InformationProcessorManager.class.getName());

    private java.util.Map<String,InformationProcessor> _informationProcessorIndex=
            new java.util.HashMap<String,InformationProcessor>();
    private java.util.List<InformationProcessor> _informationProcessors=
                new java.util.ArrayList<InformationProcessor>();
    
    /**
     * The default constructor.
     */
    public InformationProcessorManager() {
    }
    
    /**
     * This method registers the information processor.
     * 
     * @param ip The information processor
     */
    public void register(InformationProcessor ip) {
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Register: information processor name="
                        +ip.getName()+" version="
                        +ip.getVersion()+" ip="+ip);
        }
        
        synchronized (_informationProcessorIndex) {
            // Check if information processor for same name already exists
            InformationProcessor existing=_informationProcessorIndex.get(ip.getName());
            
            if (existing != null) {
                
                // Check whether newer version
                if (VersionUtil.isNewerVersion(ip.getVersion(),
                        existing.getVersion())) {
                    
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Replace existing information processor version="
                                    +existing.getVersion());
                    }
                    
                    // Replace old version
                    _informationProcessorIndex.put(ip.getName(), ip);
                    
                    _informationProcessors.remove(existing);
                    _informationProcessors.add(ip);                    
                } else {
                                      
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine("Newer version '"+existing.getVersion()
                                +"' already registered");
                    }
                }
            } else {
                _informationProcessorIndex.put(ip.getName(), ip);
                _informationProcessors.add(ip);
            }
        }
    }
    
    /**
     * This method registers the information processor.
     * 
     * @param ip The information processor
     */
    public void unregister(InformationProcessor ip) {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Unregister: information processor name="
                        +ip.getName()+" version="
                        +ip.getVersion()+" ip="+ip);
        }
        
        synchronized (_informationProcessorIndex) {
            
            if (_informationProcessors.contains(ip)) {
                _informationProcessorIndex.remove(ip.getName());
                _informationProcessors.remove(ip);
                
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
