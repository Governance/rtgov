package org.overlord.rtgov.analytics.situation.store.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.overlord.rtgov.ui.client.model.ResolutionState.IN_PROGRESS;

import java.security.Principal;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.server.interceptors.IUserContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.google.common.base.Strings;

@ContextConfiguration
public class JPASituationStoreTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Rule public TestName name = new TestName();

	private JPASituationStore _situationStore;
    
	@PersistenceContext
    private EntityManager _entityManager;
	
    @Before
    public void init() throws NamingException {
        this._situationStore = new JPASituationStore();
        this._situationStore.setEntityManager(this._entityManager);
        
        this._situationStore.setUserTransaction(new UserTransaction() {
			
			@Override
			public void setTransactionTimeout(int arg0) throws SystemException {
			}
			
			@Override
			public void setRollbackOnly() throws IllegalStateException, SystemException {
			}
			
			@Override
			public void rollback() throws IllegalStateException, SecurityException, SystemException {
			}
			
			@Override
			public int getStatus() throws SystemException {
				return Status.STATUS_ACTIVE;
			}
			
			@Override
			public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException,
					RollbackException, SecurityException, SystemException {
				
			}
			
			@Override
			public void begin() throws NotSupportedException, SystemException {
			}
		});
        IUserContext.Holder.setSecurityContext(new IUserContext() {

            @Override
            public Principal getUserPrincipal() {
                return new Principal() {

                    @Override
                    public String getName() {
                        return name.getMethodName();
                    }
                };
            }
        });
    }

    @Test
    public void getSituationNotFound() throws Exception {
        try {
            _situationStore.getSituation("1");
            Assert.fail("NoResultException expected");
        } catch (NoResultException noResultException) {
        }
    }

    @Test
    public void getSituationById() throws Exception {
        Situation situation = new Situation();
        situation.setId("getSituationNotFound");
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        assertEquals(situation, _situationStore.getSituation(situation.getId()));
    }

    @Test
    public void getSituationsByResolutionState() throws Exception {
        Situation openSituation = new Situation();
        openSituation.setId("openSituation");
        openSituation.setTimestamp(System.currentTimeMillis());
        openSituation.getProperties().put("resolutionState", ResolutionState.REOPENED.name());
        _entityManager.persist(openSituation);
        Situation closedSituation = new Situation();
        closedSituation.setId("closedSituation");
        closedSituation.setTimestamp(System.currentTimeMillis());
        closedSituation.getProperties().put("resolutionState", ResolutionState.RESOLVED.name());
        _entityManager.persist(closedSituation);

        java.util.List<Situation> situations = findSituationsByFilterBean(openSituation);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(openSituation, situations.get(0));
    }
    
    @Test
    public void findByEqualsHostText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        situation.getProperties().put(SituationStore.HOST_PROPERTY, name.getMethodName());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setHost(name.getMethodName());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }
    
    
    @Test
    public void findLikeHostText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        situation.getProperties().put(SituationStore.HOST_PROPERTY, name.getMethodName());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setHost(name.getMethodName().substring(3));
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }
    
    @Test
    public void findEqualsSubjectText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setSubject(name.getMethodName());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }
    
    @Test
    public void findLikeSubjectText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setSubject(name.getMethodName().substring(3));
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }
    
    @Test
    public void findIgnoreCaseSubjectText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setSubject(name.getMethodName().toUpperCase());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setSubject(name.getMethodName().substring(3).toLowerCase());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }
    
    @Test
    public void findEqualsDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }

    @Test
    public void findEndsWithDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName().substring(5));
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }

    @Test
    public void findStartsWithDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName().substring(0, 5));
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }

    @Test
    public void findIgnoreCaseDescriptionText() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName().toLowerCase());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setDescription(name.getMethodName().substring(5).toUpperCase());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(situation, situations.get(0));
    }

    @Test
    public void findDescriptionTextNegative() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setDescription(name.getMethodName().toLowerCase());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
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
        _entityManager.persist(unresolvedSituation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setResolutionState(ResolutionState.UNRESOLVED.name());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(unresolvedSituation, situations.get(0));
    }

	@Test
	public void assignSituation() throws Exception {
		Situation situation = new Situation();
		situation.setId("assignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals(situation, reload);
		assertFalse(reload.getProperties().containsKey("assignedTo"));
		assertFalse(reload.getProperties().containsKey("resolutionState"));
		_situationStore.assignSituation(situation.getId(), "junit");
		reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get("assignedTo"));
	}

	@Test
	public void closeSituationAndRemoveAssignment() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		_situationStore.assignSituation(situation.getId(), "junit");
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get("assignedTo"));
		_situationStore.closeSituation(situation.getId());
		reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey("assignedTo"));
	}
    
	@Test
	public void closeSituationResetOpenResolution() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		_situationStore.assignSituation(situation.getId(), "junit");
		_situationStore.updateResolutionState(situation.getId(),IN_PROGRESS);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get(SituationStore.ASSIGNED_TO_PROPERTY));
		_situationStore.closeSituation(situation.getId());
		reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
		assertFalse(reload.getProperties().containsKey(SituationStore.ASSIGNED_TO_PROPERTY));
	}
    
	@Test
	public void updateResolutionState() throws Exception {
		Situation situation = new Situation();
		situation.setId("updateResolutionState");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
		_situationStore.updateResolutionState(situation.getId(),ResolutionState.IN_PROGRESS);
		reload = _situationStore.getSituation(situation.getId());
		assertEquals(ResolutionState.IN_PROGRESS.name(), reload.getProperties().get(SituationStore.RESOLUTION_STATE_PROPERTY));
	}
    
    private java.util.List<Situation> findSituationsByFilterBean(Situation openSituation) throws Exception {
        String resolutionState = openSituation.getProperties().get("resolutionState");
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
		_entityManager.persist(situation);
		_situationStore.recordSuccessfulResubmit(situation.getId());
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals(name.getMethodName(), reload.getProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
		assertEquals(SituationStore.RESUBMIT_RESULT_SUCCESS, reload.getProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
		assertTrue(reload.getProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
		assertFalse(reload.getProperties().containsKey(SituationStore.RESUBMIT_ERROR_MESSAGE));
	}

    @Test
    public void recordResubmitFailure() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        _situationStore.recordResubmitFailure(situation.getId(), name.getMethodName());
        Situation reload = _situationStore.getSituation(situation.getId());
        assertEquals(name.getMethodName(), reload.getProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
        assertEquals(name.getMethodName(), reload.getProperties().get(SituationStore.RESUBMIT_ERROR_MESSAGE));
        assertTrue(reload.getProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
        assertEquals(SituationStore.RESUBMIT_RESULT_ERROR,
                reload.getProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
    }

    @Test
    public void recordResubmitErrorMessageMaxLength() throws Exception {
        Situation situation = new Situation();
        situation.setId(name.getMethodName());
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        _situationStore.recordResubmitFailure(situation.getId(), Strings.padEnd(name.getMethodName(), 10000, '*'));
        Situation reload = _situationStore.getSituation(situation.getId());
        assertEquals(name.getMethodName(), reload.getProperties().get(SituationStore.RESUBMIT_BY_PROPERTY));
        String errorMessage = reload.getProperties().get(SituationStore.RESUBMIT_ERROR_MESSAGE);
        assertEquals(Strings.padEnd(name.getMethodName(), 250, '*'), errorMessage);
        assertTrue(reload.getProperties().containsKey(SituationStore.RESUBMIT_AT_PROPERTY));
        assertEquals(SituationStore.RESUBMIT_RESULT_ERROR,
                reload.getProperties().get(SituationStore.RESUBMIT_RESULT_PROPERTY));
    }
}
