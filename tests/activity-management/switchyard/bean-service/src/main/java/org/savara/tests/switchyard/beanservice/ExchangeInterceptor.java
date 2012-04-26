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
package org.savara.tests.switchyard.beanservice;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.savara.bam.activity.model.soa.RequestReceived;
import org.savara.bam.activity.model.soa.RequestSent;
import org.savara.bam.activity.model.soa.ResponseReceived;
import org.savara.bam.activity.model.soa.ResponseSent;
import org.savara.bam.collector.ActivityCollector;
import org.switchyard.Exchange;
import org.switchyard.ExchangeHandler;
import org.switchyard.ExchangePhase;
import org.switchyard.HandlerException;
import org.switchyard.handlers.MessageTrace;

public class ExchangeInterceptor extends MessageTrace implements ExchangeHandler {
    
    private static final Logger LOG=Logger.getLogger(ExchangeInterceptor.class.getName());
    
    @Inject
    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;
    
    protected void init() {
        if (_activityCollector == null) {
            try {
                InitialContext ctx=new InitialContext();
            
                BeanManager beanManager=(BeanManager)ctx.lookup("java:comp/BeanManager");
                
                if (beanManager == null) {
                    LOG.severe("Failed to lookup BeanManager from JNDI");
                } else {
                    java.util.Set<Bean<?>> beans=
                                beanManager.getBeans(ActivityCollector.class);
                    
                    if (beans.size() == 0) {
                        LOG.severe("No ActivityCollector beans were found");
                    } else {
                        @SuppressWarnings("unchecked")
                        Bean<ActivityCollector> bean=(Bean<ActivityCollector>)
                                            beans.iterator().next();
                        CreationalContext<ActivityCollector> cc =
                                    beanManager.createCreationalContext(bean);
                        _activityCollector = (ActivityCollector)
                                    beanManager.getReference(bean, ActivityCollector.class, cc);
                    }
                }
            } catch(Exception e) {
                LOG.log(Level.SEVERE, "Failed to initialize activity collector", e);
            }
        }
        
        _initialized = true;
    }

    public void handleMessage(Exchange exchange) throws HandlerException {
        if (!_initialized) {
            init();
        }
        super.handleMessage(exchange);
        
        if (_activityCollector != null) {
            if (exchange.getPhase() == ExchangePhase.IN) {
                RequestSent sent=new RequestSent();
                
                _activityCollector.record(sent);
                
                RequestReceived recvd=new RequestReceived();
                
                _activityCollector.record(recvd);
                
            } else if (exchange.getPhase() == ExchangePhase.OUT) {
                ResponseSent sent=new ResponseSent();
                
                _activityCollector.record(sent);
                
                ResponseReceived recvd=new ResponseReceived();
                
                _activityCollector.record(recvd);
            }
        }
    }

    public void handleFault(Exchange exchange) {
        if (!_initialized) {
            init();
        }
        super.handleFault(exchange);
    }

}
