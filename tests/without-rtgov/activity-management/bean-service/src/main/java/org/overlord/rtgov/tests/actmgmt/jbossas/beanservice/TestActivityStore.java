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
package org.overlord.rtgov.tests.actmgmt.jbossas.beanservice;

import java.util.List;


import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.Context;
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
    public List<ActivityType> getActivityTypes(Context context) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<ActivityType> getActivityTypes(Context context, long from,
            long to) throws Exception {
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
