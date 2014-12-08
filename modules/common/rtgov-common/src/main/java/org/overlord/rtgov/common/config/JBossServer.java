/*
 * Copyright 2013-5 Red Hat Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.common.config;

/**
 * This class provides details related to a JBoss Server.
 *
 */
public final class JBossServer {

    private static String baseUrl = null;

    /**
     * The default constructor.
     */
    private JBossServer() {
    }
    
    /**
     * Uses the 'jboss.bind.address' and 'jboss.socket.binding.port-offset' parameters to construct
     * the baseUrl. The scheme defaults to http, but is set to https if the port setting ends in 443.
     * Defaults to http://localhost:8080.
     * @return baseUrl of the server.
     */
    public static String getBaseUrl() {
        if (baseUrl == null) {
            String hostname = System.getProperty("jboss.bind.address","localhost"); //$NON-NLS-1$ //$NON-NLS-2$
            Integer portOffset = Integer.valueOf(System.getProperty("jboss.socket.binding.port-offset","0")); //$NON-NLS-1$ //$NON-NLS-2$
            String port = String.valueOf(8080 + portOffset);
            String scheme = "http://"; //$NON-NLS-1$
            if (port.endsWith("443")) {
                scheme = "https://"; //$NON-NLS-1$ //$NON-NLS-2$
            }
            baseUrl = scheme + hostname + ":" + port; //$NON-NLS-1$
        }
        return baseUrl;
    }
}
