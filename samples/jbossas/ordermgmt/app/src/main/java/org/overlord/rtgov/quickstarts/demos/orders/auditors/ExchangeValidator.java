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
package org.overlord.rtgov.quickstarts.demos.orders.auditors;

import javax.inject.Named;

import org.overlord.rtgov.switchyard.exchange.AbstractExchangeValidator;
import org.switchyard.ExchangePhase;
import org.switchyard.bus.camel.audit.Audit;
import org.switchyard.bus.camel.audit.Auditor;
import org.switchyard.bus.camel.processors.Processors;

/**
 * This class observes exchanges and validates them against validation policies.
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
