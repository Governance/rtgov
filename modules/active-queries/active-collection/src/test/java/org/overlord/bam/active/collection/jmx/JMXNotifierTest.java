/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.active.collection.jmx;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.active.collection.TestObject;

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

}
