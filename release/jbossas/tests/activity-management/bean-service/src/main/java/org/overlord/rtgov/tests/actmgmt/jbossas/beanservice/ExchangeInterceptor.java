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
package org.overlord.rtgov.tests.actmgmt.jbossas.beanservice;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.model.soa.RequestSent;
import org.overlord.rtgov.activity.model.soa.ResponseReceived;
import org.overlord.rtgov.activity.model.soa.ResponseSent;
import org.overlord.rtgov.activity.collector.ActivityCollector;
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
        	try {
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
        	} catch (Exception e) {
        		throw new HandlerException("Failed to record activity", e);
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
