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
package org.overlord.bam.content.epn;

import java.io.Serializable;

import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.analytics.service.ServiceDefinition;
import org.overlord.bam.analytics.service.util.ServiceDefinitionUtil;

/**
 * This class provides an implementation of the EventProcessor
 * interface, used to derive service definitions from the
 * stream of activity units.
 *
 */
public class ServiceDefinitionProcessor extends org.overlord.bam.epn.EventProcessor {

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
            
            if (((java.util.LinkedList<Serializable>)ret).size() == 0) {
                ret = null;
            } else if (((java.util.LinkedList<Serializable>)ret).size() == 1) {
                ret = ((java.util.LinkedList<Serializable>)ret).getFirst();
            }
        }
        
        return (ret);
    }

}
