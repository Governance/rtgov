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
package org.overlord.rtgov.internal.switchyard.bpel;

import java.util.EventObject;

import org.apache.ode.bpel.evt.NewProcessInstanceEvent;
import org.overlord.rtgov.internal.switchyard.AbstractEventProcessor;

/**
 * This class provides the BPEL component implementation of the
 * event processor.
 *
 */
public class NewProcessInstanceEventProcessor extends AbstractEventProcessor {

    /**
     * This is the default constructor.
     */
    public NewProcessInstanceEventProcessor() {
        super(NewProcessInstanceEvent.class);
    }

    /**
     * {@inheritDoc}
     */
    public void handleEvent(EventObject event) {
        NewProcessInstanceEvent bpelEvent=(NewProcessInstanceEvent)event;
        
        org.overlord.rtgov.activity.model.bpm.ProcessStarted ps=
                new org.overlord.rtgov.activity.model.bpm.ProcessStarted();
        
        ps.setProcessType(((NewProcessInstanceEvent)bpelEvent).getProcessName().toString());
        ps.setInstanceId(((NewProcessInstanceEvent)bpelEvent).getProcessInstanceId().toString());
        
        recordActivity(event, ps);
    }

}
