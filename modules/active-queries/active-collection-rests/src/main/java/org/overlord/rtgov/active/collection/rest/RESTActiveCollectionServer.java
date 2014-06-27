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
package org.overlord.rtgov.active.collection.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveCollectionVisibility;
import org.overlord.rtgov.active.collection.QuerySpec;
import org.overlord.rtgov.active.collection.util.ActiveCollectionUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
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
    }
    
    /**
     * This method initializes the active collection REST service.
     */
    @PostConstruct
    public void init() {
        if (_acmManager == null) {
            _acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
            
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Active collection manager="+_acmManager);
            }
        }
    }
    
    /**
     * This method sets the active collection manager.
     * 
     * @param acm The active collection manager
     */
    public void setActiveCollectionManager(ActiveCollectionManager acm) {
        LOG.info("Set Active Collection Manager="+acm);
        _acmManager = acm;
    }
    
    /**
     * This method returns the active collection manager.
     * 
     * @return The active collection manager
     */
    public ActiveCollectionManager getActiveCollectionManager() {
        return (_acmManager);
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
    @Consumes("application/json")
    @Produces("application/json")
    public String query(QuerySpec qspec) throws Exception {
        String ret="";
        
        init();
        
        if (LOG.isLoggable(Level.FINEST)) {
            String text=null;
            
            if (qspec != null) {
                byte[] b=ActiveCollectionUtil.serializeQuerySpec(qspec);
                text = new String(b);
            }

            LOG.finest("Active Collection query="+text);        
        }
        
        if (_acmManager == null) {
            throw new Exception("Active Collection Manager is not available");
        }
        
        ActiveCollection actColl = _acmManager.getActiveCollection(qspec.getCollection());
        
        if (actColl == null) {
            
            if (qspec.getParent() == null || qspec.getPredicate() == null) {
                throw new Exception("Collection '"+qspec.getCollection()
                        +"' does not exist, and either the parent or "
                        +"predicate have not been defined");
            }
            
            // Try to get parent collection
            ActiveCollection parent = _acmManager.getActiveCollection(qspec.getParent());
            
            if (parent != null) {
                actColl = _acmManager.create(qspec.getCollection(),
                                parent, qspec.getPredicate(), qspec.getProperties());
            } else {
                throw new Exception("Unknown parent collection '"+qspec.getParent()+"'");
            }
        }

        if (actColl != null) {
            
            // Check if active collection is public
            if (actColl.getVisibility() != ActiveCollectionVisibility.Public) {
                LOG.warning("Attempt to access restricted collection: "+qspec);
                throw new Exception("Access to collection is restricted");
            }
            
            java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
        
            int count=0;
            int max=actColl.getSize();
            
            if (max > 0) {
                out.write("[".getBytes());
            }

            java.util.List<Object> results=actColl.query(qspec);
            
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
