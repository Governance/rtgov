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

import org.apache.ode.bpel.evt.VariableModificationEvent;
import org.overlord.rtgov.internal.switchyard.AbstractEventProcessor;

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
