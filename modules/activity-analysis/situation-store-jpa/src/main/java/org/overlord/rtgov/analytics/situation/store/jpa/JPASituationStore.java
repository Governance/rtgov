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

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.jpa.JpaStore;
import org.overlord.rtgov.jpa.JpaStore.JpaWork;
import org.overlord.rtgov.ui.client.model.ResolutionState;
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
    private static final String OVERLORD_RTGOV_DB = "overlord-rtgov-situations"; //$NON-NLS-1$
    
    private static volatile Messages i18n = new Messages();

    private static final Logger LOG=Logger.getLogger(JPASituationStore.class.getName());
    
    private static final String JNDI_PROPERTY = "JPASituationStore.jndi.datasource";

    private JpaStore _jpaStore = new JpaStore(OVERLORD_RTGOV_DB, JNDI_PROPERTY);
        
    protected void setJpaStore(JpaStore jpaStore) {
        _jpaStore = jpaStore;
    }

    /**
     * {@inheritDoc}
     */
    public Situation getSituation(final String id) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.GetSit", id)); //$NON-NLS-1$
        }

        Situation ret = _jpaStore.withJpa(new JpaWork<Situation>() {
            public Situation perform(EntityManager em) {
                return (Situation) em.createQuery(
                        "SELECT sit FROM Situation sit " //$NON-NLS-1$
                                + "WHERE sit.id = '" + id + "'") //$NON-NLS-1$ //$NON-NLS-2$
                        .getSingleResult();
            }
        });

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.Result", ret)); //$NON-NLS-1$
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Situation> getSituations(final SituationsQuery sitQuery) {
        final String queryString = createQuery("SELECT sit from Situation sit ", sitQuery);
        List<Situation> situations = _jpaStore.withJpa(new JpaWork<List<Situation>>() {
            public List<Situation> perform(EntityManager em) {
                Query query = em.createQuery(queryString);
                return query.getResultList();
            }
        });
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.SitResult", situations)); //$NON-NLS-1$
        }
        return (situations);
    }

    private String createQuery(String selectOrDelete, SituationsQuery sitQuery) {
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
        return queryString.toString();
    }
    
    /**
     * {@inheritDoc}
     */
    public void assignSituation(final String situationId, final String userName) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.AssSit", situationId)); //$NON-NLS-1$
        }
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(EntityManager em) {
                Situation situation = em.find(Situation.class, situationId);
                situation.getProperties().put(ASSIGNED_TO_PROPERTY, userName);
                return null;
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
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(EntityManager em) {
                Situation situation = em.find(Situation.class, situationId);
                java.util.Map<String, String> properties = situation.getProperties();
                properties.remove(ASSIGNED_TO_PROPERTY);
                // remove current state if not already resolved
                String resolutionState = properties.get(RESOLUTION_STATE_PROPERTY);
                if (resolutionState != null && ResolutionState.RESOLVED != ResolutionState.valueOf(resolutionState)) {
                    properties.remove(RESOLUTION_STATE_PROPERTY);
                }
                return null;
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
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(EntityManager em) {
                Situation situation = em.find(Situation.class, situationId);
                situation.getProperties().put(RESOLUTION_STATE_PROPERTY, resolutionState.name());
                return null;
            }
        });
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
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(EntityManager em) {
                Situation situation = em.find(Situation.class, situationId);
                Map<String, String> properties = situation.getProperties();
                if (IUserContext.Holder.getUserPrincipal() != null) {
                    properties.put(RESUBMIT_BY_PROPERTY, IUserContext.Holder.getUserPrincipal().getName());
                }
                properties.put(RESUBMIT_AT_PROPERTY, Long.toString(currentTimeMillis()));
                properties.put(RESUBMIT_RESULT_PROPERTY, RESUBMIT_RESULT_SUCCESS);
                properties.remove(RESUBMIT_ERROR_MESSAGE);
                return null;
            }
        });
    }

    @Override
    public void recordResubmitFailure(final String situationId, final String errorMessage) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("JPASituationStore.ResubmitFailure", situationId)); //$NON-NLS-1$
        }
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(EntityManager em) {
                Situation situation = em.find(Situation.class, situationId);
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
                return null;
            }
        });
    }

    @Override
    public int delete(final SituationsQuery situationQuery) {
        final String queryString = createQuery("SELECT sit from Situation sit ", situationQuery);
        return _jpaStore.withJpa(new JpaWork<Integer>() {
            public Integer perform(EntityManager em) {
                Query query = em.createQuery(queryString);
                @SuppressWarnings("unchecked")
                List<Situation> situations = query.getResultList();
                for (Situation situation : situations) {
                    em.remove(situation);
                }
                return situations.size();
            }
        });
    }
}
