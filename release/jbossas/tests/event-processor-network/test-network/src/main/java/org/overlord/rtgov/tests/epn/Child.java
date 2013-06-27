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
package org.overlord.rtgov.tests.epn;

import java.io.Serializable;

import org.overlord.rtgov.ep.EventProcessor;

/**
 * This class provides the child event processor.
 *
 */
public class Child extends EventProcessor {

    private java.util.List<java.io.Serializable> _events=new java.util.Vector<Serializable>();
    private java.util.List<java.io.Serializable> _retries=new java.util.Vector<Serializable>();
    private java.util.List<java.io.Serializable> _reject=new java.util.Vector<Serializable>();
    
    /**
     * List of events.
     * 
     * @return The events
     */
    public java.util.List<java.io.Serializable> events() {
        return (_events);
    }
    
    /**
     * List of retries.
     * 
     * @return The retries
     */
    public java.util.List<java.io.Serializable> retries() {
        return (_retries);
    }
    
    /**
     * This method requests that the supplied event should be rejected.
     * 
     * @param event The event
     */
    public void reject(java.io.Serializable event) {
        _reject.add(event);
    }
    
    @Override
    public Serializable process(String source, Serializable event,
            int retriesLeft) throws Exception {
        if (_reject.contains(event)) {
            _reject.remove(event);
            throw new Exception("Reject: "+event);
        }
        
        if (retriesLeft < 3) {
            _retries.add(event);
        }
        
        _events.add(event);
        
        return (null);
    }

}
