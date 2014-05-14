/*
 * Copyright 2013 JBoss Inc
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
package org.overlord.rtgov.analytics.situation.store;

/**
 * This enumerated type represents the resolution states.
 *
 */
public enum ResolutionState {
    
    /**
     * Any resolution state.
     */
    ANY,
    
    /**
     * Null resolution state.
     */
    NULL,
    
    /**
     * Unresolved state.
     */
    UNRESOLVED,
    
    /**
     * Resolved state.
     */
    RESOLVED,
    
    /**
     * In-progress state.
     */
    IN_PROGRESS,
    
    /**
     * Waiting state.
     */
    WAITING,
    
    /**
     * Re-opened state.
     */
    REOPENED;
}
