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
package org.overlord.rtgov.active.collection;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.active.collection.AbstractActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionListener;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.predicate.Predicate;

public class AbstractActiveCollectionManagerTest {
    
    private static final String DERIVED_AC = "DerivedAC";
	private static final String TEST_AC="TestActiveCollection";

    @Test
    public void testRegisterACS() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        // Check that the active collection for this source has been created
        if (acs.getActiveCollection() == null) {
            fail("Active collection on source has not been set");
        }
        
        if (mgr.getActiveCollection(TEST_AC) == null) {
            fail("Unable to obtain active collection from manager");
        }
    }

    @Test
    public void testNetworkListenerNotified() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        TestActiveCollectionListener l=new TestActiveCollectionListener();
        mgr.addActiveCollectionListener(l);
        
        if (l._registered.size() != 0) {
            fail("No active collections should be registered");
        }
        
        if (l._unregistered.size() != 0) {
            fail("No active collections should be unregistered");
        }
        
        try {
            mgr.register(acs);
            
            // Forces creation of active collection which is instantiated
            // lazily by the mgr
            mgr.getActiveCollection(acs.getName());
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        if (l._registered.size() != 1) {
            fail("1 active collection should be registered: "+l._registered.size());
        }
        
        if (l._unregistered.size() != 0) {
            fail("Still no active collections should be unregistered");
        }
        
        try {
            mgr.unregister(acs);
        } catch (Exception e) {
            fail("Failed to unregister active collection source: "+e);
        }
        
        if (l._registered.size() != 1) {
            fail("Still 1 active collection should be registered: "+l._registered.size());
        }
        
        if (l._unregistered.size() != 1) {
            fail("1 active collection should be unregistered: "+l._unregistered.size());
        }
    }

    @Test
    public void testAlreadyRegisteredACS() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        try {
            mgr.register(acs);
            fail("Should have thrown exception as already registered");
        } catch (Exception e) {
            // Ignore
        }
    }

    @Test
    public void testRegisterACSWithDerived() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        ActiveCollectionSource.DerivedDefinition dd=new ActiveCollectionSource.DerivedDefinition();
        dd.setName(DERIVED_AC);
        acs.getDerived().add(dd);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        // Check that the active collection for this source has been created
        if (acs.getActiveCollection() == null) {
            fail("Active collection on source has not been set");
        }
        
        if (mgr.getActiveCollection(TEST_AC) == null) {
            fail("Unable to obtain active collection from manager");
        }
        
        if (mgr.getActiveCollection(DERIVED_AC) == null) {
            fail("Unable to obtain derived active collection from manager");
        }
    }

    @Test
    public void testUnregisterACS() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        if (mgr.getActiveCollection(TEST_AC) == null) {
            fail("Unable to obtain active collection from manager");
        }

        try {
            mgr.unregister(acs);
        } catch (Exception e) {
            fail("Failed to unregister active collection source: "+e);
        }
        
        if (mgr.getActiveCollection(TEST_AC) != null) {
            fail("Should not be able to obtain active collection from manager");
        }
    }
    
    @Test
    public void testUnregisterACSWithDerived() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        ActiveCollectionSource.DerivedDefinition dd=new ActiveCollectionSource.DerivedDefinition();
        dd.setName(DERIVED_AC);
        acs.getDerived().add(dd);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        if (mgr.getActiveCollection(TEST_AC) == null) {
            fail("Unable to obtain active collection from manager");
        }

        if (mgr.getActiveCollection(DERIVED_AC) == null) {
            fail("Unable to obtain derived active collection from manager");
        }

        try {
            mgr.unregister(acs);
        } catch (Exception e) {
            fail("Failed to unregister active collection source: "+e);
        }
        
        if (mgr.getActiveCollection(TEST_AC) != null) {
            fail("Should not be able to obtain active collection from manager");
        }
        
        if (mgr.getActiveCollection(DERIVED_AC) != null) {
            fail("Should not be able to obtain derived active collection from manager");
        }
    }
    
    @Test
    public void testAlreadyUnregisteredACS() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        try {
            mgr.unregister(acs);
        } catch (Exception e) {
            fail("Failed to unregister active collection source: "+e);
        }
        
        try {
            mgr.unregister(acs);
            fail("Should have thrown exception as already unregistered");
        } catch (Exception e) {
            // Ignore
        }
    }
    
    @Test
    public void testCreateAndRemoveDerivedCollection() {
        ActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};

        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        ActiveCollection parent=mgr.getActiveCollection(TEST_AC);
        
        if (parent == null) {
        	fail("Failed to get parent collection");
        }
        
        Predicate predicate=new Predicate() {
			public boolean evaluate(ActiveCollectionContext context, Object item) {
				return true;
			}
        };
        
        mgr.create(DERIVED_AC, parent, predicate, null);
        
        if (mgr.getActiveCollection(DERIVED_AC) == null) {
        	fail("Failed to retrieve derived ac");
        }
        
        mgr.remove(DERIVED_AC);
        
        if (mgr.getActiveCollection(DERIVED_AC) != null) {
        	fail("Derived ac should no longer exist");
        }
    }
    
    @Test
    public void testRegisterACSWithHighWaterMark() {
        AbstractActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        acs.setName(TEST_AC);
        acs.setHighWaterMark(10);
        
        try {
            mgr.register(acs);
        } catch (Exception e) {
            fail("Failed to register active collection source: "+e);
        }
        
        // Check that the active collection for this source has been created
        if (acs.getActiveCollection() == null) {
            fail("Active collection on source has not been set");
        }
        
        ActiveCollection ac=mgr.getActiveCollection(TEST_AC);
        
        if (ac == null) {
            fail("Unable to obtain active collection from manager");
        }
        
        if (ac.getHighWaterMarkWarningIssued()) {
            fail("Warning should not have been issued yet");
        }
        
        for (int i=0; i < 12; i++) {
            ac.doInsert(null, new String("Object "+i));
        }
        
        mgr.cleanup();
        
        if (!ac.getHighWaterMarkWarningIssued()) {
            fail("Warning should have been issued");
        }
        
        ac.doRemove(0, null);
        ac.doRemove(0, null);
        ac.doRemove(0, null);
        
        // Perform cleanup again - which should cause the warning flag to be removed
        mgr.cleanup();
        
        if (ac.getHighWaterMarkWarningIssued()) {
            fail("Warning should no longer have be issued");
        }
        
    }
    
    @Test
    public void testActiveCollectionUnregisteredBeforeSource() {
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        ActiveList list=new ActiveList("Test");
        list.addActiveChangeListener(new ActiveChangeListener() {

            public void inserted(Object key, Object value) {
            }

            public void updated(Object key, Object value) {
            }

            public void removed(Object key, Object value) {
            }
            
        });
        
        acs.setActiveCollection(list);
        
        TestActiveCollectionListener l=new TestActiveCollectionListener();
        l.setCheckListenerRegistered(true);
        
        AbstractActiveCollectionManager mgr=new AbstractActiveCollectionManager() {};
        mgr.addActiveCollectionListener(l);
        
        try {
            // Required to register the source with the manager
            mgr.register(acs);
            
            // Need to overwrite the active collection with one that has a listener
            acs.setActiveCollection(list);
            
            mgr.unregister(acs);
        } catch (Exception e) {
            fail("Failed to unregister: "+e);
        }
    }

    public class TestActiveCollectionListener implements ActiveCollectionListener {
        
        protected java.util.List<ActiveCollection> _registered=new java.util.ArrayList<ActiveCollection>();
        protected java.util.List<ActiveCollection> _unregistered=new java.util.ArrayList<ActiveCollection>();
        protected boolean _checkListenerRegistered=false;

        public void setCheckListenerRegistered(boolean b) {
            _checkListenerRegistered = b;
        }
        
        public void registered(ActiveCollection ac) {
            _registered.add(ac);
        }

        public void unregistered(ActiveCollection ac) {
            _unregistered.add(ac);
            
            if (_checkListenerRegistered && ac.getActiveChangeListeners().size() == 0) {
                fail("No active change listeners registered");
            }
        }
        
    }
}
