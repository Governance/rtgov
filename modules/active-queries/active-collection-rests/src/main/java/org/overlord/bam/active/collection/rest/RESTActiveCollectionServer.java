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
package org.overlord.bam.active.collection.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;
import org.overlord.bam.active.collection.ActiveCollectionManager;
import org.overlord.bam.active.collection.ActiveList;
import org.overlord.bam.active.collection.QuerySpec;
import org.overlord.bam.active.collection.util.ActiveCollectionUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
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

    //@javax.inject.Inject
    private ActiveCollectionManager _acmManager=null;

    /**
     * This is the default constructor.
     */
    @SuppressWarnings("unchecked")
    public RESTActiveCollectionServer() {
        
        try {
            // Need to obtain active collection manager directly, as inject does not
            // work for REST service, and RESTeasy/CDI integration did not
            // appear to work in AS7. Directly accessing the bean manager
            // should be portable.
            BeanManager bm=InitialContext.doLookup("java:comp/BeanManager");
            
            java.util.Set<Bean<?>> beans=bm.getBeans(ActiveCollectionManager.class);
            
            for (Bean<?> b : beans) {                
                CreationalContext<Object> cc=new CreationalContext<Object>() {
                    public void push(Object arg0) {
                    }
                    public void release() {
                    }                   
                };
                
                _acmManager = (ActiveCollectionManager)((Bean<Object>)b).create(cc);
                
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Active collection manager="+_acmManager+" for bean="+b);
                }
                
                if (_acmManager != null) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    @Consumes("application/json")
    public String query(String qspec) throws Exception {
        String ret="";
        
        QuerySpec qs=ActiveCollectionUtil.deserializeQuerySpec(qspec.getBytes());
        
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Active Collection Query="+qs);        
        }
        
        if (_acmManager == null) {
            throw new Exception("Active Collection Manager is not available");
        }
        
        ActiveList alist = (ActiveList)_acmManager.getActiveCollection(qs.getCollection());
        
        if (alist == null) {
            // Try to get parent collection
            ActiveList parent = (ActiveList)_acmManager.getActiveCollection(qs.getParent());
            
            if (parent != null) {
                alist = (ActiveList)_acmManager.create(qs.getCollection(),
                                parent, qs.getPredicate());
            } else {
                throw new Exception("Unknown parent collection '"+qs.getParent()+"'");
            }
        }

        if (alist != null) {
            
            java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream();
        
            int count=0;
            int max=alist.getSize();
            
            if (max > 0) {
                out.write("[".getBytes());
            }

            for (Object obj : alist) {
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
