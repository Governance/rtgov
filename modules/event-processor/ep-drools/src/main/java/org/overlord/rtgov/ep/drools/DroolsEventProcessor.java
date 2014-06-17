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
package org.overlord.rtgov.ep.drools;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.internal.ep.DefaultEPContext;

/**
 * This class represents the Drools implementation of the Event
 * Processor.
 *
 */
public class DroolsEventProcessor extends EventProcessor {

    private static final Logger LOG=Logger.getLogger(DroolsEventProcessor.class.getName());

    private DefaultEPContext _context=null;

    private KieSession _session=null;
    private String _ruleName=null;
    private static java.util.concurrent.atomic.AtomicInteger _count=new java.util.concurrent.atomic.AtomicInteger();

    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        _context = new DefaultEPContext(getServices());
        
        _session = createSession();
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("DroolsEventProcessor init: ruleName="+_ruleName+" session="+_session);
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

        synchronized (this) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Process event '"+event+" from source '"+source
                        +"' on Drools Event Processor '"+getRuleName()
                        +"'");
            }

            _context.handle(null);

            // Get entry point
            // TODO: If not simple lookup, then may want to cache this
            EntryPoint entryPoint=_session.getEntryPoint(source);

            if (entryPoint != null) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Insert event '"+event+" from source '"+source
                            +"' on Drools Event Processor '"+getRuleName()
                            +"' into entry point "+entryPoint);
                }

                entryPoint.insert(event);

                // TODO: Not sure if possible to delay evaluation, until after
                // all events in batch have been processed/inserted - but then
                // how to trace the individual results??
                _session.fireAllRules();

            } else {
                String mesg=MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "ep-drools.Messages").getString("EP-DROOLS-3"),
                        source, getRuleName());
                
                LOG.severe(mesg);
                
                throw new Exception(mesg);
            }

            ret = (java.io.Serializable)_context.getResult();

            if (ret instanceof Exception) {
                throw (Exception)ret;
            }
        }
        
        return ret;
    }

    /**
     * This method creates a stateful knowledge session per
     * Drools event processor.
     * 
     * @return The stateful knowledge session
     * @throws Exception Failed to create session
     */
    private KieSession createSession() throws Exception {
        KieSession ret=null;
        
        KieBase kbase = loadRuleBase();
        
        if (kbase != null) {
            ret = kbase.newKieSession();

            if (ret != null) {
                ret.setGlobal("epc", _context);
                ret.fireAllRules();
            } else {
                String mesg=MessageFormat.format(
                        java.util.PropertyResourceBundle.getBundle(
                        "ep-drools.Messages").getString("EP-DROOLS-2"),
                        getRuleName());
                
                LOG.severe(mesg);
                
                throw new Exception(mesg);
            }
        }

        return (ret);
    }
    
    /**
     * This method loads the rule base associated with the Drools
     * event processor.
     * 
     * @return The knowledge base
     * @throws Exception Failed to load rule base
     */
    private KieBase loadRuleBase() throws Exception {
        String droolsRuleBase=getRuleName()+".drl";

        try {
            KieServices kieServices = KieServices.Factory.get();
            ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.overlord.rtgov.tmp", getRuleName(),
                                    String.valueOf(_count.getAndIncrement()));
            KieFileSystem kieFileSystem = kieServices.newKieFileSystem().generateAndWritePomXML(releaseId);

            kieFileSystem.write(kieServices.getResources().newClassPathResource(droolsRuleBase));

            KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
            Results results = kieBuilder.getResults();
            
            if (results.hasMessages(Message.Level.ERROR)) {
                StringBuffer buf=new StringBuffer();
                for (Message message : results.getMessages(Message.Level.ERROR)) {
                    buf.append("ERROR: "+message.toString().trim()+"\r\n");
                }
                throw new Exception(buf.toString());
            }

            KieContainer kieContainer = kieServices.newKieContainer(releaseId);
            return kieContainer.getKieBase();

        } catch (Throwable e) {
            String mesg=MessageFormat.format(
                    java.util.PropertyResourceBundle.getBundle(
                    "ep-drools.Messages").getString("EP-DROOLS-1"),
                    droolsRuleBase, getRuleName());
            
            LOG.log(Level.SEVERE, mesg, e);
            
            throw new Exception(mesg, e);
        }
    }

}
