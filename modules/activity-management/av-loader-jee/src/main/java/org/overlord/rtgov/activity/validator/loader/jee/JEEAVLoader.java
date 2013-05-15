/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.validator.loader.jee;

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

import org.overlord.rtgov.activity.util.ActivityValidatorUtil;
import org.overlord.rtgov.activity.validator.ActivityValidator;
import org.overlord.rtgov.activity.validator.ActivityValidatorManager;

/**
 * This class provides the capability to load Activity Validators from a
 * defined file.
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class JEEAVLoader {
    
    private static final Logger LOG=Logger.getLogger(JEEAVLoader.class.getName());
    
    private static final String AV_JSON = "av.json";
    private static final String AVS_MANAGER = "java:global/overlord-rtgov/ActivityValidatorManager";
    
    @Resource(lookup=AVS_MANAGER)
    private ActivityValidatorManager _avManager=null;

    private java.util.List<ActivityValidator> _activityValidators=null;
    
    /**
     * The constructor.
     */
    public JEEAVLoader() {
    }
    
    /**
     * This method initializes the Activity Validator loader.
     */
    @PostConstruct
    public void init() {
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(AV_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "av-loader-jee.Messages").getString("AV-LOADER-JEE-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _activityValidators = ActivityValidatorUtil.deserializeActivityValidatorList(b);
                
                if (_activityValidators == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "av-loader-jee.Messages").getString("AV-LOADER-JEE-2"));
                } else {
                    for (ActivityValidator ai : _activityValidators) {
                        ai.init();

                        _avManager.register(ai);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "av-loader-jee.Messages").getString("AV-LOADER-JEE-3"), e);
        }
    }
    
    /**
     * This method closes the AI loader.
     */
    @PreDestroy
    public void close() {
        
        if (_avManager != null && _activityValidators != null) {
            try {
                for (ActivityValidator ai : _activityValidators) {
                    _avManager.unregister(ai);
                }
            } catch (Throwable t) {
                if (LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, java.util.PropertyResourceBundle.getBundle(
                        "av-loader-jee.Messages").getString("AV-LOADER-JEE-4"), t);
                }
            }
        }
    }       
}
