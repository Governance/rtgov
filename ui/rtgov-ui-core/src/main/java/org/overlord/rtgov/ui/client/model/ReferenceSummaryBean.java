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
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * A simple data bean for returning summary information for single reference.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class ReferenceSummaryBean {

    private String referenceId;
    private String name;
    private String application;
    private String iface;
    private String bindings;

    /**
     * Constructor.
     */
    public ReferenceSummaryBean() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the category
     */
    public String getApplication() {
        return application;
    }

    /**
     * @return the iface
     */
    public String getIface() {
        return iface;
    }

    /**
     * @return the address
     */
    public String getBindings() {
        return bindings;
    }

    /**
     * @param name the name to set
     */
    public ReferenceSummaryBean setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param category the category to set
     */
    public ReferenceSummaryBean setApplication(String category) {
        this.application = category;
        return this;
    }

    /**
     * @param iface the iface to set
     */
    public ReferenceSummaryBean setIface(String iface) {
        this.iface = iface;
        return this;
    }

    /**
     * @param address the address to set
     */
    public ReferenceSummaryBean setBindings(String address) {
        this.bindings = address;
        return this;
    }

    /**
     * @return the serviceId
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * @param serviceId the serviceId to set
     */
    public void setReferenceId(String serviceId) {
        this.referenceId = serviceId;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((referenceId == null) ? 0 : referenceId.hashCode());
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
        ReferenceSummaryBean other = (ReferenceSummaryBean) obj;
        if (referenceId == null) {
            if (other.referenceId != null)
                return false;
        } else if (!referenceId.equals(other.referenceId))
            return false;
        return true;
    }

}
