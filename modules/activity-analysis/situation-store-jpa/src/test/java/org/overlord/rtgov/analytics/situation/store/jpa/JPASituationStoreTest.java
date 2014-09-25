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
package org.overlord.rtgov.analytics.situation.store.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.overlord.rtgov.analytics.situation.store.ResolutionState.IN_PROGRESS;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.analytics.situation.store.ResolutionState;
import org.overlord.rtgov.common.jpa.JpaStore;
import org.overlord.rtgov.common.jpa.JpaStore.JpaWork;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class JPASituationStoreTest {
    @Rule public TestName name = new TestName();
    
    private static JpaStore _jpaStore;
    private static JPASituationStore _situationStore;
	
    @Before
    public void init() throws NamingException {
    	final URL configXml = JPASituationStoreTest.class.getClassLoader().getResource("hibernate-test.cfg.xml");
    	_jpaStore = new JpaStore(configXml);
		_situationStore = new JPASituationStore(_jpaStore);
    }

    @Test
    public void getSituationById() throws Exception {
        Situation situation = new Situation();
        situation.setId("getSituationNotFound");
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        assertEquals(situation.getId(), _situationStore.getSituation(situation.getId()).getId());
    }

    @Test
    public void getSituationsByResolutionState() throws Exception {
        Situation openSituation = new Situation();
        openSituation.setId("openSituation");
        openSituation.setTimestamp(System.currentTimeMillis());
        openSituation.getSituationProperties().put("resolutionState", ResolutionState.REOPENED.name());
        persist(openSituation);
        Situation closedSituation = new Situation();
        closedSituation.setId("closedSituation");
        closedSituation.setTimestamp(System.currentTimeMillis());
        closedSituation.getSituationProperties().put("resolutionState", ResolutionState.RESOLVED.name());
        persist(closedSituation);

        java.util.List<Situation> situations = findSituationsByFilterBean(openSituation);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(openSituation.getId(), situations.get(0).getId());
    }
    
    @Test
    public void findByEqualsHostText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        situation.getSituationProperties().put(SituationStore.HOST_PROPERTY, name.getMethodName());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        Properties properties = new Properties();
        properties.setProperty("host", name.getMethodName());
        sitQuery.setProperties(properties);
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }
    
    
    @Test
    public void findLikeHostText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        situation.getSituationProperties().put(SituationStore.HOST_PROPERTY, name.getMethodName());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        Properties properties = new Properties();
        properties.setProperty("host", name.getMethodName().substring(3));
        sitQuery.setProperties(properties);
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }
    
    @Test
    public void findEqualsSubjectText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setSubject(name.getMethodName());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }
    
    @Test
    public void findLikeSubjectText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setSubject(name.getMethodName().substring(3));
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }
    
    @Test
    public void findIgnoreCaseSubjectText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName().toUpperCase());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setSubject(name.getMethodName().substring(3).toLowerCase());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }
    
    @Test
    public void findEqualsDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }

    @Test
    public void findEndsWithDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName().substring(5));
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }

    @Test
    public void findStartsWithDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName().substring(0, 5));
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }

    @Test
    public void findIgnoreCaseDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName().toLowerCase());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName().substring(5).toUpperCase());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation.getId(), situations.get(0).getId());
    }

    @Test
    public void findDescriptionTextNegative() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName().toLowerCase());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription("other");
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(situations.isEmpty());
    }
    
    @Test
    public void getSituationsByUnresolvedResolutionState() throws Exception {
        Situation unresolvedSituation = new Situation();
        unresolvedSituation.setId("unresolvedSituation");
        unresolvedSituation.setTimestamp(System.currentTimeMillis());
        persist(unresolvedSituation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setResolutionState(ResolutionState.UNRESOLVED.name());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(unresolvedSituation.getId(), situations.get(0).getId());
    }

	@Test
	public void assignSituation() throws Exception {
		Situation situation = new Situation();
		situation.setId("assignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		persist(situation);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals(situation.getId(), reload.getId());
		assertFalse(reload.getSituationProperties().containsKey("assignedTo"));
		assertFalse(reload.getSituationProperties().containsKey("resolutionState"));
		_situationStore.assignSituation(situation.getId(), "junit");
		reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getSituationProperties().get("assignedTo"));
	}

	@Test
	public void closeSituationAndRemoveAssignment() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		persist(situation);
		_situationStore.assignSituation(situation.getId(), "junit");
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getSituationProperties().get("assignedTo"));
		_situationStore.unassignSituation(situation.getId());
		reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getSituationProperties().containsKey("assignedTo"));
	}
    
    @Test
    public void deleteSituation() throws Exception {
        Situation situation = new Situation();
        situation.setId("deleteSituation");
        situation.setDescription("deleteSituation");
        situation.setTimestamp(System.currentTimeMillis());
        situation.setSituationProperties(Collections.singletonMap("1", "1"));
        situation.setContext(Sets.newHashSet(new Context(Context.Type.Conversation, "1")));
        situation.setActivityTypeIds(Sets.newHashSet(new ActivityTypeId("1", 0)));
        persist(situation);
        SituationsQuery situationQuery = new SituationsQuery();
        situationQuery.setDescription(situation.getDescription());
        _situationStore.delete(situationQuery);
        List<Situation> situations = _situationStore.getSituations(situationQuery);
        assertTrue(situations.isEmpty());
    }
    
	@Test
	public void closeSituationResetOpenResolution() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		persist(situation);
		_situationStore.assignSituation(situation.getId(), "junit");
		_situationStore.updateResolutionState(situation.getId(),IN_PROGRESS);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getSituationProperties().get(SituationStore.ASSIGNED_TO_PROPERTY));
		_situationStore.unassignSituation(situation.getId());
		reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getSituationProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
		assertFalse(reload.getSituationProperties().containsKey(SituationStore.ASSIGNED_TO_PROPERTY));
	}
    
	@Test
	public void updateResolutionState() throws Exception {
		Situation situation = new Situation();
		situation.setId("updateResolutionState");
		situation.setTimestamp(System.currentTimeMillis());
		persist(situation);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getSituationProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
		_situationStore.updateResolutionState(situation.getId(),ResolutionState.IN_PROGRESS);
		reload = _situationStore.getSituation(situation.getId());
		assertEquals(ResolutionState.IN_PROGRESS.name(), reload.getSituationProperties().get(SituationStore.RESOLUTION_STATE_PROPERTY));
	}
    
    private java.util.List<Situation> findSituationsByFilterBean(Situation openSituation) throws Exception {
        String resolutionState = openSituation.getSituationProperties().get("resolutionState");
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setResolutionState(resolutionState);
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        return situations;
    }

	@Test
	public void recordResubmit() throws Exception {
		Situation situation = new Situation();
		situation.setId(name.getMethodName());
		situation.setTimestamp(System.currentTimeMillis());
		persist(situation);
		_situationStore.recordSuccessfulResubmit(situation.getId(), name.getMethodName());
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals(name.getMethodName(), reload.getSituationProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
		assertEquals(SituationStore.RESUBMIT_RESULT_SUCCESS, reload.getSituationProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
		assertTrue(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
		assertFalse(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_ERROR_MESSAGE));
	}

    @Test
    public void recordResubmitFailure() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        _situationStore.recordResubmitFailure(situation.getId(), name.getMethodName(), name.getMethodName());
        Situation reload = _situationStore.getSituation(situation.getId());
        assertEquals(name.getMethodName(), reload.getSituationProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
        assertEquals(name.getMethodName(), reload.getSituationProperties().get(SituationStore.RESUBMIT_ERROR_MESSAGE));
        assertTrue(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
        assertEquals(SituationStore.RESUBMIT_RESULT_ERROR,
                reload.getSituationProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
    }

    @Test
    public void recordResubmitErrorMessageMaxLength() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        persist(situation);
        _situationStore.recordResubmitFailure(situation.getId(),
                Strings.padEnd(name.getMethodName(), 10000, '*'), name.getMethodName());
        Situation reload = _situationStore.getSituation(situation.getId());
        assertEquals(name.getMethodName(), reload.getSituationProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
        String errorMessage = reload.getSituationProperties().get(SituationStore.RESUBMIT_ERROR_MESSAGE);
        assertEquals(Strings.padEnd(name.getMethodName(), 250, '*'), errorMessage);
        assertTrue(reload.getSituationProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
        assertEquals(SituationStore.RESUBMIT_RESULT_ERROR,
                reload.getSituationProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
    }
    
	private void persist(final Situation situation) {
		_jpaStore.withJpa(new JpaWork<Void>() {
			public Void perform(Session s) {
				s.persist(situation);
				return null;
			}
		});

	}
}
