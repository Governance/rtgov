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
package org.overlord.rtgov.service.dependency.presentation;

import org.overlord.rtgov.analytics.service.InvocationMetric;

/**
 * This interface represents a severity analyzer algorithm used to
 * determine the severity for a component based on supplied invocation
 * metric information.
 *
 */
public interface SeverityAnalyzer {

    /**
     * This method returns the severity relevant for the supplied
     * metric information.
     * 
     * @param component The source component
     * @param summary The accumulated results from the history of invocation metrics
     * @param history The history of invocation metrics
     * @return The severity
     */
    public Severity getSeverity(Object component, InvocationMetric summary, 
                    java.util.List<InvocationMetric> history);
    
}
