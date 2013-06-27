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
package org.overlord.rtgov.ep.jpa;

import static org.junit.Assert.*;

import org.junit.Test;

public class JPAEventProcessorTest {

    @Test
    public void testPersistEvent() {
    	JPAEventProcessor ep=new JPAEventProcessor();
    	
    	ep.setEntityManager("overlord-rtgov-ep-jpa");
    	
    	try {
    		ep.init();
    	} catch (Exception e) {
    		fail("Failed to initialize JPA event processor: "+e);
    	}
    	
    	TestEvent te1=new TestEvent();
    	te1.setId("1");
    	te1.setDescription("Hello");
    	
    	try {
			ep.process(null, te1, 1);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to process test event 1 with JPA event processor: "+e);
		}
    	   	
    	TestEvent te2=new TestEvent();
    	te2.setId("2");
    	te2.setDescription("World");
    	
    	try {
			ep.process(null, te2, 1);
		} catch (Exception e) {
			fail("Failed to process test event 2 with JPA event processor: "+e);
		}
    	
    	TestEvent result=ep.getEntityMgr().find(TestEvent.class, "1");
    	
    	if (result == null) {
    		fail("Result is null");
    	}
    	
    	if (!result.getDescription().equals("Hello")) {
    		fail("Expecting 'Hello', but got: "+result.getDescription());
    	}
    }
}
