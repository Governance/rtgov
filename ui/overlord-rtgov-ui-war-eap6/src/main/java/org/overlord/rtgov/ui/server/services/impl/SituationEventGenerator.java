/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.server.services.impl;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.overlord.rtgov.ui.client.model.SituationEventBean;
import org.overlord.rtgov.ui.provider.SituationEventListener;
import org.overlord.rtgov.ui.provider.SituationsProvider;
import org.overlord.rtgov.ui.server.services.ISituationEventGenerator;

/**
 * Situation event generator. This implementation obtains the situations by
 * registering a listener on the situations provider.
 */
@Singleton
@Alternative
public class SituationEventGenerator implements ISituationEventGenerator {

	@Inject
    private RequestDispatcher _dispatcher;

	@Inject
	private SituationsProvider _provider;
	
	/**
     * Constructor.
     */
    public SituationEventGenerator() {
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public void start() {
		_provider.addSituationEventListener(new SituationEventListener() {

			@Override
			public void onSituationEvent(SituationEventBean event) {
	            MessageBuilder.createMessage()
			            .toSubject("SitWatch") //$NON-NLS-1$
			            .signalling()
			            .with("situation", event) //$NON-NLS-1$
			            .noErrorHandling()
			            .sendNowWith(_dispatcher);
			}
			
		});
	}

}
