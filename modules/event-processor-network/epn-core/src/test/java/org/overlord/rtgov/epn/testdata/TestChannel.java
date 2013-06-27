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

import org.overlord.rtgov.epn.Channel;
import org.overlord.rtgov.epn.EventList;

public class TestChannel implements Channel {
    
    private java.util.List<Serializable> _events=new java.util.Vector<Serializable>();        

    public java.util.List<Serializable> getEvents() {
        return(_events);
    }

    public void send(EventList events) throws Exception {
        for (Serializable event : events) {
            _events.add(event);
        }
    }

    public void send(EventList events, int retriesLeft) throws Exception {
        for (Serializable event : events) {
            _events.add(event);
        }
    }

    public void close() throws Exception {
    }
    
}
