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
package org.savara.bam.active.collection.epn;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;

import org.savara.bam.active.collection.ActiveCollectionSource;
import org.savara.bam.epn.EPNManager;
import org.savara.bam.epn.EventList;
import org.savara.bam.epn.NodeListener;
import org.savara.bam.epn.NotifyType;

/**
 * This class provides the Active Collection Source for listening to
 * Event Processor Network nodes.
 *
 */
public abstract class EPNActiveCollectionSource extends ActiveCollectionSource implements NodeListener {

    private static final Logger LOG=Logger.getLogger(EPNActiveCollectionSource.class.getName());

    private static final String EPN_MANAGER = "java:global/savara-bam/EPNManager";

    private EPNManager _epnManager=null;
    private String _network=null;
    
    /**
     * This method sets the network name.
     * 
     * @param network The network name
     */
    public void setNetwork(String network) {
        _network = network;
    }
    
    /**
     * This method gets the network name.
     * 
     * @return The network name
     */
    public String getNetwork() {
        return (_network);
    }
    
    /**
     * {@inheritDoc}
     */
    public void init() throws Exception {
        
        try {
            InitialContext ctx=new InitialContext();
            
            _epnManager = (EPNManager)ctx.lookup(EPN_MANAGER);
            
            _epnManager.addNodeListener(_network, this);
            
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to obtain Event Processor Network Manager", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void notify(String network, String version, String node,
            NotifyType type, EventList events) {
        if (isRelevant(network, version, node, type)) {
            processNotification(network, version, node, type, events);
        }
    }

    /**
     * This method determines whether the notification is relevant for this
     * active collection source.
     * 
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @return Whether the notification is relevant
     */
    protected boolean isRelevant(String network, String version, String node,
                            NotifyType type) {
        return (true);
    }
    
    /**
     * This method processes the notification to update the active collection
     * accordingly.
     * 
     * @param network The network
     * @param version The version
     * @param node The node
     * @param type The type
     * @param events The list of events to be processed
     */
    protected abstract void processNotification(String network, String version, String node,
                            NotifyType type, EventList events);
    
}
