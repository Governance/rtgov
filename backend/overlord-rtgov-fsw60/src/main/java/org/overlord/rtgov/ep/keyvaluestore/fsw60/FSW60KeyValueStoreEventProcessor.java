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
package org.overlord.rtgov.ep.keyvaluestore.fsw60;

import org.mvel2.MVEL;
import org.overlord.rtgov.common.service.KeyValueStore;
import org.overlord.rtgov.ep.EventProcessor;
import org.overlord.rtgov.internal.ep.DefaultEPContext;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The generic Keyvalue event processor implementation.
 */
public class FSW60KeyValueStoreEventProcessor extends EventProcessor {
    private static final Logger LOG = Logger.getLogger(FSW60KeyValueStoreEventProcessor.class.getName());

    private static final String KEY_VALUE_STORE = "KeyValueStore";

    private DefaultEPContext _context = null;
    private KeyValueStore _keyValueStore = null;
    private String _idScript = null;
    private Object _idScriptExpression = null;

    /**
     * This method returns the script responsible for identifying
     * the id.
     *
     * @return The ID script
     */
    public String getIdScript() {
        return _idScript;
    }

    /**
     * This method sets the script responsible for identifying
     * the id.
     *
     * @param script The ID script
     */
    public void setIdScript(String script) {
        _idScript = script;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() throws Exception {
        super.init();

        if (_idScript != null) {

            // Load the script
            java.io.InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(_idScript);

            if (is == null) {
                throw new Exception("Unable to locate MVEL script '" + _idScript + "'");
            } else {
                byte[] b = new byte[is.available()];
                is.read(b);
                is.close();

                // Compile expression
                _idScriptExpression = MVEL.compileExpression(new String(b));

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Initialized script=" + _idScript
                            + " compiled=" + _idScriptExpression);
                }
            }
        } else {
            _idScriptExpression = null;
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
        return process(source, event, retriesLeft, determineId(source, event, retriesLeft));
    }

    /**
     * This method processes the event using the MVEL script.
     *
     * @param source The event source
     * @param event The event
     * @param retriesLeft Number of retries remaining
     * @return The id generated from the source event of if no mvel script defined then returns  random id.
     */
    private String determineId(String source, Serializable event, int retriesLeft) {

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '" + event + " from source '" + source
                    + "' on MVEL Event Processor '" + getIdScript()
                    + "'");
        }

        if (_idScriptExpression != null) {
            java.util.Map<String, Object> vars =
                    new java.util.HashMap<String, Object>();

            vars.put("source", source);
            vars.put("event", event);
            vars.put("retriesLeft", retriesLeft);
            vars.put("epc", _context);

            synchronized (this) {
                _context.handle(null);

                return (String) MVEL.executeExpression(_idScriptExpression, vars);
            }
        } else {
            return  getRandom();
        }

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
        if (id == null) {
            throw new Exception(MessageFormat.format(java.util.PropertyResourceBundle.getBundle(
                        "ep-keyvaluestore.Messages").getString("EP-KEYVALUE-1"), event));
        }
        
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
