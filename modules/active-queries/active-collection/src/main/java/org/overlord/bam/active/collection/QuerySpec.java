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
package org.overlord.bam.active.collection;

import org.overlord.bam.active.collection.predicate.Predicate;

/**
 * This class defines the query specification that can be used to define
 * the active collection that is required. If a name and no predicate/parent
 * is specified, then a lookup on the name will be returned. If the query
 * spec defines a name, predicate and parent collection, then initially
 * a check will be made for the collection name. If it does not exist, then
 * a derived collection of that name will be created based on the parent
 * collection name and predicate.
 *
 */
public class QuerySpec {

    private String _collection=null;
    private Predicate _predicate=null;
    private String _parent=null;
    
    /**
     * The default constructor.
     */
    public QuerySpec() {
    }
    
    /**
     * This method returns the name of the collection.
     * 
     * @return The collection name
     */
    public String getCollection() {
        return (_collection);
    }
    
    /**
     * This method sets the name of the collection.
     * 
     * @param name The collection name
     */
    public void setCollection(String name) {
        _collection = name;
    }
    
    /**
     * This method returns the optional predicate. If specified,
     * along with the parent collection name, it can
     * be used to derive a sub-collection.
     * 
     * @return The predicate
     */
    public Predicate getPredicate() {
        return (_predicate);
    }
    
    /**
     * This method sets the optional predicate. If specified,
     * along with the parent collection name, it can
     * be used to derive a sub-collection.
     * 
     * @param pred The predicate
     */
    public void setPredicate(Predicate pred) {
        _predicate = pred;
    }

    /**
     * This method returns the name of the parent collection.
     * 
     * @return The parent collection name
     */
    public String getParent() {
        return (_parent);
    }
    
    /**
     * This method sets the name of the parent collection.
     * 
     * @param name The parent collection name
     */
    public void setParent(String name) {
        _parent = name;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return ("Query[collection="+_collection+" parent="
                    +_parent+" predicate="+_predicate+"]");
    }
}
