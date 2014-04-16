package org.jboss.errai.ui.client.local.spi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import org.jboss.errai.ui.shared.MessageBundle;

public class TranslationServiceImpl extends TranslationService {
  public interface org_overlord_rtgov_ui_client_local_messagesMessageBundleResource extends MessageBundle, ClientBundle {
  @Source("org/overlord/rtgov/ui/client/local/messages.json") public TextResource getContents(); }
  public TranslationServiceImpl() {
    org_overlord_rtgov_ui_client_local_messagesMessageBundleResource var1 = GWT.create(org_overlord_rtgov_ui_client_local_messagesMessageBundleResource.class);
    registerBundle(var1.getContents().getText(), null);
  }

}