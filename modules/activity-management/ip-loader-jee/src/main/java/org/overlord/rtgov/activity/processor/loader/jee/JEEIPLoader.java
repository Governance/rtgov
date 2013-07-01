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
package org.overlord.rtgov.activity.processor.loader.jee;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.overlord.rtgov.activity.processor.InformationProcessor;
import org.overlord.rtgov.activity.processor.InformationProcessorManager;
import org.overlord.rtgov.activity.processor.InformationProcessorManagerAccessor;
import org.overlord.rtgov.activity.processor.validation.IPValidationListener;
import org.overlord.rtgov.activity.processor.validation.IPValidator;
import org.overlord.rtgov.activity.util.InformationProcessorUtil;

/**
 * This class provides the capability to load Information Processors from a
 * defined file.
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class JEEIPLoader {
    
    private static final Logger LOG=Logger.getLogger(JEEIPLoader.class.getName());
    
    private static final String IP_JSON = "ip.json";
    
    private InformationProcessorManager _ipManager=null;
    private java.util.List<InformationProcessor> _informationProcessors=null;
    
    /**
     * The constructor.
     */
    public JEEIPLoader() {
    }
    
    /**
     * This method initializes the IP loader.
     */
    @PostConstruct
    public void init() {
        
        _ipManager = InformationProcessorManagerAccessor.getInformationProcessorManager();
        
        if (_ipManager == null) {
            LOG.severe(java.util.PropertyResourceBundle.getBundle(
                "ip-loader-jee.Messages").getString("IP-LOADER-JEE-5"));
        }

        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(IP_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "ip-loader-jee.Messages").getString("IP-LOADER-JEE-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _informationProcessors = InformationProcessorUtil.deserializeInformationProcessorList(b);
                
                if (_informationProcessors == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "ip-loader-jee.Messages").getString("IP-LOADER-JEE-2"));
                } else {
                    for (InformationProcessor ip : _informationProcessors) {
                        ip.init();
                        
                        if (IPValidator.validate(ip, getValidationListener())) {
                            _ipManager.register(ip);
                        } else {
                            ip.close();
                            
                            // TODO: Do we need to halt the deployment due to failures? (RTGOV-199)
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "ip-loader-jee.Messages").getString("IP-LOADER-JEE-3"), e);
        }
    }
    
    /**
     * This method returns the validation listener.
     * 
     * @return The validation listener
     */
    protected IPValidationListener getValidationListener() {
        return (new IPValidationListener() {

            public void error(InformationProcessor ip, Object target, String issue) {
                LOG.severe(issue);
            }
            
        });
    }
    
    /**
     * This method closes the EPN loader.
     */
    @PreDestroy
    public void close() {
        
        if (_ipManager != null && _informationProcessors != null) {
            try {
                for (InformationProcessor ip : _informationProcessors) {
                    _ipManager.unregister(ip);
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "ip-loader-jee.Messages").getString("IP-LOADER-JEE-4"), e);
            }
        }
    }       
}
