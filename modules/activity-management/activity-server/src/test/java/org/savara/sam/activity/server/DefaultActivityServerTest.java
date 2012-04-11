/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.savara.sam.activity.server;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.savara.bam.activity.model.Activity;
import org.savara.bam.activity.server.ActivityQuery;
import org.savara.bam.activity.server.DefaultActivityServer;
import org.savara.bam.activity.server.spi.ActivityNotifier;
import org.savara.bam.activity.server.spi.ActivityStore;

public class DefaultActivityServerTest {

    @Test
    public void testStoreAndNotify() {
        DefaultActivityServer as=new DefaultActivityServer();
        
        TestStore store=new TestStore();
        TestNotifier notifier1=new TestNotifier();
        TestNotifier notifier2=new TestNotifier();
        
        as.setActivityStore(store);
        as.getActivityNotifiers().add(notifier1);
        as.getActivityNotifiers().add(notifier2);
        
        java.util.List<Activity> list1=new java.util.Vector<Activity>();
        list1.add(new Activity());
        list1.add(new Activity());
        list1.add(new Activity());
        
        java.util.List<Activity> list2=new java.util.Vector<Activity>();
        list2.add(new Activity());
        list2.add(new Activity());
        
        try {
            as.store(list1);
            
            if (store.getActivityEvents().size() != list1.size()) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != list1.size()) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != list1.size()) {
                fail("Notifier 2 does not have correct number of events");
            }
            
            as.store(list2);
            
            if (store.getActivityEvents().size() != (list1.size()+list2.size())) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != (list1.size()+list2.size())) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != (list1.size()+list2.size())) {
                fail("Notifier 2 does not have correct number of events");
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to store: "+e);
        }
    }

    @Test
    public void testStoreFault() {
        DefaultActivityServer as=new DefaultActivityServer();
        
        TestStore store=new TestStore();
        TestNotifier notifier1=new TestNotifier();
        TestNotifier notifier2=new TestNotifier();
        
        as.setActivityStore(store);
        as.getActivityNotifiers().add(notifier1);
        as.getActivityNotifiers().add(notifier2);
        
        java.util.List<Activity> list1=new java.util.Vector<Activity>();
        list1.add(new Activity());
        list1.add(new Activity());
        list1.add(new Activity());
        
        java.util.List<Activity> list2=new java.util.Vector<Activity>();
        list2.add(new Activity());
        list2.add(new Activity());
        
        try {
            as.store(list1);
            
            if (store.getActivityEvents().size() != list1.size()) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != list1.size()) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != list1.size()) {
                fail("Notifier 2 does not have correct number of events");
            }
            
            store.fault();

            try {
                as.store(list2);
                
                fail("Should have thrown exception");
            } catch(Exception f) {
                // Ignore
            }
            
        } catch(Exception e) {
            fail("Failed to store: "+e);
        }
    }

    @Test
    public void testNotifierFault() {
        DefaultActivityServer as=new DefaultActivityServer();
        
        TestStore store=new TestStore();
        TestNotifier notifier1=new TestNotifier();
        TestNotifier notifier2=new TestNotifier();
        
        as.setActivityStore(store);
        as.getActivityNotifiers().add(notifier1);
        as.getActivityNotifiers().add(notifier2);
        
        java.util.List<Activity> list1=new java.util.Vector<Activity>();
        list1.add(new Activity());
        list1.add(new Activity());
        list1.add(new Activity());
        
        java.util.List<Activity> list2=new java.util.Vector<Activity>();
        list2.add(new Activity());
        list2.add(new Activity());
        
        try {
            as.store(list1);
            
            if (store.getActivityEvents().size() != list1.size()) {
                fail("Store does not have correct number of events");
            }
            
            if (notifier1.getActivityEvents().size() != list1.size()) {
                fail("Notifier 1 does not have correct number of events");
            }
            
            if (notifier2.getActivityEvents().size() != list1.size()) {
                fail("Notifier 2 does not have correct number of events");
            }
            
            notifier2.fault();

            try {
                as.store(list2);
                
                fail("Should have thrown exception");
            } catch(Exception f) {
                // Ignore
            }
            
        } catch(Exception e) {
            fail("Failed to store: "+e);
        }
    }

    public class TestStore implements ActivityStore {

        private List<Activity> _store=new java.util.Vector<Activity>();
        private boolean _fault=false;
        
        public void fault() {
            _fault = true;
        }
        
        public void store(List<Activity> activities) throws Exception {
            if (_fault) {
                throw new Exception("Fault in notifier");
            }
            _store.addAll(activities);
        }

        public List<Activity> query(ActivityQuery query) throws Exception {
            // TODO Auto-generated method stub
            return null;
        }
        
        public List<Activity> getActivityEvents() {
            return (_store);
        }
    }

    public class TestNotifier implements ActivityNotifier {

        private List<Activity> _store=new java.util.Vector<Activity>();
        private boolean _fault=false;
        
        public List<Activity> getActivityEvents() {
            return (_store);
        }

        public void fault() {
            _fault = true;
        }
        
        public void notify(List<Activity> activities) throws Exception {
            if (_fault) {
                throw new Exception("Fault in notifier");
            }
            _store.addAll(activities);
        }
    }
}
