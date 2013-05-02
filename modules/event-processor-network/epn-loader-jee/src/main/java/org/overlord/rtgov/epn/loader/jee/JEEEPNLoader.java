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
package org.overlord.rtgov.epn.loader.jee;

import static javax.ejb.ConcurrencyManagementType.BEAN;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.overlord.rtgov.epn.AbstractEPNLoader;
import org.overlord.rtgov.epn.EPNManager;
import org.overlord.rtgov.epn.Network;
import org.overlord.rtgov.epn.util.NetworkUtil;

/**
 * This class provides the capability to load an EPN Network from a
 * defined file.
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class JEEEPNLoader extends AbstractEPNLoader {
    
    private static final Logger LOG=Logger.getLogger(JEEEPNLoader.class.getName());
    
    private static final String EPN_JSON = "epn.json";

    @Resource(lookup=EPNManager.URI)
    private EPNManager _epnManager=null;
 
    private Network _network=null;
    
    /**
     * The constructor.
     */
    public JEEEPNLoader() {
    }
    
    /**
     * This method initializes the EPN loader.
     */
    @PostConstruct
    public void init() {
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(EPN_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "epn-loader-jee.Messages").getString("EPN-LOADER-JEE-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _network=NetworkUtil.deserialize(b);
                
                // Pre-initialize the network to avoid any contextual class
                // loading issues. Within JEE, the registration of the network
                // will be done in the context of the core war, while as the
                // event processors require the classloading context associated
                // with the network deployment.
                preInit(_network);
                
                _epnManager.register(_network);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "epn-loader-jee.Messages").getString("EPN-LOADER-JEE-2"), e);
        }
    }
    
    /**
     * This method closes the EPN loader.
     */
    @PreDestroy
    public void close() {
        
        if (_epnManager != null && _network != null) {
            try {
                _epnManager.unregister(_network.getName(), _network.getVersion());
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "epn-loader-jee.Messages").getString("EPN-LOADER-JEE-3"), e);
            }
        }
    }       
}
