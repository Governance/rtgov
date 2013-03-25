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
