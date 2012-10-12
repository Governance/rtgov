/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.bam.activity.store.jpa;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.server.ActivityStore;
import org.overlord.bam.activity.server.QuerySpec;
//import org.overlord.bam.activity.server.ActivityStore;
//import org.overlord.bam.activity.server.QuerySpec;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * This class provides the JPA implementation of the Activity Store.
 *
 */
public class JPAActivityStore implements ActivityStore {

    private static final Logger LOG=Logger.getLogger(JPAActivityStore.class.getName());
    
    private static EntityManagerFactory EMF;
    
    private EntityManager _entityManager=null;
    
    static {
        java.util.Map<String,Object> props=new java.util.HashMap<String, Object>();
        
        EMF = Persistence.createEntityManagerFactory("overlord-bam-activity", props);
    }
    
    /**
     * This is the default constructor for the JPA activity store.
     */
    public JPAActivityStore() {
        _entityManager = EMF.createEntityManager();
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Store="+activities);
        }
        
        _entityManager.getTransaction().begin();
        
        for (ActivityUnit au : activities) {
            _entityManager.persist(au);
        }
        
        _entityManager.flush();
        
        _entityManager.getTransaction().commit();
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityUnit> query(QuerySpec query) throws Exception {  
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query="+query);
        }

        @SuppressWarnings("unchecked")
        List<ActivityUnit> ret=(List<ActivityUnit>)
                _entityManager.createQuery(createQueryExpression(query))
                .getResultList();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Query="+query+" Result="+ret);
        }

        return (ret);
    }

    /**
     * This method returns the JP-QL query expression associated with the
     * supplied spec.
     * 
     * @param query The query expression
     * @return
     */
    private String createQueryExpression(QuerySpec query) {
        return ("SELECT au FROM ActivityUnit au");
    }
}
