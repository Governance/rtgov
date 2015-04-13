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
package org.overlord.rtgov.analytics.situation.store.mem;

import java.util.List;

import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.AbstractSituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;

/**
 * This class provides the in-memory based implementation of the SituationsStore interface.
 *
 */
public class MemSituationStore extends AbstractSituationStore implements SituationStore {

    private java.util.List<Situation> _situations=new java.util.ArrayList<Situation>();

    /**
     * The situation repository constructor.
     */
    public MemSituationStore() {
    }
    
    /**
     * {@inheritDoc}
     */
    protected void doStore(Situation situation) {
        _situations.add(situation);
    }
    
    /**
     * {@inheritDoc}
     */
    protected Situation doGetSituation(String id) {
        for (Situation sit : _situations) {
            if (sit.getId().equals(id)) {
                return (sit);
            }
        }
        
        return (null);
    }

    /**
     * {@inheritDoc}
     */
    public List<Situation> getSituations(SituationsQuery sitQuery) {
        List<Situation> situations = new java.util.ArrayList<Situation>();
        
        for (Situation situation : _situations) {
            if (sitQuery.matches(situation)) {
                situations.add(situation);
            }
        }
        
        return (situations);
    }

    /**
     * {@inheritDoc}
     */
    protected void doDelete(Situation situation) {
        _situations.remove(situation);
    }

}
