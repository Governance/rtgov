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

import static com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat.DATE_TIME_FULL;

import java.io.Serializable;
import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

import com.google.gwt.i18n.shared.DateTimeFormat;

/**
 * Models the full details of a situation.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class SituationBean extends SituationSummaryBean implements Serializable {

	private static final long serialVersionUID = SituationBean.class.hashCode();

    private java.util.List<NameValuePairBean> context = new java.util.ArrayList<NameValuePairBean>();
    private MessageBean message;
    private CallTraceBean callTrace;
    private boolean assignedToCurrentUser;
    private boolean takeoverPossible;
    private boolean resubmitPossible;
    private boolean manualResolutionPossible;
    private String resubmittedSituationId;

    /**
     * Constructor.
     */
    public SituationBean() {
    }
    
    /**
     * @return the context
     */
    public java.util.List<NameValuePairBean> getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(java.util.List<NameValuePairBean> context) {
        this.context = context;
    }
    
    /**
     * This method returns the optional request message associated with the situation.
     * 
     * @return The optional request message
     */
    public MessageBean getMessage() {
    	return message;
    }
    
    /**
     * This method sets the optional request message.
     * 
     * @param mesg The optional request message
     */
    public void setMessage(MessageBean mesg) {
    	this.message = mesg;
    }
    
    /**
     * @return true if this situation has a message associated with it
     */
    public boolean hasMessage() {
        return this.message != null;
    }

    /**
     * @return the callTrace
     */
    public CallTraceBean getCallTrace() {
        return callTrace;
    }

    /**
     * @param callTrace the callTrace to set
     */
    public void setCallTrace(CallTraceBean callTrace) {
        this.callTrace = callTrace;
    }

    /**
     * @return whether this situation is assigned to the currently logged-in user
     */
	public boolean isAssignedToCurrentUser() {
		return assignedToCurrentUser;
	}

	/**
	 * @param isAssignedToCurrentUser
	 */
	public void setAssignedToCurrentUser(boolean isAssignedToCurrentUser) {
		this.assignedToCurrentUser = isAssignedToCurrentUser;
	}

	/**
	 *
	 * @param isTakeoverPossible
	 */
	public void setTakeoverPossible(boolean isTakeoverPossible) {
		this.takeoverPossible = isTakeoverPossible;
	}

	/**
	 *
	 * @return whether this {@link Situation} can be taken over by the currently logged-in user
	 */
	public boolean isTakeoverPossible() {
		return takeoverPossible;
	}

    /**
     * @return The name of the user who has resubmitted this situation
     */
	public String resubmitBy() {
		return getProperties().get("resubmitBy");
	}

    /**
     * @return The date and time of the last resubmit
     */
    public String resubmitAt() {
        if (!getProperties().containsKey("resubmitAt")) {
            return null;
        }
        Date date = new Date(Long.valueOf(getProperties().get("resubmitAt")));
        return DateTimeFormat.getFormat(DATE_TIME_FULL).format(date);
    }

    /**
     * @return The error message of the last resubmit failure or null
     */
    public String resubmitResult() {
        return getProperties().get("resubmitResult");
    }

    /**
     *
     * @return true if the last resubmit failed
     */
    public boolean resubmitError() {
        return getProperties().containsKey("resubmitResult")
                && !"Success".equals(getProperties().get("resubmitResult"));
    }

    /**
     *
     * @return The error message for the last resubmit
     */
    public String resubmitErrorMessage() {
        return getProperties().get("resubmitErrorMessage");
    }

    /**
     * @return the isResubmitPossible
     */
    public boolean isResubmitPossible() {
        return resubmitPossible;
    }

    /**
     * @param isResubmitPossible the isResubmitPossible to set
     */
    public void setResubmitPossible(boolean isResubmitPossible) {
        this.resubmitPossible = isResubmitPossible;
    }
    
    /**
     * @return the isManualResolutionPossible
     */
    public boolean isManualResolutionPossible() {
        return manualResolutionPossible;
    }

    /**
     * @param isManualResolutionPossible the isManualResolutionPossible to set
     */
    public void setManualResolutionPossible(boolean isManualResolutionPossible) {
        this.manualResolutionPossible = isManualResolutionPossible;
    }
    
    /**
     * This method sets the resubmitted situation id.
     * 
     * @param id The resubmitted situation id
     */
    public void setResubmittedSituationId(String id) {
        this.resubmittedSituationId = id;
    }
    
    /**
     * This method returns the resubmitted situation id.
     * 
     * @return The resubmitted situation id
     */
    public String getResubmittedSituationId() {
        return (this.resubmittedSituationId);
    }

}
