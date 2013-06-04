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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mvel2.MVEL;
import org.overlord.rtgov.ep.EventProcessor;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;

/**
 * This class represents the JavaMail implementation of the Event
 * Processor.
 *
 */
public class MailEventProcessor extends EventProcessor {

    private static final String DEFAULT_JNDI_NAME = "java:jboss/mail/Default";

    private static final Logger LOG=Logger.getLogger(MailEventProcessor.class.getName());

    private Session _session=null;
    
    private String _jndiName=DEFAULT_JNDI_NAME;
    
    private java.util.List<String> _to=new java.util.ArrayList<String>();
    private String _from=null;
    
    private String _subjectScript=null;
    private Object _subjectScriptExpression=null;

    private String _contentScript=null;
    private Object _contentScriptExpression=null;
    
    private String _contentType="text/plain";

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        super.init();
        
        // Obtain JavaMail session
        InitialContext context=new InitialContext();
        _session = (Session)context.lookup(_jndiName);
        
        // Initialize scripts
        initSubjectScript();
        initContentScript();
        
        // Validate
        validate();
    }
    
    /**
     * This method validates that the event processor has the required
     * configuration.
     * 
     * @throws Exception
     */
    protected void validate() throws Exception {
        // Validate that the minimum number of fields are available
        if (_to == null || _to.size() == 0 || _from == null
                || _from.trim().length() == 0 || _subjectScriptExpression == null) {
            throw new RuntimeException(java.util.PropertyResourceBundle.getBundle(
                    "ep-mail.Messages").getString("EP-MAIL-1"));
        }
    }
    
    /**
     * Initialize the subject script.
     * 
     * @throws Exception Failed to initialize
     */
    protected void initSubjectScript() throws Exception {
        
        // Load the subject script
        java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_subjectScript);
        
        if (is == null) {
            throw new Exception("Unable to locate MVEL script '"+_subjectScript+"'");
        } else {
            byte[] b=new byte[is.available()];
            is.read(b);
            is.close();

            // Compile expression
            _subjectScriptExpression = MVEL.compileExpression(new String(b));

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Initialized subject script="+_subjectScript
                        +" compiled="+_subjectScriptExpression);
            }
        }
        
    }
    
    /**
     * Initialize the content script.
     * 
     * @throws Exception Failed to initialize
     */
    protected void initContentScript() throws Exception {
        
        // Load the content script
        java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(_contentScript);
        
        if (is == null) {
            throw new Exception("Unable to locate MVEL script '"+_contentScript+"'");
        } else {
            byte[] b=new byte[is.available()];
            is.read(b);
            is.close();

            // Compile expression
            _contentScriptExpression = MVEL.compileExpression(new String(b));

            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Initialized content script="+_contentScript
                        +" compiled="+_contentScriptExpression);
            }
        }
    }
    
    /**
     * This method gets the JNDI name for the mail session.
     * 
     * @return The JNDI name
     */
    public String getJNDIName() {
        return (_jndiName);
    }
    
    /**
     * This method set the JNDI name for the mail session.
     * 
     * @param jndiName The JNDI name
     */
    public void setJNDIName(String jndiName) {
        _jndiName = jndiName;
    }
    
    /**
     * This method gets the 'to' email addresses.
     * 
     * @return The 'to' email addresses
     */
    public java.util.List<String> getTo() {
        return (_to);
    }
    
    /**
     * This method set the 'to' email addresses.
     * 
     * @param to The 'to' email addresses
     */
    public void setTo(java.util.List<String> to) {
        _to = to;
    }
    
    /**
     * This method gets the 'from' email address.
     * 
     * @return The 'from' email address
     */
    public String getFrom() {
        return (_from);
    }
    
    /**
     * This method set the 'from' email address.
     * 
     * @param from The 'from' email address
     */
    public void setFrom(String from) {
        _from = from;
    }
    
    /**
     * This method returns the subject script.
     * 
     * @return The subject script
     */
    public String getSubjectScript() {
        return (_subjectScript);
    }
    
    /**
     * This method sets the subject script.
     * 
     * @param script The subject script
     */
    public void setSubjectScript(String script) {
        _subjectScript = script;
    }
    
    /**
     * This method returns the content script.
     * 
     * @return The content script
     */
    public String getContentScript() {
        return (_contentScript);
    }
    
    /**
     * This method sets the content script.
     * 
     * @param script The content script
     */
    public void setContentScript(String script) {
        _contentScript = script;
    }
    
    /**
     * This method returns the content type.
     * 
     * @return The content type
     */
    public String getContentType() {
        return (_contentType);
    }
    
    /**
     * This method sets the content type.
     * 
     * @param type The content type
     */
    public void setContentType(String type) {
        _contentType = type;
    }
    
    /**
     * This method creates the mail subject from the supplied information.
     * 
     * @param source The source
     * @param event The event
     * @param retriesLeft The retries left
     * @return The subject
     * @throws Exception Failed to create subject
     */
    protected String createSubject(String source,
            java.io.Serializable event, int retriesLeft) throws Exception {
        java.util.Map<String,Object> vars=
                new java.util.HashMap<String, Object>();
        
        vars.put("source", source);
        vars.put("event", event);
        vars.put("retriesLeft", retriesLeft);
        
        return ((String)MVEL.executeExpression(_subjectScriptExpression, vars));
    }
    
    /**
     * This method creates the mail content from the supplied information.
     * 
     * @param source The source
     * @param event The event
     * @param retriesLeft The retries left
     * @return The content, or null if no expression
     * @throws Exception Failed to create subject
     */
    protected String createContent(String source,
            java.io.Serializable event, int retriesLeft) throws Exception {
        
        if (_contentScriptExpression == null) {
            return (null);
        }
        
        java.util.Map<String,Object> vars=
                new java.util.HashMap<String, Object>();
        
        vars.put("source", source);
        vars.put("event", event);
        vars.put("retriesLeft", retriesLeft);
        
        return ((String)MVEL.executeExpression(_contentScriptExpression, vars));
    }
    
    /**
     * {@inheritDoc}
     */
    public java.io.Serializable process(String source,
                java.io.Serializable event, int retriesLeft) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '"+event+"' from source '"+source
                    +"' on Mail Event Processor with subject script '"+getSubjectScript()
                    +"' to '"+_to+"' from '"+_from+"'");
        }

        if (_to != null  && _to.size() > 0 && _from != null && _subjectScriptExpression != null) {
            String subject=createSubject(source, event, retriesLeft);
            
            String content=createContent(source, event, retriesLeft);
            
            // Construct the email
            Message message = new MimeMessage(_session);
            message.setFrom(new InternetAddress(_from));
            
            for (String to : _to) {
                Address toAddress = new InternetAddress(to);
                message.addRecipient(Message.RecipientType.TO, toAddress);
            }
            
            message.setSubject(subject);
            
            if (content != null) {
                message.setContent(content, _contentType);
            }
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Send email with subject '"+subject+"' and content: "+content);
            }

            Transport.send(message);
        }

        return (null);
    }

}
