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
package org.overlord.rtgov.internal.collector.osgi;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.TransactionManager;

import org.overlord.rtgov.activity.collector.CollectorContext;

/**
 * This class provides context information regarding the
 * JBossAS7 environment for the activity collector.
 *
 */
public class OSGiCollectorContext implements CollectorContext {

    private static final String UNKNOWN_HOST = "Unknown-Host";
    private static final String UNKNOWN_NODE = "Unknown-Node";
    private static final String NODE_PROPERTY = "karaf.name";

    private static final Logger LOG=Logger.getLogger(OSGiCollectorContext.class.getName());

    /**
     * {@inheritDoc}
     */
    public String getHost() {
        String ret=UNKNOWN_HOST;
        
        try {
            ret = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Unable to get host name: "+e);
            }
        }
        
        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    public String getNode() {
        return (System.getProperty(NODE_PROPERTY,UNKNOWN_NODE));
    }

    /**
     * {@inheritDoc}
     */
    public TransactionManager getTransactionManager() {
        return (null);
    }
}
