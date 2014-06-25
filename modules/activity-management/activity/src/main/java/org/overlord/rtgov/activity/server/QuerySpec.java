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
package org.overlord.rtgov.activity.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * This class represents a query specification.
 *
 * @deprecated No longer supported as it introduces a dependency between clients and the ActivityStore implementation
 */
public class QuerySpec implements java.io.Externalizable {

    private static final int VERSION = 1;
    
    private long _fromTimestamp=0;
    private long _toTimestamp=0;
    private String _expression=null;
    private String _format=null;
    
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
    public QuerySpec setExpression(String expr) {
        _expression = expr;
        return (this);
    }
    
    /**
     * This method returns the expression.
     * 
     * @return The expression
     */
    public String getExpression() {
        return (_expression);
    }
    
    /**
     * This method sets the format.
     * 
     * @param format The format
     * @return The query spec
     */
    public QuerySpec setFormat(String format) {
        _format = format;
        return (this);
    }
    
    /**
     * This method returns the format.
     * 
     * @return The format
     */
    public String getFormat() {
        return (_format);
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
        _expression = (String)in.readObject();
    }
    
}
