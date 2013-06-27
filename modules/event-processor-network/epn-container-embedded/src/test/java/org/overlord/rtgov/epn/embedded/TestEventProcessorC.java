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
package org.overlord.rtgov.epn.embedded;

import org.overlord.rtgov.ep.EventProcessor;

/**
 * This test event processor throws exceptions when processing event, and
 * counts the number of retries.
 *
 */
public class TestEventProcessorC extends EventProcessor {
    
    public int _retryCount=0;

    public java.io.Serializable process(String source, java.io.Serializable event, int retriesLeft) throws Exception {
        _retryCount++;
        throw new Exception("Failed to process event");
    }

}
