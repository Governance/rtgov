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
package org.overlord.rtgov.epn.testdata;

import java.io.Serializable;

import org.overlord.rtgov.ep.EventProcessor;

public class TestEventProcessorA extends EventProcessor {

    private java.util.List<Serializable> _events=new java.util.Vector<Serializable>();
    private java.util.List<Serializable> _retries=new java.util.Vector<Serializable>();
    private boolean _forwardEvents=false;
    
    public void setForwardEvents(boolean b) {
        _forwardEvents = b;
    }
    
    public void retry(Serializable event) {
        _retries.add(event);
    }
    
    public Serializable process(String source, Serializable event,
            int retriesLeft) throws Exception {
        if (_retries.contains(event)) {
            _retries.remove(event);
            throw new Exception("Please retry this event");
        }

        _events.add(event);

        return _forwardEvents ? event : null;
    }
    
    public java.util.List<Serializable> getEvents() {
        return(_events);
    }
}
