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
package org.overlord.rtgov.ui.client.local.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Some util methods for dealing with the DOM.
 * @author eric.wittmann@redhat.com
 */
public class DOMUtil {

    /**
     * Gets an element from the given parent element by ID.
     * @param context
     * @param id
     */
    public static Element findElementById(Element context, String id) {
        NodeList<Node> nodes = context.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.getItem(i);
            if (((node.getNodeType() == Node.ELEMENT_NODE))) {
                if (id.equals(((Element) node).getAttribute("id"))) { //$NON-NLS-1$
                    return (Element) node;
                } else {
                    Element elem = findElementById((Element) node, id);
                    if (elem != null) {
                        return elem;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Adds a jquery click handler to the given element.  This allows the page to get notified when
     * a native javascript element is clicked.
     * @param element
     * @param handler
     */
    public static native void addClickHandlerToElement(Element element, ClickHandler handler) /*-{
        $wnd.jQuery(element).click(function() {
            handler.@com.google.gwt.event.dom.client.ClickHandler::onClick(Lcom/google/gwt/event/dom/client/ClickEvent;)(null);
        });
    }-*/;

}
