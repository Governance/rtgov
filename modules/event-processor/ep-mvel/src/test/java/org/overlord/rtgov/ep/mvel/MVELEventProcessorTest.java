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
