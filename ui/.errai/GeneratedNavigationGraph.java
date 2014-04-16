package org.jboss.errai.ui.nav.client.local.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.async.CreationalCallback;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.HistoryToken;
import org.jboss.errai.ui.nav.client.local.NavigationEvent;
import org.overlord.rtgov.ui.client.local.pages.AbstractPage;
import org.overlord.rtgov.ui.client.local.pages.DashboardPage;
import org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage;
import org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage;
import org.overlord.rtgov.ui.client.local.pages.ServicesPage;
import org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage;
import org.overlord.rtgov.ui.client.local.pages.SituationsPage;

public class GeneratedNavigationGraph extends NavigationGraph {
  public GeneratedNavigationGraph() {
    final PageNode situationDetailsPage = new PageNode<SituationDetailsPage>() {
      public String name() {
        return "situationDetails";
      }

      public Class contentType() {
        return SituationDetailsPage.class;
      }

      public void produceContent(CreationalCallback callback) {
        bm.lookupBean(SituationDetailsPage.class).getInstance(callback);
      }

      public void pageHiding(SituationDetailsPage widget) {

      }

      public void pageHidden(SituationDetailsPage widget) {

      }

      private native void _1089186566__1195259493_id(SituationDetailsPage instance, String value) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.SituationDetailsPage::id = value;
      }-*/;

      public native void _$1203922748__onPageShowing(AbstractPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::_onPageShowing()();
      }-*/;

      public void pageShowing(SituationDetailsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        final Collection fv0 = state.getState().get("id");
        if ((fv0 == null) || fv0.isEmpty()) {
          _1089186566__1195259493_id(widget, null);
        } else {
          _1089186566__1195259493_id(widget, (String) fv0.iterator().next());
          pageState.put("id", fv0.iterator().next());
        }
        CDI.fireEvent(new NavigationEvent(state));
        _$1203922748__onPageShowing(widget);
      }

      public void pageShown(SituationDetailsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        final Collection fv0 = state.getState().get("id");
        if ((fv0 == null) || fv0.isEmpty()) {
          _1089186566__1195259493_id(widget, null);
        } else {
          _1089186566__1195259493_id(widget, (String) fv0.iterator().next());
          pageState.put("id", fv0.iterator().next());
        }
      }
    };
    pagesByName.put("situationDetails", situationDetailsPage);
    final PageNode serviceDetailsPage = new PageNode<ServiceDetailsPage>() {
      public String name() {
        return "serviceDetails";
      }

      public Class contentType() {
        return ServiceDetailsPage.class;
      }

      public void produceContent(CreationalCallback callback) {
        bm.lookupBean(ServiceDetailsPage.class).getInstance(callback);
      }

      public void pageHiding(ServiceDetailsPage widget) {

      }

      public void pageHidden(ServiceDetailsPage widget) {

      }

      private native void _$549235025__1195259493_id(ServiceDetailsPage instance, String value) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.ServiceDetailsPage::id = value;
      }-*/;

      public native void _$1203922748__onPageShowing(AbstractPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::_onPageShowing()();
      }-*/;

      public void pageShowing(ServiceDetailsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        final Collection fv0 = state.getState().get("id");
        if ((fv0 == null) || fv0.isEmpty()) {
          _$549235025__1195259493_id(widget, null);
        } else {
          _$549235025__1195259493_id(widget, (String) fv0.iterator().next());
          pageState.put("id", fv0.iterator().next());
        }
        CDI.fireEvent(new NavigationEvent(state));
        _$1203922748__onPageShowing(widget);
      }

      public void pageShown(ServiceDetailsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        final Collection fv0 = state.getState().get("id");
        if ((fv0 == null) || fv0.isEmpty()) {
          _$549235025__1195259493_id(widget, null);
        } else {
          _$549235025__1195259493_id(widget, (String) fv0.iterator().next());
          pageState.put("id", fv0.iterator().next());
        }
      }
    };
    pagesByName.put("serviceDetails", serviceDetailsPage);
    final PageNode referenceDetailsPage = new PageNode<ReferenceDetailsPage>() {
      public String name() {
        return "referenceDetails";
      }

      public Class contentType() {
        return ReferenceDetailsPage.class;
      }

      public void produceContent(CreationalCallback callback) {
        bm.lookupBean(ReferenceDetailsPage.class).getInstance(callback);
      }

      public void pageHiding(ReferenceDetailsPage widget) {

      }

      public void pageHidden(ReferenceDetailsPage widget) {

      }

      private native void _80634777__1195259493_id(ReferenceDetailsPage instance, String value) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.ReferenceDetailsPage::id = value;
      }-*/;

      public native void _$1203922748__onPageShowing(AbstractPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::_onPageShowing()();
      }-*/;

      public void pageShowing(ReferenceDetailsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        final Collection fv0 = state.getState().get("id");
        if ((fv0 == null) || fv0.isEmpty()) {
          _80634777__1195259493_id(widget, null);
        } else {
          _80634777__1195259493_id(widget, (String) fv0.iterator().next());
          pageState.put("id", fv0.iterator().next());
        }
        CDI.fireEvent(new NavigationEvent(state));
        _$1203922748__onPageShowing(widget);
      }

      public void pageShown(ReferenceDetailsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        final Collection fv0 = state.getState().get("id");
        if ((fv0 == null) || fv0.isEmpty()) {
          _80634777__1195259493_id(widget, null);
        } else {
          _80634777__1195259493_id(widget, (String) fv0.iterator().next());
          pageState.put("id", fv0.iterator().next());
        }
      }
    };
    pagesByName.put("referenceDetails", referenceDetailsPage);
    final PageNode situationsPage = new PageNode<SituationsPage>() {
      public String name() {
        return "situations";
      }

      public Class contentType() {
        return SituationsPage.class;
      }

      public void produceContent(CreationalCallback callback) {
        bm.lookupBean(SituationsPage.class).getInstance(callback);
      }

      public native void _102653079_onPageHiding(SituationsPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::onPageHiding()();
      }-*/;

      public void pageHiding(SituationsPage widget) {
        _102653079_onPageHiding(widget);
      }

      public void pageHidden(SituationsPage widget) {

      }

      public native void _$1203922748__onPageShowing(AbstractPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::_onPageShowing()();
      }-*/;

      public void pageShowing(SituationsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        CDI.fireEvent(new NavigationEvent(state));
        _$1203922748__onPageShowing(widget);
      }

      public native void _102653079_onPageShown(SituationsPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.SituationsPage::onPageShown()();
      }-*/;

      public void pageShown(SituationsPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        _102653079_onPageShown(widget);
      }
    };
    pagesByName.put("situations", situationsPage);
    final PageNode defaultPage = new PageNode<DashboardPage>() {
      public String name() {
        return "dashboard";
      }

      public Class contentType() {
        return DashboardPage.class;
      }

      public void produceContent(CreationalCallback callback) {
        bm.lookupBean(DashboardPage.class).getInstance(callback);
      }

      public void pageHiding(DashboardPage widget) {

      }

      public void pageHidden(DashboardPage widget) {

      }

      public native void _$1203922748__onPageShowing(AbstractPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::_onPageShowing()();
      }-*/;

      public void pageShowing(DashboardPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        CDI.fireEvent(new NavigationEvent(state));
        _$1203922748__onPageShowing(widget);
      }

      public native void _$1648295248_onPageShown(DashboardPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.DashboardPage::onPageShown()();
      }-*/;

      public void pageShown(DashboardPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        _$1648295248_onPageShown(widget);
      }
    };
    pagesByName.put("", defaultPage);
    pagesByRole.put(DefaultPage.class, defaultPage);
    final PageNode dashboardPage = defaultPage;
    pagesByName.put("dashboard", dashboardPage);
    pagesByRole.put(DefaultPage.class, dashboardPage);
    final PageNode servicesPage = new PageNode<ServicesPage>() {
      public String name() {
        return "services";
      }

      public Class contentType() {
        return ServicesPage.class;
      }

      public void produceContent(CreationalCallback callback) {
        bm.lookupBean(ServicesPage.class).getInstance(callback);
      }

      public void pageHiding(ServicesPage widget) {

      }

      public void pageHidden(ServicesPage widget) {

      }

      public native void _$1203922748__onPageShowing(AbstractPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.AbstractPage::_onPageShowing()();
      }-*/;

      public void pageShowing(ServicesPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        CDI.fireEvent(new NavigationEvent(state));
        _$1203922748__onPageShowing(widget);
      }

      public native void _$68797696_onPageShown(ServicesPage instance) /*-{
        instance.@org.overlord.rtgov.ui.client.local.pages.ServicesPage::onPageShown()();
      }-*/;

      public void pageShown(ServicesPage widget, HistoryToken state) {
        final Map pageState = new HashMap() {
          {

          }
        };
        _$68797696_onPageShown(widget);
      }
    };
    pagesByName.put("services", servicesPage);
  }

}