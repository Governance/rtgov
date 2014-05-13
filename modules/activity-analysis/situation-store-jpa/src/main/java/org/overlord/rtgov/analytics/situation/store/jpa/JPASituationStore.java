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

import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.hibernate.Query;
import org.hibernate.Session;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.analytics.situation.store.AbstractSituationStore;
import org.overlord.rtgov.analytics.situation.store.ResolutionState;
import org.overlord.rtgov.common.jpa.JpaStore;
import org.overlord.rtgov.common.jpa.JpaStore.JpaWork;

import com.google.common.base.Strings;

/**
 * This class provides the JPA based implementation of the SituationsStore
 * interface.
 * 
 */
@Singleton
public class JPASituationStore extends AbstractSituationStore implements SituationStore {

    private static final int PROPERTY_VALUE_MAX_LENGTH = 250;

    private static final Logger LOG = Logger.getLogger(JPASituationStore.class.getName());

    private static final String JNDI_PROPERTY = "JPASituationStore.jndi.datasource";

    private final JpaStore _jpaStore;

    /**
     * Constructor.
     */
    public JPASituationStore() {
        final URL configXml = this.getClass().getClassLoader().getResource("situationstore.hibernate.cfg.xml");
        _jpaStore = new JpaStore(configXml, JNDI_PROPERTY);
    }

    /**
     * Constructor.
     * 
     * @param jpaStore Explicit JpaStore to use
     */
    public JPASituationStore(JpaStore jpaStore) {
        _jpaStore = jpaStore;
    }

    /**
     * {@inheritDoc}
     */
    public Situation getSituation(final String id) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get situation: "+id); //$NON-NLS-1$
        }

        Situation ret = _jpaStore.withJpa(new JpaWork<Situation>() {
            public Situation perform(Session s) {
                return loadSituation(s, id);
            }
        });

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Situation="+ret); //$NON-NLS-1$
        }

        return (ret);
    }
    
    protected Situation loadSituation(final Session s, final String id) {
        return (Situation) s.createQuery("SELECT sit FROM Situation sit " //$NON-NLS-1$
                + "WHERE sit.id = '" + id + "'") //$NON-NLS-1$ //$NON-NLS-2$
                .uniqueResult();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<Situation> getSituations(final SituationsQuery sitQuery) {
        final String queryString = createQuery("SELECT sit from Situation sit ", sitQuery);
        List<Situation> situations = _jpaStore.withJpa(new JpaWork<List<Situation>>() {
            public List<Situation> perform(Session s) {
                Query query = s.createQuery(queryString);
                return query.list();
            }
        });
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Situations="+situations); //$NON-NLS-1$
        }
        return (situations);
    }

    private String createQuery(String selectOrDelete, SituationsQuery sitQuery) {
        // Build the query string
        StringBuffer queryString = new StringBuffer();

        if (sitQuery.getSeverity() != null) {
            queryString.append("sit.severity = :severity "); //$NON-NLS-1$
        }

        if (!isNullOrEmpty(sitQuery.getSubject())) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("upper(sit.subject) like '%" + sitQuery.getSubject().toUpperCase() + "%' "); //$NON-NLS-1$//$NON-NLS-2$
        }

        if (!isNullOrEmpty(sitQuery.getHost())) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("upper(sit.situationProperties['host']) like '%" + sitQuery.getHost().toUpperCase()
                    + "%'");
        }

        if (!isNullOrEmpty(sitQuery.getDescription())) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString
                    .append("upper(sit.description) like '%" + sitQuery.getDescription().toUpperCase() + "%' "); //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getType() != null && sitQuery.getType().trim().length() > 0) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("sit.type = '" + sitQuery.getType() + "' "); //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getFromTimestamp() > 0) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            queryString.append("sit.timestamp >= " + sitQuery.getFromTimestamp() + " "); //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getToTimestamp() > 0) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            // NOTE: As only the day is returned currently, will need to add a
            // day on, so that
            // the 'to' time represents the end of the day.
            queryString.append("sit.timestamp <= " + sitQuery.getToTimestamp() + " "); //$NON-NLS-1$//$NON-NLS-2$
        }

        if (sitQuery.getResolutionState() != null) {
            if (queryString.length() > 0) {
                queryString.append("AND "); //$NON-NLS-1$
            }
            if (ResolutionState.UNRESOLVED == ResolutionState.valueOf(sitQuery.getResolutionState())) {
                queryString.append("'resolutionState' not in indices(sit.situationProperties)");
            } else {
                queryString.append("sit.situationProperties['resolutionState']='" + sitQuery.getResolutionState()
                        + "'");
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
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                doAssignSituation(loadSituation(s, situationId), userName);
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void closeSituation(final String situationId) {
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                doCloseSituation(loadSituation(s, situationId));
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void updateResolutionState(final String situationId, final ResolutionState resolutionState) {
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                doUpdateResolutionState(loadSituation(s, situationId), resolutionState);
                return null;
            }
        });
    }

    @Override
    public void recordSuccessfulResubmit(final String situationId, final String userName) {
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                doRecordSuccessfulResubmit(loadSituation(s, situationId), userName);
                return null;
            }
        });
    }

    @Override
    public void recordResubmitFailure(final String situationId, final String errorMessage, final String userName) {
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                String message = Strings.nullToEmpty(errorMessage);
                if (message.length() > PROPERTY_VALUE_MAX_LENGTH) {
                    message = message.substring(0, PROPERTY_VALUE_MAX_LENGTH);
                }
                doRecordResubmitFailure(loadSituation(s, situationId), message, userName);
                return null;
            }
        });
    }

    /**
     * This method deletes the supplied situation.
     * 
     * @param situation The situation
     */
    protected void doDelete(final Situation situation) {
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                s.delete(situation);
                return null;
            }
        });
    }

}
