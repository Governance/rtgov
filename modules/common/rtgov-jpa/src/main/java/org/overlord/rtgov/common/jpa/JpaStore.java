/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.rtgov.common.jpa;

import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.overlord.rtgov.common.util.RTGovProperties;

/**
 * Provides common JPA functionality to all data stores.
 * 
 * Note: This is actually using native Hibernate ORM, rather than JPA, but I'm
 * keeping the "JPA" name for consistency's sake. Since we need to dynamically
 * provide the DataSource, JtaPlatform, etc., we can't include incomplete
 * persistence.xml files with those values missing. EE containers grab the files
 * and automatically attempt to create JPA EntityManagerFactories with them.
 * There's not an easy, cross-platform way to prevent that. So instead, fall
 * back on native ORM.
 * 
 * @author Brett Meyer
 */
public class JpaStore {
    private static final Logger LOG = Logger.getLogger(JpaStore.class.getName());

    private static final String JTA_PLATFORM_PROPERTY = "JpaStore.jtaPlatform";

    private URL _configXml;

    private String _jndiProperty;

    private SessionFactory _sessionFactory;

    @Deprecated
    private String _persistenceUnit;

    @Deprecated
    private EntityManagerFactory _entityManagerFactory;
    
    private boolean _isJta = true;

    /**
     * The constructor.
     * 
     * @param configXml
     *            The hibernate.cfg.xml URL
     * @param jndiProperty
     *            The jndi name
     */
    public JpaStore(URL configXml, String jndiProperty) {
        _configXml = configXml;
        _jndiProperty = jndiProperty;
    }

    /**
     * The constructor.
     * 
     * @param configXml
     *            The hibernate.cfg.xml URL
     */
    public JpaStore(URL configXml) {
        this(configXml, null);
    }

    /**
     * The constructor.
     * 
     * @param persistenceUnit
     *            The persistence unit name
     * @param jndiProperty
     *            The jndi name
     */
    @Deprecated
    public JpaStore(String persistenceUnit, String jndiProperty) {
        _persistenceUnit = persistenceUnit;
        _jndiProperty = jndiProperty;
    }

    /**
     * The constructor.
     * 
     * @param persistenceUnit
     *            The persistence unit name
     */
    @Deprecated
    public JpaStore(String persistenceUnit) {
        this(persistenceUnit, null);
    }

    /**
     * This method returns a Session.
     * 
     * @return The Session
     */
    private Session getSession() {
        if (_persistenceUnit == null) {
            if (_sessionFactory == null) {
                final Configuration cfg = new Configuration().configure(_configXml);
                final Properties properties = RTGovProperties.getProperties();
                if (_jndiProperty != null) {
                    String prop=RTGovProperties.getProperty(_jndiProperty);
                    if (prop != null) {
                        properties.setProperty(AvailableSettings.DATASOURCE, prop);
                    }
                }
                final String jtaPlatform = RTGovProperties.getProperty(JTA_PLATFORM_PROPERTY);
                if (jtaPlatform == null) {
                    LOG.warning("JpaStore.jtaPlatform not set in overlord-rtgov.properties!");
                    _isJta = false;
                } else {
                    properties.setProperty(AvailableSettings.JTA_PLATFORM,
                            RTGovProperties.getProperty(JTA_PLATFORM_PROPERTY));
                }
                cfg.getProperties().putAll(properties);
                _sessionFactory = cfg.buildSessionFactory();
            }

            return _sessionFactory.openSession();
        } else {
            // NOTE: For backward compatibility in EAP, I'm allowing the
            // continued use of a persistence unit.
            // Simply use it to create an EntityManager, then unwrap to the
            // native Hibernate Session.
            if (_entityManagerFactory == null) {
                final Properties properties = RTGovProperties.getProperties();
                if (_jndiProperty != null) {
                    properties.setProperty(AvailableSettings.DATASOURCE,
                            RTGovProperties.getProperty(_jndiProperty));
                }
                final String jtaPlatform = RTGovProperties.getProperty(JTA_PLATFORM_PROPERTY);
                if (jtaPlatform == null) {
                    LOG.warning("JpaStore.jtaPlatform not set in overlord-rtgov.properties!");
                    _isJta = false;
                } else {
                    properties.setProperty(AvailableSettings.JTA_PLATFORM,
                            RTGovProperties.getProperty(JTA_PLATFORM_PROPERTY));
                }
                _entityManagerFactory = Persistence.createEntityManagerFactory(_persistenceUnit, properties);
            }

            return _entityManagerFactory.createEntityManager().unwrap(Session.class);
        }
    }

    /**
     * This method performs the supplied work in a transaction.
     * 
     * @param work
     *            The unit of work
     * @param <T>
     *            The type if the return value
     * @return The return value
     */
    public <T> T withJpa(JpaWork<T> work) {
        final Session s = getSession();
        try {
            if (!_isJta) {
                s.getTransaction().begin();
            }
            final T result = work.perform(s);
            s.flush();
            if (!_isJta) {
                s.getTransaction().commit();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            s.close();
        }
    }

    /**
     * Perform work using a provided Hibernate Session.
     * 
     * @param <T>
     *            The return type
     */
    public static interface JpaWork<T> {
        /**
         * The work to be performed using the Session.
         * 
         * @param s
         *            The Session
         * @return The result of the work
         */
        public T perform(Session s);
    }
}
