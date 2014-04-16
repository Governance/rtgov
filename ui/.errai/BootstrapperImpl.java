package org.jboss.errai.ioc.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasDragEndHandlers;
import com.google.gwt.event.dom.client.HasDragEnterHandlers;
import com.google.gwt.event.dom.client.HasDragHandlers;
import com.google.gwt.event.dom.client.HasDragLeaveHandlers;
import com.google.gwt.event.dom.client.HasDragOverHandlers;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasGestureChangeHandlers;
import com.google.gwt.event.dom.client.HasGestureEndHandlers;
import com.google.gwt.event.dom.client.HasGestureStartHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.HasTouchCancelHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchMoveHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.AutoDirectionHandler.Target;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.HasConstrainedValue;
import com.google.gwt.user.client.ui.HasDirectionalSafeHtml;
import com.google.gwt.user.client.ui.HasDirectionalText;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.InsertPanel.ForIsWidget;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LabelBase;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesChangeEvents;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesFocusEvents;
import com.google.gwt.user.client.ui.SourcesKeyboardEvents;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.enterprise.inject.Instance;
import javax.inject.Provider;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.extension.InitVotes;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.databinding.client.DataBinderProvider;
import org.jboss.errai.databinding.client.DataBindingModuleBootstrapper;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.enterprise.client.cdi.CDIEventTypeLookup;
import org.jboss.errai.enterprise.client.cdi.EventProvider;
import org.jboss.errai.enterprise.client.cdi.InstanceProvider;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.api.builtin.CallerProvider;
import org.jboss.errai.ioc.client.api.builtin.DisposerProvider;
import org.jboss.errai.ioc.client.api.builtin.IOCBeanManagerProvider;
import org.jboss.errai.ioc.client.api.builtin.InitBallotProvider;
import org.jboss.errai.ioc.client.api.builtin.RootPanelProvider;
import org.jboss.errai.ioc.client.container.BeanProvider;
import org.jboss.errai.ioc.client.container.CreationalContext;
import org.jboss.errai.ioc.client.container.DestructionCallback;
import org.jboss.errai.ioc.client.container.InitializationCallback;
import org.jboss.errai.ioc.client.container.ProxyResolver;
import org.jboss.errai.ioc.client.container.SimpleCreationalContext;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.support.bus.client.BatchCallerProvider;
import org.jboss.errai.ioc.support.bus.client.MessageBusProvider;
import org.jboss.errai.ioc.support.bus.client.RequestDispatcherProvider;
import org.jboss.errai.ioc.support.bus.client.SenderProvider;
import org.jboss.errai.ui.client.local.spi.LessStyle;
import org.jboss.errai.ui.client.widget.ListWidgetProvider;
import org.jboss.errai.ui.client.widget.LocaleListBox;
import org.jboss.errai.ui.client.widget.LocaleSelector;
import org.jboss.errai.ui.nav.client.local.Navigation;
import org.jboss.errai.ui.nav.client.local.PageTransitionProvider;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactory;
import org.jboss.errai.ui.nav.client.local.TransitionAnchorFactoryProvider;
import org.jboss.errai.ui.nav.client.local.TransitionAnchorProvider;
import org.jboss.errai.ui.shared.Template;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.overlord.commons.gwt.client.local.widgets.HtmlSnippet;
import org.overlord.commons.gwt.client.local.widgets.Pager;
import org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable;
import org.overlord.commons.gwt.client.local.widgets.WidgetTable;
import org.overlord.rtgov.ui.client.local.App;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.events.MouseInEvent.HasMouseInHandlers;
import org.overlord.rtgov.ui.client.local.events.TableSortEvent.HasTableSortHandlers;
import org.overlord.rtgov.ui.client.local.pages.AbstractPage;
import org.overlord.rtgov.ui.client.local.pages.DashboardPage;
import org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage;
import org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage;
import org.overlord.rtgov.ui.client.local.pages.ServicesPage;
import org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage;
import org.overlord.rtgov.ui.client.local.pages.SituationsPage;
import org.overlord.rtgov.ui.client.local.pages.services.ApplicationNameListBox;
import org.overlord.rtgov.ui.client.local.pages.services.ProcessingStateListBox;
import org.overlord.rtgov.ui.client.local.pages.services.ReferenceTable;
import org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters;
import org.overlord.rtgov.ui.client.local.pages.services.ServiceTable;
import org.overlord.rtgov.ui.client.local.pages.situations.CallTraceDetails;
import org.overlord.rtgov.ui.client.local.pages.situations.CallTraceWidget;
import org.overlord.rtgov.ui.client.local.pages.situations.ResolutionStateListBox;
import org.overlord.rtgov.ui.client.local.pages.situations.SeverityListBox;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationPropertiesTable;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationTable;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationWatcherEvent;
import org.overlord.rtgov.ui.client.local.pages.situations.SituationWatcherEvents;
import org.overlord.rtgov.ui.client.local.services.NotificationService;
import org.overlord.rtgov.ui.client.local.services.ServicesRpcService;
import org.overlord.rtgov.ui.client.local.services.SituationsRpcService;
import org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler.VoidInvocationHandler;
import org.overlord.rtgov.ui.client.local.util.DataBindingDateTimeConverter;
import org.overlord.rtgov.ui.client.local.util.DataBindingQNameLocalPartConverter;
import org.overlord.rtgov.ui.client.local.util.DataBindingQNameNamespaceConverter;
import org.overlord.rtgov.ui.client.local.widgets.DateTimePicker;
import org.overlord.rtgov.ui.client.local.widgets.ToggleSwitch;
import org.overlord.rtgov.ui.client.local.widgets.common.AbstractFilterListBox;
import org.overlord.rtgov.ui.client.local.widgets.common.SortableTemplatedWidgetTable;
import org.overlord.rtgov.ui.client.local.widgets.common.SourceEditor;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.shared.services.IServicesService;
import org.overlord.rtgov.ui.client.shared.services.ISituationsService;

public class BootstrapperImpl implements Bootstrapper {
  {
    new CDI().initLookupTable(CDIEventTypeLookup.get());
    new DataBindingModuleBootstrapper().run();
  }
  private final SimpleInjectionContext injContext = new SimpleInjectionContext();
  private final SimpleCreationalContext context = injContext.getRootContext();
  private final BeanProvider<EventProvider> inj1765_EventProvider_creational = new BeanProvider<EventProvider>() {
    public EventProvider getInstance(final CreationalContext context) {
      final EventProvider inj1756_EventProvider = new EventProvider();
      context.addBean(context.getBeanReference(EventProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1756_EventProvider);
      return inj1756_EventProvider;
    }
  };
  private final EventProvider inj1756_EventProvider = inj1765_EventProvider_creational.getInstance(context);
  private final BeanProvider<SenderProvider> inj1766_SenderProvider_creational = new BeanProvider<SenderProvider>() {
    public SenderProvider getInstance(final CreationalContext context) {
      final SenderProvider inj1750_SenderProvider = new SenderProvider();
      context.addBean(context.getBeanReference(SenderProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1750_SenderProvider);
      return inj1750_SenderProvider;
    }
  };
  private final SenderProvider inj1750_SenderProvider = inj1766_SenderProvider_creational.getInstance(context);
  private InitializationCallback<Navigation> init_inj1767_Navigation = new InitializationCallback<Navigation>() {
    public void init(final Navigation obj) {
      _136504311_init(obj);
    }
  };
  private DestructionCallback<Navigation> destroy_inj1767_Navigation = new DestructionCallback<Navigation>() {
    public void destroy(final Navigation obj) {
      _136504311_cleanUp(obj);
    }
  };
  private final BeanProvider<Navigation> inj1768_Navigation_creational = new BeanProvider<Navigation>() {
    public Navigation getInstance(final CreationalContext context) {
      final Navigation inj1767_Navigation = new Navigation();
      context.addBean(context.getBeanReference(Navigation.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1767_Navigation);
      context.addInitializationCallback(inj1767_Navigation, init_inj1767_Navigation);
      context.addDestructionCallback(inj1767_Navigation, destroy_inj1767_Navigation);
      return inj1767_Navigation;
    }
  };
  private final Navigation inj1767_Navigation = inj1768_Navigation_creational.getInstance(context);
  private final BeanProvider<IOCBeanManagerProvider> inj1769_IOCBeanManagerProvider_creational = new BeanProvider<IOCBeanManagerProvider>() {
    public IOCBeanManagerProvider getInstance(final CreationalContext context) {
      final IOCBeanManagerProvider inj1746_IOCBeanManagerProvider = new IOCBeanManagerProvider();
      context.addBean(context.getBeanReference(IOCBeanManagerProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1746_IOCBeanManagerProvider);
      return inj1746_IOCBeanManagerProvider;
    }
  };
  private final IOCBeanManagerProvider inj1746_IOCBeanManagerProvider = inj1769_IOCBeanManagerProvider_creational.getInstance(context);
  private final BeanProvider<CallerProvider> inj1770_CallerProvider_creational = new BeanProvider<CallerProvider>() {
    public CallerProvider getInstance(final CreationalContext context) {
      final CallerProvider inj1760_CallerProvider = new CallerProvider();
      context.addBean(context.getBeanReference(CallerProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1760_CallerProvider);
      return inj1760_CallerProvider;
    }
  };
  private final CallerProvider inj1760_CallerProvider = inj1770_CallerProvider_creational.getInstance(context);
  private InitializationCallback<SeverityListBox> init_inj1376_SeverityListBox = new InitializationCallback<SeverityListBox>() {
    public void init(final SeverityListBox obj) {
      _1183065176_postConstruct(obj);
    }
  };
  private final BeanProvider<SeverityListBox> inj1772_SeverityListBox_creational = new BeanProvider<SeverityListBox>() {
    public SeverityListBox getInstance(final CreationalContext context) {
      final SeverityListBox inj1376_SeverityListBox = new SeverityListBox();
      context.addBean(context.getBeanReference(SeverityListBox.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1376_SeverityListBox);
      final ClientMessages_inj1773_proxy inj1773_proxy = new ClientMessages_inj1773_proxy();
      context.addUnresolvedProxy(new ProxyResolver<ClientMessages>() {
        public void resolve(ClientMessages obj) {
          inj1773_proxy.__$setProxiedInstance$(obj);
          context.addProxyReference(inj1773_proxy, obj);
        }
      }, ClientMessages.class, QualifierUtil.DEFAULT_QUALIFIERS);
      _1024371274__$1433900096_i18n(inj1376_SeverityListBox, inj1773_proxy);
      context.addInitializationCallback(inj1376_SeverityListBox, init_inj1376_SeverityListBox);
      return inj1376_SeverityListBox;
    }
  };
  private final BeanProvider<TextBox> inj1774_TextBox_creational = new BeanProvider<TextBox>() {
    public TextBox getInstance(final CreationalContext context) {
      final TextBox inj1531_TextBox = new TextBox();
      context.addBean(context.getBeanReference(TextBox.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1531_TextBox);
      return inj1531_TextBox;
    }
  };
  private InitializationCallback<ResolutionStateListBox> init_inj1652_ResolutionStateListBox = new InitializationCallback<ResolutionStateListBox>() {
    public void init(final ResolutionStateListBox obj) {
      _1183065176_postConstruct(obj);
    }
  };
  private final BeanProvider<ResolutionStateListBox> inj1775_ResolutionStateListBox_creational = new BeanProvider<ResolutionStateListBox>() {
    public ResolutionStateListBox getInstance(final CreationalContext context) {
      final ResolutionStateListBox inj1652_ResolutionStateListBox = new ResolutionStateListBox();
      context.addBean(context.getBeanReference(ResolutionStateListBox.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1652_ResolutionStateListBox);
      final ClientMessages_inj1773_proxy inj1773_proxy = new ClientMessages_inj1773_proxy();
      context.addUnresolvedProxy(new ProxyResolver<ClientMessages>() {
        public void resolve(ClientMessages obj) {
          inj1773_proxy.__$setProxiedInstance$(obj);
          context.addProxyReference(inj1773_proxy, obj);
        }
      }, ClientMessages.class, QualifierUtil.DEFAULT_QUALIFIERS);
      _1181247566__$1433900096_i18n(inj1652_ResolutionStateListBox, inj1773_proxy);
      context.addInitializationCallback(inj1652_ResolutionStateListBox, init_inj1652_ResolutionStateListBox);
      return inj1652_ResolutionStateListBox;
    }
  };
  private InitializationCallback<DateTimePicker> init_inj1348_DateTimePicker = new InitializationCallback<DateTimePicker>() {
    public void init(final DateTimePicker obj) {
      _$951620175_postConstruct(obj);
    }
  };
  private final BeanProvider<DateTimePicker> inj1776_DateTimePicker_creational = new BeanProvider<DateTimePicker>() {
    public DateTimePicker getInstance(final CreationalContext context) {
      final DateTimePicker inj1348_DateTimePicker = new DateTimePicker();
      context.addBean(context.getBeanReference(DateTimePicker.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1348_DateTimePicker);
      context.addInitializationCallback(inj1348_DateTimePicker, init_inj1348_DateTimePicker);
      return inj1348_DateTimePicker;
    }
  };
  private final BeanProvider<Anchor> inj1777_Anchor_creational = new BeanProvider<Anchor>() {
    public Anchor getInstance(final CreationalContext context) {
      final Anchor inj84_Anchor = new Anchor();
      context.addBean(context.getBeanReference(Anchor.class, QualifierUtil.DEFAULT_QUALIFIERS), inj84_Anchor);
      return inj84_Anchor;
    }
  };
  private InitializationCallback<SituationFilters> init_inj1513_SituationFilters = new InitializationCallback<SituationFilters>() {
    public void init(final SituationFilters obj) {
      _148888483_postConstruct(obj);
    }
  };
  private final BeanProvider<SituationFilters> inj1771_SituationFilters_creational = new BeanProvider<SituationFilters>() {
    public SituationFilters getInstance(final CreationalContext context) {
      final SituationFilters inj1513_SituationFilters = new SituationFilters();
      context.addBean(context.getBeanReference(SituationFilters.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1513_SituationFilters);
      _148888483__1024371274_severity(inj1513_SituationFilters, inj1772_SeverityListBox_creational.getInstance(context));
      _148888483__$371269162_type(inj1513_SituationFilters, inj1774_TextBox_creational.getInstance(context));
      _148888483__1181247566_resolutionState(inj1513_SituationFilters, inj1775_ResolutionStateListBox_creational.getInstance(context));
      _148888483__$951620175_timestampFrom(inj1513_SituationFilters, inj1776_DateTimePicker_creational.getInstance(context));
      _148888483__$951620175_timestampTo(inj1513_SituationFilters, inj1776_DateTimePicker_creational.getInstance(context));
      _148888483__2084144957_clearFilters(inj1513_SituationFilters, inj1777_Anchor_creational.getInstance(context));
      _148888483__$371269162_description(inj1513_SituationFilters, inj1774_TextBox_creational.getInstance(context));
      _148888483__$371269162_subject(inj1513_SituationFilters, inj1774_TextBox_creational.getInstance(context));
      _148888483__$371269162_host(inj1513_SituationFilters, inj1774_TextBox_creational.getInstance(context));
      context.addInitializationCallback(inj1513_SituationFilters, new InitializationCallback<SituationFilters>() {
        public void init(final SituationFilters obj) {
          org_overlord_rtgov_ui_client_local_pages_situations_SituationFiltersTemplateResource var2 = GWT.create(org_overlord_rtgov_ui_client_local_pages_situations_SituationFiltersTemplateResource.class);
          Element var3 = TemplateUtil.getRootTemplateElement(var2.getContents().getText(), "filter-sidebar");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/situations.html", var3);
          Map<String, Element> var4 = TemplateUtil.getDataFieldElements(var3);
          Map<String, Widget> var5 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__1024371274_severity(inj1513_SituationFilters), var4, "severity");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__$371269162_type(inj1513_SituationFilters), var4, "type");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__1181247566_resolutionState(inj1513_SituationFilters), var4, "resolutionState");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__$951620175_timestampFrom(inj1513_SituationFilters), var4, "timestampFrom");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__$951620175_timestampTo(inj1513_SituationFilters), var4, "timestampTo");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__2084144957_clearFilters(inj1513_SituationFilters), var4, "clearFilters");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__$371269162_description(inj1513_SituationFilters), var4, "description");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__$371269162_subject(inj1513_SituationFilters), var4, "subject");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters", "org/overlord/rtgov/ui/client/local/site/situations.html", _148888483__$371269162_host(inj1513_SituationFilters), var4, "host");
          var5.put("severity", _148888483__1024371274_severity(inj1513_SituationFilters));
          var5.put("type", _148888483__$371269162_type(inj1513_SituationFilters));
          var5.put("resolutionState", _148888483__1181247566_resolutionState(inj1513_SituationFilters));
          var5.put("timestampFrom", _148888483__$951620175_timestampFrom(inj1513_SituationFilters));
          var5.put("timestampTo", _148888483__$951620175_timestampTo(inj1513_SituationFilters));
          var5.put("clearFilters", _148888483__2084144957_clearFilters(inj1513_SituationFilters));
          var5.put("description", _148888483__$371269162_description(inj1513_SituationFilters));
          var5.put("subject", _148888483__$371269162_subject(inj1513_SituationFilters));
          var5.put("host", _148888483__$371269162_host(inj1513_SituationFilters));
          TemplateUtil.initWidget(inj1513_SituationFilters, var3, var5.values());
        }
      });
      context.addInitializationCallback(inj1513_SituationFilters, init_inj1513_SituationFilters);
      return inj1513_SituationFilters;
    }
  };
  private final BeanProvider<MessageBusProvider> inj1778_MessageBusProvider_creational = new BeanProvider<MessageBusProvider>() {
    public MessageBusProvider getInstance(final CreationalContext context) {
      final MessageBusProvider inj1738_MessageBusProvider = new MessageBusProvider();
      context.addBean(context.getBeanReference(MessageBusProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1738_MessageBusProvider);
      return inj1738_MessageBusProvider;
    }
  };
  private final MessageBusProvider inj1738_MessageBusProvider = inj1778_MessageBusProvider_creational.getInstance(context);
  private final BeanProvider<RequestDispatcherProvider> inj1779_RequestDispatcherProvider_creational = new BeanProvider<RequestDispatcherProvider>() {
    public RequestDispatcherProvider getInstance(final CreationalContext context) {
      final RequestDispatcherProvider inj1740_RequestDispatcherProvider = new RequestDispatcherProvider();
      context.addBean(context.getBeanReference(RequestDispatcherProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1740_RequestDispatcherProvider);
      return inj1740_RequestDispatcherProvider;
    }
  };
  private final RequestDispatcherProvider inj1740_RequestDispatcherProvider = inj1779_RequestDispatcherProvider_creational.getInstance(context);
  private final BeanProvider<InstanceProvider> inj1780_InstanceProvider_creational = new BeanProvider<InstanceProvider>() {
    public InstanceProvider getInstance(final CreationalContext context) {
      final InstanceProvider inj1752_InstanceProvider = new InstanceProvider();
      context.addBean(context.getBeanReference(InstanceProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1752_InstanceProvider);
      return inj1752_InstanceProvider;
    }
  };
  private final InstanceProvider inj1752_InstanceProvider = inj1780_InstanceProvider_creational.getInstance(context);
  private final BeanProvider<RootPanelProvider> inj1781_RootPanelProvider_creational = new BeanProvider<RootPanelProvider>() {
    public RootPanelProvider getInstance(final CreationalContext context) {
      final RootPanelProvider inj1744_RootPanelProvider = new RootPanelProvider();
      context.addBean(context.getBeanReference(RootPanelProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1744_RootPanelProvider);
      return inj1744_RootPanelProvider;
    }
  };
  private final RootPanelProvider inj1744_RootPanelProvider = inj1781_RootPanelProvider_creational.getInstance(context);
  private InitializationCallback<NotificationService> init_inj1782_NotificationService = new InitializationCallback<NotificationService>() {
    public void init(final NotificationService obj) {
      _2030280515_onPostConstruct(obj);
    }
  };
  private final BeanProvider<NotificationService> inj1783_NotificationService_creational = new BeanProvider<NotificationService>() {
    public NotificationService getInstance(final CreationalContext context) {
      final NotificationService inj1782_NotificationService = new NotificationService();
      context.addBean(context.getBeanReference(NotificationService.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1782_NotificationService);
      _2030280515__2098965610_bus(inj1782_NotificationService, inj1738_MessageBusProvider.get());
      _2030280515__624081413_dispatcher(inj1782_NotificationService, inj1740_RequestDispatcherProvider.get());
      _2030280515__$124296006_rootPanel(inj1782_NotificationService, inj1744_RootPanelProvider.get());
      _2030280515__$903668163_notificationWidgetFactory(inj1782_NotificationService, inj1752_InstanceProvider.provide(new Class[] { NotificationWidget.class }, null));
      context.addInitializationCallback(inj1782_NotificationService, init_inj1782_NotificationService);
      return inj1782_NotificationService;
    }
  };
  private final NotificationService inj1782_NotificationService = inj1783_NotificationService_creational.getInstance(context);
  private final BeanProvider<TransitionAnchorProvider> inj1784_TransitionAnchorProvider_creational = new BeanProvider<TransitionAnchorProvider>() {
    public TransitionAnchorProvider getInstance(final CreationalContext context) {
      final TransitionAnchorProvider inj1758_TransitionAnchorProvider = new TransitionAnchorProvider();
      context.addBean(context.getBeanReference(TransitionAnchorProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1758_TransitionAnchorProvider);
      _$1034438370__136504311_navigation(inj1758_TransitionAnchorProvider, inj1767_Navigation);
      return inj1758_TransitionAnchorProvider;
    }
  };
  private final TransitionAnchorProvider inj1758_TransitionAnchorProvider = inj1784_TransitionAnchorProvider_creational.getInstance(context);
  private final BeanProvider<DataBinderProvider> inj1785_DataBinderProvider_creational = new BeanProvider<DataBinderProvider>() {
    public DataBinderProvider getInstance(final CreationalContext context) {
      final DataBinderProvider inj1748_DataBinderProvider = new DataBinderProvider();
      context.addBean(context.getBeanReference(DataBinderProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1748_DataBinderProvider);
      return inj1748_DataBinderProvider;
    }
  };
  private final DataBinderProvider inj1748_DataBinderProvider = inj1785_DataBinderProvider_creational.getInstance(context);
  private final BeanProvider<ClientMessages> inj1787_ClientMessages_creational = new BeanProvider<ClientMessages>() {
    public ClientMessages getInstance(final CreationalContext context) {
      final ClientMessages inj1786_ClientMessages = new ClientMessages();
      context.addBean(context.getBeanReference(ClientMessages.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1786_ClientMessages);
      return inj1786_ClientMessages;
    }
  };
  private final ClientMessages inj1786_ClientMessages = inj1787_ClientMessages_creational.getInstance(context);
  private final BeanProvider<ServicesRpcService> inj1789_ServicesRpcService_creational = new BeanProvider<ServicesRpcService>() {
    public ServicesRpcService getInstance(final CreationalContext context) {
      final ServicesRpcService inj1788_ServicesRpcService = new ServicesRpcService();
      context.addBean(context.getBeanReference(ServicesRpcService.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1788_ServicesRpcService);
      _$1163117323__120980481_remoteServicesService(inj1788_ServicesRpcService, inj1760_CallerProvider.provide(new Class[] { IServicesService.class }, null));
      return inj1788_ServicesRpcService;
    }
  };
  private final ServicesRpcService inj1788_ServicesRpcService = inj1789_ServicesRpcService_creational.getInstance(context);
  private final BeanProvider<InlineLabel> inj1792_InlineLabel_creational = new BeanProvider<InlineLabel>() {
    public InlineLabel getInstance(final CreationalContext context) {
      final InlineLabel inj1317_InlineLabel = new InlineLabel();
      context.addBean(context.getBeanReference(InlineLabel.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1317_InlineLabel);
      return inj1317_InlineLabel;
    }
  };
  private final BeanProvider<HtmlSnippet> inj1793_HtmlSnippet_creational = new BeanProvider<HtmlSnippet>() {
    public HtmlSnippet getInstance(final CreationalContext context) {
      final HtmlSnippet inj1662_HtmlSnippet = new HtmlSnippet();
      context.addBean(context.getBeanReference(HtmlSnippet.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1662_HtmlSnippet);
      return inj1662_HtmlSnippet;
    }
  };
  private InitializationCallback<ReferenceDetailsPage> init_inj1790_ReferenceDetailsPage = new InitializationCallback<ReferenceDetailsPage>() {
    public void init(final ReferenceDetailsPage obj) {
      _$1203922748__onPostConstruct(obj);
      _80634777_onPostConstruct(obj);
    }
  };
  private final BeanProvider<ReferenceDetailsPage> inj1791_ReferenceDetailsPage_creational = new BeanProvider<ReferenceDetailsPage>() {
    public ReferenceDetailsPage getInstance(final CreationalContext context) {
      final ReferenceDetailsPage inj1790_ReferenceDetailsPage = new ReferenceDetailsPage();
      context.addBean(context.getBeanReference(ReferenceDetailsPage.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1790_ReferenceDetailsPage);
      _80634777__$1433900096_i18n(inj1790_ReferenceDetailsPage, inj1786_ClientMessages);
      _80634777__$1163117323_servicesService(inj1790_ReferenceDetailsPage, inj1788_ServicesRpcService);
      _80634777__2030280515_notificationService(inj1790_ReferenceDetailsPage, inj1782_NotificationService);
      _80634777__$483175978_reference(inj1790_ReferenceDetailsPage, inj1748_DataBinderProvider.provide(new Class[] { ReferenceBean.class }, null));
      _80634777__$229921779_toDashboardPage(inj1790_ReferenceDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { DashboardPage.class }, null));
      _80634777__$229921779_toServicesPage(inj1790_ReferenceDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { ServicesPage.class }, null));
      _80634777__$229921779_toSituationsPage(inj1790_ReferenceDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { SituationsPage.class }, null));
      _80634777__74478675_referenceName(inj1790_ReferenceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _80634777__74478675_referenceNamespace(inj1790_ReferenceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _80634777__74478675_applicationNamespace(inj1790_ReferenceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _80634777__74478675_applicationName(inj1790_ReferenceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _80634777__74478675_referenceInterface(inj1790_ReferenceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _80634777__38184088_loading(inj1790_ReferenceDetailsPage, inj1793_HtmlSnippet_creational.getInstance(context));
      _$1203922748__879292651_bus(inj1790_ReferenceDetailsPage, inj1738_MessageBusProvider.get());
      context.addInitializationCallback(inj1790_ReferenceDetailsPage, new InitializationCallback<ReferenceDetailsPage>() {
        public void init(final ReferenceDetailsPage obj) {
          org_overlord_rtgov_ui_client_local_pages_ReferenceDetailsPageTemplateResource var6 = GWT.create(org_overlord_rtgov_ui_client_local_pages_ReferenceDetailsPageTemplateResource.class);
          Element var7 = TemplateUtil.getRootTemplateElement(var6.getContents().getText(), "page");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/referenceDetails.html", var7);
          Map<String, Element> var8 = TemplateUtil.getDataFieldElements(var7);
          Map<String, Widget> var9 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__$229921779_toDashboardPage(inj1790_ReferenceDetailsPage), var8, "back-to-dashboard");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__$229921779_toServicesPage(inj1790_ReferenceDetailsPage), var8, "back-to-services");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__$229921779_toSituationsPage(inj1790_ReferenceDetailsPage), var8, "to-situations");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__74478675_referenceName(inj1790_ReferenceDetailsPage), var8, "referenceName");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__74478675_referenceNamespace(inj1790_ReferenceDetailsPage), var8, "referenceNamespace");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__74478675_applicationNamespace(inj1790_ReferenceDetailsPage), var8, "applicationNamespace");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__74478675_applicationName(inj1790_ReferenceDetailsPage), var8, "applicationName");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__74478675_referenceInterface(inj1790_ReferenceDetailsPage), var8, "referenceInterface");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage", "org/overlord/rtgov/ui/client/local/site/referenceDetails.html", _80634777__38184088_loading(inj1790_ReferenceDetailsPage), var8, "reference-details-loading-spinner");
          var9.put("back-to-dashboard", _80634777__$229921779_toDashboardPage(inj1790_ReferenceDetailsPage));
          var9.put("back-to-services", _80634777__$229921779_toServicesPage(inj1790_ReferenceDetailsPage));
          var9.put("to-situations", _80634777__$229921779_toSituationsPage(inj1790_ReferenceDetailsPage));
          var9.put("referenceName", _80634777__74478675_referenceName(inj1790_ReferenceDetailsPage));
          var9.put("referenceNamespace", _80634777__74478675_referenceNamespace(inj1790_ReferenceDetailsPage));
          var9.put("applicationNamespace", _80634777__74478675_applicationNamespace(inj1790_ReferenceDetailsPage));
          var9.put("applicationName", _80634777__74478675_applicationName(inj1790_ReferenceDetailsPage));
          var9.put("referenceInterface", _80634777__74478675_referenceInterface(inj1790_ReferenceDetailsPage));
          var9.put("reference-details-loading-spinner", _80634777__38184088_loading(inj1790_ReferenceDetailsPage));
          TemplateUtil.initWidget(inj1790_ReferenceDetailsPage, var7, var9.values());
        }
      });
      context.addInitializationCallback(inj1790_ReferenceDetailsPage, init_inj1790_ReferenceDetailsPage);
      context.addInitializationCallback(inj1790_ReferenceDetailsPage, new InitializationCallback<ReferenceDetailsPage>() {
        public void init(final ReferenceDetailsPage obj) {
          DataBinder binder = _80634777__$483175978_reference(inj1790_ReferenceDetailsPage);
          if (binder == null) {
            throw new RuntimeException("@AutoBound data binder for class org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage has not been initialized. Either initialize or add @Inject!");
          }
          binder.bind(_80634777__74478675_referenceName(inj1790_ReferenceDetailsPage), "name", new DataBindingQNameLocalPartConverter());
          binder.bind(_80634777__74478675_referenceNamespace(inj1790_ReferenceDetailsPage), "name", new DataBindingQNameNamespaceConverter());
          binder.bind(_80634777__74478675_applicationNamespace(inj1790_ReferenceDetailsPage), "application", new DataBindingQNameNamespaceConverter());
          binder.bind(_80634777__74478675_applicationName(inj1790_ReferenceDetailsPage), "application", new DataBindingQNameLocalPartConverter());
          binder.bind(_80634777__74478675_referenceInterface(inj1790_ReferenceDetailsPage), "referenceInterface", null);
        }
      });
      context.addDestructionCallback(inj1790_ReferenceDetailsPage, new DestructionCallback<ReferenceDetailsPage>() {
        public void destroy(final ReferenceDetailsPage obj) {
          _80634777__$483175978_reference(inj1790_ReferenceDetailsPage).unbind();
        }
      });
      return inj1790_ReferenceDetailsPage;
    }
  };
  private final BeanProvider<TransitionAnchorFactoryProvider> inj1794_TransitionAnchorFactoryProvider_creational = new BeanProvider<TransitionAnchorFactoryProvider>() {
    public TransitionAnchorFactoryProvider getInstance(final CreationalContext context) {
      final TransitionAnchorFactoryProvider inj1764_TransitionAnchorFactoryProvider = new TransitionAnchorFactoryProvider();
      context.addBean(context.getBeanReference(TransitionAnchorFactoryProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1764_TransitionAnchorFactoryProvider);
      _1496760654__136504311_navigation(inj1764_TransitionAnchorFactoryProvider, inj1767_Navigation);
      return inj1764_TransitionAnchorFactoryProvider;
    }
  };
  private final TransitionAnchorFactoryProvider inj1764_TransitionAnchorFactoryProvider = inj1794_TransitionAnchorFactoryProvider_creational.getInstance(context);
  private final BeanProvider<SituationWatcherEvent> inj1796_SituationWatcherEvent_creational = new BeanProvider<SituationWatcherEvent>() {
    public SituationWatcherEvent getInstance(final CreationalContext context) {
      final SituationWatcherEvent inj1795_SituationWatcherEvent = new SituationWatcherEvent();
      context.addBean(context.getBeanReference(SituationWatcherEvent.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1795_SituationWatcherEvent);
      _$1516327018__$1509039043_toDetailsPageLinkFactory(inj1795_SituationWatcherEvent, inj1764_TransitionAnchorFactoryProvider.provide(new Class[] { SituationDetailsPage.class }, null));
      return inj1795_SituationWatcherEvent;
    }
  };
  private InitializationCallback<ReferenceTable> init_inj1797_ReferenceTable = new InitializationCallback<ReferenceTable>() {
    public void init(final ReferenceTable obj) {
      _$1581558918_postContruct(obj);
    }
  };
  private final BeanProvider<ReferenceTable> inj1798_ReferenceTable_creational = new BeanProvider<ReferenceTable>() {
    public ReferenceTable getInstance(final CreationalContext context) {
      final ReferenceTable inj1797_ReferenceTable = new ReferenceTable();
      context.addBean(context.getBeanReference(ReferenceTable.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1797_ReferenceTable);
      _$1106092480__$1433900096_i18n(inj1797_ReferenceTable, inj1786_ClientMessages);
      _$1106092480__$1509039043_toDetailsPageLinkFactory(inj1797_ReferenceTable, inj1764_TransitionAnchorFactoryProvider.provide(new Class[] { ReferenceDetailsPage.class }, null));
      context.addInitializationCallback(inj1797_ReferenceTable, init_inj1797_ReferenceTable);
      return inj1797_ReferenceTable;
    }
  };
  private InitializationCallback<DashboardPage> init_inj1799_DashboardPage = new InitializationCallback<DashboardPage>() {
    public void init(final DashboardPage obj) {
      _$1203922748__onPostConstruct(obj);
    }
  };
  private final BeanProvider<DashboardPage> inj1800_DashboardPage_creational = new BeanProvider<DashboardPage>() {
    public DashboardPage getInstance(final CreationalContext context) {
      final DashboardPage inj1799_DashboardPage = new DashboardPage();
      context.addBean(context.getBeanReference(DashboardPage.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1799_DashboardPage);
      _$1648295248__$229921779_toServicesPage(inj1799_DashboardPage, inj1758_TransitionAnchorProvider.provide(new Class[] { ServicesPage.class }, null));
      _$1648295248__$229921779_toSituationsPage(inj1799_DashboardPage, inj1758_TransitionAnchorProvider.provide(new Class[] { SituationsPage.class }, null));
      _$1203922748__879292651_bus(inj1799_DashboardPage, inj1738_MessageBusProvider.get());
      context.addInitializationCallback(inj1799_DashboardPage, new InitializationCallback<DashboardPage>() {
        public void init(final DashboardPage obj) {
          org_overlord_rtgov_ui_client_local_pages_DashboardPageTemplateResource var10 = GWT.create(org_overlord_rtgov_ui_client_local_pages_DashboardPageTemplateResource.class);
          Element var11 = TemplateUtil.getRootTemplateElement(var10.getContents().getText(), "page");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/dashboard.html", var11);
          Map<String, Element> var12 = TemplateUtil.getDataFieldElements(var11);
          Map<String, Widget> var13 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.DashboardPage", "org/overlord/rtgov/ui/client/local/site/dashboard.html", _$1648295248__$229921779_toServicesPage(inj1799_DashboardPage), var12, "to-services-page");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.DashboardPage", "org/overlord/rtgov/ui/client/local/site/dashboard.html", _$1648295248__$229921779_toSituationsPage(inj1799_DashboardPage), var12, "to-situations-page");
          var13.put("to-services-page", _$1648295248__$229921779_toServicesPage(inj1799_DashboardPage));
          var13.put("to-situations-page", _$1648295248__$229921779_toSituationsPage(inj1799_DashboardPage));
          TemplateUtil.initWidget(inj1799_DashboardPage, var11, var13.values());
        }
      });
      context.addInitializationCallback(inj1799_DashboardPage, init_inj1799_DashboardPage);
      return inj1799_DashboardPage;
    }
  };
  private final BeanProvider<ListWidgetProvider> inj1801_ListWidgetProvider_creational = new BeanProvider<ListWidgetProvider>() {
    public ListWidgetProvider getInstance(final CreationalContext context) {
      final ListWidgetProvider inj1734_ListWidgetProvider = new ListWidgetProvider();
      context.addBean(context.getBeanReference(ListWidgetProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1734_ListWidgetProvider);
      return inj1734_ListWidgetProvider;
    }
  };
  private final ListWidgetProvider inj1734_ListWidgetProvider = inj1801_ListWidgetProvider_creational.getInstance(context);
  private InitializationCallback<ServiceTable> init_inj1802_ServiceTable = new InitializationCallback<ServiceTable>() {
    public void init(final ServiceTable obj) {
      _$1581558918_postContruct(obj);
    }
  };
  private final BeanProvider<ServiceTable> inj1803_ServiceTable_creational = new BeanProvider<ServiceTable>() {
    public ServiceTable getInstance(final CreationalContext context) {
      final ServiceTable inj1802_ServiceTable = new ServiceTable();
      context.addBean(context.getBeanReference(ServiceTable.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1802_ServiceTable);
      _$990608298__$1433900096_i18n(inj1802_ServiceTable, inj1786_ClientMessages);
      _$990608298__$1509039043_toDetailsPageLinkFactory(inj1802_ServiceTable, inj1764_TransitionAnchorFactoryProvider.provide(new Class[] { ServiceDetailsPage.class }, null));
      context.addInitializationCallback(inj1802_ServiceTable, init_inj1802_ServiceTable);
      return inj1802_ServiceTable;
    }
  };
  private final BeanProvider<LocaleSelector> inj1805_LocaleSelector_creational = new BeanProvider<LocaleSelector>() {
    public LocaleSelector getInstance(final CreationalContext context) {
      final LocaleSelector inj1804_LocaleSelector = new LocaleSelector();
      context.addBean(context.getBeanReference(LocaleSelector.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1804_LocaleSelector);
      return inj1804_LocaleSelector;
    }
  };
  private final LocaleSelector inj1804_LocaleSelector = inj1805_LocaleSelector_creational.getInstance(context);
  private final BeanProvider<LocaleListBox> inj1807_LocaleListBox_creational = new BeanProvider<LocaleListBox>() {
    public LocaleListBox getInstance(final CreationalContext context) {
      final LocaleListBox inj1806_LocaleListBox = new LocaleListBox();
      context.addBean(context.getBeanReference(LocaleListBox.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1806_LocaleListBox);
      _1350680564__$1232121576_selector(inj1806_LocaleListBox, inj1804_LocaleSelector);
      InitVotes.registerOneTimeInitCallback(new Runnable() {
        public void run() {
          inj1806_LocaleListBox.init();
        }
      });
      return inj1806_LocaleListBox;
    }
  };
  private InitializationCallback<App> init_inj1808_App = new InitializationCallback<App>() {
    public void init(final App obj) {
      obj.buildUI();
    }
  };
  private final BeanProvider<App> inj1809_App_creational = new BeanProvider<App>() {
    public App getInstance(final CreationalContext context) {
      final App inj1808_App = new App();
      context.addBean(context.getBeanReference(App.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1808_App);
      _$184711112__$124296006_rootPanel(inj1808_App, inj1744_RootPanelProvider.get());
      _$184711112__136504311_navigation(inj1808_App, inj1767_Navigation);
      context.addInitializationCallback(inj1808_App, init_inj1808_App);
      return inj1808_App;
    }
  };
  private final App inj1808_App = inj1809_App_creational.getInstance(context);
  private final BeanProvider<DisposerProvider> inj1810_DisposerProvider_creational = new BeanProvider<DisposerProvider>() {
    public DisposerProvider getInstance(final CreationalContext context) {
      final DisposerProvider inj1762_DisposerProvider = new DisposerProvider();
      context.addBean(context.getBeanReference(DisposerProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1762_DisposerProvider);
      _$1300398733__$652658075_beanManager(inj1762_DisposerProvider, inj1746_IOCBeanManagerProvider.get());
      return inj1762_DisposerProvider;
    }
  };
  private final DisposerProvider inj1762_DisposerProvider = inj1810_DisposerProvider_creational.getInstance(context);
  private InitializationCallback<SituationPropertiesTable> init_inj1811_SituationPropertiesTable = new InitializationCallback<SituationPropertiesTable>() {
    public void init(final SituationPropertiesTable obj) {
      _$1581558918_postContruct(obj);
    }
  };
  private final BeanProvider<SituationPropertiesTable> inj1812_SituationPropertiesTable_creational = new BeanProvider<SituationPropertiesTable>() {
    public SituationPropertiesTable getInstance(final CreationalContext context) {
      final SituationPropertiesTable inj1811_SituationPropertiesTable = new SituationPropertiesTable();
      context.addBean(context.getBeanReference(SituationPropertiesTable.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1811_SituationPropertiesTable);
      _$1542973949__$1433900096_i18n(inj1811_SituationPropertiesTable, inj1786_ClientMessages);
      context.addInitializationCallback(inj1811_SituationPropertiesTable, init_inj1811_SituationPropertiesTable);
      return inj1811_SituationPropertiesTable;
    }
  };
  private InitializationCallback<SituationTable> init_inj1813_SituationTable = new InitializationCallback<SituationTable>() {
    public void init(final SituationTable obj) {
      _$1581558918_postContruct(obj);
    }
  };
  private final BeanProvider<SituationTable> inj1814_SituationTable_creational = new BeanProvider<SituationTable>() {
    public SituationTable getInstance(final CreationalContext context) {
      final SituationTable inj1813_SituationTable = new SituationTable();
      context.addBean(context.getBeanReference(SituationTable.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1813_SituationTable);
      _$1600569994__$1433900096_i18n(inj1813_SituationTable, inj1786_ClientMessages);
      _$1600569994__$1509039043_toDetailsPageLinkFactory(inj1813_SituationTable, inj1764_TransitionAnchorFactoryProvider.provide(new Class[] { SituationDetailsPage.class }, null));
      context.addInitializationCallback(inj1813_SituationTable, init_inj1813_SituationTable);
      return inj1813_SituationTable;
    }
  };
  private final BeanProvider<SituationsRpcService> inj1816_SituationsRpcService_creational = new BeanProvider<SituationsRpcService>() {
    public SituationsRpcService getInstance(final CreationalContext context) {
      final SituationsRpcService inj1815_SituationsRpcService = new SituationsRpcService();
      context.addBean(context.getBeanReference(SituationsRpcService.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1815_SituationsRpcService);
      _$1124051892__120980481_remoteSituationsService(inj1815_SituationsRpcService, inj1760_CallerProvider.provide(new Class[] { ISituationsService.class }, null));
      return inj1815_SituationsRpcService;
    }
  };
  private final SituationsRpcService inj1815_SituationsRpcService = inj1816_SituationsRpcService_creational.getInstance(context);
  private InitializationCallback<ToggleSwitch> init_inj156_ToggleSwitch = new InitializationCallback<ToggleSwitch>() {
    public void init(final ToggleSwitch obj) {
      _1401717584_postConstruct(obj);
    }
  };
  private final BeanProvider<ToggleSwitch> inj1819_ToggleSwitch_creational = new BeanProvider<ToggleSwitch>() {
    public ToggleSwitch getInstance(final CreationalContext context) {
      final ToggleSwitch inj156_ToggleSwitch = new ToggleSwitch();
      context.addBean(context.getBeanReference(ToggleSwitch.class, QualifierUtil.DEFAULT_QUALIFIERS), inj156_ToggleSwitch);
      context.addInitializationCallback(inj156_ToggleSwitch, init_inj156_ToggleSwitch);
      return inj156_ToggleSwitch;
    }
  };
  private final BeanProvider<Button> inj1820_Button_creational = new BeanProvider<Button>() {
    public Button getInstance(final CreationalContext context) {
      final Button inj818_Button = new Button();
      context.addBean(context.getBeanReference(Button.class, QualifierUtil.DEFAULT_QUALIFIERS), inj818_Button);
      return inj818_Button;
    }
  };
  private final BeanProvider<Pager> inj1821_Pager_creational = new BeanProvider<Pager>() {
    public Pager getInstance(final CreationalContext context) {
      final Pager inj969_Pager = new Pager();
      context.addBean(context.getBeanReference(Pager.class, QualifierUtil.DEFAULT_QUALIFIERS), inj969_Pager);
      return inj969_Pager;
    }
  };
  private final BeanProvider<SituationWatcherEvents> inj1822_SituationWatcherEvents_creational = new BeanProvider<SituationWatcherEvents>() {
    public SituationWatcherEvents getInstance(final CreationalContext context) {
      final SituationWatcherEvents inj1595_SituationWatcherEvents = new SituationWatcherEvents();
      context.addBean(context.getBeanReference(SituationWatcherEvents.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1595_SituationWatcherEvents);
      _238502813__$903668163_sitWatchEventFactory(inj1595_SituationWatcherEvents, inj1752_InstanceProvider.provide(new Class[] { SituationWatcherEvent.class }, null));
      return inj1595_SituationWatcherEvents;
    }
  };
  private InitializationCallback<SituationsPage> init_inj1817_SituationsPage = new InitializationCallback<SituationsPage>() {
    public void init(final SituationsPage obj) {
      _$1203922748__onPostConstruct(obj);
      _102653079_postConstruct(obj);
    }
  };
  private final BeanProvider<SituationsPage> inj1818_SituationsPage_creational = new BeanProvider<SituationsPage>() {
    public SituationsPage getInstance(final CreationalContext context) {
      final SituationsPage inj1817_SituationsPage = new SituationsPage();
      context.addBean(context.getBeanReference(SituationsPage.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1817_SituationsPage);
      _102653079__$1433900096_i18n(inj1817_SituationsPage, inj1786_ClientMessages);
      _102653079__$1124051892_situationsService(inj1817_SituationsPage, inj1815_SituationsRpcService);
      _102653079__2030280515_notificationService(inj1817_SituationsPage, inj1782_NotificationService);
      _102653079__$229921779_toDashboardPage(inj1817_SituationsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { DashboardPage.class }, null));
      _102653079__$229921779_toServicesPage(inj1817_SituationsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { ServicesPage.class }, null));
      _102653079__148888483_filtersPanel(inj1817_SituationsPage, inj1771_SituationFilters_creational.getInstance(context));
      _102653079__1401717584_toggleFilterSwitch(inj1817_SituationsPage, inj1819_ToggleSwitch_creational.getInstance(context));
      _102653079__2119756730_retrySituations(inj1817_SituationsPage, inj1820_Button_creational.getInstance(context));
      _102653079__2119756730_refreshButton(inj1817_SituationsPage, inj1820_Button_creational.getInstance(context));
      _102653079__38184088_noDataMessage(inj1817_SituationsPage, inj1793_HtmlSnippet_creational.getInstance(context));
      _102653079__38184088_searchInProgressMessage(inj1817_SituationsPage, inj1793_HtmlSnippet_creational.getInstance(context));
      _102653079__$1600569994_situationsTable(inj1817_SituationsPage, inj1814_SituationTable_creational.getInstance(context));
      _102653079__1762635241_pager(inj1817_SituationsPage, inj1821_Pager_creational.getInstance(context));
      _102653079__2084144957_sitWatchButton(inj1817_SituationsPage, inj1777_Anchor_creational.getInstance(context));
      _102653079__238502813_sitWatchEvents(inj1817_SituationsPage, inj1822_SituationWatcherEvents_creational.getInstance(context));
      _$1203922748__879292651_bus(inj1817_SituationsPage, inj1738_MessageBusProvider.get());
      context.addInitializationCallback(inj1817_SituationsPage, new InitializationCallback<SituationsPage>() {
        public void init(final SituationsPage obj) {
          org_overlord_rtgov_ui_client_local_pages_SituationsPageTemplateResource var14 = GWT.create(org_overlord_rtgov_ui_client_local_pages_SituationsPageTemplateResource.class);
          Element var15 = TemplateUtil.getRootTemplateElement(var14.getContents().getText(), "page");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/situations.html", var15);
          Map<String, Element> var16 = TemplateUtil.getDataFieldElements(var15);
          Map<String, Widget> var17 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__$229921779_toDashboardPage(inj1817_SituationsPage), var16, "back-to-dashboard");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__$229921779_toServicesPage(inj1817_SituationsPage), var16, "to-services");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__148888483_filtersPanel(inj1817_SituationsPage), var16, "filter-sidebar");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__1401717584_toggleFilterSwitch(inj1817_SituationsPage), var16, "toggleFilterSwitch");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__2119756730_retrySituations(inj1817_SituationsPage), var16, "retrySituations");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__2119756730_refreshButton(inj1817_SituationsPage), var16, "btn-refresh");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__38184088_noDataMessage(inj1817_SituationsPage), var16, "situations-none");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__38184088_searchInProgressMessage(inj1817_SituationsPage), var16, "situations-searching");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__$1600569994_situationsTable(inj1817_SituationsPage), var16, "situations-table");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__1762635241_pager(inj1817_SituationsPage), var16, "situations-pager");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", ElementWrapperWidget.getWidget(_102653079__1424436749_rangeSpan(inj1817_SituationsPage)), var16, "situations-range");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", ElementWrapperWidget.getWidget(_102653079__1424436749_totalSpan(inj1817_SituationsPage)), var16, "situations-total");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__2084144957_sitWatchButton(inj1817_SituationsPage), var16, "sitwatch-btn");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationsPage", "org/overlord/rtgov/ui/client/local/site/situations.html", _102653079__238502813_sitWatchEvents(inj1817_SituationsPage), var16, "sitwatch-events");
          var17.put("back-to-dashboard", _102653079__$229921779_toDashboardPage(inj1817_SituationsPage));
          var17.put("to-services", _102653079__$229921779_toServicesPage(inj1817_SituationsPage));
          var17.put("filter-sidebar", _102653079__148888483_filtersPanel(inj1817_SituationsPage));
          var17.put("toggleFilterSwitch", _102653079__1401717584_toggleFilterSwitch(inj1817_SituationsPage));
          var17.put("retrySituations", _102653079__2119756730_retrySituations(inj1817_SituationsPage));
          var17.put("btn-refresh", _102653079__2119756730_refreshButton(inj1817_SituationsPage));
          var17.put("situations-none", _102653079__38184088_noDataMessage(inj1817_SituationsPage));
          var17.put("situations-searching", _102653079__38184088_searchInProgressMessage(inj1817_SituationsPage));
          var17.put("situations-table", _102653079__$1600569994_situationsTable(inj1817_SituationsPage));
          var17.put("situations-pager", _102653079__1762635241_pager(inj1817_SituationsPage));
          var17.put("situations-range", ElementWrapperWidget.getWidget(_102653079__1424436749_rangeSpan(inj1817_SituationsPage)));
          var17.put("situations-total", ElementWrapperWidget.getWidget(_102653079__1424436749_totalSpan(inj1817_SituationsPage)));
          var17.put("sitwatch-btn", _102653079__2084144957_sitWatchButton(inj1817_SituationsPage));
          var17.put("sitwatch-events", _102653079__238502813_sitWatchEvents(inj1817_SituationsPage));
          TemplateUtil.initWidget(inj1817_SituationsPage, var15, var17.values());
          ((HasClickHandlers) var17.get("sitwatch-btn")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              inj1817_SituationsPage.onSitWatchClick(event);
            }
          });
          ((HasClickHandlers) var17.get("btn-refresh")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              inj1817_SituationsPage.onRefreshClick(event);
            }
          });
          ((HasClickHandlers) var17.get("retrySituations")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              inj1817_SituationsPage.onRetryClick(event);
            }
          });
        }
      });
      context.addDestructionCallback(inj1817_SituationsPage, new DestructionCallback<SituationsPage>() {
        public void destroy(final SituationsPage obj) {
          ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(_102653079__1424436749_rangeSpan(inj1817_SituationsPage)));
          ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(_102653079__1424436749_totalSpan(inj1817_SituationsPage)));
        }
      });
      context.addInitializationCallback(inj1817_SituationsPage, init_inj1817_SituationsPage);
      return inj1817_SituationsPage;
    }
  };
  private final BeanProvider<BatchCallerProvider> inj1823_BatchCallerProvider_creational = new BeanProvider<BatchCallerProvider>() {
    public BatchCallerProvider getInstance(final CreationalContext context) {
      final BatchCallerProvider inj1742_BatchCallerProvider = new BatchCallerProvider();
      context.addBean(context.getBeanReference(BatchCallerProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1742_BatchCallerProvider);
      return inj1742_BatchCallerProvider;
    }
  };
  private final BatchCallerProvider inj1742_BatchCallerProvider = inj1823_BatchCallerProvider_creational.getInstance(context);
  private final BeanProvider<Label> inj1826_Label_creational = new BeanProvider<Label>() {
    public Label getInstance(final CreationalContext context) {
      final Label inj931_Label = new Label();
      context.addBean(context.getBeanReference(Label.class, QualifierUtil.DEFAULT_QUALIFIERS), inj931_Label);
      return inj931_Label;
    }
  };
  private final BeanProvider<FlowPanel> inj1827_FlowPanel_creational = new BeanProvider<FlowPanel>() {
    public FlowPanel getInstance(final CreationalContext context) {
      final FlowPanel inj557_FlowPanel = new FlowPanel();
      context.addBean(context.getBeanReference(FlowPanel.class, QualifierUtil.DEFAULT_QUALIFIERS), inj557_FlowPanel);
      return inj557_FlowPanel;
    }
  };
  private InitializationCallback<NotificationWidget> init_inj1824_NotificationWidget = new InitializationCallback<NotificationWidget>() {
    public void init(final NotificationWidget obj) {
      _74003371_onPostConstruct(obj);
    }
  };
  private final BeanProvider<NotificationWidget> inj1825_NotificationWidget_creational = new BeanProvider<NotificationWidget>() {
    public NotificationWidget getInstance(final CreationalContext context) {
      final NotificationWidget inj1824_NotificationWidget = new NotificationWidget();
      context.addBean(context.getBeanReference(NotificationWidget.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1824_NotificationWidget);
      _74003371__$1862661780_title(inj1824_NotificationWidget, inj1826_Label_creational.getInstance(context));
      _74003371__2119756730_closeButton(inj1824_NotificationWidget, inj1820_Button_creational.getInstance(context));
      _74003371__$757255186_body(inj1824_NotificationWidget, inj1827_FlowPanel_creational.getInstance(context));
      context.addInitializationCallback(inj1824_NotificationWidget, new InitializationCallback<NotificationWidget>() {
        public void init(final NotificationWidget obj) {
          org_overlord_rtgov_ui_client_local_services_notification_NotificationWidgetTemplateResource var18 = GWT.create(org_overlord_rtgov_ui_client_local_services_notification_NotificationWidgetTemplateResource.class);
          Element var19 = TemplateUtil.getRootTemplateElement(var18.getContents().getText(), "growl-dialog");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/dialogs/growl-dialog.html", var19);
          Map<String, Element> var20 = TemplateUtil.getDataFieldElements(var19);
          Map<String, Widget> var21 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget", "org/overlord/rtgov/ui/client/local/site/dialogs/growl-dialog.html", _74003371__$1862661780_title(inj1824_NotificationWidget), var20, "growl-dialog-header-title");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget", "org/overlord/rtgov/ui/client/local/site/dialogs/growl-dialog.html", _74003371__2119756730_closeButton(inj1824_NotificationWidget), var20, "growl-dialog-header-closeButton");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget", "org/overlord/rtgov/ui/client/local/site/dialogs/growl-dialog.html", _74003371__$757255186_body(inj1824_NotificationWidget), var20, "growl-dialog-body");
          var21.put("growl-dialog-header-title", _74003371__$1862661780_title(inj1824_NotificationWidget));
          var21.put("growl-dialog-header-closeButton", _74003371__2119756730_closeButton(inj1824_NotificationWidget));
          var21.put("growl-dialog-body", _74003371__$757255186_body(inj1824_NotificationWidget));
          TemplateUtil.initWidget(inj1824_NotificationWidget, var19, var21.values());
        }
      });
      context.addInitializationCallback(inj1824_NotificationWidget, init_inj1824_NotificationWidget);
      return inj1824_NotificationWidget;
    }
  };
  private InitializationCallback<ServiceDetailsPage> init_inj1828_ServiceDetailsPage = new InitializationCallback<ServiceDetailsPage>() {
    public void init(final ServiceDetailsPage obj) {
      _$1203922748__onPostConstruct(obj);
      _$549235025_onPostConstruct(obj);
    }
  };
  private final BeanProvider<ServiceDetailsPage> inj1829_ServiceDetailsPage_creational = new BeanProvider<ServiceDetailsPage>() {
    public ServiceDetailsPage getInstance(final CreationalContext context) {
      final ServiceDetailsPage inj1828_ServiceDetailsPage = new ServiceDetailsPage();
      context.addBean(context.getBeanReference(ServiceDetailsPage.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1828_ServiceDetailsPage);
      _$549235025__$1433900096_i18n(inj1828_ServiceDetailsPage, inj1786_ClientMessages);
      _$549235025__$1163117323_servicesService(inj1828_ServiceDetailsPage, inj1788_ServicesRpcService);
      _$549235025__2030280515_notificationService(inj1828_ServiceDetailsPage, inj1782_NotificationService);
      _$549235025__$483175978_service(inj1828_ServiceDetailsPage, inj1748_DataBinderProvider.provide(new Class[] { ServiceBean.class }, null));
      _$549235025__$229921779_toDashboardPage(inj1828_ServiceDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { DashboardPage.class }, null));
      _$549235025__$229921779_toServicesPage(inj1828_ServiceDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { ServicesPage.class }, null));
      _$549235025__$229921779_toSituationsPage(inj1828_ServiceDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { SituationsPage.class }, null));
      _$549235025__74478675_serviceName(inj1828_ServiceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _$549235025__74478675_serviceNamespace(inj1828_ServiceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _$549235025__74478675_applicationNamespace(inj1828_ServiceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _$549235025__74478675_applicationName(inj1828_ServiceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _$549235025__74478675_serviceInterface(inj1828_ServiceDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _$549235025__$1106092480_referenceTable(inj1828_ServiceDetailsPage, inj1798_ReferenceTable_creational.getInstance(context));
      _$549235025__38184088_loading(inj1828_ServiceDetailsPage, inj1793_HtmlSnippet_creational.getInstance(context));
      _$1203922748__879292651_bus(inj1828_ServiceDetailsPage, inj1738_MessageBusProvider.get());
      context.addInitializationCallback(inj1828_ServiceDetailsPage, new InitializationCallback<ServiceDetailsPage>() {
        public void init(final ServiceDetailsPage obj) {
          org_overlord_rtgov_ui_client_local_pages_ServiceDetailsPageTemplateResource var22 = GWT.create(org_overlord_rtgov_ui_client_local_pages_ServiceDetailsPageTemplateResource.class);
          Element var23 = TemplateUtil.getRootTemplateElement(var22.getContents().getText(), "page");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/serviceDetails.html", var23);
          Map<String, Element> var24 = TemplateUtil.getDataFieldElements(var23);
          Map<String, Widget> var25 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__$229921779_toDashboardPage(inj1828_ServiceDetailsPage), var24, "back-to-dashboard");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__$229921779_toServicesPage(inj1828_ServiceDetailsPage), var24, "back-to-services");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__$229921779_toSituationsPage(inj1828_ServiceDetailsPage), var24, "to-situations");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__74478675_serviceName(inj1828_ServiceDetailsPage), var24, "serviceName");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__74478675_serviceNamespace(inj1828_ServiceDetailsPage), var24, "serviceNamespace");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__74478675_applicationNamespace(inj1828_ServiceDetailsPage), var24, "applicationNamespace");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__74478675_applicationName(inj1828_ServiceDetailsPage), var24, "applicationName");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__74478675_serviceInterface(inj1828_ServiceDetailsPage), var24, "serviceInterface");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__$1106092480_referenceTable(inj1828_ServiceDetailsPage), var24, "reference-table");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage", "org/overlord/rtgov/ui/client/local/site/serviceDetails.html", _$549235025__38184088_loading(inj1828_ServiceDetailsPage), var24, "service-details-loading-spinner");
          var25.put("back-to-dashboard", _$549235025__$229921779_toDashboardPage(inj1828_ServiceDetailsPage));
          var25.put("back-to-services", _$549235025__$229921779_toServicesPage(inj1828_ServiceDetailsPage));
          var25.put("to-situations", _$549235025__$229921779_toSituationsPage(inj1828_ServiceDetailsPage));
          var25.put("serviceName", _$549235025__74478675_serviceName(inj1828_ServiceDetailsPage));
          var25.put("serviceNamespace", _$549235025__74478675_serviceNamespace(inj1828_ServiceDetailsPage));
          var25.put("applicationNamespace", _$549235025__74478675_applicationNamespace(inj1828_ServiceDetailsPage));
          var25.put("applicationName", _$549235025__74478675_applicationName(inj1828_ServiceDetailsPage));
          var25.put("serviceInterface", _$549235025__74478675_serviceInterface(inj1828_ServiceDetailsPage));
          var25.put("reference-table", _$549235025__$1106092480_referenceTable(inj1828_ServiceDetailsPage));
          var25.put("service-details-loading-spinner", _$549235025__38184088_loading(inj1828_ServiceDetailsPage));
          TemplateUtil.initWidget(inj1828_ServiceDetailsPage, var23, var25.values());
        }
      });
      context.addInitializationCallback(inj1828_ServiceDetailsPage, init_inj1828_ServiceDetailsPage);
      context.addInitializationCallback(inj1828_ServiceDetailsPage, new InitializationCallback<ServiceDetailsPage>() {
        public void init(final ServiceDetailsPage obj) {
          DataBinder binder = _$549235025__$483175978_service(inj1828_ServiceDetailsPage);
          if (binder == null) {
            throw new RuntimeException("@AutoBound data binder for class org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage has not been initialized. Either initialize or add @Inject!");
          }
          binder.bind(_$549235025__74478675_serviceName(inj1828_ServiceDetailsPage), "name", new DataBindingQNameLocalPartConverter());
          binder.bind(_$549235025__74478675_serviceNamespace(inj1828_ServiceDetailsPage), "name", new DataBindingQNameNamespaceConverter());
          binder.bind(_$549235025__74478675_applicationNamespace(inj1828_ServiceDetailsPage), "application", new DataBindingQNameNamespaceConverter());
          binder.bind(_$549235025__74478675_applicationName(inj1828_ServiceDetailsPage), "application", new DataBindingQNameLocalPartConverter());
          binder.bind(_$549235025__74478675_serviceInterface(inj1828_ServiceDetailsPage), "serviceInterface", null);
          binder.bind(_$549235025__$1106092480_referenceTable(inj1828_ServiceDetailsPage), "references", null);
        }
      });
      context.addDestructionCallback(inj1828_ServiceDetailsPage, new DestructionCallback<ServiceDetailsPage>() {
        public void destroy(final ServiceDetailsPage obj) {
          _$549235025__$483175978_service(inj1828_ServiceDetailsPage).unbind();
        }
      });
      return inj1828_ServiceDetailsPage;
    }
  };
  private final BeanProvider<InitBallotProvider> inj1830_InitBallotProvider_creational = new BeanProvider<InitBallotProvider>() {
    public InitBallotProvider getInstance(final CreationalContext context) {
      final InitBallotProvider inj1754_InitBallotProvider = new InitBallotProvider();
      context.addBean(context.getBeanReference(InitBallotProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1754_InitBallotProvider);
      return inj1754_InitBallotProvider;
    }
  };
  private final InitBallotProvider inj1754_InitBallotProvider = inj1830_InitBallotProvider_creational.getInstance(context);
  private final BeanProvider<PageTransitionProvider> inj1831_PageTransitionProvider_creational = new BeanProvider<PageTransitionProvider>() {
    public PageTransitionProvider getInstance(final CreationalContext context) {
      final PageTransitionProvider inj1736_PageTransitionProvider = new PageTransitionProvider();
      context.addBean(context.getBeanReference(PageTransitionProvider.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1736_PageTransitionProvider);
      return inj1736_PageTransitionProvider;
    }
  };
  private final PageTransitionProvider inj1736_PageTransitionProvider = inj1831_PageTransitionProvider_creational.getInstance(context);
  private InitializationCallback<ApplicationNameListBox> init_inj1384_ApplicationNameListBox = new InitializationCallback<ApplicationNameListBox>() {
    public void init(final ApplicationNameListBox obj) {
      _1183065176_postConstruct(obj);
    }
  };
  private final BeanProvider<ApplicationNameListBox> inj1834_ApplicationNameListBox_creational = new BeanProvider<ApplicationNameListBox>() {
    public ApplicationNameListBox getInstance(final CreationalContext context) {
      final ApplicationNameListBox inj1384_ApplicationNameListBox = new ApplicationNameListBox();
      context.addBean(context.getBeanReference(ApplicationNameListBox.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1384_ApplicationNameListBox);
      _$56311345__$1433900096_i18n(inj1384_ApplicationNameListBox, inj1786_ClientMessages);
      context.addInitializationCallback(inj1384_ApplicationNameListBox, init_inj1384_ApplicationNameListBox);
      return inj1384_ApplicationNameListBox;
    }
  };
  private InitializationCallback<ProcessingStateListBox> init_inj56_ProcessingStateListBox = new InitializationCallback<ProcessingStateListBox>() {
    public void init(final ProcessingStateListBox obj) {
      _1183065176_postConstruct(obj);
    }
  };
  private final BeanProvider<ProcessingStateListBox> inj1835_ProcessingStateListBox_creational = new BeanProvider<ProcessingStateListBox>() {
    public ProcessingStateListBox getInstance(final CreationalContext context) {
      final ProcessingStateListBox inj56_ProcessingStateListBox = new ProcessingStateListBox();
      context.addBean(context.getBeanReference(ProcessingStateListBox.class, QualifierUtil.DEFAULT_QUALIFIERS), inj56_ProcessingStateListBox);
      _445223724__$1433900096_i18n(inj56_ProcessingStateListBox, inj1786_ClientMessages);
      context.addInitializationCallback(inj56_ProcessingStateListBox, init_inj56_ProcessingStateListBox);
      return inj56_ProcessingStateListBox;
    }
  };
  private InitializationCallback<ServiceFilters> init_inj1832_ServiceFilters = new InitializationCallback<ServiceFilters>() {
    public void init(final ServiceFilters obj) {
      _$2088441213_postConstruct(obj);
    }
  };
  private final BeanProvider<ServiceFilters> inj1833_ServiceFilters_creational = new BeanProvider<ServiceFilters>() {
    public ServiceFilters getInstance(final CreationalContext context) {
      final ServiceFilters inj1832_ServiceFilters = new ServiceFilters();
      context.addBean(context.getBeanReference(ServiceFilters.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1832_ServiceFilters);
      _$2088441213__$56311345_applicationName(inj1832_ServiceFilters, inj1834_ApplicationNameListBox_creational.getInstance(context));
      _$2088441213__$371269162_serviceName(inj1832_ServiceFilters, inj1774_TextBox_creational.getInstance(context));
      _$2088441213__445223724_processingState(inj1832_ServiceFilters, inj1835_ProcessingStateListBox_creational.getInstance(context));
      _$2088441213__2084144957_clearFilters(inj1832_ServiceFilters, inj1777_Anchor_creational.getInstance(context));
      context.addInitializationCallback(inj1832_ServiceFilters, new InitializationCallback<ServiceFilters>() {
        public void init(final ServiceFilters obj) {
          org_overlord_rtgov_ui_client_local_pages_services_ServiceFiltersTemplateResource var26 = GWT.create(org_overlord_rtgov_ui_client_local_pages_services_ServiceFiltersTemplateResource.class);
          Element var27 = TemplateUtil.getRootTemplateElement(var26.getContents().getText(), "filter-sidebar");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/services.html", var27);
          Map<String, Element> var28 = TemplateUtil.getDataFieldElements(var27);
          Map<String, Widget> var29 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters", "org/overlord/rtgov/ui/client/local/site/services.html", _$2088441213__$56311345_applicationName(inj1832_ServiceFilters), var28, "applicationName");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters", "org/overlord/rtgov/ui/client/local/site/services.html", _$2088441213__$371269162_serviceName(inj1832_ServiceFilters), var28, "serviceName");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters", "org/overlord/rtgov/ui/client/local/site/services.html", _$2088441213__445223724_processingState(inj1832_ServiceFilters), var28, "processingState");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters", "org/overlord/rtgov/ui/client/local/site/services.html", _$2088441213__2084144957_clearFilters(inj1832_ServiceFilters), var28, "clearFilters");
          var29.put("applicationName", _$2088441213__$56311345_applicationName(inj1832_ServiceFilters));
          var29.put("serviceName", _$2088441213__$371269162_serviceName(inj1832_ServiceFilters));
          var29.put("processingState", _$2088441213__445223724_processingState(inj1832_ServiceFilters));
          var29.put("clearFilters", _$2088441213__2084144957_clearFilters(inj1832_ServiceFilters));
          TemplateUtil.initWidget(inj1832_ServiceFilters, var27, var29.values());
        }
      });
      context.addInitializationCallback(inj1832_ServiceFilters, init_inj1832_ServiceFilters);
      return inj1832_ServiceFilters;
    }
  };
  private InitializationCallback<ServicesPage> init_inj1836_ServicesPage = new InitializationCallback<ServicesPage>() {
    public void init(final ServicesPage obj) {
      _$1203922748__onPostConstruct(obj);
      _$68797696_postConstruct(obj);
    }
  };
  private final BeanProvider<ServicesPage> inj1837_ServicesPage_creational = new BeanProvider<ServicesPage>() {
    public ServicesPage getInstance(final CreationalContext context) {
      final ServicesPage inj1836_ServicesPage = new ServicesPage();
      context.addBean(context.getBeanReference(ServicesPage.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1836_ServicesPage);
      _$68797696__$1433900096_i18n(inj1836_ServicesPage, inj1786_ClientMessages);
      _$68797696__$1163117323_servicesService(inj1836_ServicesPage, inj1788_ServicesRpcService);
      _$68797696__2030280515_notificationService(inj1836_ServicesPage, inj1782_NotificationService);
      _$68797696__$229921779_toDashboardPage(inj1836_ServicesPage, inj1758_TransitionAnchorProvider.provide(new Class[] { DashboardPage.class }, null));
      _$68797696__$229921779_toSituationsPage(inj1836_ServicesPage, inj1758_TransitionAnchorProvider.provide(new Class[] { SituationsPage.class }, null));
      _$68797696__$2088441213_filtersPanel(inj1836_ServicesPage, inj1833_ServiceFilters_creational.getInstance(context));
      _$68797696__2119756730_servicesRefreshButton(inj1836_ServicesPage, inj1820_Button_creational.getInstance(context));
      _$68797696__38184088_noDataMessage(inj1836_ServicesPage, inj1793_HtmlSnippet_creational.getInstance(context));
      _$68797696__38184088_searchInProgressMessage(inj1836_ServicesPage, inj1793_HtmlSnippet_creational.getInstance(context));
      _$68797696__$990608298_servicesTable(inj1836_ServicesPage, inj1803_ServiceTable_creational.getInstance(context));
      _$68797696__1762635241_pager(inj1836_ServicesPage, inj1821_Pager_creational.getInstance(context));
      _$1203922748__879292651_bus(inj1836_ServicesPage, inj1738_MessageBusProvider.get());
      context.addInitializationCallback(inj1836_ServicesPage, new InitializationCallback<ServicesPage>() {
        public void init(final ServicesPage obj) {
          org_overlord_rtgov_ui_client_local_pages_ServicesPageTemplateResource var30 = GWT.create(org_overlord_rtgov_ui_client_local_pages_ServicesPageTemplateResource.class);
          Element var31 = TemplateUtil.getRootTemplateElement(var30.getContents().getText(), "page");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/services.html", var31);
          Map<String, Element> var32 = TemplateUtil.getDataFieldElements(var31);
          Map<String, Widget> var33 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__$229921779_toDashboardPage(inj1836_ServicesPage), var32, "back-to-dashboard");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__$229921779_toSituationsPage(inj1836_ServicesPage), var32, "to-situations");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__$2088441213_filtersPanel(inj1836_ServicesPage), var32, "filter-sidebar");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__2119756730_servicesRefreshButton(inj1836_ServicesPage), var32, "services-btn-refresh");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__38184088_noDataMessage(inj1836_ServicesPage), var32, "services-none");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__38184088_searchInProgressMessage(inj1836_ServicesPage), var32, "services-searching");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__$990608298_servicesTable(inj1836_ServicesPage), var32, "services-table");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", _$68797696__1762635241_pager(inj1836_ServicesPage), var32, "services-pager");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", ElementWrapperWidget.getWidget(_$68797696__1424436749_rangeSpan(inj1836_ServicesPage)), var32, "services-range");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.ServicesPage", "org/overlord/rtgov/ui/client/local/site/services.html", ElementWrapperWidget.getWidget(_$68797696__1424436749_totalSpan(inj1836_ServicesPage)), var32, "services-total");
          var33.put("back-to-dashboard", _$68797696__$229921779_toDashboardPage(inj1836_ServicesPage));
          var33.put("to-situations", _$68797696__$229921779_toSituationsPage(inj1836_ServicesPage));
          var33.put("filter-sidebar", _$68797696__$2088441213_filtersPanel(inj1836_ServicesPage));
          var33.put("services-btn-refresh", _$68797696__2119756730_servicesRefreshButton(inj1836_ServicesPage));
          var33.put("services-none", _$68797696__38184088_noDataMessage(inj1836_ServicesPage));
          var33.put("services-searching", _$68797696__38184088_searchInProgressMessage(inj1836_ServicesPage));
          var33.put("services-table", _$68797696__$990608298_servicesTable(inj1836_ServicesPage));
          var33.put("services-pager", _$68797696__1762635241_pager(inj1836_ServicesPage));
          var33.put("services-range", ElementWrapperWidget.getWidget(_$68797696__1424436749_rangeSpan(inj1836_ServicesPage)));
          var33.put("services-total", ElementWrapperWidget.getWidget(_$68797696__1424436749_totalSpan(inj1836_ServicesPage)));
          TemplateUtil.initWidget(inj1836_ServicesPage, var31, var33.values());
          ((HasClickHandlers) var33.get("services-btn-refresh")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              inj1836_ServicesPage.onRefreshClick(event);
            }
          });
        }
      });
      context.addDestructionCallback(inj1836_ServicesPage, new DestructionCallback<ServicesPage>() {
        public void destroy(final ServicesPage obj) {
          ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(_$68797696__1424436749_rangeSpan(inj1836_ServicesPage)));
          ElementWrapperWidget.removeWidget(ElementWrapperWidget.getWidget(_$68797696__1424436749_totalSpan(inj1836_ServicesPage)));
        }
      });
      context.addInitializationCallback(inj1836_ServicesPage, init_inj1836_ServicesPage);
      return inj1836_ServicesPage;
    }
  };
  private final BeanProvider<VoidInvocationHandler> inj1840_VoidInvocationHandler_creational = new BeanProvider<VoidInvocationHandler>() {
    public VoidInvocationHandler getInstance(final CreationalContext context) {
      final VoidInvocationHandler inj1514_VoidInvocationHandler = new VoidInvocationHandler();
      context.addBean(context.getBeanReference(VoidInvocationHandler.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1514_VoidInvocationHandler);
      _411055155__2030280515_notificationService(inj1514_VoidInvocationHandler, inj1782_NotificationService);
      return inj1514_VoidInvocationHandler;
    }
  };
  private final BeanProvider<CallTraceWidget> inj1841_CallTraceWidget_creational = new BeanProvider<CallTraceWidget>() {
    public CallTraceWidget getInstance(final CreationalContext context) {
      final CallTraceWidget inj53_CallTraceWidget = new CallTraceWidget();
      context.addBean(context.getBeanReference(CallTraceWidget.class, QualifierUtil.DEFAULT_QUALIFIERS), inj53_CallTraceWidget);
      return inj53_CallTraceWidget;
    }
  };
  private final BeanProvider<CallTraceDetails> inj1842_CallTraceDetails_creational = new BeanProvider<CallTraceDetails>() {
    public CallTraceDetails getInstance(final CreationalContext context) {
      final CallTraceDetails inj166_CallTraceDetails = new CallTraceDetails();
      context.addBean(context.getBeanReference(CallTraceDetails.class, QualifierUtil.DEFAULT_QUALIFIERS), inj166_CallTraceDetails);
      _1961580097__$1433900096_i18n(inj166_CallTraceDetails, inj1786_ClientMessages);
      return inj166_CallTraceDetails;
    }
  };
  private final BeanProvider<SourceEditor> inj1843_SourceEditor_creational = new BeanProvider<SourceEditor>() {
    public SourceEditor getInstance(final CreationalContext context) {
      final SourceEditor inj482_SourceEditor = new SourceEditor();
      context.addBean(context.getBeanReference(SourceEditor.class, QualifierUtil.DEFAULT_QUALIFIERS), inj482_SourceEditor);
      return inj482_SourceEditor;
    }
  };
  private InitializationCallback<SituationDetailsPage> init_inj1838_SituationDetailsPage = new InitializationCallback<SituationDetailsPage>() {
    public void init(final SituationDetailsPage obj) {
      _$1203922748__onPostConstruct(obj);
      _1089186566_onPostConstruct(obj);
    }
  };
  private final BeanProvider<SituationDetailsPage> inj1839_SituationDetailsPage_creational = new BeanProvider<SituationDetailsPage>() {
    public SituationDetailsPage getInstance(final CreationalContext context) {
      final SituationDetailsPage inj1838_SituationDetailsPage = new SituationDetailsPage();
      context.addBean(context.getBeanReference(SituationDetailsPage.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1838_SituationDetailsPage);
      _1089186566__$1433900096_i18n(inj1838_SituationDetailsPage, inj1786_ClientMessages);
      _1089186566__$1124051892_situationsService(inj1838_SituationDetailsPage, inj1815_SituationsRpcService);
      _1089186566__2030280515_notificationService(inj1838_SituationDetailsPage, inj1782_NotificationService);
      _1089186566__411055155_voidInvocationHandler(inj1838_SituationDetailsPage, inj1840_VoidInvocationHandler_creational.getInstance(context));
      _1089186566__$483175978_situation(inj1838_SituationDetailsPage, inj1748_DataBinderProvider.provide(new Class[] { SituationBean.class }, null));
      _1089186566__$229921779_toDashboardPage(inj1838_SituationDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { DashboardPage.class }, null));
      _1089186566__$229921779_toSituationsPage(inj1838_SituationDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { SituationsPage.class }, null));
      _1089186566__$229921779_toServicesPage(inj1838_SituationDetailsPage, inj1758_TransitionAnchorProvider.provide(new Class[] { ServicesPage.class }, null));
      _1089186566__74478675_situationName(inj1838_SituationDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _1089186566__$757255186_severity(inj1838_SituationDetailsPage, inj1827_FlowPanel_creational.getInstance(context));
      _1089186566__74478675_subject(inj1838_SituationDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _1089186566__74478675_timestamp(inj1838_SituationDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _1089186566__74478675_resolutionState(inj1838_SituationDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _1089186566__74478675_description(inj1838_SituationDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _1089186566__$1542973949_propertiesTable(inj1838_SituationDetailsPage, inj1812_SituationPropertiesTable_creational.getInstance(context));
      _1089186566__$1542973949_contextTable(inj1838_SituationDetailsPage, inj1812_SituationPropertiesTable_creational.getInstance(context));
      _1089186566__1026095717_callTrace(inj1838_SituationDetailsPage, inj1841_CallTraceWidget_creational.getInstance(context));
      _1089186566__1961580097_callTraceDetail(inj1838_SituationDetailsPage, inj1842_CallTraceDetails_creational.getInstance(context));
      _1089186566__2084144957_messageTabAnchor(inj1838_SituationDetailsPage, inj1777_Anchor_creational.getInstance(context));
      _1089186566__$1499064285_messageEditor(inj1838_SituationDetailsPage, inj1843_SourceEditor_creational.getInstance(context));
      _1089186566__2119756730_resubmitButton(inj1838_SituationDetailsPage, inj1820_Button_creational.getInstance(context));
      _1089186566__74478675_resubmitDetails(inj1838_SituationDetailsPage, inj1792_InlineLabel_creational.getInstance(context));
      _1089186566__2119756730_assignButton(inj1838_SituationDetailsPage, inj1820_Button_creational.getInstance(context));
      _1089186566__2119756730_closeButton(inj1838_SituationDetailsPage, inj1820_Button_creational.getInstance(context));
      _1089186566__2119756730_startButton(inj1838_SituationDetailsPage, inj1820_Button_creational.getInstance(context));
      _1089186566__2119756730_stopButton(inj1838_SituationDetailsPage, inj1820_Button_creational.getInstance(context));
      _1089186566__2119756730_resolveButton(inj1838_SituationDetailsPage, inj1820_Button_creational.getInstance(context));
      _1089186566__2119756730_reopenButton(inj1838_SituationDetailsPage, inj1820_Button_creational.getInstance(context));
      _1089186566__38184088_loading(inj1838_SituationDetailsPage, inj1793_HtmlSnippet_creational.getInstance(context));
      _$1203922748__879292651_bus(inj1838_SituationDetailsPage, inj1738_MessageBusProvider.get());
      context.addInitializationCallback(inj1838_SituationDetailsPage, new InitializationCallback<SituationDetailsPage>() {
        public void init(final SituationDetailsPage obj) {
          org_overlord_rtgov_ui_client_local_pages_SituationDetailsPageTemplateResource var34 = GWT.create(org_overlord_rtgov_ui_client_local_pages_SituationDetailsPageTemplateResource.class);
          Element var35 = TemplateUtil.getRootTemplateElement(var34.getContents().getText(), "page");
          TemplateUtil.translateTemplate("org/overlord/rtgov/ui/client/local/site/situationDetails.html", var35);
          Map<String, Element> var36 = TemplateUtil.getDataFieldElements(var35);
          Map<String, Widget> var37 = new LinkedHashMap<String, Widget>();
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__$229921779_toDashboardPage(inj1838_SituationDetailsPage), var36, "back-to-dashboard");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__$229921779_toSituationsPage(inj1838_SituationDetailsPage), var36, "to-situations");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__$229921779_toServicesPage(inj1838_SituationDetailsPage), var36, "to-services");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__74478675_situationName(inj1838_SituationDetailsPage), var36, "situationName");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__$757255186_severity(inj1838_SituationDetailsPage), var36, "severity");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__74478675_subject(inj1838_SituationDetailsPage), var36, "subject");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__74478675_timestamp(inj1838_SituationDetailsPage), var36, "timestamp");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__74478675_resolutionState(inj1838_SituationDetailsPage), var36, "resolutionState");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__74478675_description(inj1838_SituationDetailsPage), var36, "description");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__$1542973949_propertiesTable(inj1838_SituationDetailsPage), var36, "properties-table");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__$1542973949_contextTable(inj1838_SituationDetailsPage), var36, "context-table");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__1026095717_callTrace(inj1838_SituationDetailsPage), var36, "call-trace");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__1961580097_callTraceDetail(inj1838_SituationDetailsPage), var36, "call-trace-detail");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2084144957_messageTabAnchor(inj1838_SituationDetailsPage), var36, "messageTab");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__$1499064285_messageEditor(inj1838_SituationDetailsPage), var36, "message-editor");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2119756730_resubmitButton(inj1838_SituationDetailsPage), var36, "btn-resubmit");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__74478675_resubmitDetails(inj1838_SituationDetailsPage), var36, "resubmitDetails");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2119756730_assignButton(inj1838_SituationDetailsPage), var36, "btn-assign");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2119756730_closeButton(inj1838_SituationDetailsPage), var36, "btn-close");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2119756730_startButton(inj1838_SituationDetailsPage), var36, "btn-start");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2119756730_stopButton(inj1838_SituationDetailsPage), var36, "btn-stop");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2119756730_resolveButton(inj1838_SituationDetailsPage), var36, "btn-resolve");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__2119756730_reopenButton(inj1838_SituationDetailsPage), var36, "btn-reopen");
          TemplateUtil.compositeComponentReplace("org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage", "org/overlord/rtgov/ui/client/local/site/situationDetails.html", _1089186566__38184088_loading(inj1838_SituationDetailsPage), var36, "situation-details-loading-spinner");
          var37.put("back-to-dashboard", _1089186566__$229921779_toDashboardPage(inj1838_SituationDetailsPage));
          var37.put("to-situations", _1089186566__$229921779_toSituationsPage(inj1838_SituationDetailsPage));
          var37.put("to-services", _1089186566__$229921779_toServicesPage(inj1838_SituationDetailsPage));
          var37.put("situationName", _1089186566__74478675_situationName(inj1838_SituationDetailsPage));
          var37.put("severity", _1089186566__$757255186_severity(inj1838_SituationDetailsPage));
          var37.put("subject", _1089186566__74478675_subject(inj1838_SituationDetailsPage));
          var37.put("timestamp", _1089186566__74478675_timestamp(inj1838_SituationDetailsPage));
          var37.put("resolutionState", _1089186566__74478675_resolutionState(inj1838_SituationDetailsPage));
          var37.put("description", _1089186566__74478675_description(inj1838_SituationDetailsPage));
          var37.put("properties-table", _1089186566__$1542973949_propertiesTable(inj1838_SituationDetailsPage));
          var37.put("context-table", _1089186566__$1542973949_contextTable(inj1838_SituationDetailsPage));
          var37.put("call-trace", _1089186566__1026095717_callTrace(inj1838_SituationDetailsPage));
          var37.put("call-trace-detail", _1089186566__1961580097_callTraceDetail(inj1838_SituationDetailsPage));
          var37.put("messageTab", _1089186566__2084144957_messageTabAnchor(inj1838_SituationDetailsPage));
          var37.put("message-editor", _1089186566__$1499064285_messageEditor(inj1838_SituationDetailsPage));
          var37.put("btn-resubmit", _1089186566__2119756730_resubmitButton(inj1838_SituationDetailsPage));
          var37.put("resubmitDetails", _1089186566__74478675_resubmitDetails(inj1838_SituationDetailsPage));
          var37.put("btn-assign", _1089186566__2119756730_assignButton(inj1838_SituationDetailsPage));
          var37.put("btn-close", _1089186566__2119756730_closeButton(inj1838_SituationDetailsPage));
          var37.put("btn-start", _1089186566__2119756730_startButton(inj1838_SituationDetailsPage));
          var37.put("btn-stop", _1089186566__2119756730_stopButton(inj1838_SituationDetailsPage));
          var37.put("btn-resolve", _1089186566__2119756730_resolveButton(inj1838_SituationDetailsPage));
          var37.put("btn-reopen", _1089186566__2119756730_reopenButton(inj1838_SituationDetailsPage));
          var37.put("situation-details-loading-spinner", _1089186566__38184088_loading(inj1838_SituationDetailsPage));
          TemplateUtil.initWidget(inj1838_SituationDetailsPage, var35, var37.values());
          ((HasClickHandlers) var37.get("btn-resubmit")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              _1089186566_onResubmitClick(inj1838_SituationDetailsPage, event);
            }
          });
          ((HasClickHandlers) var37.get("btn-assign")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              _1089186566_onAssignButtonClick(inj1838_SituationDetailsPage, event);
            }
          });
          ((HasClickHandlers) var37.get("btn-close")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              _1089186566_onDeassignButtonClick(inj1838_SituationDetailsPage, event);
            }
          });
          ((HasClickHandlers) var37.get("btn-start")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              _1089186566_onStartButtonClick(inj1838_SituationDetailsPage, event);
            }
          });
          ((HasClickHandlers) var37.get("btn-stop")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              _1089186566_onStopButtonClick(inj1838_SituationDetailsPage, event);
            }
          });
          ((HasClickHandlers) var37.get("btn-resolve")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              _1089186566_onResolveButtonClick(inj1838_SituationDetailsPage, event);
            }
          });
          ((HasClickHandlers) var37.get("btn-reopen")).addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
              _1089186566_onReopenButtonClick(inj1838_SituationDetailsPage, event);
            }
          });
        }
      });
      context.addInitializationCallback(inj1838_SituationDetailsPage, init_inj1838_SituationDetailsPage);
      context.addInitializationCallback(inj1838_SituationDetailsPage, new InitializationCallback<SituationDetailsPage>() {
        public void init(final SituationDetailsPage obj) {
          DataBinder binder = _1089186566__$483175978_situation(inj1838_SituationDetailsPage);
          if (binder == null) {
            throw new RuntimeException("@AutoBound data binder for class org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage has not been initialized. Either initialize or add @Inject!");
          }
          binder.bind(_1089186566__74478675_situationName(inj1838_SituationDetailsPage), "type", null);
          binder.bind(_1089186566__74478675_subject(inj1838_SituationDetailsPage), "subject", null);
          binder.bind(_1089186566__74478675_timestamp(inj1838_SituationDetailsPage), "timestamp", new DataBindingDateTimeConverter());
          binder.bind(_1089186566__74478675_resolutionState(inj1838_SituationDetailsPage), "resolutionState", null);
          binder.bind(_1089186566__74478675_description(inj1838_SituationDetailsPage), "description", null);
          binder.bind(_1089186566__$1542973949_propertiesTable(inj1838_SituationDetailsPage), "properties", null);
          binder.bind(_1089186566__$1542973949_contextTable(inj1838_SituationDetailsPage), "context", null);
          binder.bind(_1089186566__1026095717_callTrace(inj1838_SituationDetailsPage), "callTrace", null);
        }
      });
      context.addDestructionCallback(inj1838_SituationDetailsPage, new DestructionCallback<SituationDetailsPage>() {
        public void destroy(final SituationDetailsPage obj) {
          _1089186566__$483175978_situation(inj1838_SituationDetailsPage).unbind();
        }
      });
      return inj1838_SituationDetailsPage;
    }
  };
  private InitializationCallback<LessStyle> init_inj1844_LessStyle = new InitializationCallback<LessStyle>() {
    public void init(final LessStyle obj) {
      obj.init();
    }
  };
  private final BeanProvider<LessStyle> inj1845_LessStyle_creational = new BeanProvider<LessStyle>() {
    public LessStyle getInstance(final CreationalContext context) {
      final LessStyle inj1844_LessStyle = new LessStyle();
      context.addBean(context.getBeanReference(LessStyle.class, QualifierUtil.DEFAULT_QUALIFIERS), inj1844_LessStyle);
      context.addInitializationCallback(inj1844_LessStyle, init_inj1844_LessStyle);
      return inj1844_LessStyle;
    }
  };
  private final LessStyle inj1844_LessStyle = inj1845_LessStyle_creational.getInstance(context);
  static class ClientMessages_inj1773_proxy extends ClientMessages {
    private ClientMessages $$_proxy_$$;
    @Override public String format(String a0, Object[] a1) {
      return $$_proxy_$$.format(a0, a1);
    }

    @Override public int hashCode() {
      if ($$_proxy_$$ == null) {
        throw new IllegalStateException("call to hashCode() on an unclosed proxy.");
      } else {
        return $$_proxy_$$.hashCode();
      }
    }

    @Override public boolean equals(Object o) {
      if ($$_proxy_$$ == null) {
        throw new IllegalStateException("call to equals() on an unclosed proxy.");
      } else {
        return $$_proxy_$$.equals(o);
      }
    }

    public void __$setProxiedInstance$(ClientMessages proxy) {
      $$_proxy_$$ = proxy;
    }
  }
  public interface org_overlord_rtgov_ui_client_local_pages_situations_SituationFiltersTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/situations.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_pages_ReferenceDetailsPageTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/referenceDetails.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_pages_DashboardPageTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/dashboard.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_pages_SituationsPageTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/situations.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_services_notification_NotificationWidgetTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/dialogs/growl-dialog.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_pages_ServiceDetailsPageTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/serviceDetails.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_pages_services_ServiceFiltersTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/services.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_pages_ServicesPageTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/services.html") public TextResource getContents(); }
  public interface org_overlord_rtgov_ui_client_local_pages_SituationDetailsPageTemplateResource extends Template, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/site/situationDetails.html") public TextResource getContents(); }
  private void declareBeans_0() {
    injContext.addBean(EventProvider.class, EventProvider.class, inj1765_EventProvider_creational, inj1756_EventProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, EventProvider.class, inj1765_EventProvider_creational, inj1756_EventProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SenderProvider.class, SenderProvider.class, inj1766_SenderProvider_creational, inj1750_SenderProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, SenderProvider.class, inj1766_SenderProvider_creational, inj1750_SenderProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Navigation.class, Navigation.class, inj1768_Navigation_creational, inj1767_Navigation, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(IOCBeanManagerProvider.class, IOCBeanManagerProvider.class, inj1769_IOCBeanManagerProvider_creational, inj1746_IOCBeanManagerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(Provider.class, IOCBeanManagerProvider.class, inj1769_IOCBeanManagerProvider_creational, inj1746_IOCBeanManagerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(CallerProvider.class, CallerProvider.class, inj1770_CallerProvider_creational, inj1760_CallerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, CallerProvider.class, inj1770_CallerProvider_creational, inj1760_CallerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SeverityListBox.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractFilterListBox.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ListBox.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesChangeEvents.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasChangeHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SeverityListBox.class, inj1772_SeverityListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TextBox.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(TextBoxBase.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesChangeEvents.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ValueBoxBase.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasChangeHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Target.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirection.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsEditor.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, TextBox.class, inj1774_TextBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ResolutionStateListBox.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractFilterListBox.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ListBox.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesChangeEvents.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasChangeHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ResolutionStateListBox.class, inj1775_ResolutionStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(DateTimePicker.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(TextBox.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TextBoxBase.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesChangeEvents.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ValueBoxBase.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasChangeHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Target.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirection.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsEditor.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, DateTimePicker.class, inj1776_DateTimePicker_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Anchor.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasHorizontalAlignment.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHTML.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWordWrap.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirection.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionalSafeHtml.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionalText.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasSafeHtml.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, Anchor.class, inj1777_Anchor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationFilters.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValueChangeHandlers.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SituationFilters.class, inj1771_SituationFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(MessageBusProvider.class, MessageBusProvider.class, inj1778_MessageBusProvider_creational, inj1738_MessageBusProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(Provider.class, MessageBusProvider.class, inj1778_MessageBusProvider_creational, inj1738_MessageBusProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(RequestDispatcherProvider.class, RequestDispatcherProvider.class, inj1779_RequestDispatcherProvider_creational, inj1740_RequestDispatcherProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(Provider.class, RequestDispatcherProvider.class, inj1779_RequestDispatcherProvider_creational, inj1740_RequestDispatcherProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(InstanceProvider.class, InstanceProvider.class, inj1780_InstanceProvider_creational, inj1752_InstanceProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, InstanceProvider.class, inj1780_InstanceProvider_creational, inj1752_InstanceProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(RootPanelProvider.class, RootPanelProvider.class, inj1781_RootPanelProvider_creational, inj1744_RootPanelProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(Provider.class, RootPanelProvider.class, inj1781_RootPanelProvider_creational, inj1744_RootPanelProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(NotificationService.class, NotificationService.class, inj1783_NotificationService_creational, inj1782_NotificationService, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(TransitionAnchorProvider.class, TransitionAnchorProvider.class, inj1784_TransitionAnchorProvider_creational, inj1758_TransitionAnchorProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, TransitionAnchorProvider.class, inj1784_TransitionAnchorProvider_creational, inj1758_TransitionAnchorProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(DataBinderProvider.class, DataBinderProvider.class, inj1785_DataBinderProvider_creational, inj1748_DataBinderProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, DataBinderProvider.class, inj1785_DataBinderProvider_creational, inj1748_DataBinderProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ClientMessages.class, ClientMessages.class, inj1787_ClientMessages_creational, inj1786_ClientMessages, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ServicesRpcService.class, ServicesRpcService.class, inj1789_ServicesRpcService_creational, inj1788_ServicesRpcService, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(InlineLabel.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(Label.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionalText.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirection.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsEditor.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(LabelBase.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWordWrap.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAutoHorizontalAlignment.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHorizontalAlignment.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, InlineLabel.class, inj1792_InlineLabel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HtmlSnippet.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasHTML.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, HtmlSnippet.class, inj1793_HtmlSnippet_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ReferenceDetailsPage.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractPage.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ReferenceDetailsPage.class, inj1791_ReferenceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TransitionAnchorFactoryProvider.class, TransitionAnchorFactoryProvider.class, inj1794_TransitionAnchorFactoryProvider_creational, inj1764_TransitionAnchorFactoryProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, TransitionAnchorFactoryProvider.class, inj1794_TransitionAnchorFactoryProvider_creational, inj1764_TransitionAnchorFactoryProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationWatcherEvent.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValue.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FlowPanel.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ForIsWidget.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(InsertPanel.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IndexedPanel.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ComplexPanel.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SituationWatcherEvent.class, inj1796_SituationWatcherEvent_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ReferenceTable.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValue.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TemplatedWidgetTable.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHTML.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(WidgetTable.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ReferenceTable.class, inj1798_ReferenceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(DashboardPage.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractPage.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, DashboardPage.class, inj1800_DashboardPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ListWidgetProvider.class, ListWidgetProvider.class, inj1801_ListWidgetProvider_creational, inj1734_ListWidgetProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, ListWidgetProvider.class, inj1801_ListWidgetProvider_creational, inj1734_ListWidgetProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ServiceTable.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(SortableTemplatedWidgetTable.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTableSortHandlers.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TemplatedWidgetTable.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHTML.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(WidgetTable.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ServiceTable.class, inj1803_ServiceTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(LocaleSelector.class, LocaleSelector.class, inj1805_LocaleSelector_creational, inj1804_LocaleSelector, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(LocaleListBox.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ValueListBox.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasConstrainedValue.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsEditor.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, LocaleListBox.class, inj1807_LocaleListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(App.class, App.class, inj1809_App_creational, inj1808_App, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(DisposerProvider.class, DisposerProvider.class, inj1810_DisposerProvider_creational, inj1762_DisposerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, DisposerProvider.class, inj1810_DisposerProvider_creational, inj1762_DisposerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationPropertiesTable.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValue.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
  }

  private void declareBeans_1() {
    injContext.addBean(TemplatedWidgetTable.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHTML.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(WidgetTable.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SituationPropertiesTable.class, inj1812_SituationPropertiesTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationTable.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(SortableTemplatedWidgetTable.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTableSortHandlers.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TemplatedWidgetTable.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHTML.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(WidgetTable.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SituationTable.class, inj1814_SituationTable_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationsRpcService.class, SituationsRpcService.class, inj1816_SituationsRpcService_creational, inj1815_SituationsRpcService, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ToggleSwitch.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(TextBox.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TextBoxBase.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesChangeEvents.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ValueBoxBase.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasChangeHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Target.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirection.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsEditor.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ToggleSwitch.class, inj1819_ToggleSwitch_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Button.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ButtonBase.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHTML.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasSafeHtml.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, Button.class, inj1820_Button_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Pager.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValueChangeHandlers.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FlowPanel.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ForIsWidget.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(InsertPanel.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IndexedPanel.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ComplexPanel.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, Pager.class, inj1821_Pager_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationWatcherEvents.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(FlowPanel.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ForIsWidget.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(InsertPanel.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IndexedPanel.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ComplexPanel.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SituationWatcherEvents.class, inj1822_SituationWatcherEvents_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationsPage.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractPage.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SituationsPage.class, inj1818_SituationsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(BatchCallerProvider.class, BatchCallerProvider.class, inj1823_BatchCallerProvider_creational, inj1742_BatchCallerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(Provider.class, BatchCallerProvider.class, inj1823_BatchCallerProvider_creational, inj1742_BatchCallerProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Label.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasDirectionalText.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasText.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirection.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsEditor.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(LabelBase.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWordWrap.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAutoHorizontalAlignment.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHorizontalAlignment.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, Label.class, inj1826_Label_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FlowPanel.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ForIsWidget.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(InsertPanel.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IndexedPanel.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ComplexPanel.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, FlowPanel.class, inj1827_FlowPanel_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(NotificationWidget.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasMouseInHandlers.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(org.overlord.rtgov.ui.client.local.events.MouseOutEvent.HasMouseOutHandlers.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, NotificationWidget.class, inj1825_NotificationWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ServiceDetailsPage.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractPage.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ServiceDetailsPage.class, inj1829_ServiceDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(InitBallotProvider.class, InitBallotProvider.class, inj1830_InitBallotProvider_creational, inj1754_InitBallotProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, InitBallotProvider.class, inj1830_InitBallotProvider_creational, inj1754_InitBallotProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(PageTransitionProvider.class, PageTransitionProvider.class, inj1831_PageTransitionProvider_creational, inj1736_PageTransitionProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(ContextualTypeProvider.class, PageTransitionProvider.class, inj1831_PageTransitionProvider_creational, inj1736_PageTransitionProvider, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ApplicationNameListBox.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractFilterListBox.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ListBox.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesChangeEvents.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasChangeHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ApplicationNameListBox.class, inj1834_ApplicationNameListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ProcessingStateListBox.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractFilterListBox.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValue.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ListBox.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesChangeEvents.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasChangeHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasName.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDirectionEstimator.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FocusWidget.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesClickEvents.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasClickHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDoubleClickHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocus.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Focusable.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesFocusEvents.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesKeyboardEvents.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasEnabled.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllDragAndDropHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEndHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragEnterHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragLeaveHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragOverHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDragStartHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasDropHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllFocusHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasFocusHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasBlurHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllGestureHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureStartHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureChangeHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasGestureEndHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllKeyHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyUpHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyDownHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasKeyPressHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllMouseHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseDownHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseUpHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOutHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseOverHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseMoveHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasMouseWheelHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAllTouchHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchStartHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchMoveHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchEndHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasTouchCancelHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourcesMouseEvents.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ProcessingStateListBox.class, inj1835_ProcessingStateListBox_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ServiceFilters.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValueChangeHandlers.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ServiceFilters.class, inj1833_ServiceFilters_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ServicesPage.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractPage.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, ServicesPage.class, inj1837_ServicesPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(VoidInvocationHandler.class, VoidInvocationHandler.class, inj1840_VoidInvocationHandler_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(IRpcServiceInvocationHandler.class, VoidInvocationHandler.class, inj1840_VoidInvocationHandler_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(CallTraceWidget.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValue.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasSelectionHandlers.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, CallTraceWidget.class, inj1841_CallTraceWidget_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(CallTraceDetails.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValue.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(FlowPanel.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ForIsWidget.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(InsertPanel.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IndexedPanel.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(ComplexPanel.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Panel.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(com.google.gwt.user.client.ui.HasWidgets.ForIsWidget.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasWidgets.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Iterable.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, CallTraceDetails.class, inj1842_CallTraceDetails_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SourceEditor.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(HasValue.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(TakesValue.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasValueChangeHandlers.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SourceEditor.class, inj1843_SourceEditor_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(SituationDetailsPage.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
    injContext.addBean(AbstractPage.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Composite.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsRenderable.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(Widget.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(EventListener.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasAttachHandlers.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasHandlers.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(IsWidget.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(UIObject.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(HasVisibility.class, SituationDetailsPage.class, inj1839_SituationDetailsPage_creational, null, QualifierUtil.DEFAULT_QUALIFIERS, null, false);
    injContext.addBean(LessStyle.class, LessStyle.class, inj1845_LessStyle_creational, inj1844_LessStyle, QualifierUtil.DEFAULT_QUALIFIERS, null, true);
  }

  private native static void _$990608298__$1433900096_i18n(ServiceTable instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceTable::i18n = value;
  }-*/;

  private native static void _$549235025__2030280515_notificationService(ServiceDetailsPage instance, NotificationService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::notificationService = value;
  }-*/;

  private native static HtmlSnippet _1089186566__38184088_loading(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::loading;
  }-*/;

  private native static void _1089186566__38184088_loading(SituationDetailsPage instance, HtmlSnippet value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::loading = value;
  }-*/;

  private native static HtmlSnippet _102653079__38184088_noDataMessage(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::noDataMessage;
  }-*/;

  private native static void _102653079__38184088_noDataMessage(SituationsPage instance, HtmlSnippet value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::noDataMessage = value;
  }-*/;

  private native static void _445223724__$1433900096_i18n(ProcessingStateListBox instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ProcessingStateListBox::i18n = value;
  }-*/;

  private native static void _411055155__2030280515_notificationService(VoidInvocationHandler instance, NotificationService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.rpc.IRpcServiceInvocationHandler.VoidInvocationHandler::notificationService = value;
  }-*/;

  private native static InlineLabel _80634777__74478675_referenceName(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::referenceName;
  }-*/;

  private native static void _80634777__74478675_referenceName(ReferenceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::referenceName = value;
  }-*/;

  private native static void _102653079__$1124051892_situationsService(SituationsPage instance, SituationsRpcService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::situationsService = value;
  }-*/;

  private native static void _1089186566__411055155_voidInvocationHandler(SituationDetailsPage instance, VoidInvocationHandler value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::voidInvocationHandler = value;
  }-*/;

  private native static TransitionAnchor _$549235025__$229921779_toDashboardPage(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::toDashboardPage;
  }-*/;

  private native static void _$549235025__$229921779_toDashboardPage(ServiceDetailsPage instance, TransitionAnchor<DashboardPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::toDashboardPage = value;
  }-*/;

  private native static void _1350680564__$1232121576_selector(LocaleListBox instance, LocaleSelector value) /*-{
    instance.@org.jboss.errai.ui.client.widget.LocaleListBox::selector = value;
  }-*/;

  private native static Anchor _$2088441213__2084144957_clearFilters(ServiceFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::clearFilters;
  }-*/;

  private native static void _$2088441213__2084144957_clearFilters(ServiceFilters instance, Anchor value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::clearFilters = value;
  }-*/;

  private native static void _$68797696__2030280515_notificationService(ServicesPage instance, NotificationService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::notificationService = value;
  }-*/;

  private native static void _2030280515__$903668163_notificationWidgetFactory(NotificationService instance, Instance<NotificationWidget> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.NotificationService::notificationWidgetFactory = value;
  }-*/;

  private native static void _$68797696__$1163117323_servicesService(ServicesPage instance, ServicesRpcService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::servicesService = value;
  }-*/;

  private native static void _$1203922748__879292651_bus(AbstractPage instance, ClientMessageBus value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::bus = value;
  }-*/;

  private native static ToggleSwitch _102653079__1401717584_toggleFilterSwitch(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::toggleFilterSwitch;
  }-*/;

  private native static void _102653079__1401717584_toggleFilterSwitch(SituationsPage instance, ToggleSwitch value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::toggleFilterSwitch = value;
  }-*/;

  private native static void _$68797696__$1433900096_i18n(ServicesPage instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::i18n = value;
  }-*/;

  private native static TextBox _148888483__$371269162_subject(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::subject;
  }-*/;

  private native static void _148888483__$371269162_subject(SituationFilters instance, TextBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::subject = value;
  }-*/;

  private native static SeverityListBox _148888483__1024371274_severity(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::severity;
  }-*/;

  private native static void _148888483__1024371274_severity(SituationFilters instance, SeverityListBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::severity = value;
  }-*/;

  private native static SituationPropertiesTable _1089186566__$1542973949_propertiesTable(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::propertiesTable;
  }-*/;

  private native static void _1089186566__$1542973949_propertiesTable(SituationDetailsPage instance, SituationPropertiesTable value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::propertiesTable = value;
  }-*/;

  private native static InlineLabel _$549235025__74478675_serviceNamespace(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::serviceNamespace;
  }-*/;

  private native static void _$549235025__74478675_serviceNamespace(ServiceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::serviceNamespace = value;
  }-*/;

  private native static InlineLabel _$549235025__74478675_applicationNamespace(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::applicationNamespace;
  }-*/;

  private native static void _$549235025__74478675_applicationNamespace(ServiceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::applicationNamespace = value;
  }-*/;

  private native static InlineLabel _$549235025__74478675_serviceInterface(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::serviceInterface;
  }-*/;

  private native static void _$549235025__74478675_serviceInterface(ServiceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::serviceInterface = value;
  }-*/;

  private native static ReferenceTable _$549235025__$1106092480_referenceTable(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::referenceTable;
  }-*/;

  private native static void _$549235025__$1106092480_referenceTable(ServiceDetailsPage instance, ReferenceTable value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::referenceTable = value;
  }-*/;

  private native static SituationWatcherEvents _102653079__238502813_sitWatchEvents(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::sitWatchEvents;
  }-*/;

  private native static void _102653079__238502813_sitWatchEvents(SituationsPage instance, SituationWatcherEvents value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::sitWatchEvents = value;
  }-*/;

  private native static InlineLabel _80634777__74478675_referenceInterface(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::referenceInterface;
  }-*/;

  private native static void _80634777__74478675_referenceInterface(ReferenceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::referenceInterface = value;
  }-*/;

  private native static Button _102653079__2119756730_refreshButton(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::refreshButton;
  }-*/;

  private native static void _102653079__2119756730_refreshButton(SituationsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::refreshButton = value;
  }-*/;

  private native static ServiceFilters _$68797696__$2088441213_filtersPanel(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::filtersPanel;
  }-*/;

  private native static void _$68797696__$2088441213_filtersPanel(ServicesPage instance, ServiceFilters value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::filtersPanel = value;
  }-*/;

  private native static SpanElement _102653079__1424436749_totalSpan(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::totalSpan;
  }-*/;

  private native static void _102653079__1424436749_totalSpan(SituationsPage instance, SpanElement value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::totalSpan = value;
  }-*/;

  private native static InlineLabel _80634777__74478675_applicationNamespace(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::applicationNamespace;
  }-*/;

  private native static void _80634777__74478675_applicationNamespace(ReferenceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::applicationNamespace = value;
  }-*/;

  private native static Button _1089186566__2119756730_closeButton(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::closeButton;
  }-*/;

  private native static void _1089186566__2119756730_closeButton(SituationDetailsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::closeButton = value;
  }-*/;

  private native static void _1089186566__$1433900096_i18n(SituationDetailsPage instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::i18n = value;
  }-*/;

  private native static void _102653079__$1433900096_i18n(SituationsPage instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::i18n = value;
  }-*/;

  private native static TransitionAnchor _$1648295248__$229921779_toSituationsPage(DashboardPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.DashboardPage::toSituationsPage;
  }-*/;

  private native static void _$1648295248__$229921779_toSituationsPage(DashboardPage instance, TransitionAnchor<SituationsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.DashboardPage::toSituationsPage = value;
  }-*/;

  private native static SpanElement _$68797696__1424436749_totalSpan(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::totalSpan;
  }-*/;

  private native static void _$68797696__1424436749_totalSpan(ServicesPage instance, SpanElement value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::totalSpan = value;
  }-*/;

  private native static FlowPanel _74003371__$757255186_body(NotificationWidget instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget::body;
  }-*/;

  private native static void _74003371__$757255186_body(NotificationWidget instance, FlowPanel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget::body = value;
  }-*/;

  private native static void _$1600569994__$1509039043_toDetailsPageLinkFactory(SituationTable instance, TransitionAnchorFactory<SituationDetailsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationTable::toDetailsPageLinkFactory = value;
  }-*/;

  private native static Button _1089186566__2119756730_reopenButton(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::reopenButton;
  }-*/;

  private native static void _1089186566__2119756730_reopenButton(SituationDetailsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::reopenButton = value;
  }-*/;

  private native static SituationTable _102653079__$1600569994_situationsTable(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::situationsTable;
  }-*/;

  private native static void _102653079__$1600569994_situationsTable(SituationsPage instance, SituationTable value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::situationsTable = value;
  }-*/;

  private native static HtmlSnippet _$68797696__38184088_noDataMessage(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::noDataMessage;
  }-*/;

  private native static void _$68797696__38184088_noDataMessage(ServicesPage instance, HtmlSnippet value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::noDataMessage = value;
  }-*/;

  private native static DateTimePicker _148888483__$951620175_timestampTo(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::timestampTo;
  }-*/;

  private native static void _148888483__$951620175_timestampTo(SituationFilters instance, DateTimePicker value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::timestampTo = value;
  }-*/;

  private native static void _$1163117323__120980481_remoteServicesService(ServicesRpcService instance, Caller<IServicesService> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.ServicesRpcService::remoteServicesService = value;
  }-*/;

  private native static void _$1034438370__136504311_navigation(TransitionAnchorProvider instance, Navigation value) /*-{
    instance.@org.jboss.errai.ui.nav.client.local.TransitionAnchorProvider::navigation = value;
  }-*/;

  private native static void _$1600569994__$1433900096_i18n(SituationTable instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationTable::i18n = value;
  }-*/;

  private native static InlineLabel _$549235025__74478675_serviceName(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::serviceName;
  }-*/;

  private native static void _$549235025__74478675_serviceName(ServiceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::serviceName = value;
  }-*/;

  private native static DateTimePicker _148888483__$951620175_timestampFrom(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::timestampFrom;
  }-*/;

  private native static void _148888483__$951620175_timestampFrom(SituationFilters instance, DateTimePicker value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::timestampFrom = value;
  }-*/;

  private native static InlineLabel _1089186566__74478675_subject(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::subject;
  }-*/;

  private native static void _1089186566__74478675_subject(SituationDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::subject = value;
  }-*/;

  private native static Button _102653079__2119756730_retrySituations(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::retrySituations;
  }-*/;

  private native static void _102653079__2119756730_retrySituations(SituationsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::retrySituations = value;
  }-*/;

  private native static HtmlSnippet _80634777__38184088_loading(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::loading;
  }-*/;

  private native static void _80634777__38184088_loading(ReferenceDetailsPage instance, HtmlSnippet value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::loading = value;
  }-*/;

  private native static SituationPropertiesTable _1089186566__$1542973949_contextTable(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::contextTable;
  }-*/;

  private native static void _1089186566__$1542973949_contextTable(SituationDetailsPage instance, SituationPropertiesTable value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::contextTable = value;
  }-*/;

  private native static void _$1124051892__120980481_remoteSituationsService(SituationsRpcService instance, Caller<ISituationsService> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.SituationsRpcService::remoteSituationsService = value;
  }-*/;

  private native static TransitionAnchor _$549235025__$229921779_toSituationsPage(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::toSituationsPage;
  }-*/;

  private native static void _$549235025__$229921779_toSituationsPage(ServiceDetailsPage instance, TransitionAnchor<SituationsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::toSituationsPage = value;
  }-*/;

  private native static HtmlSnippet _102653079__38184088_searchInProgressMessage(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::searchInProgressMessage;
  }-*/;

  private native static void _102653079__38184088_searchInProgressMessage(SituationsPage instance, HtmlSnippet value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::searchInProgressMessage = value;
  }-*/;

  private native static Anchor _148888483__2084144957_clearFilters(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::clearFilters;
  }-*/;

  private native static void _148888483__2084144957_clearFilters(SituationFilters instance, Anchor value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::clearFilters = value;
  }-*/;

  private native static TransitionAnchor _$1648295248__$229921779_toServicesPage(DashboardPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.DashboardPage::toServicesPage;
  }-*/;

  private native static void _$1648295248__$229921779_toServicesPage(DashboardPage instance, TransitionAnchor<ServicesPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.DashboardPage::toServicesPage = value;
  }-*/;

  private native static InlineLabel _1089186566__74478675_timestamp(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::timestamp;
  }-*/;

  private native static void _1089186566__74478675_timestamp(SituationDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::timestamp = value;
  }-*/;

  private native static void _1961580097__$1433900096_i18n(CallTraceDetails instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.CallTraceDetails::i18n = value;
  }-*/;

  private native static HtmlSnippet _$68797696__38184088_searchInProgressMessage(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::searchInProgressMessage;
  }-*/;

  private native static void _$68797696__38184088_searchInProgressMessage(ServicesPage instance, HtmlSnippet value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::searchInProgressMessage = value;
  }-*/;

  private native static ServiceTable _$68797696__$990608298_servicesTable(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::servicesTable;
  }-*/;

  private native static void _$68797696__$990608298_servicesTable(ServicesPage instance, ServiceTable value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::servicesTable = value;
  }-*/;

  private native static CallTraceWidget _1089186566__1026095717_callTrace(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::callTrace;
  }-*/;

  private native static void _1089186566__1026095717_callTrace(SituationDetailsPage instance, CallTraceWidget value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::callTrace = value;
  }-*/;

  private native static ProcessingStateListBox _$2088441213__445223724_processingState(ServiceFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::processingState;
  }-*/;

  private native static void _$2088441213__445223724_processingState(ServiceFilters instance, ProcessingStateListBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::processingState = value;
  }-*/;

  private native static void _$1542973949__$1433900096_i18n(SituationPropertiesTable instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationPropertiesTable::i18n = value;
  }-*/;

  private native static Button _1089186566__2119756730_startButton(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::startButton;
  }-*/;

  private native static void _1089186566__2119756730_startButton(SituationDetailsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::startButton = value;
  }-*/;

  private native static Anchor _102653079__2084144957_sitWatchButton(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::sitWatchButton;
  }-*/;

  private native static void _102653079__2084144957_sitWatchButton(SituationsPage instance, Anchor value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::sitWatchButton = value;
  }-*/;

  private native static DataBinder _$549235025__$483175978_service(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::service;
  }-*/;

  private native static void _$549235025__$483175978_service(ServiceDetailsPage instance, DataBinder<ServiceBean> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::service = value;
  }-*/;

  private native static InlineLabel _1089186566__74478675_situationName(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::situationName;
  }-*/;

  private native static void _1089186566__74478675_situationName(SituationDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::situationName = value;
  }-*/;

  private native static SpanElement _$68797696__1424436749_rangeSpan(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::rangeSpan;
  }-*/;

  private native static void _$68797696__1424436749_rangeSpan(ServicesPage instance, SpanElement value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::rangeSpan = value;
  }-*/;

  private native static void _80634777__$1433900096_i18n(ReferenceDetailsPage instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::i18n = value;
  }-*/;

  private native static Button _1089186566__2119756730_stopButton(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::stopButton;
  }-*/;

  private native static void _1089186566__2119756730_stopButton(SituationDetailsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::stopButton = value;
  }-*/;

  private native static TransitionAnchor _1089186566__$229921779_toDashboardPage(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::toDashboardPage;
  }-*/;

  private native static void _1089186566__$229921779_toDashboardPage(SituationDetailsPage instance, TransitionAnchor<DashboardPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::toDashboardPage = value;
  }-*/;

  private native static ResolutionStateListBox _148888483__1181247566_resolutionState(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::resolutionState;
  }-*/;

  private native static void _148888483__1181247566_resolutionState(SituationFilters instance, ResolutionStateListBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::resolutionState = value;
  }-*/;

  private native static void _$549235025__$1433900096_i18n(ServiceDetailsPage instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::i18n = value;
  }-*/;

  private native static TransitionAnchor _102653079__$229921779_toServicesPage(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::toServicesPage;
  }-*/;

  private native static void _102653079__$229921779_toServicesPage(SituationsPage instance, TransitionAnchor<ServicesPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::toServicesPage = value;
  }-*/;

  private native static void _80634777__$1163117323_servicesService(ReferenceDetailsPage instance, ServicesRpcService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::servicesService = value;
  }-*/;

  private native static void _80634777__2030280515_notificationService(ReferenceDetailsPage instance, NotificationService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::notificationService = value;
  }-*/;

  private native static void _1089186566__2030280515_notificationService(SituationDetailsPage instance, NotificationService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::notificationService = value;
  }-*/;

  private native static SourceEditor _1089186566__$1499064285_messageEditor(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::messageEditor;
  }-*/;

  private native static void _1089186566__$1499064285_messageEditor(SituationDetailsPage instance, SourceEditor value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::messageEditor = value;
  }-*/;

  private native static void _$549235025__$1163117323_servicesService(ServiceDetailsPage instance, ServicesRpcService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::servicesService = value;
  }-*/;

  private native static Anchor _1089186566__2084144957_messageTabAnchor(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::messageTabAnchor;
  }-*/;

  private native static void _1089186566__2084144957_messageTabAnchor(SituationDetailsPage instance, Anchor value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::messageTabAnchor = value;
  }-*/;

  private native static void _1089186566__$1124051892_situationsService(SituationDetailsPage instance, SituationsRpcService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::situationsService = value;
  }-*/;

  private native static InlineLabel _80634777__74478675_applicationName(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::applicationName;
  }-*/;

  private native static void _80634777__74478675_applicationName(ReferenceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::applicationName = value;
  }-*/;

  private native static FlowPanel _1089186566__$757255186_severity(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::severity;
  }-*/;

  private native static void _1089186566__$757255186_severity(SituationDetailsPage instance, FlowPanel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::severity = value;
  }-*/;

  private native static Button _1089186566__2119756730_resolveButton(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resolveButton;
  }-*/;

  private native static void _1089186566__2119756730_resolveButton(SituationDetailsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resolveButton = value;
  }-*/;

  private native static TransitionAnchor _$68797696__$229921779_toDashboardPage(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::toDashboardPage;
  }-*/;

  private native static void _$68797696__$229921779_toDashboardPage(ServicesPage instance, TransitionAnchor<DashboardPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::toDashboardPage = value;
  }-*/;

  private native static void _2030280515__2098965610_bus(NotificationService instance, MessageBus value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.NotificationService::bus = value;
  }-*/;

  private native static void _102653079__2030280515_notificationService(SituationsPage instance, NotificationService value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::notificationService = value;
  }-*/;

  private native static TextBox _148888483__$371269162_description(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::description;
  }-*/;

  private native static void _148888483__$371269162_description(SituationFilters instance, TextBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::description = value;
  }-*/;

  private native static InlineLabel _1089186566__74478675_description(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::description;
  }-*/;

  private native static void _1089186566__74478675_description(SituationDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::description = value;
  }-*/;

  private native static DataBinder _1089186566__$483175978_situation(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::situation;
  }-*/;

  private native static void _1089186566__$483175978_situation(SituationDetailsPage instance, DataBinder<SituationBean> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::situation = value;
  }-*/;

  private native static Button _74003371__2119756730_closeButton(NotificationWidget instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget::closeButton;
  }-*/;

  private native static void _74003371__2119756730_closeButton(NotificationWidget instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget::closeButton = value;
  }-*/;

  private native static TransitionAnchor _$549235025__$229921779_toServicesPage(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::toServicesPage;
  }-*/;

  private native static void _$549235025__$229921779_toServicesPage(ServiceDetailsPage instance, TransitionAnchor<ServicesPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::toServicesPage = value;
  }-*/;

  private native static SpanElement _102653079__1424436749_rangeSpan(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::rangeSpan;
  }-*/;

  private native static void _102653079__1424436749_rangeSpan(SituationsPage instance, SpanElement value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::rangeSpan = value;
  }-*/;

  private native static Label _74003371__$1862661780_title(NotificationWidget instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget::title;
  }-*/;

  private native static void _74003371__$1862661780_title(NotificationWidget instance, Label value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget::title = value;
  }-*/;

  private native static TextBox _$2088441213__$371269162_serviceName(ServiceFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::serviceName;
  }-*/;

  private native static void _$2088441213__$371269162_serviceName(ServiceFilters instance, TextBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::serviceName = value;
  }-*/;

  private native static InlineLabel _1089186566__74478675_resubmitDetails(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resubmitDetails;
  }-*/;

  private native static void _1089186566__74478675_resubmitDetails(SituationDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resubmitDetails = value;
  }-*/;

  private native static void _1181247566__$1433900096_i18n(ResolutionStateListBox instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.ResolutionStateListBox::i18n = value;
  }-*/;

  private native static void _$1106092480__$1433900096_i18n(ReferenceTable instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ReferenceTable::i18n = value;
  }-*/;

  private native static void _2030280515__$124296006_rootPanel(NotificationService instance, RootPanel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.NotificationService::rootPanel = value;
  }-*/;

  private native static InlineLabel _$549235025__74478675_applicationName(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::applicationName;
  }-*/;

  private native static void _$549235025__74478675_applicationName(ServiceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::applicationName = value;
  }-*/;

  private native static void _1496760654__136504311_navigation(TransitionAnchorFactoryProvider instance, Navigation value) /*-{
    instance.@org.jboss.errai.ui.nav.client.local.TransitionAnchorFactoryProvider::navigation = value;
  }-*/;

  private native static TextBox _148888483__$371269162_type(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::type;
  }-*/;

  private native static void _148888483__$371269162_type(SituationFilters instance, TextBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::type = value;
  }-*/;

  private native static void _$184711112__$124296006_rootPanel(App instance, RootPanel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.App::rootPanel = value;
  }-*/;

  private native static InlineLabel _80634777__74478675_referenceNamespace(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::referenceNamespace;
  }-*/;

  private native static void _80634777__74478675_referenceNamespace(ReferenceDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::referenceNamespace = value;
  }-*/;

  private native static TransitionAnchor _80634777__$229921779_toDashboardPage(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::toDashboardPage;
  }-*/;

  private native static void _80634777__$229921779_toDashboardPage(ReferenceDetailsPage instance, TransitionAnchor<DashboardPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::toDashboardPage = value;
  }-*/;

  private native static void _$990608298__$1509039043_toDetailsPageLinkFactory(ServiceTable instance, TransitionAnchorFactory<ServiceDetailsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceTable::toDetailsPageLinkFactory = value;
  }-*/;

  private native static TransitionAnchor _1089186566__$229921779_toServicesPage(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::toServicesPage;
  }-*/;

  private native static void _1089186566__$229921779_toServicesPage(SituationDetailsPage instance, TransitionAnchor<ServicesPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::toServicesPage = value;
  }-*/;

  private native static Button _1089186566__2119756730_assignButton(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::assignButton;
  }-*/;

  private native static void _1089186566__2119756730_assignButton(SituationDetailsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::assignButton = value;
  }-*/;

  private native static TextBox _148888483__$371269162_host(SituationFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::host;
  }-*/;

  private native static void _148888483__$371269162_host(SituationFilters instance, TextBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::host = value;
  }-*/;

  private native static Button _1089186566__2119756730_resubmitButton(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resubmitButton;
  }-*/;

  private native static void _1089186566__2119756730_resubmitButton(SituationDetailsPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resubmitButton = value;
  }-*/;

  private native static TransitionAnchor _$68797696__$229921779_toSituationsPage(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::toSituationsPage;
  }-*/;

  private native static void _$68797696__$229921779_toSituationsPage(ServicesPage instance, TransitionAnchor<SituationsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::toSituationsPage = value;
  }-*/;

  private native static TransitionAnchor _1089186566__$229921779_toSituationsPage(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::toSituationsPage;
  }-*/;

  private native static void _1089186566__$229921779_toSituationsPage(SituationDetailsPage instance, TransitionAnchor<SituationsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::toSituationsPage = value;
  }-*/;

  private native static TransitionAnchor _80634777__$229921779_toServicesPage(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::toServicesPage;
  }-*/;

  private native static void _80634777__$229921779_toServicesPage(ReferenceDetailsPage instance, TransitionAnchor<ServicesPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::toServicesPage = value;
  }-*/;

  private native static TransitionAnchor _102653079__$229921779_toDashboardPage(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::toDashboardPage;
  }-*/;

  private native static void _102653079__$229921779_toDashboardPage(SituationsPage instance, TransitionAnchor<DashboardPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::toDashboardPage = value;
  }-*/;

  private native static Button _$68797696__2119756730_servicesRefreshButton(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::servicesRefreshButton;
  }-*/;

  private native static void _$68797696__2119756730_servicesRefreshButton(ServicesPage instance, Button value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::servicesRefreshButton = value;
  }-*/;

  private native static void _$1300398733__$652658075_beanManager(DisposerProvider instance, SyncBeanManager value) /*-{
    instance.@org.jboss.errai.ioc.client.api.builtin.DisposerProvider::beanManager = value;
  }-*/;

  private native static void _$1106092480__$1509039043_toDetailsPageLinkFactory(ReferenceTable instance, TransitionAnchorFactory<ReferenceDetailsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ReferenceTable::toDetailsPageLinkFactory = value;
  }-*/;

  private native static InlineLabel _1089186566__74478675_resolutionState(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resolutionState;
  }-*/;

  private native static void _1089186566__74478675_resolutionState(SituationDetailsPage instance, InlineLabel value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::resolutionState = value;
  }-*/;

  private native static Pager _102653079__1762635241_pager(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::pager;
  }-*/;

  private native static void _102653079__1762635241_pager(SituationsPage instance, Pager value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::pager = value;
  }-*/;

  private native static ApplicationNameListBox _$2088441213__$56311345_applicationName(ServiceFilters instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::applicationName;
  }-*/;

  private native static void _$2088441213__$56311345_applicationName(ServiceFilters instance, ApplicationNameListBox value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::applicationName = value;
  }-*/;

  private native static Pager _$68797696__1762635241_pager(ServicesPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::pager;
  }-*/;

  private native static void _$68797696__1762635241_pager(ServicesPage instance, Pager value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::pager = value;
  }-*/;

  private native static HtmlSnippet _$549235025__38184088_loading(ServiceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::loading;
  }-*/;

  private native static void _$549235025__38184088_loading(ServiceDetailsPage instance, HtmlSnippet value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::loading = value;
  }-*/;

  private native static DataBinder _80634777__$483175978_reference(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::reference;
  }-*/;

  private native static void _80634777__$483175978_reference(ReferenceDetailsPage instance, DataBinder<ReferenceBean> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::reference = value;
  }-*/;

  private native static SituationFilters _102653079__148888483_filtersPanel(SituationsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::filtersPanel;
  }-*/;

  private native static void _102653079__148888483_filtersPanel(SituationsPage instance, SituationFilters value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::filtersPanel = value;
  }-*/;

  private native static void _2030280515__624081413_dispatcher(NotificationService instance, RequestDispatcher value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.NotificationService::dispatcher = value;
  }-*/;

  private native static void _1024371274__$1433900096_i18n(SeverityListBox instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SeverityListBox::i18n = value;
  }-*/;

  private native static void _$56311345__$1433900096_i18n(ApplicationNameListBox instance, ClientMessages value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ApplicationNameListBox::i18n = value;
  }-*/;

  private native static void _$184711112__136504311_navigation(App instance, Navigation value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.App::navigation = value;
  }-*/;

  private native static void _$1516327018__$1509039043_toDetailsPageLinkFactory(SituationWatcherEvent instance, TransitionAnchorFactory<SituationDetailsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationWatcherEvent::toDetailsPageLinkFactory = value;
  }-*/;

  private native static TransitionAnchor _80634777__$229921779_toSituationsPage(ReferenceDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::toSituationsPage;
  }-*/;

  private native static void _80634777__$229921779_toSituationsPage(ReferenceDetailsPage instance, TransitionAnchor<SituationsPage> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::toSituationsPage = value;
  }-*/;

  private native static void _238502813__$903668163_sitWatchEventFactory(SituationWatcherEvents instance, Instance<SituationWatcherEvent> value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationWatcherEvents::sitWatchEventFactory = value;
  }-*/;

  private native static CallTraceDetails _1089186566__1961580097_callTraceDetail(SituationDetailsPage instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::callTraceDetail;
  }-*/;

  private native static void _1089186566__1961580097_callTraceDetail(SituationDetailsPage instance, CallTraceDetails value) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::callTraceDetail = value;
  }-*/;

  public native static void _136504311_init(Navigation instance) /*-{
    instance.@org.jboss.errai.ui.nav.client.local.Navigation::init()();
  }-*/;

  public native static void _136504311_cleanUp(Navigation instance) /*-{
    instance.@org.jboss.errai.ui.nav.client.local.Navigation::cleanUp()();
  }-*/;

  public native static void _1183065176_postConstruct(AbstractFilterListBox instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.widgets.common.AbstractFilterListBox::postConstruct()();
  }-*/;

  public native static void _$951620175_postConstruct(DateTimePicker instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.widgets.DateTimePicker::postConstruct()();
  }-*/;

  public native static void _148888483_postConstruct(SituationFilters instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.situations.SituationFilters::postConstruct()();
  }-*/;

  public native static void _2030280515_onPostConstruct(NotificationService instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.NotificationService::onPostConstruct()();
  }-*/;

  public native static void _$1203922748__onPostConstruct(AbstractPage instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::_onPostConstruct()();
  }-*/;

  public native static void _80634777_onPostConstruct(ReferenceDetailsPage instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::onPostConstruct()();
  }-*/;

  public native static void _$1581558918_postContruct(TemplatedWidgetTable instance) /*-{
    instance.@org.overlord.commons.gwt.client.local.widgets.TemplatedWidgetTable::postContruct()();
  }-*/;

  public native static void _1401717584_postConstruct(ToggleSwitch instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.widgets.ToggleSwitch::postConstruct()();
  }-*/;

  public native static void _102653079_postConstruct(SituationsPage instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::postConstruct()();
  }-*/;

  public native static void _74003371_onPostConstruct(NotificationWidget instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.services.notification.NotificationWidget::onPostConstruct()();
  }-*/;

  public native static void _$549235025_onPostConstruct(ServiceDetailsPage instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::onPostConstruct()();
  }-*/;

  public native static void _$2088441213_postConstruct(ServiceFilters instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.services.ServiceFilters::postConstruct()();
  }-*/;

  public native static void _$68797696_postConstruct(ServicesPage instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::postConstruct()();
  }-*/;

  public native static void _1089186566_onResubmitClick(SituationDetailsPage instance, ClickEvent a0) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onResubmitClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void _1089186566_onAssignButtonClick(SituationDetailsPage instance, ClickEvent a0) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onAssignButtonClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void _1089186566_onDeassignButtonClick(SituationDetailsPage instance, ClickEvent a0) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onDeassignButtonClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void _1089186566_onStartButtonClick(SituationDetailsPage instance, ClickEvent a0) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onStartButtonClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void _1089186566_onStopButtonClick(SituationDetailsPage instance, ClickEvent a0) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onStopButtonClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void _1089186566_onResolveButtonClick(SituationDetailsPage instance, ClickEvent a0) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onResolveButtonClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void _1089186566_onReopenButtonClick(SituationDetailsPage instance, ClickEvent a0) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onReopenButtonClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(a0);
  }-*/;

  public native static void _1089186566_onPostConstruct(SituationDetailsPage instance) /*-{
    instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::onPostConstruct()();
  }-*/;

  // The main IOC bootstrap method.
  public SimpleInjectionContext bootstrapContainer() {
    declareBeans_0();
    declareBeans_1();
    return injContext;
  }
}