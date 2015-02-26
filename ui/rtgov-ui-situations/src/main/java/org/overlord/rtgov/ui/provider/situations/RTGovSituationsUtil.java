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
package org.overlord.rtgov.ui.provider.situations;

import java.util.Date;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.ui.client.model.NameValuePairBean;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationEventBean;

/**
 * Utility class for RTGov situations.
 *
 */
public class RTGovSituationsUtil {

    /**
     * Prefix identifying internal properties.
     */
    public static final String INTERNAL_PROPERTY_PREFIX="_";

    /**
     * The property name used to defined the resubmitted sitation id.
     */
    public static final String HEADER_RESUBMITTED_SITUATION_ID = ActivityType.RTGOV_PROPERTY_PREFIX+"resubmittedSituationId";

    /**
     * The property name used to defined the "assigned to" value.
     */
    public static final String HEADER_ASSIGNED_TO = ActivityType.RTGOV_PROPERTY_PREFIX+Situation.class.getSimpleName()
                                    +INTERNAL_PROPERTY_PREFIX+"assignedTo";

    /**
     * The property name used to defined the "resolutionState" value.
     */
    public static final String HEADER_RESOLUTION_STATE = ActivityType.RTGOV_PROPERTY_PREFIX+Situation.class.getSimpleName()
                                    +INTERNAL_PROPERTY_PREFIX+"resolutionState";

    /**
     * Constructor.
     */
    private RTGovSituationsUtil() {
    }

    /**
     * Get situation summary from the original situation.
     *
     * @param situation The situation
     * @return The summary
     */
    public static SituationBean getSituationBean(Situation situation) {
    	SituationBean ret=new SituationBean();

    	ret.setSituationId(situation.getId());
    	if (situation.getSeverity() != null) {
    		ret.setSeverity(situation.getSeverity().name().toLowerCase());
    	}
    	ret.setType(situation.getType());
    	ret.setSubject(situation.getSubject());
    	ret.setTimestamp(new Date(situation.getTimestamp()));
    	ret.setDescription(situation.getDescription());
    	
        // RTGOV-499: Use deprecated method until no longer needing to support FSW6.0
    	for (String key : situation.getProperties().keySet()) {
    	    // RTGOV-499 When FSW6.0 support no longer required, use constant on ActivityType
    	    if (!key.startsWith(INTERNAL_PROPERTY_PREFIX)) {
    	        ret.getProperties().put(key, situation.getProperties().get(key));
    	    }
    	}
    	
    	for (Context context : situation.getContext()) {
            if (context.getType() == null) {
                context.setType(Context.Type.Conversation);
            }
    	    if (context.getType() != Context.Type.Message) {
    	        ret.getContext().add(new NameValuePairBean(context.getType().name(),
    	                        context.getValue()));
    	    }
    	}
    	
    	ret.setResubmittedSituationId(situation.getProperties().get(HEADER_RESUBMITTED_SITUATION_ID));

    	return (ret);
    }

    /**
     * Get situation event from the original situation.
     *
     * @param situation The situation
     * @return The event
     */
    public static SituationEventBean getSituationEventBean(Situation situation) {
    	SituationEventBean ret=new SituationEventBean();

    	ret.setSituationId(situation.getId());
    	if (situation.getSeverity() != null) {
    		ret.setSeverity(situation.getSeverity().name().toLowerCase());
    	}
    	ret.setType(situation.getType());
    	ret.setSubject(situation.getSubject());
    	ret.setTimestamp(new Date(situation.getTimestamp()));

    	return (ret);
    }
}
