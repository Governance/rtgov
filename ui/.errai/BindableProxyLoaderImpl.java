package org.jboss.errai.databinding.client;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.ui.shared.api.Locale;
import org.overlord.rtgov.ui.client.model.CallTraceBean;
import org.overlord.rtgov.ui.client.model.ComponentServiceBean;
import org.overlord.rtgov.ui.client.model.ComponentServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;

public class BindableProxyLoaderImpl implements BindableProxyLoader {
  public void loadBindableProxies() {
    class org_overlord_rtgov_ui_client_model_ServiceBeanProxy extends ServiceBean implements BindableProxy {
      private BindableProxyAgent<ServiceBean> agent;
      public org_overlord_rtgov_ui_client_model_ServiceBeanProxy(InitialState initialState) {
        this(new ServiceBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_ServiceBeanProxy(ServiceBean target, InitialState initialState) {
        agent = new BindableProxyAgent<ServiceBean>(this, target, initialState);
        agent.propertyTypes.put("references", new PropertyType(List.class, false, true));
        agent.propertyTypes.put("application", new PropertyType(QName.class, false, false));
        agent.propertyTypes.put("serviceInterface", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("serviceId", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("name", new PropertyType(QName.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ServiceBean unwrap() {
        return agent.target;
      }

      public ServiceBean deepUnwrap() {
        final ServiceBean clone = new ServiceBean();
        clone.setReferences(agent.target.getReferences());
        clone.setApplication(agent.target.getApplication());
        clone.setServiceInterface(agent.target.getServiceInterface());
        clone.setServiceId(agent.target.getServiceId());
        clone.setName(agent.target.getName());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_ServiceBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_ServiceBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public List getReferences() {
        return agent.target.getReferences();
      }

      public void setReferences(List<ReferenceSummaryBean> references) {
        List<ReferenceSummaryBean> oldValue = agent.target.getReferences();
        references = agent.ensureBoundListIsProxied("references", references);
        agent.target.setReferences(references);
        agent.updateWidgetsAndFireEvent("references", oldValue, references);
      }

      public QName getApplication() {
        return agent.target.getApplication();
      }

      public void setApplication(QName application) {
        QName oldValue = agent.target.getApplication();
        agent.target.setApplication(application);
        agent.updateWidgetsAndFireEvent("application", oldValue, application);
      }

      public String getServiceInterface() {
        return agent.target.getServiceInterface();
      }

      public void setServiceInterface(String serviceInterface) {
        String oldValue = agent.target.getServiceInterface();
        agent.target.setServiceInterface(serviceInterface);
        agent.updateWidgetsAndFireEvent("serviceInterface", oldValue, serviceInterface);
      }

      public String getServiceId() {
        return agent.target.getServiceId();
      }

      public void setServiceId(String serviceId) {
        String oldValue = agent.target.getServiceId();
        agent.target.setServiceId(serviceId);
        agent.updateWidgetsAndFireEvent("serviceId", oldValue, serviceId);
      }

      public QName getName() {
        return agent.target.getName();
      }

      public void setName(QName name) {
        QName oldValue = agent.target.getName();
        agent.target.setName(name);
        agent.updateWidgetsAndFireEvent("name", oldValue, name);
      }

      public Object get(String property) {
        if (property.equals("references")) {
          return getReferences();
        }
        if (property.equals("application")) {
          return getApplication();
        }
        if (property.equals("serviceInterface")) {
          return getServiceInterface();
        }
        if (property.equals("serviceId")) {
          return getServiceId();
        }
        if (property.equals("name")) {
          return getName();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("references")) {
          agent.target.setReferences((List<ReferenceSummaryBean>) value);
          return;
        }
        if (property.equals("application")) {
          agent.target.setApplication((QName) value);
          return;
        }
        if (property.equals("serviceInterface")) {
          agent.target.setServiceInterface((String) value);
          return;
        }
        if (property.equals("serviceId")) {
          agent.target.setServiceId((String) value);
          return;
        }
        if (property.equals("name")) {
          agent.target.setName((QName) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(ServiceBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ServiceBeanProxy((ServiceBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ServiceBeanProxy(state);
      }
    });
    class org_overlord_rtgov_ui_client_model_ServiceSummaryBeanProxy extends ServiceSummaryBean implements BindableProxy {
      private BindableProxyAgent<ServiceSummaryBean> agent;
      public org_overlord_rtgov_ui_client_model_ServiceSummaryBeanProxy(InitialState initialState) {
        this(new ServiceSummaryBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_ServiceSummaryBeanProxy(ServiceSummaryBean target, InitialState initialState) {
        agent = new BindableProxyAgent<ServiceSummaryBean>(this, target, initialState);
        agent.propertyTypes.put("iface", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("application", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("serviceId", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("bindings", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("name", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ServiceSummaryBean unwrap() {
        return agent.target;
      }

      public ServiceSummaryBean deepUnwrap() {
        final ServiceSummaryBean clone = new ServiceSummaryBean();
        clone.setIface(agent.target.getIface());
        clone.setApplication(agent.target.getApplication());
        clone.setServiceId(agent.target.getServiceId());
        clone.setBindings(agent.target.getBindings());
        clone.setName(agent.target.getName());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_ServiceSummaryBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_ServiceSummaryBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public String getIface() {
        return agent.target.getIface();
      }

      public ServiceSummaryBean setIface(String iface) {
        String oldValue = agent.target.getIface();
        final ServiceSummaryBean returnValueOfSetter = agent.target.setIface(iface);
        agent.updateWidgetsAndFireEvent("iface", oldValue, iface);
        return returnValueOfSetter;
      }

      public String getApplication() {
        return agent.target.getApplication();
      }

      public ServiceSummaryBean setApplication(String application) {
        String oldValue = agent.target.getApplication();
        final ServiceSummaryBean returnValueOfSetter = agent.target.setApplication(application);
        agent.updateWidgetsAndFireEvent("application", oldValue, application);
        return returnValueOfSetter;
      }

      public String getServiceId() {
        return agent.target.getServiceId();
      }

      public void setServiceId(String serviceId) {
        String oldValue = agent.target.getServiceId();
        agent.target.setServiceId(serviceId);
        agent.updateWidgetsAndFireEvent("serviceId", oldValue, serviceId);
      }

      public String getBindings() {
        return agent.target.getBindings();
      }

      public ServiceSummaryBean setBindings(String bindings) {
        String oldValue = agent.target.getBindings();
        final ServiceSummaryBean returnValueOfSetter = agent.target.setBindings(bindings);
        agent.updateWidgetsAndFireEvent("bindings", oldValue, bindings);
        return returnValueOfSetter;
      }

      public String getName() {
        return agent.target.getName();
      }

      public ServiceSummaryBean setName(String name) {
        String oldValue = agent.target.getName();
        final ServiceSummaryBean returnValueOfSetter = agent.target.setName(name);
        agent.updateWidgetsAndFireEvent("name", oldValue, name);
        return returnValueOfSetter;
      }

      public Object get(String property) {
        if (property.equals("iface")) {
          return getIface();
        }
        if (property.equals("application")) {
          return getApplication();
        }
        if (property.equals("serviceId")) {
          return getServiceId();
        }
        if (property.equals("bindings")) {
          return getBindings();
        }
        if (property.equals("name")) {
          return getName();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("iface")) {
          agent.target.setIface((String) value);
          return;
        }
        if (property.equals("application")) {
          agent.target.setApplication((String) value);
          return;
        }
        if (property.equals("serviceId")) {
          agent.target.setServiceId((String) value);
          return;
        }
        if (property.equals("bindings")) {
          agent.target.setBindings((String) value);
          return;
        }
        if (property.equals("name")) {
          agent.target.setName((String) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(ServiceSummaryBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ServiceSummaryBeanProxy((ServiceSummaryBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ServiceSummaryBeanProxy(state);
      }
    });
    class org_overlord_rtgov_ui_client_model_SituationSummaryBeanProxy extends SituationSummaryBean implements BindableProxy {
      private BindableProxyAgent<SituationSummaryBean> agent;
      public org_overlord_rtgov_ui_client_model_SituationSummaryBeanProxy(InitialState initialState) {
        this(new SituationSummaryBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_SituationSummaryBeanProxy(SituationSummaryBean target, InitialState initialState) {
        agent = new BindableProxyAgent<SituationSummaryBean>(this, target, initialState);
        agent.propertyTypes.put("timestamp", new PropertyType(Date.class, false, false));
        agent.propertyTypes.put("description", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("subject", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("assignedTo", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("severity", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("properties", new PropertyType(Map.class, false, false));
        agent.propertyTypes.put("type", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("resolutionState", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("situationId", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public SituationSummaryBean unwrap() {
        return agent.target;
      }

      public SituationSummaryBean deepUnwrap() {
        final SituationSummaryBean clone = new SituationSummaryBean();
        clone.setTimestamp(agent.target.getTimestamp());
        clone.setDescription(agent.target.getDescription());
        clone.setSubject(agent.target.getSubject());
        clone.setSeverity(agent.target.getSeverity());
        clone.setProperties(agent.target.getProperties());
        clone.setType(agent.target.getType());
        clone.setSituationId(agent.target.getSituationId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_SituationSummaryBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_SituationSummaryBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public Date getTimestamp() {
        return agent.target.getTimestamp();
      }

      public void setTimestamp(Date timestamp) {
        Date oldValue = agent.target.getTimestamp();
        agent.target.setTimestamp(timestamp);
        agent.updateWidgetsAndFireEvent("timestamp", oldValue, timestamp);
      }

      public String getDescription() {
        return agent.target.getDescription();
      }

      public void setDescription(String description) {
        String oldValue = agent.target.getDescription();
        agent.target.setDescription(description);
        agent.updateWidgetsAndFireEvent("description", oldValue, description);
      }

      public String getSubject() {
        return agent.target.getSubject();
      }

      public void setSubject(String subject) {
        String oldValue = agent.target.getSubject();
        agent.target.setSubject(subject);
        agent.updateWidgetsAndFireEvent("subject", oldValue, subject);
      }

      public String getAssignedTo() {
        return agent.target.getAssignedTo();
      }

      public String getSeverity() {
        return agent.target.getSeverity();
      }

      public void setSeverity(String severity) {
        String oldValue = agent.target.getSeverity();
        agent.target.setSeverity(severity);
        agent.updateWidgetsAndFireEvent("severity", oldValue, severity);
      }

      public Map getProperties() {
        return agent.target.getProperties();
      }

      public void setProperties(Map<String, String> properties) {
        Map<String, String> oldValue = agent.target.getProperties();
        agent.target.setProperties(properties);
        agent.updateWidgetsAndFireEvent("properties", oldValue, properties);
      }

      public String getType() {
        return agent.target.getType();
      }

      public void setType(String type) {
        String oldValue = agent.target.getType();
        agent.target.setType(type);
        agent.updateWidgetsAndFireEvent("type", oldValue, type);
      }

      public String getResolutionState() {
        return agent.target.getResolutionState();
      }

      public String getSituationId() {
        return agent.target.getSituationId();
      }

      public void setSituationId(String situationId) {
        String oldValue = agent.target.getSituationId();
        agent.target.setSituationId(situationId);
        agent.updateWidgetsAndFireEvent("situationId", oldValue, situationId);
      }

      public Object get(String property) {
        if (property.equals("timestamp")) {
          return getTimestamp();
        }
        if (property.equals("description")) {
          return getDescription();
        }
        if (property.equals("subject")) {
          return getSubject();
        }
        if (property.equals("assignedTo")) {
          return getAssignedTo();
        }
        if (property.equals("severity")) {
          return getSeverity();
        }
        if (property.equals("properties")) {
          return getProperties();
        }
        if (property.equals("type")) {
          return getType();
        }
        if (property.equals("resolutionState")) {
          return getResolutionState();
        }
        if (property.equals("situationId")) {
          return getSituationId();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("timestamp")) {
          agent.target.setTimestamp((Date) value);
          return;
        }
        if (property.equals("description")) {
          agent.target.setDescription((String) value);
          return;
        }
        if (property.equals("subject")) {
          agent.target.setSubject((String) value);
          return;
        }
        if (property.equals("severity")) {
          agent.target.setSeverity((String) value);
          return;
        }
        if (property.equals("properties")) {
          agent.target.setProperties((Map<String, String>) value);
          return;
        }
        if (property.equals("type")) {
          agent.target.setType((String) value);
          return;
        }
        if (property.equals("situationId")) {
          agent.target.setSituationId((String) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(SituationSummaryBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_SituationSummaryBeanProxy((SituationSummaryBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_SituationSummaryBeanProxy(state);
      }
    });
    class org_overlord_rtgov_ui_client_model_ComponentServiceBeanProxy extends ComponentServiceBean implements BindableProxy {
      private BindableProxyAgent<ComponentServiceBean> agent;
      public org_overlord_rtgov_ui_client_model_ComponentServiceBeanProxy(InitialState initialState) {
        this(new ComponentServiceBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_ComponentServiceBeanProxy(ComponentServiceBean target, InitialState initialState) {
        agent = new BindableProxyAgent<ComponentServiceBean>(this, target, initialState);
        agent.propertyTypes.put("application", new PropertyType(QName.class, false, false));
        agent.propertyTypes.put("serviceInterface", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("serviceId", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("name", new PropertyType(QName.class, false, false));
        agent.propertyTypes.put("serviceImplementation", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ComponentServiceBean unwrap() {
        return agent.target;
      }

      public ComponentServiceBean deepUnwrap() {
        final ComponentServiceBean clone = new ComponentServiceBean();
        clone.setApplication(agent.target.getApplication());
        clone.setServiceInterface(agent.target.getServiceInterface());
        clone.setServiceId(agent.target.getServiceId());
        clone.setName(agent.target.getName());
        clone.setServiceImplementation(agent.target.getServiceImplementation());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_ComponentServiceBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_ComponentServiceBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public QName getApplication() {
        return agent.target.getApplication();
      }

      public void setApplication(QName application) {
        QName oldValue = agent.target.getApplication();
        agent.target.setApplication(application);
        agent.updateWidgetsAndFireEvent("application", oldValue, application);
      }

      public String getServiceInterface() {
        return agent.target.getServiceInterface();
      }

      public void setServiceInterface(String serviceInterface) {
        String oldValue = agent.target.getServiceInterface();
        agent.target.setServiceInterface(serviceInterface);
        agent.updateWidgetsAndFireEvent("serviceInterface", oldValue, serviceInterface);
      }

      public String getServiceId() {
        return agent.target.getServiceId();
      }

      public void setServiceId(String serviceId) {
        String oldValue = agent.target.getServiceId();
        agent.target.setServiceId(serviceId);
        agent.updateWidgetsAndFireEvent("serviceId", oldValue, serviceId);
      }

      public QName getName() {
        return agent.target.getName();
      }

      public void setName(QName name) {
        QName oldValue = agent.target.getName();
        agent.target.setName(name);
        agent.updateWidgetsAndFireEvent("name", oldValue, name);
      }

      public String getServiceImplementation() {
        return agent.target.getServiceImplementation();
      }

      public void setServiceImplementation(String serviceImplementation) {
        String oldValue = agent.target.getServiceImplementation();
        agent.target.setServiceImplementation(serviceImplementation);
        agent.updateWidgetsAndFireEvent("serviceImplementation", oldValue, serviceImplementation);
      }

      public Object get(String property) {
        if (property.equals("application")) {
          return getApplication();
        }
        if (property.equals("serviceInterface")) {
          return getServiceInterface();
        }
        if (property.equals("serviceId")) {
          return getServiceId();
        }
        if (property.equals("name")) {
          return getName();
        }
        if (property.equals("serviceImplementation")) {
          return getServiceImplementation();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("application")) {
          agent.target.setApplication((QName) value);
          return;
        }
        if (property.equals("serviceInterface")) {
          agent.target.setServiceInterface((String) value);
          return;
        }
        if (property.equals("serviceId")) {
          agent.target.setServiceId((String) value);
          return;
        }
        if (property.equals("name")) {
          agent.target.setName((QName) value);
          return;
        }
        if (property.equals("serviceImplementation")) {
          agent.target.setServiceImplementation((String) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(ComponentServiceBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ComponentServiceBeanProxy((ComponentServiceBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ComponentServiceBeanProxy(state);
      }
    });
    class org_overlord_rtgov_ui_client_model_ReferenceBeanProxy extends ReferenceBean implements BindableProxy {
      private BindableProxyAgent<ReferenceBean> agent;
      public org_overlord_rtgov_ui_client_model_ReferenceBeanProxy(InitialState initialState) {
        this(new ReferenceBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_ReferenceBeanProxy(ReferenceBean target, InitialState initialState) {
        agent = new BindableProxyAgent<ReferenceBean>(this, target, initialState);
        agent.propertyTypes.put("application", new PropertyType(QName.class, false, false));
        agent.propertyTypes.put("referenceInterface", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("name", new PropertyType(QName.class, false, false));
        agent.propertyTypes.put("referenceId", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ReferenceBean unwrap() {
        return agent.target;
      }

      public ReferenceBean deepUnwrap() {
        final ReferenceBean clone = new ReferenceBean();
        clone.setApplication(agent.target.getApplication());
        clone.setReferenceInterface(agent.target.getReferenceInterface());
        clone.setName(agent.target.getName());
        clone.setReferenceId(agent.target.getReferenceId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_ReferenceBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_ReferenceBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public QName getApplication() {
        return agent.target.getApplication();
      }

      public void setApplication(QName application) {
        QName oldValue = agent.target.getApplication();
        agent.target.setApplication(application);
        agent.updateWidgetsAndFireEvent("application", oldValue, application);
      }

      public String getReferenceInterface() {
        return agent.target.getReferenceInterface();
      }

      public void setReferenceInterface(String referenceInterface) {
        String oldValue = agent.target.getReferenceInterface();
        agent.target.setReferenceInterface(referenceInterface);
        agent.updateWidgetsAndFireEvent("referenceInterface", oldValue, referenceInterface);
      }

      public QName getName() {
        return agent.target.getName();
      }

      public void setName(QName name) {
        QName oldValue = agent.target.getName();
        agent.target.setName(name);
        agent.updateWidgetsAndFireEvent("name", oldValue, name);
      }

      public String getReferenceId() {
        return agent.target.getReferenceId();
      }

      public void setReferenceId(String referenceId) {
        String oldValue = agent.target.getReferenceId();
        agent.target.setReferenceId(referenceId);
        agent.updateWidgetsAndFireEvent("referenceId", oldValue, referenceId);
      }

      public Object get(String property) {
        if (property.equals("application")) {
          return getApplication();
        }
        if (property.equals("referenceInterface")) {
          return getReferenceInterface();
        }
        if (property.equals("name")) {
          return getName();
        }
        if (property.equals("referenceId")) {
          return getReferenceId();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("application")) {
          agent.target.setApplication((QName) value);
          return;
        }
        if (property.equals("referenceInterface")) {
          agent.target.setReferenceInterface((String) value);
          return;
        }
        if (property.equals("name")) {
          agent.target.setName((QName) value);
          return;
        }
        if (property.equals("referenceId")) {
          agent.target.setReferenceId((String) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(ReferenceBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ReferenceBeanProxy((ReferenceBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ReferenceBeanProxy(state);
      }
    });
    class org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBeanProxy extends ComponentServiceSummaryBean implements BindableProxy {
      private BindableProxyAgent<ComponentServiceSummaryBean> agent;
      public org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBeanProxy(InitialState initialState) {
        this(new ComponentServiceSummaryBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBeanProxy(ComponentServiceSummaryBean target, InitialState initialState) {
        agent = new BindableProxyAgent<ComponentServiceSummaryBean>(this, target, initialState);
        agent.propertyTypes.put("iface", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("application", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("serviceId", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("implementation", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("name", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ComponentServiceSummaryBean unwrap() {
        return agent.target;
      }

      public ComponentServiceSummaryBean deepUnwrap() {
        final ComponentServiceSummaryBean clone = new ComponentServiceSummaryBean();
        clone.setIface(agent.target.getIface());
        clone.setApplication(agent.target.getApplication());
        clone.setServiceId(agent.target.getServiceId());
        clone.setImplementation(agent.target.getImplementation());
        clone.setName(agent.target.getName());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public String getIface() {
        return agent.target.getIface();
      }

      public ComponentServiceSummaryBean setIface(String iface) {
        String oldValue = agent.target.getIface();
        final ComponentServiceSummaryBean returnValueOfSetter = agent.target.setIface(iface);
        agent.updateWidgetsAndFireEvent("iface", oldValue, iface);
        return returnValueOfSetter;
      }

      public String getApplication() {
        return agent.target.getApplication();
      }

      public ComponentServiceSummaryBean setApplication(String application) {
        String oldValue = agent.target.getApplication();
        final ComponentServiceSummaryBean returnValueOfSetter = agent.target.setApplication(application);
        agent.updateWidgetsAndFireEvent("application", oldValue, application);
        return returnValueOfSetter;
      }

      public String getServiceId() {
        return agent.target.getServiceId();
      }

      public void setServiceId(String serviceId) {
        String oldValue = agent.target.getServiceId();
        agent.target.setServiceId(serviceId);
        agent.updateWidgetsAndFireEvent("serviceId", oldValue, serviceId);
      }

      public String getImplementation() {
        return agent.target.getImplementation();
      }

      public ComponentServiceSummaryBean setImplementation(String implementation) {
        String oldValue = agent.target.getImplementation();
        final ComponentServiceSummaryBean returnValueOfSetter = agent.target.setImplementation(implementation);
        agent.updateWidgetsAndFireEvent("implementation", oldValue, implementation);
        return returnValueOfSetter;
      }

      public String getName() {
        return agent.target.getName();
      }

      public ComponentServiceSummaryBean setName(String name) {
        String oldValue = agent.target.getName();
        final ComponentServiceSummaryBean returnValueOfSetter = agent.target.setName(name);
        agent.updateWidgetsAndFireEvent("name", oldValue, name);
        return returnValueOfSetter;
      }

      public Object get(String property) {
        if (property.equals("iface")) {
          return getIface();
        }
        if (property.equals("application")) {
          return getApplication();
        }
        if (property.equals("serviceId")) {
          return getServiceId();
        }
        if (property.equals("implementation")) {
          return getImplementation();
        }
        if (property.equals("name")) {
          return getName();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("iface")) {
          agent.target.setIface((String) value);
          return;
        }
        if (property.equals("application")) {
          agent.target.setApplication((String) value);
          return;
        }
        if (property.equals("serviceId")) {
          agent.target.setServiceId((String) value);
          return;
        }
        if (property.equals("implementation")) {
          agent.target.setImplementation((String) value);
          return;
        }
        if (property.equals("name")) {
          agent.target.setName((String) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(ComponentServiceSummaryBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBeanProxy((ComponentServiceSummaryBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBeanProxy(state);
      }
    });
    class org_jboss_errai_ui_shared_api_LocaleProxy extends Locale implements BindableProxy {
      private BindableProxyAgent<Locale> agent;
      public org_jboss_errai_ui_shared_api_LocaleProxy(InitialState initialState) {
        this(new Locale(), initialState);
      }

      public org_jboss_errai_ui_shared_api_LocaleProxy(Locale target, InitialState initialState) {
        agent = new BindableProxyAgent<Locale>(this, target, initialState);
        agent.propertyTypes.put("locale", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("label", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public Locale unwrap() {
        return agent.target;
      }

      public Locale deepUnwrap() {
        final Locale clone = new Locale();
        clone.setLocale(agent.target.getLocale());
        clone.setLabel(agent.target.getLabel());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_jboss_errai_ui_shared_api_LocaleProxy) {
          obj = ((org_jboss_errai_ui_shared_api_LocaleProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public String getLocale() {
        return agent.target.getLocale();
      }

      public void setLocale(String locale) {
        String oldValue = agent.target.getLocale();
        agent.target.setLocale(locale);
        agent.updateWidgetsAndFireEvent("locale", oldValue, locale);
      }

      public String getLabel() {
        return agent.target.getLabel();
      }

      public void setLabel(String label) {
        String oldValue = agent.target.getLabel();
        agent.target.setLabel(label);
        agent.updateWidgetsAndFireEvent("label", oldValue, label);
      }

      public Object get(String property) {
        if (property.equals("locale")) {
          return getLocale();
        }
        if (property.equals("label")) {
          return getLabel();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("locale")) {
          agent.target.setLocale((String) value);
          return;
        }
        if (property.equals("label")) {
          agent.target.setLabel((String) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(Locale.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_jboss_errai_ui_shared_api_LocaleProxy((Locale) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_jboss_errai_ui_shared_api_LocaleProxy(state);
      }
    });
    class org_overlord_rtgov_ui_client_model_ReferenceSummaryBeanProxy extends ReferenceSummaryBean implements BindableProxy {
      private BindableProxyAgent<ReferenceSummaryBean> agent;
      public org_overlord_rtgov_ui_client_model_ReferenceSummaryBeanProxy(InitialState initialState) {
        this(new ReferenceSummaryBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_ReferenceSummaryBeanProxy(ReferenceSummaryBean target, InitialState initialState) {
        agent = new BindableProxyAgent<ReferenceSummaryBean>(this, target, initialState);
        agent.propertyTypes.put("iface", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("application", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("bindings", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("name", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("referenceId", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public ReferenceSummaryBean unwrap() {
        return agent.target;
      }

      public ReferenceSummaryBean deepUnwrap() {
        final ReferenceSummaryBean clone = new ReferenceSummaryBean();
        clone.setIface(agent.target.getIface());
        clone.setApplication(agent.target.getApplication());
        clone.setBindings(agent.target.getBindings());
        clone.setName(agent.target.getName());
        clone.setReferenceId(agent.target.getReferenceId());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_ReferenceSummaryBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_ReferenceSummaryBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public String getIface() {
        return agent.target.getIface();
      }

      public ReferenceSummaryBean setIface(String iface) {
        String oldValue = agent.target.getIface();
        final ReferenceSummaryBean returnValueOfSetter = agent.target.setIface(iface);
        agent.updateWidgetsAndFireEvent("iface", oldValue, iface);
        return returnValueOfSetter;
      }

      public String getApplication() {
        return agent.target.getApplication();
      }

      public ReferenceSummaryBean setApplication(String application) {
        String oldValue = agent.target.getApplication();
        final ReferenceSummaryBean returnValueOfSetter = agent.target.setApplication(application);
        agent.updateWidgetsAndFireEvent("application", oldValue, application);
        return returnValueOfSetter;
      }

      public String getBindings() {
        return agent.target.getBindings();
      }

      public ReferenceSummaryBean setBindings(String bindings) {
        String oldValue = agent.target.getBindings();
        final ReferenceSummaryBean returnValueOfSetter = agent.target.setBindings(bindings);
        agent.updateWidgetsAndFireEvent("bindings", oldValue, bindings);
        return returnValueOfSetter;
      }

      public String getName() {
        return agent.target.getName();
      }

      public ReferenceSummaryBean setName(String name) {
        String oldValue = agent.target.getName();
        final ReferenceSummaryBean returnValueOfSetter = agent.target.setName(name);
        agent.updateWidgetsAndFireEvent("name", oldValue, name);
        return returnValueOfSetter;
      }

      public String getReferenceId() {
        return agent.target.getReferenceId();
      }

      public void setReferenceId(String referenceId) {
        String oldValue = agent.target.getReferenceId();
        agent.target.setReferenceId(referenceId);
        agent.updateWidgetsAndFireEvent("referenceId", oldValue, referenceId);
      }

      public Object get(String property) {
        if (property.equals("iface")) {
          return getIface();
        }
        if (property.equals("application")) {
          return getApplication();
        }
        if (property.equals("bindings")) {
          return getBindings();
        }
        if (property.equals("name")) {
          return getName();
        }
        if (property.equals("referenceId")) {
          return getReferenceId();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("iface")) {
          agent.target.setIface((String) value);
          return;
        }
        if (property.equals("application")) {
          agent.target.setApplication((String) value);
          return;
        }
        if (property.equals("bindings")) {
          agent.target.setBindings((String) value);
          return;
        }
        if (property.equals("name")) {
          agent.target.setName((String) value);
          return;
        }
        if (property.equals("referenceId")) {
          agent.target.setReferenceId((String) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }
    }
    BindableProxyFactory.addBindableProxy(ReferenceSummaryBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ReferenceSummaryBeanProxy((ReferenceSummaryBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_ReferenceSummaryBeanProxy(state);
      }
    });
    class org_overlord_rtgov_ui_client_model_SituationBeanProxy extends SituationBean implements BindableProxy {
      private BindableProxyAgent<SituationBean> agent;
      public org_overlord_rtgov_ui_client_model_SituationBeanProxy(InitialState initialState) {
        this(new SituationBean(), initialState);
      }

      public org_overlord_rtgov_ui_client_model_SituationBeanProxy(SituationBean target, InitialState initialState) {
        agent = new BindableProxyAgent<SituationBean>(this, target, initialState);
        agent.propertyTypes.put("resubmitPossible", new PropertyType(Boolean.class, false, false));
        agent.propertyTypes.put("resubmitError", new PropertyType(Boolean.class, false, false));
        agent.propertyTypes.put("resubmitBy", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("subject", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("assignedTo", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("severity", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("resubmitResult", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("properties", new PropertyType(Map.class, false, false));
        agent.propertyTypes.put("type", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("situationId", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("timestamp", new PropertyType(Date.class, false, false));
        agent.propertyTypes.put("message", new PropertyType(MessageBean.class, false, false));
        agent.propertyTypes.put("description", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("context", new PropertyType(Map.class, false, false));
        agent.propertyTypes.put("assignedToCurrentUser", new PropertyType(Boolean.class, false, false));
        agent.propertyTypes.put("resubmitErrorMessage", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("callTrace", new PropertyType(CallTraceBean.class, false, false));
        agent.propertyTypes.put("takeoverPossible", new PropertyType(Boolean.class, false, false));
        agent.propertyTypes.put("resubmitAt", new PropertyType(String.class, false, false));
        agent.propertyTypes.put("resolutionState", new PropertyType(String.class, false, false));
        agent.copyValues();
      }

      public BindableProxyAgent getAgent() {
        return agent;
      }

      public void updateWidgets() {
        agent.updateWidgetsAndFireEvents();
      }

      public SituationBean unwrap() {
        return agent.target;
      }

      public SituationBean deepUnwrap() {
        final SituationBean clone = new SituationBean();
        clone.setResubmitPossible(agent.target.isResubmitPossible());
        clone.setSubject(agent.target.getSubject());
        clone.setSeverity(agent.target.getSeverity());
        clone.setProperties(agent.target.getProperties());
        clone.setType(agent.target.getType());
        clone.setSituationId(agent.target.getSituationId());
        clone.setTimestamp(agent.target.getTimestamp());
        clone.setMessage(agent.target.getMessage());
        clone.setDescription(agent.target.getDescription());
        clone.setContext(agent.target.getContext());
        clone.setAssignedToCurrentUser(agent.target.isAssignedToCurrentUser());
        clone.setCallTrace(agent.target.getCallTrace());
        clone.setTakeoverPossible(agent.target.isTakeoverPossible());
        return clone;
      }

      public boolean equals(Object obj) {
        if (obj instanceof org_overlord_rtgov_ui_client_model_SituationBeanProxy) {
          obj = ((org_overlord_rtgov_ui_client_model_SituationBeanProxy) obj).unwrap();
        }
        return agent.target.equals(obj);
      }

      public int hashCode() {
        return agent.target.hashCode();
      }

      public String toString() {
        return agent.target.toString();
      }

      public boolean isResubmitPossible() {
        return agent.target.isResubmitPossible();
      }

      public void setResubmitPossible(boolean resubmitPossible) {
        boolean oldValue = agent.target.isResubmitPossible();
        agent.target.setResubmitPossible(resubmitPossible);
        agent.updateWidgetsAndFireEvent("resubmitPossible", oldValue, resubmitPossible);
      }

      public boolean isResubmitError() {
        return agent.target.isResubmitError();
      }

      public String getResubmitBy() {
        return agent.target.getResubmitBy();
      }

      public String getSubject() {
        return agent.target.getSubject();
      }

      public void setSubject(String subject) {
        String oldValue = agent.target.getSubject();
        agent.target.setSubject(subject);
        agent.updateWidgetsAndFireEvent("subject", oldValue, subject);
      }

      public String getAssignedTo() {
        return agent.target.getAssignedTo();
      }

      public String getSeverity() {
        return agent.target.getSeverity();
      }

      public void setSeverity(String severity) {
        String oldValue = agent.target.getSeverity();
        agent.target.setSeverity(severity);
        agent.updateWidgetsAndFireEvent("severity", oldValue, severity);
      }

      public String getResubmitResult() {
        return agent.target.getResubmitResult();
      }

      public Map getProperties() {
        return agent.target.getProperties();
      }

      public void setProperties(Map<String, String> properties) {
        Map<String, String> oldValue = agent.target.getProperties();
        agent.target.setProperties(properties);
        agent.updateWidgetsAndFireEvent("properties", oldValue, properties);
      }

      public String getType() {
        return agent.target.getType();
      }

      public void setType(String type) {
        String oldValue = agent.target.getType();
        agent.target.setType(type);
        agent.updateWidgetsAndFireEvent("type", oldValue, type);
      }

      public String getSituationId() {
        return agent.target.getSituationId();
      }

      public void setSituationId(String situationId) {
        String oldValue = agent.target.getSituationId();
        agent.target.setSituationId(situationId);
        agent.updateWidgetsAndFireEvent("situationId", oldValue, situationId);
      }

      public Date getTimestamp() {
        return agent.target.getTimestamp();
      }

      public void setTimestamp(Date timestamp) {
        Date oldValue = agent.target.getTimestamp();
        agent.target.setTimestamp(timestamp);
        agent.updateWidgetsAndFireEvent("timestamp", oldValue, timestamp);
      }

      public MessageBean getMessage() {
        return agent.target.getMessage();
      }

      public void setMessage(MessageBean message) {
        MessageBean oldValue = agent.target.getMessage();
        agent.target.setMessage(message);
        agent.updateWidgetsAndFireEvent("message", oldValue, message);
      }

      public String getDescription() {
        return agent.target.getDescription();
      }

      public void setDescription(String description) {
        String oldValue = agent.target.getDescription();
        agent.target.setDescription(description);
        agent.updateWidgetsAndFireEvent("description", oldValue, description);
      }

      public Map getContext() {
        return agent.target.getContext();
      }

      public void setContext(Map<String, String> context) {
        Map<String, String> oldValue = agent.target.getContext();
        agent.target.setContext(context);
        agent.updateWidgetsAndFireEvent("context", oldValue, context);
      }

      public boolean isAssignedToCurrentUser() {
        return agent.target.isAssignedToCurrentUser();
      }

      public void setAssignedToCurrentUser(boolean assignedToCurrentUser) {
        boolean oldValue = agent.target.isAssignedToCurrentUser();
        agent.target.setAssignedToCurrentUser(assignedToCurrentUser);
        agent.updateWidgetsAndFireEvent("assignedToCurrentUser", oldValue, assignedToCurrentUser);
      }

      public String getResubmitErrorMessage() {
        return agent.target.getResubmitErrorMessage();
      }

      public CallTraceBean getCallTrace() {
        return agent.target.getCallTrace();
      }

      public void setCallTrace(CallTraceBean callTrace) {
        CallTraceBean oldValue = agent.target.getCallTrace();
        agent.target.setCallTrace(callTrace);
        agent.updateWidgetsAndFireEvent("callTrace", oldValue, callTrace);
      }

      public boolean isTakeoverPossible() {
        return agent.target.isTakeoverPossible();
      }

      public void setTakeoverPossible(boolean takeoverPossible) {
        boolean oldValue = agent.target.isTakeoverPossible();
        agent.target.setTakeoverPossible(takeoverPossible);
        agent.updateWidgetsAndFireEvent("takeoverPossible", oldValue, takeoverPossible);
      }

      public String getResubmitAt() {
        return agent.target.getResubmitAt();
      }

      public String getResolutionState() {
        return agent.target.getResolutionState();
      }

      public Object get(String property) {
        if (property.equals("resubmitPossible")) {
          return isResubmitPossible();
        }
        if (property.equals("resubmitError")) {
          return isResubmitError();
        }
        if (property.equals("resubmitBy")) {
          return getResubmitBy();
        }
        if (property.equals("subject")) {
          return getSubject();
        }
        if (property.equals("assignedTo")) {
          return getAssignedTo();
        }
        if (property.equals("severity")) {
          return getSeverity();
        }
        if (property.equals("resubmitResult")) {
          return getResubmitResult();
        }
        if (property.equals("properties")) {
          return getProperties();
        }
        if (property.equals("type")) {
          return getType();
        }
        if (property.equals("situationId")) {
          return getSituationId();
        }
        if (property.equals("timestamp")) {
          return getTimestamp();
        }
        if (property.equals("message")) {
          return getMessage();
        }
        if (property.equals("description")) {
          return getDescription();
        }
        if (property.equals("context")) {
          return getContext();
        }
        if (property.equals("assignedToCurrentUser")) {
          return isAssignedToCurrentUser();
        }
        if (property.equals("resubmitErrorMessage")) {
          return getResubmitErrorMessage();
        }
        if (property.equals("callTrace")) {
          return getCallTrace();
        }
        if (property.equals("takeoverPossible")) {
          return isTakeoverPossible();
        }
        if (property.equals("resubmitAt")) {
          return getResubmitAt();
        }
        if (property.equals("resolutionState")) {
          return getResolutionState();
        }
        throw new NonExistingPropertyException(property);
      }

      public void set(String property, Object value) {
        if (property.equals("resubmitPossible")) {
          agent.target.setResubmitPossible((Boolean) value);
          return;
        }
        if (property.equals("subject")) {
          agent.target.setSubject((String) value);
          return;
        }
        if (property.equals("severity")) {
          agent.target.setSeverity((String) value);
          return;
        }
        if (property.equals("properties")) {
          agent.target.setProperties((Map<String, String>) value);
          return;
        }
        if (property.equals("type")) {
          agent.target.setType((String) value);
          return;
        }
        if (property.equals("situationId")) {
          agent.target.setSituationId((String) value);
          return;
        }
        if (property.equals("timestamp")) {
          agent.target.setTimestamp((Date) value);
          return;
        }
        if (property.equals("message")) {
          agent.target.setMessage((MessageBean) value);
          return;
        }
        if (property.equals("description")) {
          agent.target.setDescription((String) value);
          return;
        }
        if (property.equals("context")) {
          agent.target.setContext((Map<String, String>) value);
          return;
        }
        if (property.equals("assignedToCurrentUser")) {
          agent.target.setAssignedToCurrentUser((Boolean) value);
          return;
        }
        if (property.equals("callTrace")) {
          agent.target.setCallTrace((CallTraceBean) value);
          return;
        }
        if (property.equals("takeoverPossible")) {
          agent.target.setTakeoverPossible((Boolean) value);
          return;
        }
        throw new NonExistingPropertyException(property);
      }

      public boolean hasMessage() {
        final boolean returnValue = agent.target.hasMessage();
        agent.updateWidgetsAndFireEvents();
        return returnValue;
      }
    }
    BindableProxyFactory.addBindableProxy(SituationBean.class, new BindableProxyProvider() {
      public BindableProxy getBindableProxy(Object model, InitialState state) {
        return new org_overlord_rtgov_ui_client_model_SituationBeanProxy((SituationBean) model, state);
      }
      public BindableProxy getBindableProxy(InitialState state) {
        return new org_overlord_rtgov_ui_client_model_SituationBeanProxy(state);
      }
    });
  }
}