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
package org.overlord.rtgov.quickstarts.demos.orders.interceptors;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.xml.namespace.QName;

import org.switchyard.Exchange;
import org.switchyard.ExchangeInterceptor;
import org.switchyard.HandlerException;
import org.switchyard.label.BehaviorLabel;

/**
 * Interceptor introduced to fix issue found when working on RTGOV-263,
 * where OrderAck could not be converted into InputSource.
 * 
 * Once swyd fix applied, then will be able to remove this interceptor.
 *
 */
@Named("ContentType")
public class ContentTypeInterceptor implements ExchangeInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void before(String target, Exchange exchange) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void after(String target, Exchange exchange) throws HandlerException {
        QName msgType = exchange.getContract().getProviderOperation().getOutputType();
        exchange.getMessage().getContext().setProperty(
                Exchange.CONTENT_TYPE, msgType).addLabels(BehaviorLabel.TRANSIENT.label());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getTargets() {
        return Arrays.asList(PROVIDER);
    }

}
