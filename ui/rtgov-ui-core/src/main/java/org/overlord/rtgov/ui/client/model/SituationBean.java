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
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, String> context = new HashMap<String, String>();
    private MessageBean message;
    private CallTraceBean callTrace;
    private boolean isAssignedToCurrentUser;
    private boolean isTakeoverPossible;
    private boolean isResubmitPossible;

    /**
     * Constructor.
     */
    public SituationBean() {
    }

    /**
     * @return the context
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(Map<String, String> context) {
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
		return isAssignedToCurrentUser;
	}

	/**
	 * @param isAssignedToCurrentUser
	 */
	public void setAssignedToCurrentUser(boolean isAssignedToCurrentUser) {
		this.isAssignedToCurrentUser = isAssignedToCurrentUser;
	}

	/**
	 *
	 * @param isTakeoverPossible
	 */
	public void setTakeoverPossible(boolean isTakeoverPossible) {
		this.isTakeoverPossible = isTakeoverPossible;
	}

	/**
	 *
	 * @return whether this {@link Sitatuion} can be taken over by the currently logged-in user
	 */
	public boolean isTakeoverPossible() {
		return isTakeoverPossible;
	}

    /**
     * @return The name of the user who has resubmitted this situation
     */
	public String getResubmitBy() {
		return getProperties().get("resubmitBy");
	}

    /**
     * @return The date and time of the last resubmit
     */
    public String getResubmitAt() {
        if (!getProperties().containsKey("resubmitAt")) {
            return null;
        }
        Date date = new Date(Long.valueOf(getProperties().get("resubmitAt")));
        return DateTimeFormat.getFormat(DATE_TIME_FULL).format(date);
    }

    /**
     * @return The error message of the last resubmit failure or null
     */
    public String getResubmitResult() {
        return getProperties().get("resubmitResult");
    }

    /**
     *
     * @return true if the last resubmit failed
     */
    public boolean isResubmitError() {
        return getProperties().containsKey("resubmitResult")
                && !"Success".equals(getProperties().get("resubmitResult"));
    }

    /**
     *
     * @return The error message for the last resubmit
     */
    public String getResubmitErrorMessage() {
        return getProperties().get("resubmitErrorMessage");
    }

    /**
     * @return the isResubmitPossible
     */
    public boolean isResubmitPossible() {
        return isResubmitPossible;
    }

    /**
     * @param isResubmitPossible the isResubmitPossible to set
     */
    public void setResubmitPossible(boolean isResubmitPossible) {
        this.isResubmitPossible = isResubmitPossible;
    }
}
