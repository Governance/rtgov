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
package org.overlord.rtgov.internal.situation.manager.jmx;

/**
 * This interface represents the management component for the situation
 * manager.
 *
 */
public interface SituationMgrMBean {

    /**
     * This method ignores the supplied subject.
     * 
     * @param subject The subject
     * @param reason The reason for ignoring the subject
     * @throws Exception Failed to ignore
     */
    public void ignore(String subject, String reason) throws Exception;
    
    /**
     * This method observes the previously ignored
     * supplied subject.
     * 
     * @param subject The subject
     * @throws Exception Failed to observe
     */
    public void observe(String subject) throws Exception;
    
}
