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
    	ret.getProperties().putAll(situation.getProperties());
    	
    	for (Context context : situation.getContext()) {
            if (context.getType() == null) {
                context.setType(Context.Type.Conversation);
            }
    	    if (context.getType() != Context.Type.Message) {
    	        ret.getContext().add(new NameValuePairBean(context.getType().name(),
    	                        context.getValue()));
    	    }
    	}

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
