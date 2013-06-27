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
package org.overlord.rtgov.activity.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.validator.ActivityValidator;
import org.overlord.rtgov.activity.validator.TestCacheManager;
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
        ai1.getEventProcessor().getServices().put("testCache", new TestCacheManager());
        
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
