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
 * .
 * User: imk@redhat.com
 * Date: 19/03/14
 * Time: 22:11
 */
public class ElasticSearchProcessor extends EventProcessor {
    private static final Logger LOG = Logger.getLogger(ElasticSearchProcessor.class.getName());

    private static final String KEY_VALUE_STORE = "KeyValueStore";
    protected DefaultEPContext _context = null;
    private KeyValueStore keyValueStore = null;
    private String script = null;
    private Object scriptExpression = null;
    // TODO
    private String correleationScript = null;

    public Object getScriptExpression() {
        return scriptExpression;
    }

    public void setScriptExpression(Object scriptExpression) {
        this.scriptExpression = scriptExpression;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    @Override
    public void init() throws Exception {
        super.init();
        if (script != null) {

            // Load the script
            java.io.InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(script);

            if (is == null) {
                throw new Exception("Unable to locate MVEL script '" + script + "'");
            } else {
                byte[] b = new byte[is.available()];
                is.read(b);
                is.close();

                // Compile expression
                scriptExpression = MVEL.compileExpression(new String(b));

                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Initialized script=" + script
                            + " compiled=" + scriptExpression);
                }
            }
        } else {
            scriptExpression = MVEL.compileExpression("java.lang.String t = (java.util.UUID.randomUUID().toString());\n" +
                    "t;");
        }

        _context = new DefaultEPContext(getServices());
        /**
         * expect type SimpleDocumentRepo;
         */
        keyValueStore = (KeyValueStore) getServices().get(KEY_VALUE_STORE);
    }

    @Override
    public Serializable process(String source, Serializable event, int retriesLeft) throws Exception {
        //   if (!getScript().equals("NONE")) {
        return process(source, event, retriesLeft, processMvel(source, event, retriesLeft));
        //   } else {
        //     return process(source,  event, retriesLeft, getRandom());
        //   }


    }

    private String processMvel(String source, Serializable event, int retriesLeft) {
        java.io.Serializable ret = null;

        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process event '" + event + " from source '" + source
                    + "' on MVEL Event Processor '" + getScript()
                    + "'");
        }

        if (scriptExpression != null) {
            java.util.Map<String, Object> vars =
                    new java.util.HashMap<String, Object>();

            vars.put("source", source);
            vars.put("event", event);
            vars.put("retriesLeft", retriesLeft);
            vars.put("epc", _context);

            synchronized (this) {
                _context.handle(null);

                return (String) MVEL.executeExpression(scriptExpression, vars);

            }

        }

        return null;
    }

    /**
     * @return
     */
    public Serializable process(String source, Serializable event, int retriesLeft, String id) throws Exception {

        keyValueStore.add(id, event);

        return event;
    }


    protected String getRandom() {
        return (UUID.randomUUID().toString());
    }
}
