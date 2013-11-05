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
package org.overlord.rtgov.internal.switchyard.exchange;

import java.util.EventObject;

import org.switchyard.Exchange;
import org.switchyard.runtime.event.ExchangeInitiatedEvent;

/**
 * This class provides the ExchangeInitiatedEvent implementation of the
 * event processor.
 *
 */
public class ExchangeInitiatedEventProcessor extends AbstractExchangeEventProcessor {
    
    /**
     * This is the default constructor.
     */
    public ExchangeInitiatedEventProcessor() {
        super(ExchangeInitiatedEvent.class, false);        
    }

    /**
     * {@inheritDoc}
     */
    protected Exchange getExchange(EventObject event) {
        return (((ExchangeInitiatedEvent)event).getExchange());
    }

}

