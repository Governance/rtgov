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
package org.overlord.rtgov.internal.active.collection.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveCollectionVisibility;
import org.overlord.rtgov.active.collection.QuerySpec;
import org.overlord.rtgov.active.collection.util.ActiveCollectionUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * This class represents the RESTful interface to the active collection server.
 *
 */
@Path("/acm")
@ApplicationScoped
public class RESTActiveCollectionServer {

    private static final Logger LOG=Logger.getLogger(RESTActiveCollectionServer.class.getName());
    
    private static final ObjectMapper MAPPER=new ObjectMapper();

    private ActiveCollectionManager _acmManager=null;
    
    /**
     * This is the default constructor.
     */
    public RESTActiveCollectionServer() {
        
        _acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
        
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Active collection manager="+_acmManager);
        }
    }
    
    /**
     * This method sets the activity server.
     * 
     * @param acm The activity server
     */
    public void setActivityCollectionManager(ActiveCollectionManager acm) {
        LOG.info("Set Active Collection Manager="+acm);
        _acmManager = acm;
    }
    
    /**
     * This method handles queries.
     * 
     * @param qspec The query spec
     * @return The list of objects
     * @throws Exception Failed to query
     */
    @POST
    @Path("/query")
    @Produces("application/json")
    public String query(String qspec) throws Exception {
        String ret="";
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Active Collection JSON Query="+qspec);        
        }
        
        QuerySpec qs=ActiveCollectionUtil.deserializeQuerySpec(qspec.getBytes());
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Active Collection Query="+qs);        
        }
        
        if (_acmManager == null) {
            throw new Exception("Active Collection Manager is not available");
        }
        
        ActiveCollection actColl = _acmManager.getActiveCollection(qs.getCollection());
        
        if (actColl == null) {
            
            if (qs.getParent() == null || qs.getPredicate() == null) {
                throw new Exception("Collection '"+qs.getCollection()
                        +"' does not exist, and either the parent or "
                        +"predicate have not been defined");
            }
            
            // Try to get parent collection
            ActiveCollection parent = _acmManager.getActiveCollection(qs.getParent());
            
            if (parent != null) {
                actColl = _acmManager.create(qs.getCollection(),
                                parent, qs.getPredicate(), qs.getProperties());
            } else {
                throw new Exception("Unknown parent collection '"+qs.getParent()+"'");
            }
        }

        if (actColl != null) {
            
            // Check if active collection is public
            if (actColl.getVisibility() != ActiveCollectionVisibility.Public) {
                LOG.warning("Attempt to access restricted collection: "+qs);
                throw new Exception("Access to collection is restricted");
            }
            
            java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
        
            int count=0;
            int max=actColl.getSize();
            
            if (max > 0) {
                out.write("[".getBytes());
            }

            java.util.List<Object> results=actColl.query(qs);
            
            for (Object obj : results) {
                MAPPER.writeValue(out, obj);
                
                count++;
                if (count < max) {
                    out.write(",".getBytes());
                }
            }
            
            if (max > 0) {
                out.write("]".getBytes());
            }

            out.close();
            
            ret = new String(out.toByteArray());
        }
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Active Collection Result="+ret);        
        }

        return (ret);
    }

}
