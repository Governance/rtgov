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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.Principal;

import javax.interceptor.InterceptorBinding;

/**
 * Strategy interface to access user related information.
 *
 * @see Principal
 */
public interface IUserContext {

    /**
     * @return The current {@link Principal}
     */
    Principal getUserPrincipal();

    class Holder {
        private static final ThreadLocal<IUserContext> securityContextHolder = new ThreadLocal<IUserContext>();

        private Holder() {
        }

        public static void setPrincipal(final Principal principal) {
            setSecurityContext(new IUserContext() {

                @Override
                public Principal getUserPrincipal() {
                    return principal;
                }

            });
        }

        public static Principal getUserPrincipal() {
            IUserContext securityContext = getSecurityContext();
            return (securityContext != null ? securityContext.getUserPrincipal() : null);
        }

        public static IUserContext getSecurityContext() {
            return securityContextHolder.get();
        }

        public static void setSecurityContext(IUserContext securityContext) {
            securityContextHolder.set(securityContext);
        }

        public static void removeSecurityContext() {
            securityContextHolder.remove();
        }
    }

    /**
     * {@link InterceptorBinding} annotation to activate
     * {@link UserContextInterceptor}
     */
    @InterceptorBinding
    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Binding {
    }

}
