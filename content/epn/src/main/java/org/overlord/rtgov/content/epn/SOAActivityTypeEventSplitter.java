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
package org.overlord.rtgov.content.epn;

import java.io.Serializable;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.model.soa.RPCActivityType;

/**
 * This class provides an implementation of the EventProcessor
 * interface, used to identify and split out SOA related events
 * for use by subsequent event processor nodes.
 *
 */
public class SOAActivityTypeEventSplitter extends org.overlord.rtgov.ep.EventProcessor {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable process(String source, Serializable event,
            int retriesLeft) throws Exception {
        Serializable ret=null;
        
        if (event instanceof ActivityUnit) {
            ret = new java.util.LinkedList<Serializable>();
            
            for (ActivityType at : ((ActivityUnit)event).getActivityTypes()) {
                if (at instanceof RPCActivityType) {
                    ((java.util.LinkedList<Serializable>)ret).add((RPCActivityType)at);
                }
            }
            
            if (((java.util.LinkedList<Serializable>)ret).size() == 0) {
                ret = null;
            } else if (((java.util.LinkedList<Serializable>)ret).size() == 1) {
                ret = ((java.util.LinkedList<Serializable>)ret).getFirst();
            }
        }
        
        return (ret);
    }

}
