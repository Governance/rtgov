/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.ep.mvel;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.ep.mvel.MVELEventProcessor;

public class MVELEventProcessorTest {

    private static final String SOURCE_VALUE = "sourceValue";

    @Test
    public void testProcess() {
        MVELEventProcessor ep=new MVELEventProcessor();
        
        ep.setScript("script/Process.mvel");
        
        try {
            ep.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        ActivityUnit au=new ActivityUnit();
        
        RequestReceived rr=new RequestReceived();
        
        au.getActivityTypes().add(rr);
        
        java.util.Properties props=null;
        
        try {
            props = (java.util.Properties)ep.process(SOURCE_VALUE, au, 1);
        } catch (Exception e) {
            fail("Failed to process: "+e);
        }
        
        if (props == null) {
            fail("Properties not returned");
        }
        
        if (!props.get("source").equals(SOURCE_VALUE)) {
            fail("Source value incorrect");
        }
        
        if (props.get("event") != au) {
            fail("Event incorrect");
        }
        
        if (((Integer)props.get("retriesLeft")) != 1) {
            fail("Retries Left incorrect");
        }
    }
}
