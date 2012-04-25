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
package org.savara.tests.switchyard.beanservice;

import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class BeanServiceTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
            .addClass(InventoryService.class)
            .addClass(InventoryServiceBean.class)
            .addClass(Item.class)
            .addClass(ItemNotFoundException.class)
            .addClass(Order.class)
            .addClass(OrderAck.class)
            .addClass(OrderService.class)
            .addClass(OrderServiceBean.class)
            .addClass(Transformers.class)
            .addClass(ExchangeInterceptor.class)
            .addAsResource("wsdl/OrderService.wsdl")
            .addAsResource("META-INF/switchyard.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    org.savara.tests.switchyard.beanservice.OrderService _orderService;

    @Test
    public void submitOrder() {
        
        if (_orderService == null) {
            fail("Order Service has not been set");
        }
        
        Order order=new Order();
        order.setOrderId("abc");
        order.setItemId("BUTTER");
        order.setQuantity(10);
        
        OrderAck ack=_orderService.submitOrder(order);
        
        if (ack == null) {
            fail("Acknowledgement is null");
        }
    }
}