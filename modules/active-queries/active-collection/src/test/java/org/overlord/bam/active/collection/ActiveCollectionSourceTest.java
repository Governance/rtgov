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
package org.overlord.bam.active.collection;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.active.collection.ActiveList;

public class ActiveCollectionSourceTest {

    @Test
    public void testActiveChangeListenerFailConfig() {
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.getActiveChangeListeners().add(new TestActiveChangeListener());
        
        try {
            acs.init();
        
            fail("Should have failed initialization as no active collection");
        } catch (Exception e) {
        }
    }
    
    @Test
    public void testActiveChangeListenerRegistered() {
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.getActiveChangeListeners().add(new TestActiveChangeListener());

        ActiveList list=new ActiveList("TestList");
        
        acs.setActiveCollection(list);
       
        if (list.getActiveChangeListeners().size() != 0) {
            fail("Should be 0 listeners: "+list.getActiveChangeListeners().size());
        }

        try {
            acs.init();       
        } catch (Exception e) {
            fail("Failed to initialize the active collection source: "+e);
        }
        
        if (list.getActiveChangeListeners().size() != 1) {
            fail("Should be 1 listener: "+list.getActiveChangeListeners().size());
        }
        
        try {
            acs.close();       
        } catch (Exception e) {
            fail("Failed to close the active collection source: "+e);
        }
        
        if (list.getActiveChangeListeners().size() != 0) {
            fail("Should be 0 listeners again (after close): "+list.getActiveChangeListeners().size());
        }
    }
    
    public static class TestActiveChangeListener extends AbstractActiveChangeListener {
        
        protected java.util.List<Object> _insertedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _insertedValue=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _upatedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _updatedValue=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _removedKey=new java.util.ArrayList<Object>();
        protected java.util.List<Object> _removedValue=new java.util.ArrayList<Object>();        

        public void inserted(Object key, Object value) {
            _insertedKey.add(key);
            _insertedValue.add(value);
        }

        public void updated(Object key, Object value) {
            _upatedKey.add(key);
            _updatedValue.add(value);
        }

        public void removed(Object key, Object value) {
            _removedKey.add(key);
            _removedValue.add(value);
        }
        
    }
}
    
