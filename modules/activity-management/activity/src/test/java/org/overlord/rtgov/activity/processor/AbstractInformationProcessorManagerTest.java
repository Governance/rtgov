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
package org.overlord.rtgov.activity.processor;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.processor.AbstractInformationProcessorManager;
import org.overlord.rtgov.activity.processor.InformationProcessor;
import org.overlord.rtgov.activity.processor.InformationProcessorManager;

public class AbstractInformationProcessorManagerTest {

    private static final String IP_NAME1 = "IPName1";

    @Test
    public void testRegister() {
        InformationProcessorManager ipm=new AbstractInformationProcessorManager(){};
        
        InformationProcessor ip=new InformationProcessor();
        ip.setName(IP_NAME1);
        ip.setVersion("1");
        
        try {
            ipm.register(ip);
        } catch (Exception e) {
            fail("Failed to register");
        }
        
        if (ipm.getInformationProcessor(IP_NAME1) == null) {
            fail("Information processor not registered");
        }
    }

    @Test
    public void testRegisterFailed() {
        InformationProcessorManager ipm=new AbstractInformationProcessorManager(){};
        
        InformationProcessor ip=new InformationProcessor() {
            
            public void init() throws Exception {
                throw new Exception("FAIL");
            }
        };
        
        ip.setName(IP_NAME1);
        ip.setVersion("1");
        
        try {
            ipm.register(ip);
            fail("Should have failed to register");
        } catch (Exception e) {
        }
        
    }

    @Test
    public void testRegisterNewer() {
        InformationProcessorManager ipm=new AbstractInformationProcessorManager(){};
        
        InformationProcessor ip1=new InformationProcessor();
        ip1.setName(IP_NAME1);
        ip1.setVersion("1");
        
        try {
            ipm.register(ip1);
        } catch (Exception e) {
            fail("Failed to register");
        }
               
        if (ipm.getInformationProcessor(IP_NAME1) == null) {
            fail("Information processor not registered");
        }
        
        if (!ipm.getInformationProcessor(IP_NAME1).getVersion().equals("1")) {
            fail("Information processor not version 1: "+
                        ipm.getInformationProcessor(IP_NAME1).getVersion());
        }

        InformationProcessor ip2=new InformationProcessor();
        ip2.setName(IP_NAME1);
        ip2.setVersion("2");
        
        try {
            ipm.register(ip2);
        } catch (Exception e) {
            fail("Failed to register");
        }
        
        if (ipm.getInformationProcessor(IP_NAME1) == null) {
            fail("Information processor not registered");
        }
        
        if (!ipm.getInformationProcessor(IP_NAME1).getVersion().equals("2")) {
            fail("Information processor not version 2: "+
                        ipm.getInformationProcessor(IP_NAME1).getVersion());
        }

    }
    
    @Test
    public void testRegisterOlder() {
        InformationProcessorManager ipm=new AbstractInformationProcessorManager(){};
        
        InformationProcessor ip1=new InformationProcessor();
        ip1.setName(IP_NAME1);
        ip1.setVersion("2");
        
        try {
            ipm.register(ip1);
        } catch (Exception e) {
            fail("Failed to register");
        }
               
        if (ipm.getInformationProcessor(IP_NAME1) == null) {
            fail("Information processor not registered");
        }
        
        if (!ipm.getInformationProcessor(IP_NAME1).getVersion().equals("2")) {
            fail("Information processor not version 2: "+
                        ipm.getInformationProcessor(IP_NAME1).getVersion());
        }

        InformationProcessor ip2=new InformationProcessor();
        ip2.setName(IP_NAME1);
        ip2.setVersion("1");
        
        try {
            ipm.register(ip2);
        } catch (Exception e) {
            fail("Failed to register");
        }
        
        if (ipm.getInformationProcessor(IP_NAME1) == null) {
            fail("Information processor not registered");
        }
        
        // Version should remain 2
        if (!ipm.getInformationProcessor(IP_NAME1).getVersion().equals("2")) {
            fail("Information processor not version 2: "+
                        ipm.getInformationProcessor(IP_NAME1).getVersion());
        }

    }
    
    @Test
    public void testUnregisterNewerRegisterOlder() {
        InformationProcessorManager ipm=new AbstractInformationProcessorManager(){};
        
        InformationProcessor ip1=new InformationProcessor();
        ip1.setName(IP_NAME1);
        ip1.setVersion("2");
        
        try {
            ipm.register(ip1);
        } catch (Exception e) {
            fail("Failed to register");
        }
               
        if (ipm.getInformationProcessor(IP_NAME1) == null) {
            fail("Information processor not registered");
        }
        
        if (!ipm.getInformationProcessor(IP_NAME1).getVersion().equals("2")) {
            fail("Information processor not version 2: "+
                        ipm.getInformationProcessor(IP_NAME1).getVersion());
        }

        try {
            ipm.unregister(ip1);
        } catch (Exception e) {
            fail("Failed to unregister");
        }
        
        InformationProcessor ip2=new InformationProcessor();
        ip2.setName(IP_NAME1);
        ip2.setVersion("1");
        
        try {
            ipm.register(ip2);
        } catch (Exception e) {
            fail("Failed to register");
        }
        
        if (ipm.getInformationProcessor(IP_NAME1) == null) {
            fail("Information processor not registered");
        }
        
        if (!ipm.getInformationProcessor(IP_NAME1).getVersion().equals("1")) {
            fail("Information processor not version 1: "+
                        ipm.getInformationProcessor(IP_NAME1).getVersion());
        }

    }
}
