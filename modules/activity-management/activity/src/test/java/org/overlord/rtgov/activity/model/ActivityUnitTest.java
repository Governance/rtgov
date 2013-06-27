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
package org.overlord.rtgov.activity.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.mom.MessageSent;
import org.overlord.rtgov.activity.model.soa.RequestSent;

public class ActivityUnitTest {

    @Test
    public void testNoDuplicateContext() {
        ActivityUnit au=new ActivityUnit();
        
        Context c1=new Context();
        c1.setType(Context.Type.Conversation);
        c1.setValue("v1");
        
        MessageSent ms1=new MessageSent();
        au.getActivityTypes().add(ms1);
        
        ms1.getContext().add(c1);
        
        MessageSent ms2=new MessageSent();
        au.getActivityTypes().add(ms2);
        
        Context c2=new Context();
        c2.setType(Context.Type.Conversation);
        c2.setValue("v1");
        
        ms2.getContext().add(c2);
        
        Context c3=new Context();
        c3.setType(Context.Type.Endpoint);
        c3.setValue("v3");
        
        ms2.getContext().add(c3);
        
        if (au.contexts().size() != 2) {
            fail("Should be 2 contexts: "+au.contexts().size());
        }
    }

    @Test
    public void testDerivedContext() {
        ActivityUnit au=new ActivityUnit();
        
        RequestSent rs=new RequestSent();
        rs.setMessageId("mid");
        au.getActivityTypes().add(rs);
        
        java.util.Set<Context> contexts=au.contexts();
        
        if (contexts.size() != 1) {
            fail("Should be 1 context: "+contexts.size());
        }
        
        Context c=contexts.iterator().next();
        
        if (c.getType() != Context.Type.Message) {
            fail("Context type is not message");
        }
        
        if (!c.getValue().equals("mid")) {
            fail("Context value is not correct");
        }
    }

}
