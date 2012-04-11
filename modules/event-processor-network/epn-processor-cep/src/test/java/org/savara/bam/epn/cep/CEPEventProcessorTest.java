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
package org.savara.bam.epn.cep;

import static org.junit.Assert.*;

import org.junit.Test;
import org.savara.bam.activity.model.Activity;
import org.savara.bam.activity.model.MessageExchange;
import org.savara.bam.activity.model.MessageExchange.InvocationType;
import org.savara.bam.epn.cep.CEPEventProcessor;

public class CEPEventProcessorTest {

    private static final int TIME_INTERVAL = 2000;
    private static final int GAP_INTERVAL = 3*60*1000;

    @Test
    public void testPurchasingResponseTime() {
        CEPEventProcessor ep=new CEPEventProcessor();
        ep.setRuleName("PurchasingResponseTime");
        
        Activity e1=new Activity();
        e1.setId("e1");
        MessageExchange me1=new MessageExchange();
        me1.setCorrelation("corr1");
        me1.setInvocationType(InvocationType.Request);
        e1.setActivityType(me1);
        
        Activity e2=new Activity();
        e2.setId("e2");
        MessageExchange me2=new MessageExchange();
        me2.setCorrelation("corr2");
        me2.setInvocationType(InvocationType.Response);
        e2.setActivityType(me2);
        
        Activity e3=new Activity();
        e3.setId("e3");
        e3.setTimestamp(e3.getTimestamp()+TIME_INTERVAL);
        MessageExchange me3=new MessageExchange();
        me3.setCorrelation("corr1");
        me3.setInvocationType(InvocationType.Response);
        e3.setActivityType(me3);
        
        try {            
            ep.init();
            
            java.util.Properties props1=(java.util.Properties)ep.process("Purchasing", e1, 0);
            
            if (props1 != null) {
                fail("Should be no result 1");
            }
            
            java.util.Properties props2=(java.util.Properties)ep.process("Purchasing", e2, 0);
            
            if (props2 != null) {
                fail("Should be no result 2");
            }
            
            java.util.Properties props3=(java.util.Properties)ep.process("Purchasing", e3, 0);
            
            if (props3 == null) {
                fail("Result should not be null");
            }
            
            String reqId=(String)props3.get("requestId");
            String respId=(String)props3.get("responseId");
            
            if (!reqId.equals(e1.getId())) {
                fail("Request id incorrect");
            }
            
            if (!respId.equals(e3.getId())) {
                fail("Response id incorrect");
            }
            
            if (!props3.containsKey("responseTime")) {
                fail("Response time not found");
            }
            
            long responseTime=(Long)props3.get("responseTime");
            
            if (responseTime != TIME_INTERVAL) {
                fail("Response time should be "+TIME_INTERVAL+": "+responseTime);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Exception: "+e);
        }
    }

    @Test
    public void testPurchasingResponseTimeOutOfOrder() {
        CEPEventProcessor ep=new CEPEventProcessor();
        ep.setRuleName("PurchasingResponseTime");
        
        Activity e1=new Activity();
        e1.setId("e1");
        e1.setTimestamp(e1.getTimestamp()+GAP_INTERVAL);
        MessageExchange me1=new MessageExchange();
        me1.setCorrelation("corr1");
        me1.setInvocationType(InvocationType.Request);
        e1.setActivityType(me1);
        
        Activity e3=new Activity();
        e3.setId("e3");
        e3.setTimestamp(e3.getTimestamp()+GAP_INTERVAL+TIME_INTERVAL);
        MessageExchange me3=new MessageExchange();
        me3.setCorrelation("corr1");
        me3.setInvocationType(InvocationType.Response);
        e3.setActivityType(me3);
        
        try {            
            ep.init();
            
            java.util.Properties props3=(java.util.Properties)ep.process("Purchasing", e3, 0);
            
            if (props3 != null) {
                fail("Should be no result 1");
            }
            
            java.util.Properties props1=(java.util.Properties)ep.process("Purchasing", e1, 0);
            
            if (props1 == null) {
                fail("Result should not be null");
            }
            
            String reqId=(String)props1.get("requestId");
            String respId=(String)props1.get("responseId");
            
            if (!reqId.equals(e1.getId())) {
                fail("Request id incorrect");
            }
            
            if (!respId.equals(e3.getId())) {
                fail("Response id incorrect");
            }
            
            if (!props1.containsKey("responseTime")) {
                fail("Response time not found");
            }
            
            long responseTime=(Long)props1.get("responseTime");
            
            if (responseTime != TIME_INTERVAL) {
                fail("Response time should be "+TIME_INTERVAL+": "+responseTime);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail("Exception: "+e);
        }
    }
}
