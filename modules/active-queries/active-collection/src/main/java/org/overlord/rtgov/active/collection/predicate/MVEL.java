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
package org.overlord.rtgov.active.collection.predicate;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.security.AccessControlContext;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.NoSuchAlgorithmException;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.URIParameter;
import java.security.cert.Certificate;
import java.text.MessageFormat;
import java.util.logging.Logger;

import org.overlord.rtgov.active.collection.ActiveCollectionContext;

/**
 * This class provides an MVEL implementation of the
 * predicate interface.
 *
 */
public class MVEL extends Predicate {

    private static final Logger LOG=Logger.getLogger(MVEL.class.getName());
    
    /**
     * The RTGov security policy.
     */
    public static final String RTGOV_POLICY = "rtgov.security.policy";
    
    private String _expression;
    private volatile Serializable _expressionCompiled;

    private static AccessControlContext RTGOV_ACC;

    /**
     * This is the default constructor for the MVEL predicate.
     */
    public MVEL() {
    }
    
    /**
     * This constructor initializes the expression for the MVEL
     * predicate.
     * 
     * @param expr The predicate
     */
    public MVEL(String expr) {
        setExpression(expr);
    }
    
    /**
     * This method sets the expression.
     * 
     * @param expr The expression
     */
    public void setExpression(String expr) {
        _expression = expr;
        
        // Reset state
        _expressionCompiled = null;
    }
    
    /**
     * This method gets the expression.
     * 
     * @return The expression
     */
    public String getExpression() {
        return (_expression);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean evaluate(final ActiveCollectionContext context, final Object item) {
        boolean ret=false;

        if (_expressionCompiled == null) {
            if (_expression != null) {
                synchronized (this) {
                    _expressionCompiled = org.mvel2.MVEL.compileExpression(_expression);
                }
            }
        }

        final Serializable expressionCompiled = _expressionCompiled;
        if (expressionCompiled != null) {
            final PrivilegedAction<Boolean> evaluateAction = new MVELEvaluateAction(context, expressionCompiled, item);
            if (System.getSecurityManager() != null) {
                try {
                    ret = AccessController.doPrivileged(evaluateAction , RTGOV_ACC);
                } catch (final RuntimeException re) {
                    Throwable cause = re.getCause();
                    while (cause != null) {
                        if (cause instanceof AccessControlException) {
                            LOG.warning(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                                    "active-collection.Messages").getString("ACTIVE-COLLECTION-15"),
                                    _expression, cause.getMessage()));
                            break;
                        } else {
                            cause = cause.getCause();
                        }
                    }
                }
            } else {
                ret = evaluateAction.run();
            }
        }
        
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("MVEL["+_expression+"]");
    }

    /**
     * MVEL privileged action.
     *
     */
    private class MVELEvaluateAction implements PrivilegedAction<Boolean> {
       private final ActiveCollectionContext _context;
       private final Serializable _expressionCompiled;
       private final Object _item;

       MVELEvaluateAction(final ActiveCollectionContext context, final Serializable expressionCompiled, final Object item) {
           _context = context;
           _expressionCompiled = expressionCompiled;
           _item = item;
       }

        public Boolean run() {
            Boolean ret=null;

            java.util.Map<String,Object> vars=new java.util.HashMap<String,Object>();
            vars.put("context", _context);

            Object result=org.mvel2.MVEL.executeExpression(_expressionCompiled, _item, vars);

            if (result instanceof Boolean) {
                ret = Boolean.class.cast(result);
            } else {
                LOG.severe(MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "active-collection.Messages").getString("ACTIVE-COLLECTION-2"),
                        _expression, result, _item));
            }

            return ret;
        }
    }

    static {
        final String rtgovPolicy = System.getProperty(RTGOV_POLICY);
        if (rtgovPolicy != null) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager == null) {
                LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "active-collection.Messages").getString("ACTIVE-COLLECTION-12"));
                RTGOV_ACC = null;
            } else {
                final URI rtgovPolicyResource = new File(rtgovPolicy).toURI();
                try {
                    final Policy policy = Policy.getInstance("JavaPolicy", new URIParameter(rtgovPolicyResource));
                    final CodeSource codeSource = new CodeSource(null, (Certificate[])null);
                    final PermissionCollection perms = policy.getPermissions(codeSource);
                    final ProtectionDomain[] pds = {new ProtectionDomain(codeSource, perms)};
                    RTGOV_ACC = new AccessControlContext(pds);
                    LOG.info(java.util.PropertyResourceBundle.getBundle(
                            "active-collection.Messages").getString("ACTIVE-COLLECTION-13"));
                } catch (final NoSuchAlgorithmException nsae) {
                    LOG.severe(java.util.PropertyResourceBundle.getBundle(
                        "active-collection.Messages").getString("ACTIVE-COLLECTION-14"));
                }
            }
        } else {
            RTGOV_ACC = null;
        }
    }
}
