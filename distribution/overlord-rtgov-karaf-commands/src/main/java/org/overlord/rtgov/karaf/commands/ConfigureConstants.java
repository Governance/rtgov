/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.rtgov.karaf.commands;

/**
 * Configure Constants
 *
 * @author David Virgil Naranjo
 */
public interface ConfigureConstants {

    public static final String RTGOV_PROPERTIES_FILE_NAME = "overlord-rtgov.properties"; //$NON-NLS-1$

    public static final String RTGOV_ALL_PROFILE = "all.profile";
    public static final String RTGOV_CLIENT_PROFILE = "client.profile";

    // FABRIC CONSTANTS

    // Ui headers:

    public static final String RTGOV_HEADER_HREF = "overlord.headerui.apps.rtgov.href"; //$NON-NLS-1$
    public static final String RTGOV_HEADER_HREF_VALUE = "/rtgov-ui/"; //$NON-NLS-1$
    public static final String RTGOV_HEADER_LABEL = "overlord.headerui.apps.rtgov.label"; //$NON-NLS-1$
    public static final String RTGOV_HEADER_LABEL_VALUE = "RuntimeGovernance"; //$NON-NLS-1$
    public static final String RTGOV_HEADER_PRIMARY_BRAND = "overlord.headerui.apps.rtgov.primary-brand"; //$NON-NLS-1$
    public static final String RTGOV_HEADER_PRIMARY_BRAND_VALUE = "JBoss Overlord"; //$NON-NLS-1$
    public static final String RTGOV_HEADER_SECOND_BRAND = "overlord.headerui.apps.rtgov.secondary-brand"; //$NON-NLS-1$
    public static final String RTGOV_HEADER_SECOND_BRAND_VALUE = "Runtime Governance"; //$NON-NLS-1$
}
