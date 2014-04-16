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
package org.overlord.rtgov.ui.client.model;

import java.io.Serializable;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Results of batch retry.
 * 
 */
@Portable
public class BatchRetryResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private int processedCount;
    private int failedCount;
    private int ignoredCount;

    /**
     * Constructor.
     */
    public BatchRetryResult() {
    }

    public BatchRetryResult(int processedCount, int failedCount, int ignoredCount) {
        super();
        this.processedCount = processedCount;
        this.failedCount = failedCount;
        this.ignoredCount = ignoredCount;
    }

    /**
     * @return the processedCount
     */
    public int getProcessedCount() {
        return processedCount;
    }

    /**
     * @param processedCount
     *            the processedCount to set
     */
    public void setProcessedCount(int processedCount) {
        this.processedCount = processedCount;
    }

    /**
     * @return the failedCount
     */
    public int getFailedCount() {
        return failedCount;
    }

    /**
     * @param failedCount
     *            the failedCount to set
     */
    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    /**
     * @return the ignoredCount
     */
    public int getIgnoredCount() {
        return ignoredCount;
    }

    /**
     * @param ignoredCount
     *            the ignoredCount to set
     */
    public void setIgnoredCount(int ignoredCount) {
        this.ignoredCount = ignoredCount;
    }

}
