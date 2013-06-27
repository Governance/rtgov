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
package org.overlord.rtgov.ep.mail;

import static org.junit.Assert.*;

import org.junit.Test;
import org.overlord.rtgov.ep.mail.MailEventProcessor;

public class MailEventProcessorTest {

    private static final String THE_EVENT = "<THE EVENT>";
    private static final String SOURCE_VALUE = "sourceValue";

    @Test
    public void testValidateOk() {
        MailEventProcessor ep=new MailEventProcessor();
        
        ep.setFrom("gbrown@redhat.com");
        ep.getTo().add("gbrown@redhat.com");
        ep.setSubjectScript("script/Subject.mvel");
        
        try {
            ep.initSubjectScript();
        } catch(Exception e) {
            fail("Failed to init subject: "+e);
        }
        
        try {
            ep.validate();
        } catch (Exception e) {
            fail("Failed to validate: "+e);
        }
    }
    
    @Test
    public void testValidateFailFrom() {
        MailEventProcessor ep=new MailEventProcessor();
        
        //ep.setFrom("gbrown@redhat.com");
        ep.getTo().add("gbrown@redhat.com");
        ep.setSubjectScript("script/Subject.mvel");
        
        try {
            ep.initSubjectScript();
        } catch(Exception e) {
            fail("Failed to init subject: "+e);
        }
        
        try {
            ep.validate();
            fail("Should have failed to validate");
        } catch (Exception e) {
            if (!e.getMessage().equals(java.util.PropertyResourceBundle.getBundle(
                    "ep-mail.Messages").getString("EP-MAIL-1"))) {
                fail("Incorrect message: "+e.getMessage());
            }
        }
    }
    
    @Test
    public void testValidateFailTo() {
        MailEventProcessor ep=new MailEventProcessor();
        
        ep.setFrom("gbrown@redhat.com");
        //ep.getTo().add("gbrown@redhat.com");
        ep.setSubjectScript("script/Subject.mvel");
        
        try {
            ep.initSubjectScript();
        } catch(Exception e) {
            fail("Failed to init subject: "+e);
        }
        
        try {
            ep.validate();
            fail("Should have failed to validate");
        } catch (Exception e) {
            if (!e.getMessage().equals(java.util.PropertyResourceBundle.getBundle(
                    "ep-mail.Messages").getString("EP-MAIL-1"))) {
                fail("Incorrect message: "+e.getMessage());
            }
        }
    }
    
    @Test
    public void testValidateFailSubject() {
        MailEventProcessor ep=new MailEventProcessor();
        
        ep.setFrom("gbrown@redhat.com");
        ep.getTo().add("gbrown@redhat.com");
        ep.setSubjectScript("script/Subject.mvel"); // Script name but not initialized to produce compile version
        
        try {
            ep.validate();
            fail("Should have failed to validate");
        } catch (Exception e) {
            if (!e.getMessage().equals(java.util.PropertyResourceBundle.getBundle(
                    "ep-mail.Messages").getString("EP-MAIL-1"))) {
                fail("Incorrect message: "+e.getMessage());
            }
        }
    }
    
    @Test
    public void testSubject() {
        MailEventProcessor ep=new MailEventProcessor();
        
        ep.setFrom("gbrown@redhat.com");
        ep.getTo().add("gbrown@redhat.com");
        ep.setSubjectScript("script/Subject.mvel");
        ep.setContentScript("script/Content.mvel");
        
        try {
            ep.initSubjectScript();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        try {
            String subject=ep.createSubject(SOURCE_VALUE, THE_EVENT, 1);
            
            if (subject == null) {
                fail("No subject returned");
            }
            
            if (!subject.equals("Processed event from source '"+SOURCE_VALUE+"'")) {
                fail("Unexpected subject: "+subject);
            }
        } catch (Exception e) {
            fail("Failed to test subject: "+e);
        }
    }
    
    @Test
    public void testContent() {
        MailEventProcessor ep=new MailEventProcessor();
        
        ep.setContentScript("script/Content.mvel");
        
        try {
            ep.initContentScript();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        try {
            String content=ep.createContent(SOURCE_VALUE, THE_EVENT, 1);
            
            if (content == null) {
                fail("No content returned");
            }
            
            if (!content.equals("Processed the event: "+THE_EVENT)) {
                fail("Unexpected content: "+content);
            }
        } catch (Exception e) {
            fail("Failed to test content: "+e);
        }
    }

}
