/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-13, Red Hat Middleware LLC, and others contributors as indicated
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
package org.overlord.rtgov.ep.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="RTGOV_TEST_EVENT")
public class TestEvent implements java.io.Serializable {

	private static final long serialVersionUID = 7232940306592053980L;

	private String _id=null;
    private String _description=null;

    /**
     * This method sets the id.
     * 
     * @param id The id
     */
    public void setId(String id) {
        _id = id;
    }
    
    /**
     * This method gets the id.
     * 
     * @return The id
     */
    @Id
    public String getId() {
        return (_id);
    }
    
    /**
     * This method sets the description.
     * 
     * @param description The description
     */
    public void setDescription(String description) {
    	_description = description;
    }
    
    /**
     * This method gets the description.
     * 
     * @return The description
     */
    public String getDescription() {
        return (_description);
    }
    
}
