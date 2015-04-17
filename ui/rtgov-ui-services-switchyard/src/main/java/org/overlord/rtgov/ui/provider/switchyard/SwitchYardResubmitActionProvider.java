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
package org.overlord.rtgov.ui.provider.switchyard;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.overlord.rtgov.ui.client.model.BindingBean;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.provider.ResubmitActionProvider;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;
import org.switchyard.remote.http.HttpInvokerLabel;

/**
 * This class provides a SwitchYard implementation of the ServicesProvider
 * interface obtaining its information via JMX.
 *
 */
public class SwitchYardResubmitActionProvider extends ResubmitActionProvider {
	
    private static final String DOM_FORMAT = "dom";

    private static final Logger LOG=Logger.getLogger(SwitchYardResubmitActionProvider.class.getName());
    
    private SwitchYardServicesProvider _servicesProvider;
    
    private static volatile Messages i18n = new Messages();

    /**
     * The constructor.
     */
    public SwitchYardResubmitActionProvider(SwitchYardServicesProvider provider) {
        _servicesProvider = provider;
    }
    
    /**
     * {@inheritDoc}
     */
	public boolean isResubmitSupported(String service, String operation) throws UiException {
		try {
			Map<String, BindingBean> serviceBindings = _servicesProvider.getServiceBindings(service);
			for (String btype : serviceBindings.keySet()) {
			    if (btype.equalsIgnoreCase(SwitchYardServicesProvider.BINDING_TYPE_SCA)) {
			        return (true);
			    }
			}
			return (false);
		} catch (InstanceNotFoundException infe) {
		    return (false);
		} catch (Exception e) {
			throw new UiException(i18n.format("SwitchYardResubmitActionProvider.IsResubmitSupported", service, operation), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void resubmit(String service, String operation, MessageBean message) throws UiException {

		// Currently assumes message is xml
		org.w3c.dom.Document doc=null;
		
		try {
			DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			java.io.InputStream is=new java.io.ByteArrayInputStream(message.getContent().getBytes());
			
			doc = builder.parse(is);
			
			is.close();
		} catch (Exception e) {
			throw new UiException(e);
		}

		Object content=new DOMSource(doc.getDocumentElement());

		java.util.List<String> urls=_servicesProvider.getURLList();
		Exception exc=null;
		
		for (int i=0; i < urls.size(); i++) {
			try {
				// Create a new remote client invoker
				RemoteInvoker invoker = new HttpInvoker(urls.get(i));
				
				// Create the request message
				RemoteMessage rm = new RemoteMessage();
				rm.setService(javax.xml.namespace.QName.valueOf(service)).setOperation(operation).setContent(content);
				
				// Check if header properties need to be initialized
				if (message.getHeaders().size() > 0) {
				    for (String headerName : message.getHeaders().keySet()) {
				        String value=message.getHeaders().get(headerName);
				        String format=message.getHeaderFormats().get(headerName);
				        
				        configureHeader(rm, headerName, value, format);
				    }
				}
				
				// Invoke the service
				RemoteMessage reply = invoker.invoke(rm);
				if (reply.isFault()) {
					if (reply.getContent() instanceof Exception) {
						throw new UiException((Exception)reply.getContent());
					}
					throw new UiException("Fault response received: "+reply.getContent());
				}
				
				// Clear previous exceptions
				exc = null;
				
				break;
			} catch (NullPointerException npe) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Remote invocation of switchyard service["+service+"] operation["
							+operation+"] failed to deserialize response");
				}

				// RTGOV-650 Assume that response means successful resubmission, as due to npe
				// we currently have no visibility of the response to determine either way.
				// If the response indicates an error, this should result in another Situation
				// being created, thus resetting the resolution state to IN_PROGRESS anyway.
				
				// Clear previous exceptions
				exc = null;

				break;
			} catch (java.io.IOException e) {
				exc = e;
			}
		}
		
		if (exc != null) {
			// Report exception
			throw new UiException(exc);
		}
	}
	
	/**
	 * This method configures the supplied header property, in the appropriate format, on the
	 * supplied remote message.
	 * 
	 * @param rm The remote message
	 * @param headerName The header name
	 * @param value The value
	 * @param format The required format (text, dom, etc.)
	 * @throws UiException Failed to configure header property
	 */
	protected void configureHeader(RemoteMessage rm, String headerName, String value, String format)
	                                    throws UiException{
        Object propValue=value;
	    
        if (format != null && format.equals(DOM_FORMAT)) {
	        
            try {
                // Convert to DOM
                javax.xml.parsers.DocumentBuilderFactory factory=
                        javax.xml.parsers.DocumentBuilderFactory.newInstance();
                
                factory.setNamespaceAware(true);
                
                javax.xml.parsers.DocumentBuilder builder=
                        factory.newDocumentBuilder();
                
                java.io.InputStream is=
                        new java.io.ByteArrayInputStream(value.getBytes());
                
                org.w3c.dom.Document doc=builder.parse(is);
                
                is.close();
                
                propValue = doc.getDocumentElement();
            } catch (Exception e) {
                throw new UiException("Failed to configure header '"+headerName+"'", e);
            }
        }
	    
        Property prop=rm.getContext().setProperty(headerName, propValue, Scope.MESSAGE);
        prop.addLabels(HttpInvokerLabel.HEADER.label());
	}
	
}
