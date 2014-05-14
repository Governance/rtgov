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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.overlord.rtgov.ui.client.model.ResolutionState;

/**
 * A simple data bean for returning summary information for single deployment.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class SituationSummaryBean {

    private String situationId;
    private String severity;
    private String type;
    private String subject;
    private Date timestamp;
    private String description;
    private Map<String, String> properties = new HashMap<String, String>();

    /**
     * Constructor.
     */
    public SituationSummaryBean() {
    }

    /**
     * @return the situationId
     */
    public String getSituationId() {
        return situationId;
    }

    /**
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @param situationId the situationId to set
     */
    public void setSituationId(String situationId) {
        this.situationId = situationId;
    }

    /**
     * @param severity the severity to set
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
	 * @return the resolutionState
	 */
    public String getResolutionState() {
        String resolutionState = ResolutionState.UNRESOLVED.name();
        if (properties != null && properties.get("resolutionState") != null) {
            resolutionState = properties.get("resolutionState");
        }
        return resolutionState;
    }
    
    /**
	 * @return the assignedTo User
	 */
    public String getAssignedTo() {
        String assignedTo = null;
        if (properties != null && properties.get("assignedTo") != null) {
        	assignedTo = properties.get("assignedTo");
        }
        return assignedTo;
    }

	/**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((situationId == null) ? 0 : situationId.hashCode());
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
        SituationSummaryBean other = (SituationSummaryBean) obj;
        if (situationId == null) {
            if (other.situationId != null)
                return false;
        } else if (!situationId.equals(other.situationId))
            return false;
        return true;
    }

}
