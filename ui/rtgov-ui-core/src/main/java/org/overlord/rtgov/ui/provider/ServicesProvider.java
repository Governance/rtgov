/*
 * Copyright 2013-4 Red Hat Inc
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
package org.overlord.rtgov.ui.provider;

import java.util.List;

import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;

/**
 * This interface provides access to information regarding services being managed
 * through runtime governance.
 *
 */
public interface ServicesProvider {
	
	/**
	 * This method returns the name of the provider.
	 * 
	 * @return The provider name
	 */
	public String getName();
	
	/**
	 * This method determines whether the supplied service is
	 * known to this provider.
	 * 
	 * @param service The service
	 * @return Whether this provider knows the service
	 */
	public boolean isServiceKnown(String service);
 
    /**
     * This method determines whether this provider support's a resubmit of the
     * supplied service operation.
     * 
     * @param service The service
     * @param operation The operation
     * @return Whether this provider support's a resubmit for the given service
     *         and operation
     */
    public boolean isResubmitSupported(String service, String operation);

	/**
	 * This method resubmits the supplied message to the target service
	 * and operation.
	 * 
	 * @param service The service
	 * @param operation The operation
	 * @param message The message
	 * @throws UiException Failed to resubmit the message
	 */
	public void resubmit(String service, String operation, MessageBean message) throws UiException;

	/**
	 * This method returns the list of application names.
	 * 
	 * @return The application names
	 * @throws UiException Failed to get list of application names
	 */
    public List<QName> getApplicationNames() throws UiException;

    /**
     * This method returns the list of services that pass the supplied filters.
     * 
     * @param filters The filters
     * @return The list of services
     * @throws UiException Failed to find services
     */
    public List<ServiceSummaryBean> findServices(ServicesFilterBean filters) throws UiException;
    
    /**
     * This method returns the service details associated with a unique id.
     * 
     * @param uuid The unique id for the service
     * @return The service details, or null if not known
     * @throws UiException Failed to get service
     */
    public ServiceBean getService(String uuid) throws UiException;
    
    /**
     * This method returns the reference details for the supplied unique id.
     * 
     * @param uuid The unique id for the reference
     * @return The reference details, or null if not known
     * @throws UiException Failed to get reference
     */
    public ReferenceBean getReference(String uuid) throws UiException;

}
