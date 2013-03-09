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
package org.overlord.rtgov.activity.interceptor.loader.jee;

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

import org.overlord.rtgov.activity.interceptor.ActivityInterceptor;
import org.overlord.rtgov.activity.interceptor.ActivityInterceptorManager;
import org.overlord.rtgov.activity.util.ActivityInterceptorUtil;

/**
 * This class provides the capability to load Activity Interceptors from a
 * defined file.
 *
 */
@ApplicationScoped
@Singleton
@Startup
@ConcurrencyManagement(BEAN)
public class JEEAILoader {
    
    private static final Logger LOG=Logger.getLogger(JEEAILoader.class.getName());
    
    private static final String AIS_JSON = "ais.json";
    private static final String AIS_MANAGER = "java:global/overlord-rtgov/ActivityInterceptorManager";
    
    private ActivityInterceptorManager _aiManager=null;
    private java.util.List<ActivityInterceptor> _activityInterceptors=null;
    
    /**
     * The constructor.
     */
    public JEEAILoader() {
    }
    
    /**
     * This method initializes the AI loader.
     */
    @PostConstruct
    public void init() {
        
        try {
            InitialContext ctx=new InitialContext();
            
            _aiManager = (ActivityInterceptorManager)ctx.lookup(AIS_MANAGER);

            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(AIS_JSON);
            
            if (is == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "ai-loader-jee.Messages").getString("AI-LOADER-JEE-1"));
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
                
                _activityInterceptors = ActivityInterceptorUtil.deserializeActivityInterceptorList(b);
                
                if (_activityInterceptors == null) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                            "ai-loader-jee.Messages").getString("AI-LOADER-JEE-2"));
                } else {
                    for (ActivityInterceptor ai : _activityInterceptors) {
                    	ai.init();
                    	
                        _aiManager.register(ai);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "ai-loader-jee.Messages").getString("AI-LOADER-JEE-3"), e);
        }
    }
    
    /**
     * This method closes the AI loader.
     */
    @PreDestroy
    public void close() {
        
        if (_aiManager != null && _activityInterceptors != null) {
            try {
                for (ActivityInterceptor ai : _activityInterceptors) {
                    _aiManager.unregister(ai);
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                        "ai-loader-jee.Messages").getString("AI-LOADER-JEE-4"), e);
            }
        }
    }       
}
