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
package org.overlord.rtgov.ep.cep;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.KieBase;
import org.kie.KieServices;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieModuleModel;
import org.kie.builder.KieRepository;
import org.kie.conf.EqualityBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.KieContainer;
import org.kie.runtime.KieSession;
import org.kie.runtime.rule.SessionEntryPoint;
import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.internal.ep.DefaultEPContext;

/**
 * This class represents the CEP implementation of the Event
 * Processor.
 *
 */
public class CEPEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(CEPEventProcessor.class.getName());

    private DefaultEPContext _context=null; //new DefaultEPNContext();

    private static final java.util.Map<String,KieSession> SESSIONS=
                new java.util.HashMap<String,KieSession>();
    private KieSession _session=null;
    private String _ruleName=null;

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        _context = new DefaultEPContext(getServices());
        
        _session = createSession();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("CEPEventProcessor init: ruleName="+_ruleName+" session="+_session);
        }
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
    	java.io.Serializable ret=null;

    	synchronized(this) {
	        if (LOG.isLoggable(Level.FINEST)) {
	            LOG.finest("Process event '"+event+" from source '"+source
	                    +"' on CEP Event Processor '"+getRuleName()
	                    +"'");
	        }
	
	        _context.handle(null);
	        
	        // Get entry point
	        // TODO: If not simple lookup, then may want to cache this
	        SessionEntryPoint entryPoint=_session.getEntryPoint(source);
	        
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
	        
	        ret = (java.io.Serializable)_context.getResult();
    	}
        
        return ret;
    }

    /**
     * This method creates a stateful knowledge session per
     * CEP event processor.
     * 
     * @return The stateful knowledge session
     */
    private KieSession createSession() {
        KieSession ret=null;
        
        synchronized (SESSIONS) {
            ret = SESSIONS.get(getRuleName());
            
            if (ret == null) {              
                KieBase kbase = loadRuleBase();
                
                if (kbase != null) {
                    ret = kbase.newKieSession();

                    if (ret != null) {
                        ret.setGlobal("epc", _context);
                        ret.fireAllRules();
                        
                        SESSIONS.put(getRuleName(), ret);
                    } else {
                        System.out.println("The kieSession is null!!!");
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
    private KieBase loadRuleBase() {
        String cepRuleBase=getRuleName()+".drl";

        try {
            KieServices ks = KieServices.Factory.get();
            KieRepository kr = ks.getRepository();

            KieModuleModel kmm = ks.newKieModuleModel();
            KieBaseModel kbm = kmm.newKieBaseModel(getRuleName())
                        .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                        .setEventProcessingMode(EventProcessingOption.STREAM);
            kbm.setDefault(true);

            KieFileSystem kfs = ks.newKieFileSystem();
            kfs.write("src/main/resources/" + kbm.getName() + "/rule1.drl", ks.getResources().newClassPathResource(cepRuleBase));
            kfs.writeKModuleXML(kmm.toXML());

            KieBuilder kb = ks.newKieBuilder(kfs);
            kb.buildAll();

            KieContainer container = ks.newKieContainer(kr.getDefaultReleaseId());
            KieBase kbase = container.getKieBase();
            //TODO: hack it for now, this attribute should be supported in the following drools6 release.
            System.setProperty("kie.mbean", "enabled");
            return kbase;

        } catch (Throwable e) {
            LOG.log(Level.SEVERE, MessageFormat.format(
                            java.util.PropertyResourceBundle.getBundle(
                            "ep-cep.Messages").getString("EP-CEP-2"),
                            cepRuleBase, getRuleName()), e);
        }

        return (null);
    }

}
