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
import javax.naming.InitialContext;

import org.overlord.rtgov.activity.processor.InformationProcessor;
import org.overlord.rtgov.activity.processor.InformationProcessorManager;
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
    private static final String IP_MANAGER = "java:global/overlord-rtgov/InformationProcessorManager";
    
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
        
        try {
            InitialContext ctx=new InitialContext();
            
            _ipManager = (InformationProcessorManager)ctx.lookup(IP_MANAGER);

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
