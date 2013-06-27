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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.ActivityUnit;
import org.overlord.rtgov.activity.util.ActivityUtil;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.util.ServiceDefinitionUtil;

/**
 * This class provides an implementation of the EventProcessor
 * interface, used to derive service definitions from the
 * stream of activity units.
 *
 */
public class ServiceDefinitionProcessor extends org.overlord.rtgov.ep.EventProcessor {

    private static final Logger LOG=Logger.getLogger(ServiceDefinitionProcessor.class.getName());
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable process(String source, Serializable event,
            int retriesLeft) throws Exception {
        Serializable ret=null;
        
        if (event instanceof ActivityUnit) {

            java.util.Iterator<ServiceDefinition> iter=
                        ServiceDefinitionUtil.derive((ActivityUnit)event).iterator();
            
            while (iter.hasNext()) {
                if (ret == null) {
                    ret = new java.util.LinkedList<ServiceDefinition>();
                }
                ((java.util.LinkedList<ServiceDefinition>)ret).add(iter.next());
            }

            if (LOG.isLoggable(Level.FINEST)) {
                java.util.LinkedList<ServiceDefinition> list=
                        (java.util.LinkedList<ServiceDefinition>)ret;
                
                byte[] aub=ActivityUtil.serializeActivityUnit((ActivityUnit)event);
                
                LOG.finest("Transforming activity unit: "+new String(aub));

                if (list != null) {
                    LOG.finest("Service definition list size="+list.size());
                    
                    for (int i=0; i < list.size(); i++) {
                        byte[] sdb=ServiceDefinitionUtil.serializeServiceDefinition(list.get(i));
                        
                        LOG.finest("Service definition("+i+"): "+new String(sdb));
                    }
                } else {
                    LOG.finest("Service definition list is null");
                }
            }
            
            if (ret != null) {
                if (((java.util.LinkedList<Serializable>)ret).size() == 0) {
                    ret = null;
                } else if (((java.util.LinkedList<Serializable>)ret).size() == 1) {
                    ret = ((java.util.LinkedList<Serializable>)ret).getFirst();
                }
            }
        }
        
        return (ret);
    }

}
