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
package org.overlord.rtgov.analytics.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.Situation.Severity;

public class SituationUtilTest {

    private static final int TEST_ACTIVITY_TYPE_ID_INDEX = 1;
    private static final String TEST_ACTIVITY_TYPE_ID = "TestActivityTypeId";
    private static final String TEST_TYPE = "TestType";
    private static final int TEST_TIMESTAMP = 15263748;
    private static final String TEST_SUBJECT = "TestSubject";
    private static final String TEST_ID = "TestId";
    private static final String TEST_DESCRIPTION = "TestDescription";
    private static final String TEST_PROPERTY_VALUE = "TestPropertyValue";
    private static final String TEST_PROPERTY = "TestProperty";
    private static final String TEST_CONVERSATION = "TestConversation";

    @Test
    public void testSerializeSituationProperties() {
        Situation situation=new Situation();
        situation.getSituationProperties().put(TEST_PROPERTY, TEST_PROPERTY_VALUE);
        
        String json=null;
        
        try {
            byte[] b=SituationUtil.serializeSituation(situation);
            
            if (b != null) {
                json = new String(b);
            }
        } catch (Exception e) {
            fail("Failed to serialize: "+e);
        }
        
        assertNotNull(json);
        
        if (json.contains("situationProperties")) {
            fail("Field 'situationProperties' should not be present in json");
        }
        
        if (!json.contains("properties")) {
            fail("Field 'properties' should"
                    + " be present in json");
        }
    }
    
    @Test
    public void testSerialize() {
        Situation situation=new Situation();
        
        situation.getContext().add(new Context(Context.Type.Conversation, TEST_CONVERSATION));
        situation.getSituationProperties().put(TEST_PROPERTY, TEST_PROPERTY_VALUE);
        situation.getActivityTypeIds().add(new ActivityTypeId(TEST_ACTIVITY_TYPE_ID, TEST_ACTIVITY_TYPE_ID_INDEX));

        situation.setDescription(TEST_DESCRIPTION);
        situation.setId(TEST_ID);
        situation.setSeverity(Severity.Critical);
        situation.setSubject(TEST_SUBJECT);
        situation.setTimestamp(TEST_TIMESTAMP);
        situation.setType(TEST_TYPE);
        
        Situation result=null;
        
        try {
            byte[] b=SituationUtil.serializeSituation(situation);
            
            result = SituationUtil.deserializeSituation(b);
            
        } catch (Exception e) {
            fail("Failed to serialize/deserialize: "+e);
        }
        
        assertNotNull(result);
        
        assertEquals("Context size", situation.getContext().size(), result.getContext().size());
        assertEquals("Context value", situation.getContext().iterator().next(), result.getContext().iterator().next());
        
        assertEquals("Properties size", situation.getSituationProperties().size(), result.getSituationProperties().size());
        assertEquals("Properties value", situation.getSituationProperties().get(TEST_PROPERTY), result.getSituationProperties().get(TEST_PROPERTY));

        assertEquals("ActivityTypeIds size", situation.getActivityTypeIds().size(), result.getActivityTypeIds().size());
        assertEquals("ActivityTypeIds value", situation.getActivityTypeIds().iterator().next(), result.getActivityTypeIds().iterator().next());

        assertEquals("Description", situation.getDescription(), result.getDescription());
        assertEquals("Id", situation.getId(), result.getId());
        assertEquals("Severity", situation.getSeverity(), result.getSeverity());
        assertEquals("Subject", situation.getSubject(), result.getSubject());
        assertEquals("Timestamp", situation.getTimestamp(), result.getTimestamp());
        assertEquals("Type", situation.getType(), result.getType());
    
    }
}
