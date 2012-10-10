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
package org.overlord.bam.epn.cep;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.conf.MBeansOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.overlord.bam.epn.EventProcessor;
import org.overlord.bam.internal.epn.DefaultEPNContext;

/**
 * This class represents the CEP implementation of the Event
 * Processor.
 *
 */
public class CEPEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(CEPEventProcessor.class.getName());

    private DefaultEPNContext _context=null; //new DefaultEPNContext();

    private static final java.util.Map<String,StatefulKnowledgeSession> SESSIONS=
                new java.util.HashMap<String,StatefulKnowledgeSession>();
    private StatefulKnowledgeSession _session=null;
    private String _ruleName=null;

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        _context = new DefaultEPNContext(getServices());
        
        _session = createSession();
    }
    
    /**
     * This method returns the rule name.
     * 
     * @return The rule name
     */
    public String getRuleName() {
        return (_ruleName);
    }
    
    /**
     * This method sets the rule name.
     * 
     * @param ruleName The rule name
     */
    public void setRuleName(String ruleName) {
        _ruleName = ruleName;
    }
    
    /**
     * {@inheritDoc}
     */
    public java.io.Serializable process(String source,
                java.io.Serializable event, int retriesLeft) throws Exception {

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '"+event+" from source '"+source
                    +"' on CEP Event Processor '"+getRuleName()
                    +"'");
        }

        _context.forward(null);
        
        // Get entry point
        // TODO: If not simple lookup, then may want to cache this
        WorkingMemoryEntryPoint entryPoint=_session.getWorkingMemoryEntryPoint(source);
        
        if (entryPoint != null) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Insert event '"+event+" from source '"+source
                        +"' on CEP Event Processor '"+getRuleName()
                        +"' into entry point "+entryPoint);
            }
            
            entryPoint.insert(event);
            
            // TODO: Not sure if possible to delay evaluation, until after
            // all events in batch have been processed/inserted - but then
            // how to trace the individual results??
            _session.fireAllRules();
            
        } else if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("No entry point for source Event Processor '"+source
                    +"' on CEP Event Processor '"+getRuleName()+"'");
        }
        
        return (java.io.Serializable)_context.getResult();
    }

    /**
     * This method creates a stateful knowledge session per
     * CEP event processor.
     * 
     * @return The stateful knowledge session
     */
    private StatefulKnowledgeSession createSession() {
        StatefulKnowledgeSession ret=null;
        
        synchronized (SESSIONS) {
            ret = SESSIONS.get(getRuleName());
            
            if (ret == null) {              
                KnowledgeBase kbase = loadRuleBase();
                
                if (kbase != null) {
                    ret = kbase.newStatefulKnowledgeSession();

                    if (ret != null) {
                        ret.setGlobal("epn", _context);
                        ret.fireAllRules();
                        
                        SESSIONS.put(getRuleName(), ret);
                    }
                }
            }
        }

        return (ret);
    }
    
    /**
     * This method loads the rule base associated with the CEP
     * event processor.
     * 
     * @return The knowledge base
     */
    private KnowledgeBase loadRuleBase() {
        String cepRuleBase=getRuleName()+".drl";

        try {
            KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            
            java.io.InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("/"+cepRuleBase);
            
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(cepRuleBase);
            }

            builder.add(ResourceFactory.newInputStreamResource(is),
                    ResourceType.determineResourceType(cepRuleBase));
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Loaded CEP rules '"+cepRuleBase+"'");     
            }

            if (builder.hasErrors()) {
                LOG.severe(MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "epn-cep.Messages").getString("EPN-CEP-1"),
                        builder.getErrors()));
            } else {
                KnowledgeBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
                conf.setOption(EventProcessingOption.STREAM);
                conf.setOption(MBeansOption.ENABLED);
                KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(getRuleName(), conf);
                kbase.addKnowledgePackages(builder.getKnowledgePackages());
                return kbase;
            }
        
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "epn-cep.Messages").getString("EPN-CEP-2"),
                            cepRuleBase, getRuleName()), e);
        }

        return (null);
    }

}
