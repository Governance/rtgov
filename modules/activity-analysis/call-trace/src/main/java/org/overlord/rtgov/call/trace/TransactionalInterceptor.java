/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
