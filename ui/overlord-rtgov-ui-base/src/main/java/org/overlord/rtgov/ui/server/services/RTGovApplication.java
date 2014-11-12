/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.server.services;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.overlord.rtgov.common.util.BeanResolverUtil;
import org.overlord.rtgov.ui.client.shared.services.IServicesService;
import org.overlord.rtgov.ui.client.shared.services.ISituationsService;

/**
 * Services application.
 */
@ApplicationPath("/rest")
public class RTGovApplication extends Application {
    
    private java.util.Set<Object> _singletons=new java.util.HashSet<Object>();

    public RTGovApplication() {
        IServicesService services = BeanResolverUtil.getBean(IServicesService.class);
        if (services != null) {
            _singletons.add(services);
        }
        
        ISituationsService situations = BeanResolverUtil.getBean(ISituationsService.class);
        if (situations != null) {
            _singletons.add(situations);
        }
    }

    public Set<Object> getSingletons() {
        return (_singletons);
    }
}