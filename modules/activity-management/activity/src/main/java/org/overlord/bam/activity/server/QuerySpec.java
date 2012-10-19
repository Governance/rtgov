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
package org.overlord.bam.activity.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.overlord.bam.activity.model.ActivityType;
import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;

/**
 * This class represents a query specification.
 *
 */
public class QuerySpec implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private long _fromTimestamp=0;
    private long _toTimestamp=0;
    private Expression _expression=null;
    
    /**
     * This is the default constructor.
     */
    public QuerySpec() {
    }
    
    /**
     * This method sets the 'from' timestamp. If
     * set to 0, then query will start from the
     * oldest activity event.
     * 
     * @param from The 'from' timetamp
     * @return The query spec
     */
    public QuerySpec setFromTimestamp(long from) {
        _fromTimestamp = from;       
        return (this);
    }
    
    /**
     * This method gets the 'from' timestamp. If
     * value is 0, then query will start from the
     * oldest activity event.
     * 
     * @return The 'from' timetamp
     */
    public long getFromTimestamp() {
        return (_fromTimestamp);
    }

    /**
     * This method sets the 'to' timestamp. If
     * set to 0, then query will end with the
     * newest activity event.
     * 
     * @param to The 'to' timetamp
     * @return The query spec
     */
    public QuerySpec setToTimestamp(long to) {
        _toTimestamp = to;
        return (this);
    }
    
    /**
     * This method gets the 'to' timestamp. If
     * value is 0, then query will start with the
     * newest activity event.
     * 
     * @return The 'to' timetamp
     */
    public long getToTimestamp() {
        return (_toTimestamp);
    }
    
    /**
     * This method sets the expression.
     * 
     * @param expr The expression
     * @return The query spec
     */
    public QuerySpec setExpression(Expression expr) {
        _expression = expr;
        return (this);
    }
    
    /**
     * This method returns the expression.
     * 
     * @return The expression
     */
    public Expression getExpression() {
        return (_expression);
    }
    
    /**
     * This method applies the query spec to the supplied list of activity units, returning
     * the list of contained activity events that pass the query criteria.
     * 
     * @param activities The list of activity units to evaluate
     * @return The list of activity types that pass the query criteria
     */
    public java.util.List<ActivityType> evaluate(java.util.List<ActivityUnit> activities) {
        java.util.List<ActivityType> ret=new java.util.ArrayList<ActivityType>();
        
        for (ActivityUnit au : activities) {
            for (ActivityType at : au.getActivityTypes()) {
                if (evaluate(at)) {
                    ret.add(at);
                }
            }
        }
        
        return (ret);
    }
    
    /**
     * This method evaluates the supplied activity type against the
     * criteria defined by this query spec.
     * 
     * @param at The activity type
     * @return Whether the activity type matches the query spec
     */
    protected boolean evaluate(ActivityType at) {
        boolean ret=true;
        
        if (_fromTimestamp != 0
                && _fromTimestamp > at.getTimestamp()) {
            ret = false;
        } else if (_toTimestamp != 0
                && _toTimestamp < at.getTimestamp()) {
            ret = false;
        }
        
        // Evaluate the expression
        if (ret && _expression != null) {
            ret = _expression.evaluate(at);
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("QuerySpec[from="+_fromTimestamp+" to="+_toTimestamp
                +" expression="+_expression+"]");
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeLong(_fromTimestamp);
        out.writeLong(_toTimestamp);
        out.writeObject(_expression);
    }

    /**
     * {@inheritDoc}
     */
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        in.readInt(); // Consume version number - not required at the moment
        
        _fromTimestamp = in.readLong();
        _toTimestamp = in.readLong();
        _expression = (Expression)in.readObject();
    }
    
    /**
     * This enumerated type defines the supported operators.
     *
     */
    public static enum Operator {
        
        /**
         * Match a single context, with no sub-expressions.
         */
        Match,
        
        /**
         * Logical AND of any context or sub-expression defined.
         */
        And,
        
        /**
         * Logical OR of any context or sub-expression defined.
         */
        Or,
        
        /**
         * Match a single context or sub-expression.
         */
        Not
    }
    
    /**
     * This class represents an operator and parameters.
     * 
     */
    public static class Expression implements java.io.Externalizable {

        private static final int VERSION = 1;
        
        private Operator _operator=null;
        private java.util.List<Context> _contexts=new java.util.ArrayList<Context>();
        private java.util.Map<String,String> _properties=new java.util.HashMap<String,String>();
        private java.util.List<Expression> _expressions=new java.util.ArrayList<Expression>();
        
        /**
         * The default constructor.
         */
        public Expression() {
        }
        
        /**
         * The context constructor.
         * 
         * @param operator The operator
         * @param context The list of contexts
         */
        public Expression(Operator operator,
                            Context... context) {
            this(operator, null, context);
        }
       
        /**
         * The context constructor.
         * 
         * @param operator The operator
         * @param props The optional properties
         * @param context The list of contexts
         */
        public Expression(Operator operator, java.util.Map<String,String> props,
                            Context... context) {
            _operator = operator;
            
            if (props != null) {
                _properties = props;
            }
            
            for (Context c : context) {
                _contexts.add(c);
            }
        }
       
        /**
         * The sub-expression constructor.
         * 
         * @param operator The operator
         * @param expr The list of sub-expressions
         */
        public Expression(Operator operator, Expression... expr) {
            _operator = operator;
            
            for (Expression e : expr) {
                _expressions.add(e);
            }
        }
        
        /**
         * This method returns the operator associated with the
         * expression.
         * 
         * @return The operator
         */
        public Operator getOperator() {
            return (_operator);
        }
        
        /**
         * This method sets the operator associated with the
         * expression.
         * 
         * @param operator The operator
         */
        public void setOperator(Operator operator) {
            _operator = operator;
        }
        
        /**
         * This method returns the list of contexts.
         * 
         * @return The contexts
         */
        public java.util.List<Context> getContexts() {
            return (_contexts);
        }
        
        /**
         * This method returns the list of properties.
         * 
         * @return The properties
         */
        public java.util.Map<String,String> getProperties() {
            return (_properties);
        }
        
        /**
         * This method returns the list of sub-expressions.
         * 
         * @return The expressions
         */
        public java.util.List<Expression> getExpressions() {
            return (_expressions);
        }
        
        /**
         * This method evaluates the supplied activity type
         * against the expression.
         * 
         * @param at The activity type
         * @return The result of the evaluation
         */
        public boolean evaluate(ActivityType at) {
            return (evaluate(at.getContext(), at.getProperties()));
        }
        
        /**
         * This method evaluates the supplied activity unit
         * against the expression.
         * 
         * @param contexts The list of contexts
         * @param properties The set of properties
         * @return The result of the evaluation
         */
        protected boolean evaluate(java.util.List<Context> contexts,
                java.util.Map<String,String> properties) {
            boolean ret=false;
            
            if (_operator == Operator.Match) {
                ret = evaluateMatch(contexts, properties);
            } else if (_operator == Operator.And) {
                ret = evaluateAnd(contexts, properties);
            } else if (_operator == Operator.Or) {
                ret = evaluateOr(contexts, properties);
            } else if (_operator == Operator.Not) {
                ret = evaluateNot(contexts, properties);
            }
            
            return (ret);
        }
        
        /**
         * This method evaluates the Match operator.
         * 
         * @param contexts The list of contexts
         * @param properties The set of properties
         * @return The result
         */
        protected boolean evaluateMatch(java.util.List<Context> contexts,
                java.util.Map<String,String> properties) {
            if (_contexts.size() > 0) {
                if (!contexts.contains(_contexts.get(0))
                        || _contexts.size() > 1) {
                    return (false);
                } else {
                    // Return true as long as no properties
                    // have been defined
                    return (_properties.size() == 0);
                }
            }
            if (_properties.size() > 0) {
                if (_properties.size() == 1) {
                    String key=_properties.keySet().iterator().next();
                    if (properties.containsKey(key)
                            && properties.get(key).equals(_properties.get(key))) {
                        return (true);
                    }
                }
            }
            return (false);
        }
        
        /**
         * This method evaluates the And operator.
         * 
         * @param contexts The list of contexts
         * @param properties The set of properties
         * @return The result
         */
        protected boolean evaluateAnd(java.util.List<Context> contexts,
                java.util.Map<String,String> properties) {
            for (Context c : _contexts) {
                if (!contexts.contains(c)) {
                    return (false);
                }
            }
            
            for (String key : _properties.keySet()) {
                if (!properties.containsKey(key)
                        || !properties.get(key).equals(_properties.get(key))) {
                    return (false);
                }
            }
            
            for (Expression expr : _expressions) {
                if (!expr.evaluate(contexts, properties)) {
                    return (false);
                }
            }
            
            return (true);
        }
        
        /**
         * This method evaluates the Or operator.
         * 
         * @param contexts The list of contexts
         * @param properties The set of properties
         * @return The result
         */
        protected boolean evaluateOr(java.util.List<Context> contexts,
                java.util.Map<String,String> properties) {
            for (Context c : _contexts) {
                if (contexts.contains(c)) {
                    return (true);
                }
            }
            
            for (String key : _properties.keySet()) {
                if (properties.containsKey(key)
                        && properties.get(key).equals(_properties.get(key))) {
                    return (true);
                }
            }
            
            for (Expression expr : _expressions) {
                if (expr.evaluate(contexts, properties)) {
                    return (true);
                }
            }
            
            return (false);
        }
        
        /**
         * This method evaluates the Not operator.
         * 
         * @param contexts The list of contexts
         * @param properties The set of properties
         * @return The result
         */
        protected boolean evaluateNot(java.util.List<Context> contexts,
                java.util.Map<String,String> properties) {
            
            if (_contexts.size() > 0) {
                return (!contexts.contains(_contexts.get(0)));
            } else if (_properties.size() > 0) {
                if (_properties.size() == 1) {
                    String key=_properties.keySet().iterator().next();
                    if (!properties.containsKey(key)
                            || !properties.get(key).equals(_properties.get(key))) {
                        return (true);
                    }                 
                }
            } else if (_expressions.size() > 0) {
                return (!_expressions.get(0).evaluate(contexts, properties));
            }
            
            return (false);
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            StringBuffer buf=new StringBuffer();
            buf.append("(");
            buf.append(_operator.name());
            
            for (Context c : _contexts) {
                buf.append(" "+c);
            }
            
            for (Expression e : _expressions) {
                buf.append(" "+e);
            }
            
            buf.append(")");
            
            return (buf.toString());
        }
        
        /**
         * {@inheritDoc}
         */
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(VERSION);
            
            out.writeObject(_operator);
            
            out.writeInt(_contexts.size());
            
            for (int i=0; i < _contexts.size(); i++) {
                out.writeObject(_contexts.get(i));
            }
            
            out.writeInt(_expressions.size());
            
            for (int i=0; i < _expressions.size(); i++) {
                out.writeObject(_expressions.get(i));
            }
        }

        /**
         * {@inheritDoc}
         */
        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            in.readInt(); // Consume version number - not required at the moment
            
            _operator = (Operator)in.readObject();
            
            int num=in.readInt();
            
            for (int i=0; i < num; i++) {
                _contexts.add((Context)in.readObject());
            }
            
            num = in.readInt();
            
            for (int i=0; i < num; i++) {
                _expressions.add((Expression)in.readObject());
            }
        }
        
    }
}
