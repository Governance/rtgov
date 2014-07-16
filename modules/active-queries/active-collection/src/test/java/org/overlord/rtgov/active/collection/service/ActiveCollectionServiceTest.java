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
package org.overlord.rtgov.active.collection.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.active.collection.AbstractActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.ActiveMap;

public class ActiveCollectionServiceTest {

    private static final String TEST_LIST = "TestList";
    private static final String TEST_MAP = "TestMap";

    @Test
    public void testActiveList() {
        ActiveCollectionService cm=new ActiveCollectionService();
        
        TestActiveCollectionManager acm=new TestActiveCollectionManager();
        cm.setActiveCollectionManager(acm);
        
        ActiveList al=new ActiveList(TEST_LIST);
        acm.addActiveCollection(al);
        
        if (cm.getList(TEST_LIST) == null) {
            fail("Failed to retrieve list");
        }
    }

    @Test
    public void testActiveMap() {
        ActiveCollectionService cm=new ActiveCollectionService();
        
        TestActiveCollectionManager acm=new TestActiveCollectionManager();
        cm.setActiveCollectionManager(acm);
        
        ActiveMap am=new ActiveMap(TEST_MAP);
        acm.addActiveCollection(am);
        
        if (cm.getMap(TEST_MAP) == null) {
            fail("Failed to retrieve map");
        }
    }

    public static class TestActiveCollectionManager extends AbstractActiveCollectionManager {
        
        private java.util.Map<String,ActiveCollection> _collections=new java.util.HashMap<String,ActiveCollection>();
        
        public ActiveCollection getActiveCollection(String name) {
            return (_collections.get(name));
        }
        
        public void addActiveCollection(ActiveCollection ac) {
            _collections.put(ac.getName(), ac);
        }
    }
}
