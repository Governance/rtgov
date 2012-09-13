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
package org.overlord.bam.samples.policy.acs;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.active.collection.ActiveCollectionSource;
import org.overlord.bam.active.collection.ActiveMap;
import org.overlord.bam.active.collection.embedded.EmbeddedActiveCollectionManager;
import org.overlord.bam.active.collection.util.ActiveCollectionUtil;

public class ACSTest {

    @Test
    public void testSuspendCustomer() {
        EmbeddedActiveCollectionManager acm=new EmbeddedActiveCollectionManager();
        
        ActiveCollectionSource testSource=null;
        
        // Load source
        try {
            java.io.InputStream is=ClassLoader.getSystemResourceAsStream("acs.json");
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            java.util.List<ActiveCollectionSource> acsList=ActiveCollectionUtil.deserializeACS(b);
            
            if (acsList.size() != 1) {
                fail("Should only be 1 source: "+acsList.size());
            }
            
            // Create generic active collection source implementation
            // from custom version in the config file
            testSource = new ActiveCollectionSource(acsList.get(0));
            
            acm.register(testSource);

        } catch (Exception e) {
            fail("Failed to register source: "+e);
        }
        
        // Get the ActiveMap
        ActiveMap am=(ActiveMap)testSource.getActiveCollection();
        
        if (am.getSize() != 0) {
            fail("Map should be empty: "+am.getSize());
        }
        
        // Send in a suspend customer notification
        testSource.maintainEntry(null, "+Fred");
        
        if (am.getSize() != 1) {
            fail("Should now be 1 suspended customer: "+am.getSize());
        }
        
        if (am.get("Fred") == null) {
            fail("No entry for customer 'Fred'");
        }
    }

    @Test
    public void testUnsuspendCustomer() {
        EmbeddedActiveCollectionManager acm=new EmbeddedActiveCollectionManager();
        
        ActiveCollectionSource testSource=null;
        
        // Load source
        try {
            java.io.InputStream is=ClassLoader.getSystemResourceAsStream("acs.json");
            
            byte[] b=new byte[is.available()];
            is.read(b);
            
            is.close();
            
            java.util.List<ActiveCollectionSource> acsList=ActiveCollectionUtil.deserializeACS(b);
            
            if (acsList.size() != 1) {
                fail("Should only be 1 source: "+acsList.size());
            }
            
            // Create generic active collection source implementation
            // from custom version in the config file
            testSource = new ActiveCollectionSource(acsList.get(0));
            
            acm.register(testSource);

        } catch (Exception e) {
            fail("Failed to register source: "+e);
        }
        
        // Get the ActiveMap
        ActiveMap am=(ActiveMap)testSource.getActiveCollection();
        
        if (am.getSize() != 0) {
            fail("Map should be empty: "+am.getSize());
        }
        
        // Add customer 'Fred' to map
        testSource.insert("Fred", "Fred");
        
        if (am.get("Fred") == null) {
            fail("No entry for customer 'Fred'");
        }
        
        // Send in a unsuspend customer notification
        testSource.maintainEntry(null, "-Fred");
        
        if (am.getSize() != 0) {
            fail("Should now be 0 suspended customers: "+am.getSize());
        }
    }

}
