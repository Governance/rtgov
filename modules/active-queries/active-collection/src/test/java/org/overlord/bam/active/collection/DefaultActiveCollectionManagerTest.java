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
import org.overlord.bam.active.collection.ActiveCollectionManager;
import org.overlord.bam.active.collection.ActiveCollectionSource;
import org.overlord.bam.active.collection.DefaultActiveCollectionManager;

public class DefaultActiveCollectionManagerTest {
    
    private static final String DERIVED_AC = "DerivedAC";
	private static final String TEST_AC="TestActiveCollection";

    @Test
    public void testRegisterACS() {
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
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
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
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
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
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
    public void testUnregisterACS() {
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
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
        
        // Check that the active collection for this source has been removed
        if (acs.getActiveCollection() != null) {
            fail("Active collection on source should not be set");
        }
        
        if (mgr.getActiveCollection(TEST_AC) != null) {
            fail("Should not be able to obtain active collection from manager");
        }
    }
    
    @Test
    public void testAlreadyUnregisteredACS() {
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
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
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();

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
			public boolean evaluate(Object item) {
				return true;
			}
        };
        
        mgr.create(DERIVED_AC, parent, predicate);
        
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
        DefaultActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
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
            ac.insert(null, new String("Object "+i));
        }
        
        mgr.cleanup();
        
        if (!ac.getHighWaterMarkWarningIssued()) {
            fail("Warning should have been issued");
        }
        
        ac.remove(0, null);
        ac.remove(0, null);
        ac.remove(0, null);
        
        // Perform cleanup again - which should cause the warning flag to be removed
        mgr.cleanup();
        
        if (ac.getHighWaterMarkWarningIssued()) {
            fail("Warning should no longer have be issued");
        }
        
    }

    public class TestActiveCollectionListener implements ActiveCollectionListener {
        
        protected java.util.List<ActiveCollection> _registered=new java.util.ArrayList<ActiveCollection>();
        protected java.util.List<ActiveCollection> _unregistered=new java.util.ArrayList<ActiveCollection>();

        public void registered(ActiveCollection ac) {
            _registered.add(ac);
        }

        public void unregistered(ActiveCollection ac) {
            _unregistered.add(ac);
        }
        
    }
}
