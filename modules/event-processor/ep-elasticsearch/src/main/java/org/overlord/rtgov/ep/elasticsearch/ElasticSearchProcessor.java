/*
 * 2012-4 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.overlord.rtgov.ep.elasticsearch;

import org.mvel2.MVEL;
import org.overlord.rtgov.common.service.KeyValueStore;
import org.overlord.rtgov.ep.DefaultEPContext;
import org.overlord.rtgov.ep.EventProcessor;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ElasticSearch event processor implementation.
 */
public class ElasticSearchProcessor extends EventProcessor {
    private static final Logger LOG = Logger.getLogger(ElasticSearchProcessor.class.getName());

    private static final String KEY_VALUE_STORE = "KeyValueStore";
    
    private DefaultEPContext _context = null;
    private KeyValueStore _keyValueStore = null;
    private String _script = null;
    private Object _scriptExpression = null;
    
    // TODO
    //private String correleationScript = null;

    /**
     * This method returns the script expression.
     * 
     * @return The script expression
     */
    public Object getScriptExpression() {
        return _scriptExpression;
    }

    /**
     * This method returns the script expression.
     * 
     * @param scriptExpression The script expression
     */
    public void setScriptExpression(Object scriptExpression) {
        _scriptExpression = scriptExpression;
    }

    /**
     * This method returns the script.
     * 
     * @return The script
     */
    public String getScript() {
        return _script;
    }

    /**
     * This method sets the script.
     * 
     * @param script The script
     */
    public void setScript(String script) {
        _script = script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();
        if (_script != null) {

            // Load the script
            java.io.InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(_script);

            if (is == null) {
                throw new Exception("Unable to locate MVEL script '" + _script + "'");
            } else {
                byte[] b = new byte[is.available()];
                is.read(b);
                is.close();

                // Compile expression
                _scriptExpression = MVEL.compileExpression(new String(b));

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Initialized script=" + _script
                            + " compiled=" + _scriptExpression);
                }
            }
        } else {
            _scriptExpression = MVEL.compileExpression("java.lang.String t = (java.util.UUID.randomUUID().toString());\n"
                        + "t;");
        }

        _context = new DefaultEPContext(getServices());
        
        /**
         * expect type SimpleDocumentRepo;
         */
        _keyValueStore = (KeyValueStore) getServices().get(KEY_VALUE_STORE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable process(String source, Serializable event, int retriesLeft) throws Exception {
        //   if (!getScript().equals("NONE")) {
        return process(source, event, retriesLeft, processMvel(source, event, retriesLeft));
        //   } else {
        //     return process(source,  event, retriesLeft, getRandom());
        //   }


    }

    /**
     * This method processes the event using the MVEL script.
     * 
     * @param source The event source
     * @param event The event
     * @param retriesLeft Number of retries remaining
     * @return The processed event
     */
    private String processMvel(String source, Serializable event, int retriesLeft) {

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '" + event + " from source '" + source
                    + "' on MVEL Event Processor '" + getScript()
                    + "'");
        }

        if (_scriptExpression != null) {
            java.util.Map<String, Object> vars =
                    new java.util.HashMap<String, Object>();

            vars.put("source", source);
            vars.put("event", event);
            vars.put("retriesLeft", retriesLeft);
            vars.put("epc", _context);

            synchronized (this) {
                _context.handle(null);

                return (String) MVEL.executeExpression(_scriptExpression, vars);
            }
        }

        return null;
    }

    /**
     * This method processes the inbound event and stores it associated with
     * the supplied key (id).
     * 
     * @param source The source
     * @param event The event
     * @param retriesLeft The number of retrieves remaining
     * @param id The id
     * @return The event
     * @throws Exception Failed to store the event
     */
    public Serializable process(String source, Serializable event, int retriesLeft, String id) throws Exception {

        _keyValueStore.add(id, event);

        return event;
    }

    /**
     * This method generates a random string.
     * 
     * @return The random string
     */
    protected String getRandom() {
        return (UUID.randomUUID().toString());
    }
}
