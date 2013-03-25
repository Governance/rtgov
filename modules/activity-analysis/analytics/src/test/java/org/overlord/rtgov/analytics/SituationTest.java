/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.analytics;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.Context.Type;
import org.overlord.rtgov.analytics.Situation.Severity;

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
		
		java.util.List<ActivityTypeId> atid1=new java.util.Vector<ActivityTypeId>();
		atid1.add(new ActivityTypeId("1", 1));
		
		s1.setActivityTypeIds(atid1);
		
		java.util.List<Context> ctxt1=new java.util.Vector<Context>();
		
		ctxt1.add(new Context(Type.Message, "M1"));
		
		s1.setContext(ctxt1);

		s1.getProperties().put("Prop1", "Value1");
		
		s1.setDescription("This is the first situation");
		s1.setSeverity(Severity.Critical);
		s1.setSubject("OrderService");
		s1.setTimestamp(System.currentTimeMillis());
		s1.setType("SLA violation");
		
		em.persist(s1);

		Situation s2=new Situation();
		
		java.util.List<ActivityTypeId> atid2=new java.util.Vector<ActivityTypeId>();
		atid2.add(new ActivityTypeId("2", 2));
		
		s2.setActivityTypeIds(atid2);
		
		java.util.List<Context> ctxt2=new java.util.Vector<Context>();
		
		ctxt2.add(new Context(Type.Message, "M2"));
		ctxt2.add(new Context(Type.Endpoint, "E2"));
		
		s2.setContext(ctxt2);
		
		s2.getProperties().put("Prop2", "Value2");
		s2.getProperties().put("Prop3", "Value3");
		
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

}
