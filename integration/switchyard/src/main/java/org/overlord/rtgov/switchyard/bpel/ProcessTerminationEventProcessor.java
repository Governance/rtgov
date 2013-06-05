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
package org.overlord.rtgov.switchyard.bpel;

import java.util.EventObject;

import org.apache.ode.bpel.evt.ProcessTerminationEvent;
import org.overlord.rtgov.switchyard.AbstractEventProcessor;

/**
 * This class provides the BPEL component implementation of the
 * event processor.
 *
 */
public class ProcessTerminationEventProcessor extends AbstractEventProcessor {

    /**
     * This is the default constructor.
     */
    public ProcessTerminationEventProcessor() {
        super(ProcessTerminationEvent.class);
    }

    /**
     * {@inheritDoc}
     */
    public void handleEvent(EventObject event) {
        ProcessTerminationEvent bpelEvent=(ProcessTerminationEvent)event;
        
        org.overlord.rtgov.activity.model.bpm.ProcessCompleted pc=
                new org.overlord.rtgov.activity.model.bpm.ProcessCompleted();
        
        pc.setProcessType(bpelEvent.getProcessName().toString());
        pc.setInstanceId(bpelEvent.getProcessInstanceId().toString());
        
        recordActivity(event, pc);
    }

}
