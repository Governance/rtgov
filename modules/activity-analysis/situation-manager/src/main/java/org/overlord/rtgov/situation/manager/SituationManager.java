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
package org.overlord.rtgov.situation.manager;

import org.overlord.rtgov.analytics.situation.IgnoreSubject;

/**
 * This interface represents the component for managing situations.
 *
 */
public interface SituationManager {

    /**
     * This method causes the supplied situation subject
     * to be ignored.
     * 
     * @param details The ignore subject details
     * @throws Exception Failed to ignore subject
     */
    public void ignore(IgnoreSubject details) throws Exception;
    
    /**
     * This method causes the supplied subject, that was previously
     * ignored, to be observed.
     * 
     * @param subject The previously ignored subject
     * @param principal The optional principal
     * @throws Exception Failed to observe subject
     */
    public void observe(String subject, String principal) throws Exception;
    
}
