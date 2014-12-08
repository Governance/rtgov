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

import org.overlord.rtgov.common.registry.ServiceClose;
import org.overlord.rtgov.common.registry.ServiceInit;

public class TestServiceImpl implements TestService {
    
    private boolean _init=false;
    private boolean _close=false;    
    private boolean _fail=false;

    public TestServiceImpl() {
    }
    
    public void fail() {
        _fail = true;
    }
    
    @ServiceInit
    public void init() {
        if (_fail) {
            throw new RuntimeException();
        }
        _init = true;
    }
    
    public boolean isInit() {
        return (_init);
    }

    @ServiceClose
    public void close() {
        if (_fail) {
            throw new RuntimeException();
        }
        _close = true;
    }
    
    public boolean isClose() {
        return (_close);
    }
}
