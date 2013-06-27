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
package org.overlord.rtgov.jee;

import javax.annotation.Resource;

import org.overlord.rtgov.activity.collector.ActivityCollector;
import org.overlord.rtgov.activity.model.ActivityType;

/**
 * This interface represents the capability for validating
 * activity information from a JEE application.
 *
 */
public class DefaultActivityValidator implements ActivityValidator {

    private static final String ACTIVITY_COLLECTOR = "java:global/overlord-rtgov/ActivityCollector";

    @Resource(lookup=ACTIVITY_COLLECTOR)
    private ActivityCollector _activityCollector=null;
    
    /**
     * {@inheritDoc}
     */
    public void validate(ActivityType actType) throws Exception {
         _activityCollector.validate(actType);
    }
    
}
