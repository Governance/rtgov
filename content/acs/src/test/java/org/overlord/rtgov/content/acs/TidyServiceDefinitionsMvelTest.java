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
