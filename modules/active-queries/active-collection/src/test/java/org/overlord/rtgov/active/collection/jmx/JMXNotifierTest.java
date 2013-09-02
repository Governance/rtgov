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
package org.overlord.rtgov.active.collection.jmx;

import static org.junit.Assert.*;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.junit.Test;
import org.overlord.rtgov.active.collection.TestObject;
import org.overlord.rtgov.active.collection.jmx.JMXNotifier;

public class JMXNotifierTest {

    private static final String TEST_OBJECT_DESCRIPTION = "TestObjectDescription";
    private static final String UNSPECIFIED = "Unspecified";
    private static final String INSERT_TYPE_1 = "InsertType1";
    private static final String UPDATE_TYPE_1 = "UpdateType1";
    private static final String REMOVE_TYPE_1 = "RemoveType1";

    @Test
    public void testDescriptionStatic() {
        JMXNotifier notifier=new JMXNotifier();
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        TestObject to=new TestObject();
        to.setName(TEST_OBJECT_DESCRIPTION);
        
        String result=notifier.getDescription(to);
        
        if (!result.equals(to.toString())) {
            fail("Incorrect description");
        }
    }

    @Test
    public void testDescriptionScript() {
        JMXNotifier notifier=new JMXNotifier();
        
        notifier.setDescriptionScript("scripts/Description.mvel");
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        TestObject to=new TestObject();
        to.setName(TEST_OBJECT_DESCRIPTION);
        
        String result=notifier.getDescription(to);
        
        if (!result.equals(TEST_OBJECT_DESCRIPTION)) {
            fail("Incorrect description");
        }
    }

    @Test
    public void testInsertTypeStatic() {
        JMXNotifier notifier=new JMXNotifier();
        
        notifier.setInsertType(INSERT_TYPE_1);
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        String result=notifier.getInsertType(UNSPECIFIED);
        
        if (!result.equals(INSERT_TYPE_1)) {
            fail("Incorrect type");
        }
    }

    @Test
    public void testInsertTypeScript() {
        JMXNotifier notifier=new JMXNotifier();
        
        notifier.setInsertTypeScript("scripts/Type.mvel");
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        TestObject to=new TestObject();
        to.setName(INSERT_TYPE_1);
        
        String result=notifier.getInsertType(to);
        
        if (!result.equals(INSERT_TYPE_1)) {
            fail("Incorrect type");
        }
    }

    @Test
    public void testUpdateTypeStatic() {
        JMXNotifier notifier=new JMXNotifier();
        
        notifier.setUpdateType(UPDATE_TYPE_1);
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        String result=notifier.getUpdateType(UNSPECIFIED);
        
        if (!result.equals(UPDATE_TYPE_1)) {
            fail("Incorrect type");
        }
    }

    @Test
    public void testUpdateTypeScript() {
        JMXNotifier notifier=new JMXNotifier();
        
        notifier.setUpdateTypeScript("scripts/Type.mvel");
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        TestObject to=new TestObject();
        to.setName(UPDATE_TYPE_1);
        
        String result=notifier.getUpdateType(to);
        
        if (!result.equals(UPDATE_TYPE_1)) {
            fail("Incorrect type");
        }
    }

    @Test
    public void testRemoveTypeStatic() {
        JMXNotifier notifier=new JMXNotifier();
        
        notifier.setRemoveType(REMOVE_TYPE_1);
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        String result=notifier.getRemoveType(UNSPECIFIED);
        
        if (!result.equals(REMOVE_TYPE_1)) {
            fail("Incorrect type");
        }
    }

    @Test
    public void testRemoveTypeScript() {
        JMXNotifier notifier=new JMXNotifier();
        
        notifier.setRemoveTypeScript("scripts/Type.mvel");
        
        try {
            notifier.init();
        } catch (Exception e) {
            fail("Failed to init notifier: "+e);
        }
        
        TestObject to=new TestObject();
        to.setName(REMOVE_TYPE_1);
        
        String result=notifier.getRemoveType(to);
        
        if (!result.equals(REMOVE_TYPE_1)) {
            fail("Incorrect type");
        }
    }

    @Test
    public void testNotificationListener() {
        JMXNotifier notifier=new JMXNotifier();
        
        NotificationListener l=new NotificationListener() {
            public void handleNotification(Notification notification,
                    Object handback) {
            }           
        };
        
        // Register
        notifier.addNotificationListener(l, null, "Hello");
        
        // Unregister
        try {
            notifier.removeNotificationListener(l);
            
        } catch (Exception e) {
            fail("Failed to unregister listener");
        }
    }

    @Test
    public void testNotificationListenerNotFound() {
        JMXNotifier notifier=new JMXNotifier();
        
        NotificationListener l=new NotificationListener() {
            public void handleNotification(Notification notification,
                    Object handback) {
            }           
        };
        
        // Unregister
        try {
            notifier.removeNotificationListener(l);
            
            fail("Should have failed to remove listener");                
        } catch (Exception e) {
        }
    }

    @Test
    public void testNotificationListenerUnregisterMultiple() {
        JMXNotifier notifier=new JMXNotifier();
        
        NotificationListener l=new NotificationListener() {
            public void handleNotification(Notification notification,
                    Object handback) {
            }           
        };
        
        // Register
        notifier.addNotificationListener(l, null, "Hello");
        notifier.addNotificationListener(l, null, "Goodbye");

        // Unregister
        try {
            notifier.removeNotificationListener(l);
        } catch (Exception e) {            
            fail("Should NOT have failed to remove listeners");                
        }
        
        try {
            notifier.removeNotificationListener(l);
            fail("Should have failed, as first call to remove listener should have removed both");                
        } catch (Exception e) {            
        }
    }
}
