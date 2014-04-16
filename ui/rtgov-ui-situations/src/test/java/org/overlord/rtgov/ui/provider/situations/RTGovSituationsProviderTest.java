/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.provider.situations;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.soa.RequestReceived;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.store.mem.MemActivityStore;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.Situation.Severity;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationEventBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.provider.SituationEventListener;

public class RTGovSituationsProviderTest {

	private static final String TEST_ID = "TestId";
	private static final String TEST_AU = "TestAU";
	private static final String TEST_CONTENT = "TestContent";
	private static final String TEST_TYPE = "TestType";
	private static final String TEST_SUBJECT = "TestSubject";

	@Test
	public void testNotifySituation() {
		RTGovSituationsProvider provider=new RTGovSituationsProvider();
		
		// ActiveList doesn't support 'add', so need to override with method that
		// inserts into collection.
		ActiveList situations=new ActiveList("test") {
			public boolean add(Object obj) {
				insert(null, obj);
				return (true);
			}
		};
		
		provider.setSituations(situations);
		
		provider.init();
		
		final java.util.List<SituationEventBean> events=new java.util.ArrayList<SituationEventBean>();
		
		provider.addSituationEventListener(new SituationEventListener() {

			@Override
			public void onSituationEvent(SituationEventBean event) {
				events.add(event);
			}
			
		});
		
		Situation sit=new Situation();
		sit.setSubject(TEST_SUBJECT);
		
		situations.add(sit);
		
		if (events.size() != 1) {
			fail("Situation was not generated as an event");
		}
		
		SituationEventBean seb=events.get(0);
		
		if (!seb.getSubject().equals(TEST_SUBJECT)) {
			fail("Event subject incorrect: "+seb.getSubject());
		}
	}

	@Test
	public void testCreateQuery() {
		SituationsFilterBean sfb=new SituationsFilterBean();
		
		long fromTimestamp=12345678;
		long toTimestamp=87654321;
		
		sfb.setSeverity(Severity.Critical.toString().toLowerCase());
		sfb.setTimestampFrom(new java.util.Date(fromTimestamp));
		sfb.setTimestampTo(new java.util.Date(toTimestamp));
		sfb.setType(TEST_TYPE);
		
		SituationsQuery query=RTGovSituationsProvider.createQuery(sfb);
		
		if (query == null) {
			fail("No query returned");
		}
		
		if (query.getSeverity() != Severity.Critical) {
			fail("Severity was incorrect: "+query.getSeverity());
		}
		
		if (query.getFromTimestamp() != fromTimestamp) {
			fail("FromTimestamp was incorrect: "+query.getFromTimestamp());
		}
		
		if (query.getToTimestamp() != (toTimestamp)) {
			fail("ToTimestamp was incorrect: "+query.getToTimestamp());
		}
		
		if (!query.getType().equals(TEST_TYPE)) {
			fail("Type was incorrect: "+query.getType());
		}
	}
	
	@Test
	public void testSearch() {
		RTGovSituationsProvider provider=new RTGovSituationsProvider();
		
		SituationStore sits=new SituationStore() {
			public Situation getSituation(String id) {
				throw new AssertionError("Fail");
			}
			public java.util.List<Situation> getSituations(SituationsQuery query) {
				if (!query.getType().equals(TEST_TYPE)) {
					throw new AssertionError("Unexpected query type: "+query.getType());
				}
				Situation s1=new Situation();
				s1.setSubject(TEST_SUBJECT);
				java.util.List<Situation> ret=new java.util.ArrayList<Situation>();
				ret.add(s1);
				return (ret);
			}
			public void assignSituation(String situationId, String userName) throws Exception {
				throw new Exception("Fail");
			}
			public void closeSituation(String situationId) throws Exception {
				throw new Exception("Fail");
			}
			public void updateResolutionState(String situationId, ResolutionState resolutionState) throws Exception {
				throw new Exception("Fail");
			}

			public void recordResubmitFailure(String situationId, String message) {
				Assert.fail();
			};

			public void recordSuccessfulResubmit(String situationId) {
				Assert.fail();
			};
		};
		
		provider.setSituationStore(sits);
		provider.setSituations(new ActiveList("Test"));
		provider.init();
		
		SituationsFilterBean sfb=new SituationsFilterBean();
		sfb.setType(TEST_TYPE);

		try {
			java.util.List<SituationSummaryBean> results=provider.search(sfb);
			
			if (results == null) {
				fail("No results");
			}
			
			if (results.size() != 1) {
				fail("Expecting 1 result: "+results.size());
			}
			
			SituationSummaryBean ssb=results.get(0);
			
			if (!ssb.getSubject().equals(TEST_SUBJECT)) { 
				fail("Subject was incorrect: "+ssb.getSubject());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Search failed: "+e.getMessage());
		}
	}
	
	@Test
	public void testGetMessage() {
		RTGovSituationsProvider provider=new RTGovSituationsProvider();
		
		ActivityStore acts=new MemActivityStore();
		provider.setActivityStore(acts);
		
		ActivityUnit au=new ActivityUnit();
		au.setId(TEST_AU);
		
		RequestReceived rr=new RequestReceived();
		au.getActivityTypes().add(rr);
		
		rr.setContent(TEST_CONTENT);
		
		java.util.List<ActivityUnit> aulist=new java.util.ArrayList<ActivityUnit>();
		aulist.add(au);
		
		try {
			acts.store(aulist);
		} catch (Exception e) {
			fail("Failed to store: "+e.getMessage());
		}
		
		provider.setSituations(new ActiveList("Test"));
		provider.init();
		
		Situation sit=new Situation();
		
		sit.getActivityTypeIds().add(new ActivityTypeId(TEST_AU, 0));
		
		try {
			MessageBean mb=provider.getMessage(sit);
			
			if (mb == null) {
				fail("Message not returned");
			}
			
			if (mb.getContent() == null) {
				fail("Message has no content");
			}
			
			if (!mb.getContent().equals(TEST_CONTENT)) {
				fail("Content is incorrect: "+mb.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Get message failed: "+e.getMessage());
		}
	}
	
	@Test
	public void testGetSituation() {
		RTGovSituationsProvider provider=new RTGovSituationsProvider();
		
		SituationStore sits=new SituationStore() {
			public Situation getSituation(String id) {
				if (!id.equals(TEST_ID)) {
				    throw new AssertionError("Incorrect id");
				}
				Situation ret=new Situation();
				ret.setSubject("Service|Operation");
				ret.setId(id);
				return (ret);
			}
			public java.util.List<Situation> getSituations(SituationsQuery query) {
				throw new AssertionError("Fail");
			}
			public void assignSituation(String situationId, String userName) throws Exception {
				throw new Exception("Fail");
			}
			public void closeSituation(String situationId) throws Exception {
				throw new Exception("Fail");
			}
			public void updateResolutionState(String situationId, ResolutionState resolutionState) throws Exception {
				throw new Exception("Fail");
			}
			public void recordResubmitFailure(String situationId, String message) {
				Assert.fail();
			};

			public void recordSuccessfulResubmit(String situationId) {
				Assert.fail();
			};

		};
		
		provider.setSituationStore(sits);
		provider.setSituations(new ActiveList("Test"));
		provider.init();
		
		try {
			SituationBean sb=provider.getSituation(TEST_ID);
			
			if (sb == null) {
				fail("Result is null");
			}
			
			if (!sb.getSituationId().equals(TEST_ID)) {
				fail("Situation id is incorrect: "+sb.getSituationId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Get situation failed: "+e.getMessage());
		}
		
		
	}
}
