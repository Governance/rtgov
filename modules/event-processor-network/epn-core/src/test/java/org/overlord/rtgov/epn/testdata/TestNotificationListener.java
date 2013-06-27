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

import org.overlord.rtgov.epn.EventList;
import org.overlord.rtgov.epn.NotificationListener;

public class TestNotificationListener implements NotificationListener {

    private java.util.List<Entry> _entries=new java.util.Vector<Entry>();
    
    public void notify(String subject, EventList events) {
        _entries.add(new Entry(subject,events));
    }

    public java.util.List<Entry> getEntries() {
        return (_entries);
    }
    
    public class Entry {
        private String _subject=null;
        private EventList _events=null;
        
        public Entry(String subject, EventList events) {
            _subject = subject;
             _events = events;
        }
        
        public String getSubject() {
            return (_subject);
        }
        
        public EventList getEvents() {
            return(_events);
        }
    }
}
