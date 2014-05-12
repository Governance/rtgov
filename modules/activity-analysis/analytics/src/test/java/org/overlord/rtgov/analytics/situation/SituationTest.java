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
package org.overlord.rtgov.analytics.situation;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.Situation.Severity;

public class SituationTest {

	public EntityManager getEntityManager() {
		EntityManager ret=null;
		
		try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("overlord-rtgov-situations", null);
			
			ret = emf.createEntityManager();
			
		} catch (Exception e) {
			e.printStackTrace();
			
			fail("Failed to get entity manager: "+e);
		}
		
		return (ret);
	}

	@Test
	public void testPersistSituation() {
		EntityManager em=getEntityManager();
		
		em.getTransaction().begin();
		
		Situation s1=new Situation();
		
		java.util.Set<ActivityTypeId> atid1=new java.util.LinkedHashSet<ActivityTypeId>();
		atid1.add(new ActivityTypeId("1", 1));
		
		s1.setActivityTypeIds(atid1);
		
		java.util.Set<Context> ctxt1=new java.util.LinkedHashSet<Context>();
		
		ctxt1.add(new Context(Type.Message, "M1"));
		
		s1.setContext(ctxt1);

		s1.getSituationProperties().put("Prop1", "Value1");
		
		s1.setDescription("This is the first situation");
		s1.setSeverity(Severity.Critical);
		s1.setSubject("OrderService");
		s1.setTimestamp(System.currentTimeMillis());
		s1.setType("SLA violation");
		
		em.persist(s1);

		Situation s2=new Situation();
		
		java.util.Set<ActivityTypeId> atid2=new java.util.LinkedHashSet<ActivityTypeId>();
		atid2.add(new ActivityTypeId("2", 2));
		
		s2.setActivityTypeIds(atid2);
		
		java.util.Set<Context> ctxt2=new java.util.LinkedHashSet<Context>();
		
		ctxt2.add(new Context(Type.Message, "M2"));
		ctxt2.add(new Context(Type.Endpoint, "E2"));
		
		s2.setContext(ctxt2);
		
		s2.getSituationProperties().put("Prop2", "Value2");
		s2.getSituationProperties().put("Prop3", "Value3");
		
		s2.setDescription("This is the second situation");
		s2.setSeverity(Severity.Critical);
		s2.setSubject("InventoryService");
		s2.setTimestamp(System.currentTimeMillis());
		s2.setType("BLA violation");
		
		em.persist(s2);
		
		em.flush();
		
		em.getTransaction().commit();
		
		// Get all situations
		Query query1=em.createQuery("SELECT sit from Situation sit");
		
		java.util.List<?> results1=query1.getResultList();
		
		if (results1.size() != 2) {
			fail("Query 1 should have returned 2 situation: "+results1.size());
		}
		
		// Get situation for subject 'OrderService'
		Query query2=em.createQuery("SELECT sit from Situation sit WHERE sit.subject = 'OrderService'");
		
		java.util.List<?> results2=query2.getResultList();
		
		if (results2.size() != 1) {
			fail("Query 2 should have returned 1 situation: "+results2.size());
		}
		
		// Get situation for property 'Prop2' value 'Value2'
		/* TODO: Fails due to "org.hibernate.QueryException: cannot dereference scalar collection element"
		 * 
		Query query3=em.createQuery("SELECT sit FROM Situation sit " +
				"JOIN sit.properties as p where p.value = 'Value2'");
		
		java.util.List<?> results3=query3.getResultList();
		
		if (results3.size() != 1) {
			fail("Query 3 should have returned 1 situation: "+results3.size());
		}
		*/
	}

	@Test
	public void testCreateSubject1() {
		if (Situation.createSubject() != null) {
			fail("Should be null subject");
		}
	}

	@Test
	public void testCreateSubject2() {
		if (!Situation.createSubject("hello").equals("hello")) {
			fail("Incorrect one subject part");
		}
	}

	@Test
	public void testCreateSubject3() {
		if (!Situation.createSubject("hello", "world").equals("hello"+Situation.SUBJECT_SEPARATOR_CHAR+"world")) {
			fail("Incorrect two subject part");
		}
	}

	@Test
	public void testCreateSubject4() {
		if (!Situation.createSubject("hello", null, "world").equals("hello"+Situation.SUBJECT_SEPARATOR_CHAR
				+Situation.SUBJECT_SEPARATOR_CHAR+"world")) {
			fail("Incorrect missing middle subject part");
		}
	}

	@Test
	public void testCreateSubject5() {
		if (!Situation.createSubject("hello", null, "world", null).equals("hello"+Situation.SUBJECT_SEPARATOR_CHAR
				+Situation.SUBJECT_SEPARATOR_CHAR+"world")) {
			fail("Incorrect missing middle with missing end subject part");
		}
	}
}
