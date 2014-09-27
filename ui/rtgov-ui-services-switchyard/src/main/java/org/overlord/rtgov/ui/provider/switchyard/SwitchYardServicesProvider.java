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


import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveMap;
import org.overlord.rtgov.analytics.service.ServiceDefinition;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.service.dependency.ServiceDependencyBuilder;
import org.overlord.rtgov.service.dependency.ServiceGraph;
import org.overlord.rtgov.service.dependency.layout.ServiceGraphLayoutImpl;
import org.overlord.rtgov.service.dependency.svg.SVGServiceGraphGenerator;
import org.overlord.rtgov.ui.client.model.BindingBean;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.provider.ServicesProvider;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;
import org.switchyard.remote.http.HttpInvokerLabel;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class provides a SwitchYard implementation of the ServicesProvider
 * interface obtaining its information via JMX.
 *
 */
public class SwitchYardServicesProvider implements ServicesProvider {
	
	private static final String BINDING_TYPE_SCA = "sca";

	private static final Logger LOG=Logger.getLogger(SwitchYardServicesProvider.class.getName());
	
    private static volatile Messages i18n = new Messages();

	private static final String PROVIDER_NAME = "switchyard";

	// Properties
	private static final String SWITCHYARD_RESUBMIT_HANDLER_SERVER_URLS = "SwitchYardServicesProvider.serverURLs";
	private static final String SWITCHYARD_JMX_URL = "SwitchYardServicesProvider.jmxURL";
	private static final String SWITCHYARD_JMX_USERNAME = "SwitchYardServicesProvider.jmxUsername";
	private static final String SWITCHYARD_JMX_PASSWORD = "SwitchYardServicesProvider.jmxPassword";

	protected static final String DEFAULT_REMOTE_INVOKER_URL = "http://localhost:8080/switchyard-remote";
	
	private String _serverURLs=null;
    
	private java.util.List<String> _urlList=new java.util.ArrayList<String>();
	
	private MBeanServerConnection _mbeanServerConnection;
	private String _jmxUrl=null;
	private String _jmxUsername=null;
	private String _jmxPassword=null;

    private static final char ESCAPE_CHAR = '\\';
    private static final char SEPARATOR_CHAR = ':';
    /**
     * The constructor.
     */
    public SwitchYardServicesProvider() {
		_serverURLs = RTGovProperties.getProperties().getProperty(SWITCHYARD_RESUBMIT_HANDLER_SERVER_URLS);

		_jmxUrl = RTGovProperties.getProperties().getProperty(SWITCHYARD_JMX_URL);
		_jmxUsername = RTGovProperties.getProperties().getProperty(SWITCHYARD_JMX_USERNAME);
		_jmxPassword = RTGovProperties.getProperties().getProperty(SWITCHYARD_JMX_PASSWORD);
    }
    
	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return PROVIDER_NAME;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isServiceKnown(String service) {
	    try {
            MBeanServerConnection mBeanServerConnection = getMBeanServerConnection();
            mBeanServerConnection.getAttributes(new ObjectName(
                "org.switchyard.admin:type=Service,name=\"" + service + "\""), new String[] { "Bindings" });
	    } catch (Exception e) {
	        return (false);
	    }
		return (true);
	}

	@Override
	public boolean isResubmitSupported(String service, String operation) throws UiException {
		try {
			Map<String, BindingBean> serviceBindings = getServiceBindings(service);
			for (String btype : serviceBindings.keySet()) {
			    if (btype.equalsIgnoreCase(BINDING_TYPE_SCA)) {
			        return (true);
			    }
			}
			return (false);
		} catch (InstanceNotFoundException infe) {
		    return (false);
		} catch (Exception e) {
			throw new UiException(i18n.format("SwitchYardServicesProvider.IsResubmitSupported", service, operation), e);
		}
	}

	private Map<String, BindingBean> getReferenceBindings(String service) throws Exception {
		return getBindings("Reference", service);
	}

	private Map<String, BindingBean> getServiceBindings(String service) throws Exception {
		return getBindings("Service", service);
	}

	private Map<String, BindingBean> getBindings(String type, String service) throws Exception {
		Map<String, BindingBean> result = Maps.newHashMapWithExpectedSize(2);
		MBeanServerConnection mBeanServerConnection = getMBeanServerConnection();
		AttributeList attributeList = mBeanServerConnection.getAttributes(new ObjectName("org.switchyard.admin:type="
		        + type + ",name=\"" + service + "\""), new String[] { "Bindings" });
		ObjectName[] bindings = (ObjectName[]) getAttributeValue(attributeList.get(0));
		if (bindings != null && bindings.length > 0) {
			for (int i = 0; i < bindings.length; i++) {
				ObjectName objectName = bindings[i];
				attributeList = mBeanServerConnection.getAttributes(objectName, new String[] { "Type", "State" });
				BindingBean bindingBean = new BindingBean();
				bindingBean.setType(nullToEmpty((String) getAttributeValue(attributeList.get(0))).toUpperCase());
				bindingBean.setState(nullToEmpty((String) getAttributeValue(attributeList.get(1))).toUpperCase());
				result.put(bindingBean.getType(), bindingBean);
			}
		}
		return result;
	}

	/**
	 * This method sets the comma separated list of SwitchYard server URLs.
	 * 
	 * @param urls The server URLs
	 */
	public void setServerURLs(String urls) {
		synchronized (_urlList) {
			_serverURLs = urls;
			
			_urlList.clear();
		}
	}
	
	/**
	 * This method returns the comma separated list of SwitchYard server URLs.
	 * 
	 * @return The server URLs
	 */
	public String getServerURLs() {
		return (_serverURLs);
	}
	
	/**
	 * This method returns a list of URLs to use for a particular invocation.
	 * If multiple URLs are available, the list will round robin to balance the
	 * load - however if one URL fails, then the next one in the list will be
	 * tried until successful or end of list reached.
	 * 
	 * @return The list of URLs
	 */
	protected java.util.List<String> getURLList() {
		java.util.List<String> ret=null;
		
		synchronized (_urlList) {
			if (_urlList.size() == 0) {
				
				if (getServerURLs() != null && getServerURLs().trim().length() > 0) {
					String[] urls=getServerURLs().split("[, ]");
					
					for (int i=0; i < urls.length; i++) {
						String url=urls[i].trim();
						
						if (url.length() > 0) {
							_urlList.add(url);
						}
					}
					
				} else {
					_urlList.add(DEFAULT_REMOTE_INVOKER_URL);
				}
			}
			
			if (_urlList.size() == 1) {
				// Only one entry in the list, so just return it
				ret = _urlList;
			} else {
				ret = new java.util.ArrayList<String>(_urlList);
				
				Collections.rotate(_urlList, -1);
			}
		}
		
		return (ret);
	}
	
	/**
	 * This method sets the JMX server URL.
	 * 
	 * @param url The JMX server URL
	 */
	public void setJMXURL(String url) {
		_jmxUrl = url;
	}
	
	/**
	 * This method returns the JMX server URL.
	 * 
	 * @return The JMX server URL
	 */
	public String getJMXURL() {
		return (_jmxUrl);
	}
	
	/**
	 * This method sets the JMX Username.
	 * 
	 * @param username The JMX Username
	 */
	public void setJMXUsername(String username) {
		_jmxUsername = username;
	}
	
	/**
	 * This method returns the JMX username.
	 * 
	 * @return The JMX username
	 */
	public String getJMXUsername() {
		return (_jmxUsername);
	}
	
	/**
	 * This method sets the JMX Password.
	 * 
	 * @param password The JMX Password
	 */
	public void setJMXPassword(String password) {
		_jmxPassword = password;
	}
	
	/**
	 * This method returns the JMX Password.
	 * 
	 * @return The JMX Password
	 */
	public String getJMXPassword() {
		return (_jmxPassword);
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

		java.util.List<String> urls=getURLList();
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
				
				continue;
			} catch (NullPointerException npe) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine("Remote invocation of switchyard service["+service+"] operation["
							+operation+"] failed to deserialize response");
				}
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
	    
        if (format != null && format.equals("dom")) {
	        
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
	
	/**
	 * This method returns the mbean server connection.
	 * 
	 * @return The MBean server connection
	 */
	protected synchronized MBeanServerConnection getMBeanServerConnection() throws UiException {
		if (_mbeanServerConnection == null) {
			if (LOG.isLoggable(Level.FINEST)) {
				LOG.finest("Creating JMX connector.....");
			}
			
			if (getJMXURL() == null) {
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.finest("Creating JMX connector by accessing platform bean server");
				}
				_mbeanServerConnection = ManagementFactory.getPlatformMBeanServer();
			} else {
				try {
					JMXServiceURL url = 
						    new JMXServiceURL(getJMXURL());
					
					java.util.Map<String, String[]> env = new java.util.HashMap<String, String[]>();
					
					if (_jmxUsername != null && _jmxPassword != null) {
						
						if (LOG.isLoggable(Level.FINEST)) {
							LOG.finest("Creating JMX connector for user '"+_jmxUsername+"'");
						}
						
						String[] creds = new String[2];
						creds[0] = _jmxUsername;
						creds[1] = _jmxPassword;
						env.put(JMXConnector.CREDENTIALS, creds);
					} else if (LOG.isLoggable(Level.FINEST)) {
						LOG.finest("Creating JMX connector with no user credentials");
					}
					
					JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
					
					_mbeanServerConnection = jmxc.getMBeanServerConnection();
				} catch (Exception e) {
					throw new UiException(i18n.format("SwitchYardServicesProvider.JMXConnectionFailed"), e);
				}
				
				if (LOG.isLoggable(Level.FINEST)) {
					LOG.finest("Created JMX connector: "+_mbeanServerConnection);
				}
			}
		}
		
		return (_mbeanServerConnection);
	}
	
	/**
	 * {@inheritDoc}
	 */
    public List<QName> getApplicationNames() throws UiException {
        final List<QName> apps = new ArrayList<QName>();

        try {
	        MBeanServerConnection con=getMBeanServerConnection();
	        
	        java.util.Set<ObjectInstance> results=
	        		con.queryMBeans(new ObjectName("org.switchyard.admin:type=Application,name=*"), null);
	        
	        for (ObjectInstance result : results) {
	        	java.util.Map<String,String> map=result.getObjectName().getKeyPropertyList();
	        			
	        	if (map.containsKey("name")) {
			        String name=result.getObjectName().getKeyProperty("name");
			        
		        	apps.add(parseQName(stripQuotes(name)));
	        	}
	        }
        } catch (Exception e) {
			throw new UiException(i18n.format("SwitchYardServicesProvider.AppNamesFailed"), e);
        }
        
        return apps;
    }

	/**
	 * {@inheritDoc}
	 */
    public java.util.List<ServiceSummaryBean> findServices(final ServicesFilterBean filters) throws UiException {
        final ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();

        try {
	        MBeanServerConnection con=getMBeanServerConnection();
	        
	        java.util.Set<ObjectInstance> results=
	        		con.queryMBeans(new ObjectName("org.switchyard.admin:type=Service,name=*"), null);
	        
            // TODO: Request all attributes in one operation
            
			for (ObjectInstance result : results) {
				java.util.Map<String, String> map = result.getObjectName().getKeyPropertyList();

				if (map.containsKey("name")) {
					AttributeList attrs = con.getAttributes(result.getObjectName(), new String[] { "Name",
					        "Application", "Interface" });
					String name = (String) getAttributeValue(attrs.get(0));

					if (!isSet(filters.getServiceName()) || filters.getServiceName().equals(name)) {
						ObjectName app = (ObjectName) getAttributeValue(attrs.get(1));
						String appName = stripQuotes(app.getKeyProperty("name"));

						if (!isSet(filters.getApplicationName()) || filters.getApplicationName().equals(appName)) {
							ServiceSummaryBean ssb = new ServiceSummaryBean();
							ssb.setName(name);
							ssb.setApplication(appName);
							ssb.setIface((String) getAttributeValue(attrs.get(2)));
							ssb.setBindings(Sets.newHashSet(getServiceBindings(name).values()));
							ssb.setServiceId(generateId(appName, name));
							services.add(ssb);
						}
					}
				}
			}
        } catch (Exception e) {
			throw new UiException(i18n.format("SwitchYardServicesProvider.GetServicesFailed"), e);
        }
        
        return services;
    }
    
    protected Object getAttributeValue(Object attr) {
    	if (attr instanceof javax.management.Attribute) {
    		return (((javax.management.Attribute)attr).getValue());
    	}
    	return (attr);
    }
    
    protected String stripQuotes(String text) {
    	if (text.length() >= 2 && text.charAt(0) == '\"'
    				&& text.charAt(text.length()-1) == '\"') {
    		return (text.substring(1, text.length()-1));
    	}
    	return (text);
    }

	/**
	 * This method returns the list of references associated with the supplied application
	 * and service.
	 * 
	 * @param applicationName The application
	 * @param serviceName The service name
	 * @return The list of references
	 * @throws UiException Failed to get the references
	 */
    protected List<ReferenceSummaryBean> getReferences(final String applicationName,
    							final String serviceName) throws UiException {
        final List<ReferenceSummaryBean> references = new ArrayList<ReferenceSummaryBean>();

        try {
	        MBeanServerConnection con=getMBeanServerConnection();
	        
            // TODO: Request all attributes in one operation
            
	        java.util.Set<ObjectInstance> results=
	        		con.queryMBeans(new ObjectName("org.switchyard.admin:type=Reference,name=*"), null);
	        
	        for (ObjectInstance result : results) {
	        	AttributeList attrs=con.getAttributes(result.getObjectName(),
	        					new String[]{"Name", "Application", "Interface"});
		        String name=(String)getAttributeValue(attrs.get(0));

		        ReferenceSummaryBean rsb=new ReferenceSummaryBean();
		        rsb.setName(name);
		        
		        ObjectName app=(ObjectName)getAttributeValue(attrs.get(1));
		        String appName=stripQuotes(app.getKeyProperty("name"));
		        
		        if (isSet(applicationName) ||
		        					applicationName.equals(appName)) {
			        rsb.setApplication(appName);
		        	
			        rsb.setBindings(Sets.newHashSet(getReferenceBindings(name).values()));
			        rsb.setIface((String)getAttributeValue(attrs.get(2)));
			        
			        rsb.setReferenceId(generateId(appName, name));
			        
		        	references.add(rsb);
		        }
	        }
        } catch (Exception e) {
			throw new UiException(i18n.format("SwitchYardServicesProvider.GetReferencesFailed",
								applicationName, serviceName), e);
        }

        return references;
    }

	/**
	 * {@inheritDoc}
	 */
    public ServiceBean getService(final String uuid) throws UiException {
        final ServiceBean serviceResult = new ServiceBean();

        final List<String> ids = parseId(uuid);
        if (ids.size() == 2) {
            final String applicationName = ids.get(0);
            final String serviceName = ids.get(1);

            // TODO: Request all attributes in one operation
            
            try {
    	        MBeanServerConnection con=getMBeanServerConnection();
    	        
    	        ObjectInstance instance=con.getObjectInstance(
    	        		new ObjectName("org.switchyard.admin:type=Service,name=\""+serviceName+"\""));
    	        
		        serviceResult.setName(parseQName(serviceName));
		        
		        serviceResult.setApplication(parseQName(applicationName));
		        
		        AttributeList attrs=con.getAttributes(instance.getObjectName(), new String[]{"Interface"});
		        
		        serviceResult.setServiceInterface((String)getAttributeValue(attrs.get(0)));
		        
		        serviceResult.setServiceId(uuid);
		        
		        serviceResult.setReferences(getReferences(applicationName, serviceName));
		        
		        //ObjectName app=(ObjectName)con.getAttribute(result.getObjectName(), "Application");
		        //String appName=stripQuotes(app.getKeyProperty("name"));
		        serviceResult.setServiceGraph(buildGraph(serviceName));
            } catch (Exception e) {
    			throw new UiException(i18n.format("SwitchYardServicesProvider.GetServiceFailed",
    								applicationName, serviceName), e);
            }
            
        }
        return serviceResult;
    }

    private String buildGraph(String serviceName) throws Exception {
        ActiveCollectionManager activeCollectionManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
        ActiveCollection activeCollection = activeCollectionManager.getActiveCollection("ServiceDefinitions");
        ActiveCollection activeSituations = activeCollectionManager.getActiveCollection("Situations");
        Set<ServiceDefinition> serviceDefinitions = Sets.newHashSet();
        List<Situation> situations = Lists.newArrayList();
        for (Object entry : Optional.<Iterable<?>> fromNullable(activeCollection).or(emptySet())) {
            if (entry instanceof ActiveMap.Entry
                    && ((ActiveMap.Entry) entry).getValue() instanceof ServiceDefinition) {
                serviceDefinitions.add((ServiceDefinition) ((ActiveMap.Entry) entry).getValue());
            }
        }
        for (Object obj : Optional.<Iterable<?>> fromNullable(activeSituations).or(emptyList())) {
            if (obj instanceof Situation) {
                situations.add((Situation) obj);
            }
        }
        ServiceGraph serviceGraph = ServiceDependencyBuilder.buildGraph(serviceDefinitions, situations,
                serviceName);
        if (serviceGraph == null) {
            throw new Exception("Failed to generate service dependency overview");
        }
        serviceGraph.setDescription("Generated: " + new Date());
        ServiceGraphLayoutImpl serviceGraphLayout = new ServiceGraphLayoutImpl();
        serviceGraphLayout.layout(serviceGraph);
        SVGServiceGraphGenerator serviceGraphGenerator = new SVGServiceGraphGenerator();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        serviceGraphGenerator.generate(serviceGraph, 0, byteArrayOutputStream);
        byteArrayOutputStream.close();
        return new String(byteArrayOutputStream.toByteArray());
    }

    /**
	 * {@inheritDoc}
	 */
    public ReferenceBean getReference(final String uuid) throws UiException {
        final ReferenceBean referenceResult = new ReferenceBean();

        final List<String> ids = parseId(uuid);
        if (ids.size() == 2) {
            final String applicationName = ids.get(0);
            final String referenceName = ids.get(1);

            // TODO: Request all attributes in one operation
            
            try {
    	        MBeanServerConnection con=getMBeanServerConnection();
    	        
    	        ObjectInstance instance=con.getObjectInstance(
    	        		new ObjectName("org.switchyard.admin:type=Reference,name=\""+referenceName+"\""));
    	        
    	        referenceResult.setName(parseQName(referenceName));
		        
    	        referenceResult.setApplication(parseQName(applicationName));
		        
		        AttributeList attrs=con.getAttributes(instance.getObjectName(), new String[]{"Interface"});
		        
		        referenceResult.setReferenceInterface((String)getAttributeValue(attrs.get(0)));
		        
    	        referenceResult.setReferenceId(uuid);
		        
            } catch (Exception e) {
    			throw new UiException(i18n.format("SwitchYardServicesProvider.GetReferenceFailed",
    					applicationName, referenceName), e);
            }
        }
        
        return referenceResult;
    }

    private static QName parseQName(final String value) {
        final javax.xml.namespace.QName qname = javax.xml.namespace.QName.valueOf(value);
        return new QName(qname.getNamespaceURI(), qname.getLocalPart());
    }

    private static boolean isSet(final String name) {
        return ((name != null) && (name.trim().length() > 0));
    }

    public static String generateId(final String application, final String name) {
        return escape(application) + ':' + escape(name);
    }

    private static List<String> parseId(final String id) {
        if (id == null) {
            return null;
        }
        final List<String> ids = new ArrayList<String>();
        final StringBuilder unescaped = new StringBuilder();

        final int length = id.length();
        for(int count = 0 ; count < length ; count++) {
            final char ch = id.charAt(count);
            switch (ch) {
            case ESCAPE_CHAR:
                count++;
                if (count < length) {
                    unescaped.append(id.charAt(count));
                }
                break;
            case SEPARATOR_CHAR:
                ids.add(unescaped.toString());
                unescaped.setLength(0);
                break;
            default:
                unescaped.append(ch);
            }
        }
        ids.add(unescaped.toString());
        return ids;
    }

    private static String escape(final String val) {
        if (val == null) {
            return null;
        }
        final StringBuilder escaped = new StringBuilder();
        final int length = val.length();
        for(int count = 0 ; count < length ; count++) {
            final char ch = val.charAt(count);
            switch (ch) {
            case ESCAPE_CHAR:
            case SEPARATOR_CHAR:
                escaped.append(ESCAPE_CHAR);
            default:
                escaped.append(ch);
            }
        }
        return escaped.toString();
    }

}
