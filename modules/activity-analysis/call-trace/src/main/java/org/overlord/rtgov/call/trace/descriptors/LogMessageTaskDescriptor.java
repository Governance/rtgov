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
package org.overlord.rtgov.call.trace.descriptors;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.app.LogMessage;

/**
 * This class provides a descriptor for the LogMessage
 * activity.
 *
 */
public class LogMessageTaskDescriptor implements TaskDescriptor {

    /**
     * {@inheritDoc}
     */
    public boolean isSupported(ActivityType at) {
        return (at instanceof LogMessage);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getDescription(ActivityType at) {
        return (((LogMessage)at).getLevel()+": "
                    +((LogMessage)at).getMessage());
    }
    
}
