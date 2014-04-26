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
package org.overlord.rtgov.ui.server.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.server.services.IServicesServiceImpl;

/**
 * Concrete implementation of the services service. :)
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
@Alternative
public class MockServicesServiceImpl implements IServicesServiceImpl {

    /**
     * Constructor.
     */
    public MockServicesServiceImpl() {
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.IServicesServiceImpl#getApplicationNames()
     */
    @Override
    public List<QName> getApplicationNames() throws UiException {
        List<QName> apps = new ArrayList<QName>();
        apps.add(new QName("urn:jboss:demos:applications", "Contract")); //$NON-NLS-1$ //$NON-NLS-2$
        apps.add(new QName("urn:jboss:demos:applications", "GGRL")); //$NON-NLS-1$ //$NON-NLS-2$
        return apps;
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.IServicesServiceImpl#findServices(org.overlord.rtgov.ui.client.model.ServicesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ServiceResultSetBean findServices(ServicesFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        ServiceResultSetBean rval = new ServiceResultSetBean();
        ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();
        rval.setServices(services);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);

        ServiceSummaryBean service = new ServiceSummaryBean();
        service.setServiceId("1"); //$NON-NLS-1$
        service.setName("CreateApplicationWebservice"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("{urn:jboss:demo:create-application}CreateApplicationPT"); //$NON-NLS-1$
        service.setBindings("SOAP, JMS"); //$NON-NLS-1$
        services.add(service);

        service = new ServiceSummaryBean();
        service.setServiceId("2"); //$NON-NLS-1$
        service.setName("CreateQuoteWebservice"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("{urn:jboss:demo:create-application}CreateQuotePT"); //$NON-NLS-1$
        service.setBindings("SOAP"); //$NON-NLS-1$
        services.add(service);

        return rval;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesServiceImpl#getService(java.lang.String)
     */
    @Override
    public ServiceBean getService(String uuid) throws UiException {
        ServiceBean service = new ServiceBean();
        service.setServiceId("1"); //$NON-NLS-1$
        service.setName(new QName("urn:jboss:demo:services", "CreateApplicationWebservice")); //$NON-NLS-1$ //$NON-NLS-2$
        service.setApplication(new QName("urn:jboss:demos:applications", "Contract")); //$NON-NLS-1$ //$NON-NLS-2$
        service.setServiceInterface("{urn:jboss:demo:create-application}CreateApplicationPT"); //$NON-NLS-1$
        service.setServiceGraph("<?xml version='1.0' encoding='UTF-8'?><svg height='30' width='400'><script type='text/javascript'>console.log('SVGRoot='+window.SVGRoot);</script><text x='0' y='15' fill='red'>Service Graph not available in Mock implementation</text>Sorry, your browser does not support inline SVG.</svg>");
        
        ReferenceSummaryBean reference = new ReferenceSummaryBean();
        reference.setReferenceId("1"); //$NON-NLS-1$
        reference.setName("CreateApplicationService"); //$NON-NLS-1$
        reference.setApplication("Contract"); //$NON-NLS-1$
        reference.setIface("org.jboss.demos.services.ICreateApplication"); //$NON-NLS-1$
        reference.setBindings("SOAP, JMS"); //$NON-NLS-1$
        service.getReferences().add(reference);

        reference = new ReferenceSummaryBean();
        reference.setReferenceId("2"); //$NON-NLS-1$
        reference.setName("CreateQuoteService"); //$NON-NLS-1$
        reference.setApplication("Contract"); //$NON-NLS-1$
        reference.setIface("org.jboss.demos.services.ICreateQuote"); //$NON-NLS-1$
        reference.setBindings("SOAP"); //$NON-NLS-1$
        service.getReferences().add(reference);

        return service;
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.IServicesServiceImpl#getReference(java.lang.String)
     */
    @Override
    public ReferenceBean getReference(String serviceId) throws UiException {
        ReferenceBean reference = new ReferenceBean();
        reference.setReferenceId("1"); //$NON-NLS-1$
        reference.setName(new QName("urn:jboss:demo:services", "CreateApplicationService")); //$NON-NLS-1$ //$NON-NLS-2$
        reference.setApplication(new QName("urn:jboss:demos:applications", "Contract")); //$NON-NLS-1$ //$NON-NLS-2$
        reference.setReferenceInterface("org.jboss.demos.services.ICreateApplication"); //$NON-NLS-1$
        return reference;
    }

}
