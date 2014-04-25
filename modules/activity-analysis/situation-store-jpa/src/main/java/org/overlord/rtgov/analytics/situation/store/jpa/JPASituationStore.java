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
package org.overlord.rtgov.analytics.situation.store.jpa;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.System.currentTimeMillis;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.annotation.Resource;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.ui.provider.situations.Messages;
import org.overlord.rtgov.ui.server.interceptors.IUserContext;

import com.google.common.base.Strings;

/**
 * This class provides the JPA based implementation of the SituationsStore interface.
 *
 */
@Singleton
public class JPASituationStore implements SituationStore {

    private static final int PROPERTY_VALUE_MAX_LENGTH = 250;
    private static final String USER_TRANSACTION = "java:comp/UserTransaction"; //$NON-NLS-1$
    private static final String OVERLORD_RTGOV_DB = "overlord-rtgov-situations"; //$NON-NLS-1$
    
    private static volatile Messages i18n = new Messages();

    @Resource
    private UserTransaction _transaction;

    @PersistenceContext(unitName=OVERLORD_RTGOV_DB)
    private EntityManager _entityManager;

    private static final Logger LOG=Logger.getLogger(JPASituationStore.class.getName());

    /**
     * The situation repository constructor.
     */
    public JPASituationStore() {
    }
    
    /**
     * This method sets a transaction.
     * 
     * @param txn The transaction
     */
    protected void setUserTransaction(UserTransaction txn) {
        _transaction = txn;
    }

    /**
     * This method returns a transaction.
     *
     * @return The transaction
     */
    protected UserTransaction getTransaction() throws Exception {
        return (_transaction != null ? _transaction
                : (UserTransaction)new InitialContext().lookup(USER_TRANSACTION));
    }

    /**
     * This method sets an entity manager. This can be used
     * for testing purposes.
     * 
     * @param em The entity manager
     */
    protected void setEntityManager(EntityManager em) {
        _entityManager = em;
    }

    /**
     * This method returns an entity manager.
     *
     * @return The entity manager
     */
    protected EntityManager getEntityManager() {
        return (_entityManager);
    }

    /**
     * {@inheritDoc}
     */
    public Situation getSituation(String id) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.GetSit", id)); //$NON-NLS-1$
        }

        EntityManager em=getEntityManager();

        Situation ret=(Situation)em.createQuery("SELECT sit FROM Situation sit " //$NON-NLS-1$
                                +"WHERE sit.id = '"+id+"'") //$NON-NLS-1$ //$NON-NLS-2$
                                .getSingleResult();

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.Result", ret)); //$NON-NLS-1$
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Situation> getSituations(SituationsQuery sitQuery) {
        Query query = createQuery("SELECT sit from Situation sit ", sitQuery, getEntityManager());
        List<Situation> situations = query.getResultList();
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.SitResult", situations)); //$NON-NLS-1$
        }
        return (situations);
    }

    private Query createQuery(String selectOrDelete, SituationsQuery sitQuery, EntityManager entityManager) {
        // Build the query string
        StringBuffer queryString=new StringBuffer();

        if (sitQuery.getSeverity() != null) {
            queryString.append("sit.severity = :severity "); //$NON-NLS-1$
        }
        
        if (!isNullOrEmpty(sitQuery.getSubject())) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("upper(sit.subject) like '%"+sitQuery.getSubject().toUpperCase()+"%' ");  //$NON-NLS-1$//$NON-NLS-2$
        }
        
        if (!isNullOrEmpty(sitQuery.getHost())) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("upper(sit.properties['host']) like '%" + sitQuery.getHost().toUpperCase() + "%'");
        }
        
        if (!isNullOrEmpty(sitQuery.getDescription())) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("upper(sit.description) like '%"+sitQuery.getDescription().toUpperCase()+"%' ");  //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getType() != null && sitQuery.getType().trim().length() > 0) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("sit.type = '"+sitQuery.getType()+"' ");  //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getFromTimestamp() > 0) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("sit.timestamp >= "+sitQuery.getFromTimestamp()+" ");  //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getToTimestamp() > 0) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            // NOTE: As only the day is returned currently, will need to add a day on, so that
            // the 'to' time represents the end of the day.
            queryString.append("sit.timestamp <= "+sitQuery.getToTimestamp()+" ");  //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getResolutionState() != null) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            if (ResolutionState.UNRESOLVED == ResolutionState.valueOf(sitQuery.getResolutionState())) {
                queryString.append("'resolutionState' not in indices(sit.properties)");
            } else {
                queryString.append("sit.properties['resolutionState']='" + sitQuery.getResolutionState() + "'");
            }
        }

        if (queryString.length() > 0) {
            queryString.insert(0, "WHERE "); //$NON-NLS-1$
        }
        queryString.insert(0, selectOrDelete); //$NON-NLS-1$
        Query query=entityManager.createQuery(queryString.toString());
        if (sitQuery.getSeverity() != null) {
            query.setParameter("severity", sitQuery.getSeverity()); //$NON-NLS-1$
        }
        return query;
    }
    
    /**
     * {@inheritDoc}
     */
    public void assignSituation(final String situationId, final String userName) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.AssSit", situationId)); //$NON-NLS-1$
        }
        doInTransaction(new EntityManagerCallback.Void() {
            @Override
            public void doExecute(EntityManager entityManager) {
                Situation situation = entityManager.find(Situation.class, situationId);
                situation.getProperties().put(ASSIGNED_TO_PROPERTY, userName);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void closeSituation(final String situationId) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.DeassSit", situationId)); //$NON-NLS-1$
        }
        doInTransaction(new EntityManagerCallback.Void() {
            @Override
            public void doExecute(EntityManager entityManager) {
                Situation situation = entityManager.find(Situation.class, situationId);
                java.util.Map<String, String> properties = situation.getProperties();
                properties.remove(ASSIGNED_TO_PROPERTY);
                // remove current state if not already resolved
                String resolutionState = properties.get(RESOLUTION_STATE_PROPERTY);
                if (resolutionState != null && ResolutionState.RESOLVED != ResolutionState.valueOf(resolutionState)) {
                    properties.remove(RESOLUTION_STATE_PROPERTY);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void updateResolutionState(final String situationId, final ResolutionState resolutionState) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.UpdRState", situationId)); //$NON-NLS-1$
        }
        doInTransaction(new EntityManagerCallback.Void() {
            @Override
            public void doExecute(EntityManager entityManager) {
                Situation situation = entityManager.find(Situation.class, situationId);
                situation.getProperties().put(RESOLUTION_STATE_PROPERTY, resolutionState.name());
            }
        });
    }
    
    private <T> T doInTransaction(EntityManagerCallback<T> callback) {
        EntityManager entityManager = getEntityManager();
        UserTransaction userTransaction = null;
        T result = null;
        try {
            userTransaction = getTransaction();
            boolean handleTransaction = userTransaction.getStatus() != Status.STATUS_ACTIVE;
            if (handleTransaction) {
                userTransaction.begin();
                //entityManager.joinTransaction();
            }
            result = callback.execute(entityManager);
            if (handleTransaction) {
                userTransaction.commit();
            }
        } catch (Exception exception) {
            try {
                if (userTransaction != null && userTransaction.getStatus() == Status.STATUS_ACTIVE) {
                    userTransaction.rollback();
                }
            } catch (Exception rollbackException) {
                rollbackException.printStackTrace();
            }
        }
        return result;
    }

    /**
     * This class provides the situation results.
     *
     */
    public static class SituationsResult {
        
        private java.util.List<Situation> _situations=null;
        private int _totalCount=0;
        
        /**
         * This is the constructor for the situation results.
         * 
         * @param situations The situations relevant for the requested page
         * @param total The total number
         */
        public SituationsResult(java.util.List<Situation> situations, int total) {
            _situations = situations;
            _totalCount = total;
        }
        
        /**
         * This method returns the list of situations for the
         * selected page.
         * 
         * @return The situations
         */
        public java.util.List<Situation> getSituations() {
            return (_situations);
        }
        
        /**
         * This method returns the total number of situations available.
         * 
         * @return The total number of situations
         */
        public int getTotalCount() {
            return (_totalCount);
        }
    }

    @Override
    public void recordSuccessfulResubmit(final String situationId) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.Resubmit", situationId)); //$NON-NLS-1$
        }
        doInTransaction(new EntityManagerCallback.Void() {
            @Override
            public void doExecute(EntityManager entityManager) {
                Situation situation = entityManager.find(Situation.class, situationId);
                Map<String, String> properties = situation.getProperties();
                if (IUserContext.Holder.getUserPrincipal() != null) {
                    properties.put(RESUBMIT_BY_PROPERTY, IUserContext.Holder.getUserPrincipal().getName());
                }
                properties.put(RESUBMIT_AT_PROPERTY, Long.toString(currentTimeMillis()));
                properties.put(RESUBMIT_RESULT_PROPERTY, RESUBMIT_RESULT_SUCCESS);
                properties.remove(RESUBMIT_ERROR_MESSAGE);
            }
        });
    }

    @Override
    public void recordResubmitFailure(final String situationId, final String errorMessage) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.ResubmitFailure", situationId)); //$NON-NLS-1$
        }
        doInTransaction(new EntityManagerCallback.Void() {
            @Override
            public void doExecute(EntityManager entityManager) {
                Situation situation = entityManager.find(Situation.class, situationId);
                Map<String, String> properties = situation.getProperties();
                if (IUserContext.Holder.getUserPrincipal() != null) {
                    properties.put(RESUBMIT_BY_PROPERTY, IUserContext.Holder.getUserPrincipal().getName());
                }
                properties.put(RESUBMIT_AT_PROPERTY, Long.toString(currentTimeMillis()));
                properties.put(RESUBMIT_RESULT_PROPERTY, RESUBMIT_RESULT_ERROR);
                String message = Strings.nullToEmpty(errorMessage);
                if (message.length() > PROPERTY_VALUE_MAX_LENGTH) {
                    message = message.substring(0, PROPERTY_VALUE_MAX_LENGTH);
                }
                properties.put(RESUBMIT_ERROR_MESSAGE, message);
            }
        });

    }

    @Override
    public int delete(final SituationsQuery situationQuery) {
        return doInTransaction(new EntityManagerCallback<Integer>() {

            @Override
            public Integer execute(EntityManager entityManager) {
                Query query = createQuery("SELECT sit from Situation sit ", situationQuery, entityManager);
                @SuppressWarnings("unchecked")
                List<Situation> situations = query.getResultList();
                for (Situation situation : situations) {
                    entityManager.remove(situation);
                }
                return situations.size();
            }

        });
    }
}
