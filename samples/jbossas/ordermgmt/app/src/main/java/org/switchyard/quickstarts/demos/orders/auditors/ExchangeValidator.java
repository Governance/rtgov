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
package org.switchyard.quickstarts.demos.orders.auditors;

import javax.inject.Named;

import org.overlord.rtgov.switchyard.exchange.AbstractExchangeValidator;
import org.switchyard.ExchangePhase;
import org.switchyard.bus.camel.audit.Audit;
import org.switchyard.bus.camel.audit.Auditor;
import org.switchyard.bus.camel.processors.Processors;

/**
 * This class observes exchanges and uses the information to create activity
 * events.
 *
 */
@Audit({Processors.TRANSFORMATION})
@Named("ExchangeValidator")
public class ExchangeValidator extends AbstractExchangeValidator implements Auditor {
    
    /**
     * {@inheritDoc}
     */
    public void afterCall(Processors processor, org.apache.camel.Exchange exch) {
        
        ExchangePhase phase=exch.getProperty("org.switchyard.bus.camel.phase", ExchangePhase.class);        

        if (phase == ExchangePhase.OUT) {
            handleExchange(exch, phase);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void beforeCall(Processors processor, org.apache.camel.Exchange exch) {

        ExchangePhase phase=exch.getProperty("org.switchyard.bus.camel.phase", ExchangePhase.class);        

        if (phase == ExchangePhase.IN) {
            handleExchange(exch, phase);
        }
    }
}
