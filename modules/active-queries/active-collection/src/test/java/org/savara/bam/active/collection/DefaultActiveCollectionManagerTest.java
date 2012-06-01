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
package org.savara.bam.active.collection;

import static org.junit.Assert.*;

import org.junit.Test;

public class DefaultActiveCollectionManagerTest {
    
    private static final String TEST_AC="TestActiveCollection";

    @Test
    public void testRegisterACS() {
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
        DefaultActiveCollectionSource acs=new DefaultActiveCollectionSource();
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
    public void testAlreadyRegisteredACS() {
        ActiveCollectionManager mgr=new DefaultActiveCollectionManager();
        
        DefaultActiveCollectionSource acs=new DefaultActiveCollectionSource();
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
        
        DefaultActiveCollectionSource acs=new DefaultActiveCollectionSource();
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
        
        DefaultActiveCollectionSource acs=new DefaultActiveCollectionSource();
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
}
