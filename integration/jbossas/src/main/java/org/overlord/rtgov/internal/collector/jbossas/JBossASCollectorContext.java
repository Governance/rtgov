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
package org.overlord.rtgov.internal.collector.jbossas;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

import org.overlord.commons.services.ServiceInit;
import org.overlord.rtgov.activity.collector.CollectorContext;

/**
 * This class provides context information regarding the
 * JBossAS7 environment for the activity collector.
 *
 */
public class JBossASCollectorContext implements CollectorContext {

    private static final String TRANSACTION_MANAGER = "java:jboss/TransactionManager";

    private static final Logger LOG=Logger.getLogger(JBossASCollectorContext.class.getName());

    private TransactionManager _transactionManager=null;

    /**
     * This method initializes the collector context.
     */
    @ServiceInit
    public void init() {
        try {
            InitialContext ctx=new InitialContext();
            
            _transactionManager = (TransactionManager)ctx.lookup(TRANSACTION_MANAGER);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, java.util.PropertyResourceBundle.getBundle(
                    "rtgov-jbossas.Messages").getString("RTGOV-JBOSSAS-1"), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getHost() {
        return (System.getProperty("jboss.qualified.host.name","Unknown-Host"));
    }

    /**
     * {@inheritDoc}
     */
    public String getNode() {
        return (System.getProperty("jboss.node.name","Unknown-Node"));
    }

    /**
     * {@inheritDoc}
     */
    public TransactionManager getTransactionManager() {
        return (_transactionManager);
    }
}
