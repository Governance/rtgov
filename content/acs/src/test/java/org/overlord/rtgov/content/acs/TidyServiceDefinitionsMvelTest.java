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
package org.overlord.rtgov.content.acs;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.mvel2.MVEL;
import org.overlord.rtgov.active.collection.ActiveCollectionSource;
import org.overlord.rtgov.active.collection.ActiveMap;
import org.overlord.rtgov.analytics.service.ServiceDefinition;

public class TidyServiceDefinitionsMvelTest {

    private static final String SCRIPT="TidyServiceDefinitions.mvel";
    
    @Test
    public void testTidyServiceDescriptions() {
        Object expression=null;
        
        try {
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(SCRIPT);
            
            if (is == null) {
               fail("Unable to locate '"+SCRIPT+"'");
            } else {
                byte[] b=new byte[is.available()];
                is.read(b);
                is.close();
    
                // Compile expression
                expression=MVEL.compileExpression(new String(b));                
            }
        } catch(Exception e) {
            fail("Failed to test script: "+e);
        }

        java.util.Map<String,Object> vars=
                new java.util.HashMap<String, Object>();
        
        java.util.Map<String,Object> internalVariables=
                new java.util.HashMap<String, Object>();
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        acs.getProperties().put("maxSnapshots", 3);
        
        ActiveMap map=new ActiveMap("TestMap");
        
        acs.setActiveCollection(map);
        
        // Add some service definitions
        ServiceDefinition sd1=new ServiceDefinition();
        sd1.setServiceType("sd1");
        acs.insert(sd1.getServiceType(), sd1);
        
        ServiceDefinition sd2=new ServiceDefinition();
        sd2.setServiceType("sd2");
        acs.insert(sd2.getServiceType(), sd2);

        vars.put("acs", acs);
        vars.put("variables", internalVariables);
        
        MVEL.executeExpression(expression, vars);
        
        @SuppressWarnings("unchecked")
        java.util.List<Object> snapshots=(List<Object>)
                internalVariables.get("snapshots");
        
        if (snapshots == null) {
            fail("No snapshots recorded");
        }
        
        if (snapshots.size() != 1) {
            fail("Expecting 1 snapshot: "+snapshots.size());
        }
        
        MVEL.executeExpression(expression, vars);
        
        if (snapshots.size() != 2) {
            fail("Expecting 2 snapshot: "+snapshots.size());
        }
        
        MVEL.executeExpression(expression, vars);
        
        if (snapshots.size() != 3) {
            fail("Expecting 3 snapshot: "+snapshots.size());
        }
        
        // Need to check that only three snapshots (as defined
        // by the cleanup cycle) will be retained
        MVEL.executeExpression(expression, vars);
        
        if (snapshots.size() != 3) {
            fail("Expecting 3 snapshot: "+snapshots.size());
        }
    }

}
