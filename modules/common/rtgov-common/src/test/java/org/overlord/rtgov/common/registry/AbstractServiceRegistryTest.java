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
package org.overlord.rtgov.common.registry;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.overlord.rtgov.common.registry.AbstractServiceRegistry;

public class AbstractServiceRegistryTest {

    @Test
    public void testServiceInit() {
        AbstractServiceRegistry sr=new AbstractServiceRegistry() {

            @Override
            public <T> T getSingleService(Class<T> serviceInterface)
                    throws IllegalStateException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> Set<T> getServices(Class<T> serviceInterface) {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        
        TestServiceImpl impl=new TestServiceImpl();
        
        sr.init(impl);
        
        if (!impl.isInit()) {
            fail("TestServiceImpl has not been initialized"); //$NON-NLS-1$
        }
        
        if (impl.isClose()) {
            fail("TestServiceImpl should not have been closed"); //$NON-NLS-1$
        }
    }

    @Test
    public void testServiceClose() {
        AbstractServiceRegistry sr=new AbstractServiceRegistry() {

            @Override
            public <T> T getSingleService(Class<T> serviceInterface)
                    throws IllegalStateException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> Set<T> getServices(Class<T> serviceInterface) {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        
        TestServiceImpl impl=new TestServiceImpl();
        
        sr.close(impl);
        
        if (impl.isInit()) {
            fail("TestServiceImpl should not have been initialized"); //$NON-NLS-1$
        }
        
        if (!impl.isClose()) {
            fail("TestServiceImpl has not been closed"); //$NON-NLS-1$
        }
    }
    
    @Test
    public void testServiceInitException() {
        AbstractServiceRegistry sr=new AbstractServiceRegistry() {

            @Override
            public <T> T getSingleService(Class<T> serviceInterface)
                    throws IllegalStateException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> Set<T> getServices(Class<T> serviceInterface) {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        
        TestServiceImpl impl=new TestServiceImpl();
        impl.fail();
        
        try {
            sr.init(impl);
            
            fail("Should have thrown exception"); //$NON-NLS-1$
        } catch (Exception e) {
            // Ignore
        }
    }

    @Test
    public void testServiceCloseFail() {
        AbstractServiceRegistry sr=new AbstractServiceRegistry() {

            @Override
            public <T> T getSingleService(Class<T> serviceInterface)
                    throws IllegalStateException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public <T> Set<T> getServices(Class<T> serviceInterface) {
                // TODO Auto-generated method stub
                return null;
            }
            
        };
        
        TestServiceImpl impl=new TestServiceImpl();
        impl.fail();
        
        try {
            sr.close(impl);
            
            fail("Should have thrown exception"); //$NON-NLS-1$
        } catch (Exception e) {
            // Ignore
        }
    }
}
