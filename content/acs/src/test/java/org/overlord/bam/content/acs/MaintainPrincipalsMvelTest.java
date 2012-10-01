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
package org.overlord.bam.content.acs;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mvel2.MVEL;
import org.overlord.bam.active.collection.ActiveCollectionSource;
import org.overlord.bam.active.collection.ActiveMap;
import org.overlord.bam.analytics.principal.PrincipalAction;

public class MaintainPrincipalsMvelTest {

    private static final String SCRIPT="MaintainPrincipals.mvel";
    
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
        
        ActiveCollectionSource acs=new ActiveCollectionSource();
        
        ActiveMap map=new ActiveMap("TestMap");
        
        acs.setActiveCollection(map);
        
        // Add some service definitions
        java.util.Map<String, Object> fred=new java.util.HashMap<String, Object>();
        fred.put("principal", "fred");
        fred.put("suspended", Boolean.TRUE);
        acs.insert("fred", fred);
        
        PrincipalAction action1=new PrincipalAction();
        action1.setPrincipal("fred");
        action1.getProperties().put("suspended", Boolean.FALSE);
       
        vars.put("acs", acs);
        vars.put("value", action1);
        
        MVEL.executeExpression(expression, vars);
        
        if (!map.containsKey("fred")) {
            fail("Map does not contain entry for 'fred'");
        }
        
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> props=
                    (java.util.Map<String, Object>)map.get("fred");
        
        if (!props.containsKey("suspended")) {
            fail("No 'suspended' property");
        }
        
        if (props.get("suspended") != Boolean.FALSE) {
            fail("'suspended' property is incorrect");
        }
    }
}
