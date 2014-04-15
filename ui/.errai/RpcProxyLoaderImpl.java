package org.jboss.errai.bus.client.framework;

import java.util.List;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.builder.RemoteCallSendable;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.common.client.framework.ProxyProvider;
import org.jboss.errai.common.client.framework.RemoteServiceProxyFactory;
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.shared.services.IServicesService;
import org.overlord.rtgov.ui.client.shared.services.ISituationsService;

public class RpcProxyLoaderImpl implements RpcProxyLoader {
  public void loadProxies(final MessageBus bus) {
    class org_overlord_rtgov_ui_client_shared_services_IServicesServiceImpl extends AbstractRpcProxy implements IServicesService {
      public List getApplicationNames() {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("getApplicationNames:", qualifiers, new Object[] { }).respondTo(List.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("getApplicationNames:", qualifiers, new Object[] { }).respondTo(List.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_IServicesServiceImpl.this.sendRequest(bus, sendable);
        return null;
      }

      public ServiceResultSetBean findServices(final ServicesFilterBean a0, final int a1, final String a2, final boolean a3) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("findServices:org.overlord.rtgov.ui.client.model.ServicesFilterBean:int:java.lang.String:boolean:", qualifiers, new Object[] { a0, a1, a2, a3 }).respondTo(ServiceResultSetBean.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("findServices:org.overlord.rtgov.ui.client.model.ServicesFilterBean:int:java.lang.String:boolean:", qualifiers, new Object[] { a0, a1, a2, a3 }).respondTo(ServiceResultSetBean.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_IServicesServiceImpl.this.sendRequest(bus, sendable);
        return null;
      }

      public ServiceBean getService(final String a0) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("getService:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(ServiceBean.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("getService:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(ServiceBean.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_IServicesServiceImpl.this.sendRequest(bus, sendable);
        return null;
      }

      public ReferenceBean getReference(final String a0) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("getReference:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(ReferenceBean.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.IServicesService").endpoint("getReference:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(ReferenceBean.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_IServicesServiceImpl.this.sendRequest(bus, sendable);
        return null;
      }
    }
    RemoteServiceProxyFactory.addRemoteProxy(IServicesService.class, new ProxyProvider() {
      public Object getProxy() {
        return new org_overlord_rtgov_ui_client_shared_services_IServicesServiceImpl();
      }
    });
    class org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl extends AbstractRpcProxy implements ISituationsService {
      public SituationResultSetBean search(final SituationsFilterBean a0, final int a1, final String a2, final boolean a3) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("search:org.overlord.rtgov.ui.client.model.SituationsFilterBean:int:java.lang.String:boolean:", qualifiers, new Object[] { a0, a1, a2, a3 }).respondTo(SituationResultSetBean.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("search:org.overlord.rtgov.ui.client.model.SituationsFilterBean:int:java.lang.String:boolean:", qualifiers, new Object[] { a0, a1, a2, a3 }).respondTo(SituationResultSetBean.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl.this.sendRequest(bus, sendable);
        return null;
      }

      public SituationBean get(final String a0) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("get:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(SituationBean.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("get:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(SituationBean.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl.this.sendRequest(bus, sendable);
        return null;
      }

      public void resubmit(final String a0, final String a1) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("resubmit:java.lang.String:java.lang.String:", qualifiers, new Object[] { a0, a1 }).respondTo(void.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("resubmit:java.lang.String:java.lang.String:", qualifiers, new Object[] { a0, a1 }).respondTo(void.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl.this.sendRequest(bus, sendable);
      }

      public BatchRetryResult resubmit(final SituationsFilterBean a0) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("resubmit:org.overlord.rtgov.ui.client.model.SituationsFilterBean:", qualifiers, new Object[] { a0 }).respondTo(BatchRetryResult.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("resubmit:org.overlord.rtgov.ui.client.model.SituationsFilterBean:", qualifiers, new Object[] { a0 }).respondTo(BatchRetryResult.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl.this.sendRequest(bus, sendable);
        return null;
      }

      public void assign(final String a0) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("assign:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(void.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("assign:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(void.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl.this.sendRequest(bus, sendable);
      }

      public void deassign(final String a0) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("deassign:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(void.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("deassign:java.lang.String:", qualifiers, new Object[] { a0 }).respondTo(void.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl.this.sendRequest(bus, sendable);
      }

      public void updateResolutionState(final String a0, final String a1) {
        RemoteCallSendable sendable = null;
        if (errorCallback == null) {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("updateResolutionState:java.lang.String:java.lang.String:", qualifiers, new Object[] { a0, a1 }).respondTo(void.class, remoteCallback).defaultErrorHandling();
        } else {
          sendable = MessageBuilder.createCall().call("org.overlord.rtgov.ui.client.shared.services.ISituationsService").endpoint("updateResolutionState:java.lang.String:java.lang.String:", qualifiers, new Object[] { a0, a1 }).respondTo(void.class, remoteCallback).errorsHandledBy(errorCallback);
        }
        org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl.this.sendRequest(bus, sendable);
      }
    }
    RemoteServiceProxyFactory.addRemoteProxy(ISituationsService.class, new ProxyProvider() {
      public Object getProxy() {
        return new org_overlord_rtgov_ui_client_shared_services_ISituationsServiceImpl();
      }
    });
  }
}