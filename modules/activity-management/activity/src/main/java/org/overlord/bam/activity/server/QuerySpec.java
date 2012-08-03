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

import org.overlord.bam.activity.model.ActivityUnit;
import org.overlord.bam.activity.model.Context;

/**
 * This class represents a query specification.
 *
 */
public class QuerySpec implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private String _id=null;
    private long _fromTimestamp=0;
    private long _toTimestamp=0;
    private Expression _expression=null;
    
    /**
     * This is the default constructor.
     */
    public QuerySpec() {
    }
    
    /**
     * This method sets the activity unit id to retrieve.
     * 
     * @param id The activity unit id
     * @return The query spec
     */
    public QuerySpec setId(String id) {
        _id = id;
        return (this);
    }
    
    /**
     * This method returns the activity unit id to retrieve.
     * 
     * @return The activity unit id, or null if not relevant
     */
    public String getId() {
        return (_id);
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
     * the subset that pass the query criteria.
     * 
     * @param activities The list of activity units to evaluate
     * @return The list of activity units that pass the query criteria
     */
    public java.util.List<ActivityUnit> evaluate(java.util.List<ActivityUnit> activities) {
        java.util.List<ActivityUnit> ret=new java.util.ArrayList<ActivityUnit>();
        
        for (ActivityUnit au : activities) {
            if (evaluate(au)) {
                ret.add(au);
            }
        }
        
        return (ret);
    }
    
    /**
     * This method evaluates the supplied activity unit against the
     * criteria defined by this query spec.
     * 
     * @param au The activity unit
     * @return Whether the activity unit matches the query spec
     */
    protected boolean evaluate(ActivityUnit au) {
        boolean ret=true;
        
        if (_id != null && !_id.equals(au.getId())) {
            ret = false;
        } else if (_fromTimestamp != 0 && au.getActivityTypes().size() > 0
                && _fromTimestamp > au.getActivityTypes().get(au.getActivityTypes().size()-1).getTimestamp()) {
            ret = false;
        } else if (_toTimestamp != 0 && au.getActivityTypes().size() > 0
                && _toTimestamp < au.getActivityTypes().get(0).getTimestamp()) {
            ret = false;
        }
        
        // Evaluate the expression
        if (ret && _expression != null) {
            ret = _expression.evaluate(au);
        }
        
        return (ret);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("QuerySpec[id="+_id+" from="+_fromTimestamp+" to="+_toTimestamp
                +" expression="+_expression+"]");
    }
    
    /**
     * {@inheritDoc}
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(VERSION);
        
        out.writeObject(_id);
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
        
        _id = (String)in.readObject();
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
        public Expression(Operator operator, Context... context) {
            _operator = operator;
            
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
         * This method returns the list of sub-expressions.
         * 
         * @return The expressions
         */
        public java.util.List<Expression> getExpressions() {
            return (_expressions);
        }
        
        /**
         * This method evaluates the supplied activity unit
         * against the expression.
         * 
         * @param au The activity unit
         * @return The result of the evaluation
         */
        public boolean evaluate(ActivityUnit au) {
            boolean ret=false;
            
            if (_operator == Operator.Match) {
                ret = evaluateMatch(au);
            } else if (_operator == Operator.And) {
                ret = evaluateAnd(au);
            } else if (_operator == Operator.Or) {
                ret = evaluateOr(au);
            } else if (_operator == Operator.Not) {
                ret = evaluateNot(au);
            }
            
            return (ret);
        }
        
        /**
         * This method evaluates the Match operator.
         * 
         * @param au The activity unit
         * @return The result
         */
        protected boolean evaluateMatch(ActivityUnit au) {
            if (_contexts.size() == 0) {
                return (false);
            }
            return (au.getContext().contains(_contexts.get(0)));
        }
        
        /**
         * This method evaluates the And operator.
         * 
         * @param au The activity unit
         * @return The result
         */
        protected boolean evaluateAnd(ActivityUnit au) {
            for (Context c : _contexts) {
                if (!au.getContext().contains(c)) {
                    return (false);
                }
            }
            
            for (Expression expr : _expressions) {
                if (!expr.evaluate(au)) {
                    return (false);
                }
            }
            
            return (true);
        }
        
        /**
         * This method evaluates the Or operator.
         * 
         * @param au The activity unit
         * @return The result
         */
        protected boolean evaluateOr(ActivityUnit au) {
            for (Context c : _contexts) {
                if (au.getContext().contains(c)) {
                    return (true);
                }
            }
            
            for (Expression expr : _expressions) {
                if (expr.evaluate(au)) {
                    return (true);
                }
            }
            
            return (false);
        }
        
        /**
         * This method evaluates the Not operator.
         * 
         * @param au The activity unit
         * @return The result
         */
        protected boolean evaluateNot(ActivityUnit au) {
            
            if (_contexts.size() > 0) {
                return (!au.getContext().contains(_contexts.get(0)));
            }
            
            if (_expressions.size() > 0) {
                return (!_expressions.get(0).evaluate(au));
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
