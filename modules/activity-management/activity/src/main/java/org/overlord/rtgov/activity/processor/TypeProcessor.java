/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.activity.processor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.Context;

/**
 * This class is responsible for processing a type of information.
 *
 */
public class TypeProcessor {
    
    private static final Logger LOG=Logger.getLogger(TypeProcessor.class.getName());

    private InformationTransformer _transformer=null;
    
    private java.util.List<ContextEvaluator> _contextEvaluators=
            new java.util.ArrayList<ContextEvaluator>();
    
    private java.util.List<PropertyEvaluator> _propertyEvaluators=
            new java.util.ArrayList<PropertyEvaluator>();
    
    private ScriptEvaluator _scriptEvaluator=null;
    
    /**
     * Initialize the type processor.
     * 
     * @throws Exception Failed to initialize
     */
    public void init() throws Exception {
        if (_transformer != null) {
            _transformer.init();
        }
        
        for (ContextEvaluator ce : _contextEvaluators) {
            ce.getEvaluator().init();
        }
        
        for (PropertyEvaluator pe : _propertyEvaluators) {
            pe.getEvaluator().init();
        }
        
        if (_scriptEvaluator != null) {
            _scriptEvaluator.init();
        }
    }
    
    /**
     * This method returns the information transformer used
     * to derived the public representation of the information.
     * 
     * @return The public representation's transformer
     */
    public InformationTransformer getTransformer() {
        return (_transformer);    
    }
    
    /**
     * This method sets the information transformer used
     * to derived the public representation of the information.
     * 
     * @param transformer The public representation's transformer
     */
    public void setTransformer(InformationTransformer transformer) {
        _transformer = transformer;    
    }
    
    /**
     * This method returns the context evaluators.
     * 
     * @return The context evaluators
     */
    public java.util.List<ContextEvaluator> getContexts() {
        return (_contextEvaluators);
    }
    
    /**
     * This method sets the context evaluators.
     * 
     * @param ce The context evaluators
     */
    public void setContexts(java.util.List<ContextEvaluator> ce) {
        _contextEvaluators = ce;
    }
    
    /**
     * This method returns the property evaluators.
     * 
     * @return The property evaluators
     */
    public java.util.List<PropertyEvaluator> getProperties() {
        return (_propertyEvaluators);
    }
    
    /**
     * This method sets the property evaluators.
     * 
     * @param pe The property evaluators
     */
    public void setProperties(java.util.List<PropertyEvaluator> pe) {
        _propertyEvaluators = pe;
    }
    
    /**
     * This method returns the script evaluator.
     * 
     * @return The script evaluator
     */
    public ScriptEvaluator getScript() {
        return (_scriptEvaluator);
    }
    
    /**
     * This method sets the script evaluator.
     * 
     * @param script The script evaluator
     */
    public void setScript(ScriptEvaluator script) {
        _scriptEvaluator = script;
    }
    
    /**
     * This method processes the supplied information to extract the
     * context and property details for association with the supplied
     * activity type.
     * 
     * @param information The information
     * @param actType The activity type
     * @return The representation of the information to be made public
     */
    public String process(Object information, ActivityType actType) {
        String ret=null;
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Process information: "+information);
        }
        
        for (int i=0; i < _contextEvaluators.size(); i++) {
            ContextEvaluator ce=_contextEvaluators.get(i);

            String val=ce.getEvaluator().evaluate(information);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Context evaluator '"+ce+"' = "+val);
            }
            
            if (val != null) {
                actType.getContext().add(new Context(ce.getType(), val));
            }
        }
        
        for (int i=0; i < _propertyEvaluators.size(); i++) {
            PropertyEvaluator pe=_propertyEvaluators.get(i);

            String val=pe.getEvaluator().evaluate(information);
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Property evaluator '"+pe+"' = "+val);
            }
            
            if (val != null) {
                actType.getProperties().put(pe.getName(), val);
            }
        }
        
        if (getScript() != null) {
            getScript().evaluate(information, actType);
        }
        
        if (getTransformer() != null) {
            ret = getTransformer().transform(information);
        }
        
        return (ret);
    }
    
    /**
     * Close the type processor.
     * 
     * @throws Exception Failed to close
     */
    public void close() throws Exception {
        if (_transformer != null) {
            _transformer.close();
        }
        
        for (ContextEvaluator ce : _contextEvaluators) {
            ce.getEvaluator().close();
        }
        
        for (PropertyEvaluator pe : _propertyEvaluators) {
            pe.getEvaluator().close();
        }
        
        if (_scriptEvaluator != null) {
            _scriptEvaluator.close();
        }
    }

    /**
     * This class identifies the property name to be associated
     * with the value extracted using the expression evaluator.
     */
    public static class PropertyEvaluator {
        
        private String _name=null;
        private ExpressionEvaluator _evaluator=null;
        
        /**
         * The default constructor for the property evaluator.
         */
        public PropertyEvaluator() {
        }
        
        /**
         * This method gets the property name.
         * 
         * @return The property name
         */
        public String getName() {
            return (_name);
        }
        
        /**
         * This method sets the property name.
         * 
         * @param name The property name
         */
        public void setName(String name) {
            _name = name;
        }
        
        /**
         * This method gets the context value
         * expression evaluator.
         * 
         * @return The context value expression evaluator
         */
        public ExpressionEvaluator getEvaluator() {
            return (_evaluator);
        }
        
        /**
         * This method sets the context value's
         * expression evaluator.
         * 
         * @param evaluator The expression evaluator
         */
        public void setEvaluator(ExpressionEvaluator evaluator) {
            _evaluator = evaluator;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            return ("[ name="+_name+" expression="+_evaluator.getExpression()+" ]");
        }
    }
    
    /**
     * This class identifies the context type to be associated
     * with the value extracted using the expression evaluator.
     */
    public static class ContextEvaluator {
        
        private Context.Type _type=null;
        private ExpressionEvaluator _evaluator=null;
        
        /**
         * The default constructor for the context evaluator.
         */
        public ContextEvaluator() {
        }
        
        /**
         * This method gets the context type.
         * 
         * @return The context type
         */
        public Context.Type getType() {
            return (_type);
        }
        
        /**
         * This method sets the context type.
         * 
         * @param type The context type
         */
        public void setType(Context.Type type) {
            _type = type;
        }
        
        /**
         * This method gets the context value
         * expression evaluator.
         * 
         * @return The context value expression evaluator
         */
        public ExpressionEvaluator getEvaluator() {
            return (_evaluator);
        }
        
        /**
         * This method sets the context value's
         * expression evaluator.
         * 
         * @param evaluator The expression evaluator
         */
        public void setEvaluator(ExpressionEvaluator evaluator) {
            _evaluator = evaluator;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            return ("[ type="+_type+" expression="+_evaluator.getExpression()+" ]");
        }
    }
}
