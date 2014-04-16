/*
 * 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.ui.server.interceptors;

import static org.jboss.errai.bus.server.api.RpcContext.getServletRequest;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Used to bind {@link IUserContext} to the current method invocation.
 *
 * @see IUserContext
 */
@Interceptor
@IUserContext.Binding
public class UserContextInterceptor {

    @AroundInvoke
    public Object bindUserContext(InvocationContext invocationContext) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) getServletRequest();
        IUserContext.Holder.setPrincipal(servletRequest.getUserPrincipal());
        try {
            return invocationContext.proceed();
        } finally {
            IUserContext.Holder.removeSecurityContext();
        }
    }
}
