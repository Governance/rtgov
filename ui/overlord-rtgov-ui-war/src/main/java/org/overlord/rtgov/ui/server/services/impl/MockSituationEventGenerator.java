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

import java.security.SecureRandom;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.overlord.rtgov.ui.client.model.SituationEventBean;
import org.overlord.rtgov.ui.server.services.ISituationEventGenerator;

/**
 * Randomly generates situation events.
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
@Alternative
public class MockSituationEventGenerator implements ISituationEventGenerator, Runnable {

    @Inject
    private RequestDispatcher dispatcher;

    /**
     * Constructor.
     */
    public MockSituationEventGenerator() {
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.ISituationEventGenerator#start()
     */
    @Override
    public void start() {
        new Thread(this).start();
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        SecureRandom random = new SecureRandom();
        System.out.println("Starting up the Mock Situation Event Generator!"); //$NON-NLS-1$
        while (Boolean.TRUE) {
            System.out.println("Sending a situation event: " + new Date()); //$NON-NLS-1$
            SituationEventBean payload = createRandomSituationEvent(random);
            MessageBuilder.createMessage()
                .toSubject("SitWatch") //$NON-NLS-1$
                .signalling()
                .with("situation", payload) //$NON-NLS-1$
                .noErrorHandling()
                .sendNowWith(dispatcher);
            try {
                long delay = random.nextInt(15000) + 5000l;
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @param random
     */
    private SituationEventBean createRandomSituationEvent(SecureRandom random) {
        String [] severity = { "low", "medium", "high", "critical" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        String [] type = {
                "Rate Limit Exceeded",  //$NON-NLS-1$
                "SLA Violation",  //$NON-NLS-1$
                "Rate Limit Approaching",  //$NON-NLS-1$
                "Some Other Random Error" }; //$NON-NLS-1$
        String [] subject = {
                "{urn:namespace}ImportantService|VeryImportantOperation", //$NON-NLS-1$
                "{urn:namespace}ServiceA|OperationB", //$NON-NLS-1$
                "{urn:namespace}SomeService|AnotherOperation" }; //$NON-NLS-1$
        SituationEventBean event = new SituationEventBean();
        event.setSituationId("1"); //$NON-NLS-1$
        event.setSeverity(severity[random.nextInt(4)]);
        event.setType(type[random.nextInt(4)]);
        event.setSubject(subject[random.nextInt(3)]);
        event.setTimestamp(new Date());
        return event;
    }

}
