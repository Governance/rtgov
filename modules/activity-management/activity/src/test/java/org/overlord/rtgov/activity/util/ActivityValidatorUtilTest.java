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
import org.overlord.rtgov.activity.validator.ActivityValidator;
import org.overlord.rtgov.activity.validator.TestEventProcessor1;
import org.overlord.rtgov.activity.validator.TestEventProcessor2;
import org.overlord.rtgov.activity.validator.TestPredicate1;

public class ActivityValidatorUtilTest {

    @Test
    public void test() {
        ActivityValidator ai1=new ActivityValidator();    
        ai1.setName("av1");
        ai1.setVersion("1");
        ai1.setPredicate(new TestPredicate1());
        ai1.setEventProcessor(new TestEventProcessor1());
        ai1.getEventProcessor().getServices().put("testCache",
        		new org.overlord.rtgov.ep.service.InMemoryCacheManager());
        
        ActivityValidator ai2=new ActivityValidator();
        ai2.setName("av2");
        ai2.setVersion("2");
        ai2.setEventProcessor(new TestEventProcessor2());

        try {
            java.util.List<ActivityValidator> ais=new java.util.Vector<ActivityValidator>();
            ais.add(ai1);
            ais.add(ai2);
            
            byte[] b=ActivityValidatorUtil.serializeActivityValidatorList(ais);
            
            if (b == null) {
                fail("null returned");
            }
            
            java.io.InputStream is=ActivityValidatorUtilTest.class.getResourceAsStream("/json/avList.json");
            byte[] inb2=new byte[is.available()];
            is.read(inb2);
            is.close();
            
            java.util.List<ActivityValidator> ais2=
            		ActivityValidatorUtil.deserializeActivityValidatorList(inb2);
            
            byte[] b2=ActivityValidatorUtil.serializeActivityValidatorList(ais2);            
            
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
