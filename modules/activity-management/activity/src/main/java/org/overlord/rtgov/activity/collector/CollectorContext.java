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
package org.overlord.rtgov.activity.collector;

import javax.transaction.TransactionManager;

/**
 * This interface is responsible for providing the initial
 * system wide and user related information associated with the activity
 * event collector.
 *
 */
public interface CollectorContext {
    
    /**
     * This method returns the host name.
     * 
     * @return The host name
     */
    public String getHost();
    
    /**
     * This method returns the name of the node.
     * If running in a clustered environment, this
     * name will uniquely identify the environment
     * within the cluster.
     * 
     * @return The node name
     */
    public String getNode();
    
    /**
     * This method returns the transaction manager,
     * if available.
     * 
     * @return The transaction manager, or null if not available
     */
    public TransactionManager getTransactionManager();
    
}
