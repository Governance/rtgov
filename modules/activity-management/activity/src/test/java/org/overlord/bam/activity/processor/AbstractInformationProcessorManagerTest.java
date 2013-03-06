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
package org.overlord.rtgov.activity.processor;

import static org.junit.Assert.*;

import org.junit.Test;

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
