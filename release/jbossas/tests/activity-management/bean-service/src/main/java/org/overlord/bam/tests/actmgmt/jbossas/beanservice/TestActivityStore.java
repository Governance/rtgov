/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.tests.actmgmt.jbossas.beanservice;

import java.util.List;


import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.server.ActivityStore;
import org.overlord.rtgov.activity.server.QuerySpec;

public class TestActivityStore implements ActivityStore {

    private static List<ActivityUnit> _activities=new java.util.Vector<ActivityUnit>();
    private static int _storeCount=0;
    
    /**
     * This method resets the test information.
     */
    public static void reset() {
        synchronized(_activities) {
            _activities.clear();
            _storeCount = 0;
        }
    }
    
    /**
     * This method returns the number of times the store method was
     * invoked.
     * 
     * @return The store count
     */
    public static int getStoreCount() {
        return (_storeCount);
    }
    
    /**
     * This method returns the stored activities.
     * 
     * @return The activities
     */
    public static java.util.List<ActivityUnit> getActivities() {
        return (_activities);
    }
    
    /**
     * {@inheritDoc}
     */
    public void store(List<ActivityUnit> activities) throws Exception {
        synchronized(_activities) {
            _activities.addAll(activities);
            _storeCount++;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(String context) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<ActivityType> query(QuerySpec query) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ActivityUnit getActivityUnit(String id) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
