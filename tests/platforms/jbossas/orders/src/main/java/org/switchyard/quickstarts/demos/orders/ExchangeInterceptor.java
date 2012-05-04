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
package org.switchyard.quickstarts.demos.orders;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
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

public class ExchangeInterceptor implements ExchangeHandler {
    
    private static final Logger LOG=Logger.getLogger(ExchangeInterceptor.class.getName());
    
    private static final String ACTIVITY_COLLECTOR = "java:global/savara-bam/ActivityCollector";

    private ActivityCollector _activityCollector=null;
    
    private boolean _initialized=false;
    
    @PostConstruct
    protected void init() {
        if (_activityCollector == null) {
            try {
                InitialContext ctx=new InitialContext();
                
                _activityCollector = (ActivityCollector)ctx.lookup(ACTIVITY_COLLECTOR);
            
            } catch(Exception e) {
                LOG.log(Level.SEVERE, "Failed to initialize activity collector", e);
            }
        }
        
        LOG.info("*********** Exchange Interceptor Initialized with collector="+_activityCollector);
        
        _initialized = true;
    }

    public void handleMessage(Exchange exchange) throws HandlerException {
        if (!_initialized) {
            init();
        }
        
        LOG.info("********* (init="+_initialized+") HANDLE MESSAGE FOR EXCHANGE="+exchange);
        
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
    }

}
