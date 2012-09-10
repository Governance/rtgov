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
package org.overlord.bam.activity.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.bam.activity.model.mom.MessageSent;
import org.overlord.bam.activity.model.soa.RequestSent;

public class ActivityUnitTest {

    @Test
    public void testNoDuplicateContext() {
        ActivityUnit au=new ActivityUnit();
        
        Context c1=new Context();
        c1.setType(Context.Type.Conversation);
        c1.setValue("v1");
        
        au.getContext().add(c1);
        
        MessageSent ms=new MessageSent();
        au.getActivityTypes().add(ms);
        
        Context c2=new Context();
        c2.setType(Context.Type.Conversation);
        c2.setValue("v1");
        
        ms.getContext().add(c2);
        
        Context c3=new Context();
        c3.setType(Context.Type.Endpoint);
        c3.setValue("v3");
        
        ms.getContext().add(c3);
        
        if (au.getAllContexts().size() != 2) {
            fail("Should be 2 contexts: "+au.getAllContexts().size());
        }
    }

    @Test
    public void testDerivedContext() {
        ActivityUnit au=new ActivityUnit();
        
        RequestSent rs=new RequestSent();
        rs.setMessageId("mid");
        au.getActivityTypes().add(rs);
        
        java.util.Set<Context> contexts=au.getAllContexts();
        
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
