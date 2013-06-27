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
package org.overlord.rtgov.call.trace;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

/**
 * This class provides the CDI interceptor around transactional beans.
 *
 */
@Transactional @Interceptor
public class TransactionalInterceptor {
    
    private static final Logger LOG=Logger.getLogger(TransactionalInterceptor.class.getName());

    @Resource
    private UserTransaction _tx;
        
    /**
     * This method manages the scope of the transaction around an invoked
     * bean.
     * 
     * @param context The invocation context
     * @return The result from the performed method
     * @throws Exception Failed to perform method
     */
    @AroundInvoke
    public Object manageTransaction(InvocationContext context) throws Exception {
        Object result=null;
        
        if (_tx.getStatus() == Status.STATUS_NO_TRANSACTION) {
            _tx.begin();
        
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Starting transaction: context="+context+" op="+context.getMethod());
            }
            
            try {
                result = context.proceed();
                
                _tx.commit();
                
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Committing transaction: context="+context);
                }
            } catch (Exception e) {
                _tx.rollback();
                
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Rolling back transaction: context="+context+" exception="+e);
                }
                
                throw e;
            }
        } else {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Transaction already active: context="+context+" op="+context.getMethod());
            }
            result = context.proceed();
        }
        
        return result;
    }
}
