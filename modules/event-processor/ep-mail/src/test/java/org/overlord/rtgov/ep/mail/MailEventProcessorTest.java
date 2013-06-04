/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-11, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
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
