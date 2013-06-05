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

import org.apache.ode.bpel.evt.VariableModificationEvent;
import org.overlord.rtgov.switchyard.AbstractEventProcessor;

/**
 * This class provides the BPEL component implementation of the
 * event processor.
 *
 */
public class VariableModificationEventProcessor extends AbstractEventProcessor {

    /**
     * This is the default constructor.
     */
    public VariableModificationEventProcessor() {
        super(VariableModificationEvent.class);
    }

    /**
     * {@inheritDoc}
     */
    public void handleEvent(EventObject event) {
        VariableModificationEvent bpelEvent=(VariableModificationEvent)event;
        
        org.overlord.rtgov.activity.model.bpm.ProcessVariableSet pvs=
                new org.overlord.rtgov.activity.model.bpm.ProcessVariableSet();
        
        org.w3c.dom.Node value=bpelEvent.getNewValue();
        
        // Unwrap if single part message
        if (value.getLocalName().equals("message") && value.getChildNodes().getLength() == 1) {
            // Unwrap message and single part
            value = value.getFirstChild().getFirstChild();
        } else if (value.getLocalName().equals("temporary-simple-type-wrapper")) {
            value = value.getFirstChild();   
        }
        
        String type=value.getLocalName();
        
        if (value.getNamespaceURI() != null) {
            type = "{"+value.getNamespaceURI()+"}"+type;
        }
        
        pvs.setVariableName(bpelEvent.getVarName());
        pvs.setVariableType(type);
        pvs.setVariableValue(getActivityCollector().processInformation(null, type,
                value, null, pvs));
        
        pvs.setProcessType(bpelEvent.getProcessName().toString());
        pvs.setInstanceId(bpelEvent.getProcessInstanceId().toString());
        
        recordActivity(event, pvs);
    }

}
