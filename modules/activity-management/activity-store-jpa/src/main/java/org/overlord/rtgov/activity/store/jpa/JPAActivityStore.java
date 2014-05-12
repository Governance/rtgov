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
package org.overlord.rtgov.activity.store.jpa;

import java.net.URL;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.hibernate.Session;
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;
import org.overlord.rtgov.activity.util.ActivityUtil;
import org.overlord.rtgov.jpa.JpaStore;
import org.overlord.rtgov.jpa.JpaStore.JpaWork;

/**
 * This class provides the JPA implementation of the Activity Store.
 * 
 */
@Singleton
public class JPAActivityStore implements ActivityStore {

    private static final Logger LOG = Logger.getLogger(JPAActivityStore.class.getName());

    private static final String JNDI_PROPERTY = "JPAActivityStore.jndi.datasource";

    private final JpaStore _jpaStore;

    /**
     * Constructor.
     */
    public JPAActivityStore() {
        final URL configXml = this.getClass().getClassLoader().getResource("activitystore.hibernate.cfg.xml");
        _jpaStore = new JpaStore(configXml, JNDI_PROPERTY);
    }

    /**
     * Constructor.
     * 
     * @param jpaStore Explicit JpaStore to use
     */
    public JPAActivityStore(JpaStore jpaStore) {
        _jpaStore = jpaStore;
    }

    /**
     * {@inheritDoc}
     */
    public void store(final List<ActivityUnit> activities) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store=" + new String(ActivityUtil.serializeActivityUnitList(activities)));
        }

        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                for (int i = 0; i < activities.size(); i++) {
                    s.persist(activities.get(i));
                }
                return null;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public ActivityUnit getActivityUnit(final String id) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Get Activity Unit=" + id);
        }

        ActivityUnit ret = _jpaStore.withJpa(new JpaWork<ActivityUnit>() {
            public ActivityUnit perform(Session s) {
                return (ActivityUnit) s.createQuery(
                        "SELECT au FROM ActivityUnit au WHERE au.id = '" + id + "'").uniqueResult();
            }
        });

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ActivityUnit id=" + id + " Result="
                    + new String(ActivityUtil.serializeActivityUnit(ret)));
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public java.util.List<ActivityType> getActivityTypes(final Context context, final long from, final long to)
            throws Exception {
        List<ActivityType> ret = null;

        if (from == 0 && to == 0) {
            ret = _jpaStore.withJpa(new JpaWork<List<ActivityType>>() {
                public List<ActivityType> perform(Session s) {
                    return (List<ActivityType>) s.createQuery(
                            "SELECT at from ActivityType at " + "JOIN at.context ctx "
                                    + "WHERE ctx.value = '" + context.getValue() + "' " + "AND ctx.type = '"
                                    + context.getType().name() + "'").list();
                }
            });

        } else {
            ret = _jpaStore.withJpa(new JpaWork<List<ActivityType>>() {
                public List<ActivityType> perform(Session s) {
                    return (List<ActivityType>) s.createQuery(
                            "SELECT at from ActivityType at " + "JOIN at.context ctx "
                                    + "WHERE ctx.value = '" + context.getValue() + "' " + "AND ctx.type = '"
                                    + context.getType().name() + "' " + "AND at.timestamp >= " + from + " "
                                    + "AND at.timestamp <= " + to).list();
                }
            });
        }

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("ActivityTypes context '" + context + "' from=" + from + " to=" + to + " Result="
                    + new String(ActivityUtil.serializeActivityTypeList(ret)));
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        return (getActivityTypes(context, 0, 0));
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query=" + query);
        }

        if (query.getFormat() == null || !query.getFormat().equalsIgnoreCase("jpql")) {
            throw new IllegalArgumentException(MessageFormat.format(java.util.PropertyResourceBundle
                    .getBundle("activity-store-jpa.Messages").getString("ACTIVITY-STORE-JPA-1"), (query
                    .getFormat() == null ? "" : query.getFormat())));
        }

        if (query.getExpression() == null || !query.getExpression().toLowerCase().startsWith("select ")) {
            throw new IllegalArgumentException(java.util.PropertyResourceBundle.getBundle(
                    "activity-store-jpa.Messages").getString("ACTIVITY-STORE-JPA-2"));
        }

        return (query(query.getExpression()));
    }

    /**
     * This method performs the query associated with the supplied query
     * expression, returning the results as a list of activity types.
     * 
     * @param query
     *            The query expression
     * @return The list of activity types
     * @throws Exception
     *             Failed to perform query
     */
    @SuppressWarnings("unchecked")
    public List<ActivityType> query(final String query) throws Exception {

        List<ActivityType> ret = _jpaStore.withJpa(new JpaWork<List<ActivityType>>() {
            public List<ActivityType> perform(Session s) {
                return (List<ActivityType>) s.createQuery(query).list();
            }
        });

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query=" + query + " Result="
                    + new String(ActivityUtil.serializeActivityTypeList(ret)));
        }

        return (ret);
    }

    /**
     * This method removes the supplied activity unit.
     * 
     * @param au
     *            The activity unit
     * @throws Exception
     *             Failed to remove activity unit
     */
    public void remove(final ActivityUnit au) throws Exception {
        _jpaStore.withJpa(new JpaWork<Void>() {
            public Void perform(Session s) {
                // Cascading delete is not working from activity unit to
                // activity types,
                // so resorting to native SQL for now to delete an activity unit
                // and its
                // associated components
                s.createSQLQuery("DELETE FROM RTGOV_ACTIVITY_CONTEXT WHERE unitId = '" + au.getId() + "'")
                        .executeUpdate();

                s.createSQLQuery("DELETE FROM RTGOV_ACTIVITY_PROPERTIES WHERE unitId = '" + au.getId() + "'")
                        .executeUpdate();

                s.createSQLQuery("DELETE FROM RTGOV_ACTIVITIES WHERE unitId = '" + au.getId() + "'")
                        .executeUpdate();

                s.createSQLQuery("DELETE FROM RTGOV_ACTIVITY_UNITS WHERE id = '" + au.getId() + "'")
                        .executeUpdate();
                return null;
            }
        });
    }
}
