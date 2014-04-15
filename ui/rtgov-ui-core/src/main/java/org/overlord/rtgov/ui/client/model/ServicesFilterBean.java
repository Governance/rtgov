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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * All of the user's filter settings (configured on the left-hand sidebar of
 * the Services page).
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
public class ServicesFilterBean {

    private String applicationName;
    private String serviceName;
    private String processingState;

    /**
     * Constructor.
     */
    public ServicesFilterBean() {
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the processingState
     */
    public String getProcessingState() {
        return processingState;
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public ServicesFilterBean setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    /**
     * @param appName the appName to set
     */
    public ServicesFilterBean setApplicationName(String appName) {
        this.applicationName = appName;
        return this;
    }

    /**
     * @param processingState the processingState to set
     */
    public ServicesFilterBean setProcessingState(String processingState) {
        this.processingState = processingState;
        return this;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
        result = prime * result + ((processingState == null) ? 0 : processingState.hashCode());
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServicesFilterBean other = (ServicesFilterBean) obj;
        if (applicationName == null) {
            if (other.applicationName != null)
                return false;
        } else if (!applicationName.equals(other.applicationName))
            return false;
        if (processingState == null) {
            if (other.processingState != null)
                return false;
        } else if (!processingState.equals(other.processingState))
            return false;
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        return true;
    }

}
