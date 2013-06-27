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
package org.overlord.rtgov.epn.validation;

import org.overlord.rtgov.epn.Network;

/**
 * This interface is used to report validation issues
 * for an Event Processor Network.
 *
 */
public interface EPNValidationListener {

    /**
     * This method identifies an issue with a part of
     * an Event Processor Network.
     * 
     * @param epn The network
     * @param target The object resulting in the error
     * @param issue The description of the issue
     */
    public void error(Network epn, Object target, String issue);
    
}
