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
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Models the full details of a reference.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class ReferenceBean implements Serializable {

    private static final long serialVersionUID = ReferenceBean.class.hashCode();

    private String referenceId;
    private QName name;
    private QName application;
    private String referenceInterface;

    /**
     * Constructor.
     */
    public ReferenceBean() {
    }

    /**
     * @return the referenceId
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * @return the name
     */
    public QName getName() {
        return name;
    }

    /**
     * @return the application
     */
    public QName getApplication() {
        return application;
    }

    /**
     * @return the referenceInterface
     */
    public String getReferenceInterface() {
        return referenceInterface;
    }

    /**
     * @param referenceId the referenceId to set
     */
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * @param name the name to set
     */
    public void setName(QName name) {
        this.name = name;
    }

    /**
     * @param application the application to set
     */
    public void setApplication(QName application) {
        this.application = application;
    }

    /**
     * @param referenceInterface the referenceInterface to set
     */
    public void setReferenceInterface(String referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

}
