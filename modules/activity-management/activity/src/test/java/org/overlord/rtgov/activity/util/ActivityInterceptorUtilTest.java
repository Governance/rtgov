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
package org.overlord.rtgov.activity.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.interceptor.ActivityInterceptor;
import org.overlord.rtgov.activity.interceptor.TestEventProcessor1;
import org.overlord.rtgov.activity.interceptor.TestEventProcessor2;
import org.overlord.rtgov.activity.interceptor.TestPredicate1;

public class ActivityInterceptorUtilTest {

    @Test
    public void test() {
        ActivityInterceptor ai1=new ActivityInterceptor();    
        ai1.setName("ai1");
        ai1.setVersion("1");
        ai1.setPredicate(new TestPredicate1());
        ai1.setEventProcessor(new TestEventProcessor1());
        ai1.getEventProcessor().getServices().put("testCache",
        		new org.overlord.rtgov.ep.service.InMemoryCacheManager());
        
        ActivityInterceptor ai2=new ActivityInterceptor();
        ai2.setName("ai2");
        ai2.setVersion("2");
        ai2.setEventProcessor(new TestEventProcessor2());

        try {
            java.util.List<ActivityInterceptor> ais=new java.util.Vector<ActivityInterceptor>();
            ais.add(ai1);
            ais.add(ai2);
            
            byte[] b=ActivityInterceptorUtil.serializeActivityInterceptorList(ais);
            
            if (b == null) {
                fail("null returned");
            }
            
            java.io.InputStream is=ActivityInterceptorUtilTest.class.getResourceAsStream("/json/aiList.json");
            byte[] inb2=new byte[is.available()];
            is.read(inb2);
            is.close();
            
            java.util.List<ActivityInterceptor> ais2=
            		ActivityInterceptorUtil.deserializeActivityInterceptorList(inb2);
            
            byte[] b2=ActivityInterceptorUtil.serializeActivityInterceptorList(ais2);            
            
            String s1=new String(b);
            String s2=new String(b2);
            
            if (!s1.equals(s2)) {
                fail("JSON is different: created="+s1+" stored="+s2);
            }

        } catch(Exception e) {
            e.printStackTrace();
            fail("Failed to serialize: "+e);
        }
    }
  
}
