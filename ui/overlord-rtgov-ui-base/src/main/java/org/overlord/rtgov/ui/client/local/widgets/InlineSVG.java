package org.overlord.rtgov.ui.client.local.widgets;

import javax.annotation.PostConstruct;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.InlineHTML;

/**
 * Thin wrapper around {@link InlineHTML} to work with errai's data binding.
 */
public class InlineSVG extends InlineHTML {

    private static int cidCounter = 1;
    private String cid;

    private static String generateUniqueCid() {
        return "svg-" + cidCounter++; //$NON-NLS-1$
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        addAttachHandler(new Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    cid = generateUniqueCid();
                    getElement().addClassName(cid);
                }
            }
        });
    }

    @Override
    public void setText(String text) {
        setInnerSVG(cid, text);
    }

    /**
     * All of the markup and content within a given element.
     * @param text
     */
    public native void setInnerSVG(String cid, String svg) /*-{
        var selector = '.' + cid;
        $wnd.SVGRoot = $wnd.jQuery(svg.replace(/<\?xml.*\?>/g, '')).get(0);
        $wnd.SVGDocument = $wnd.SVGRoot;
        $wnd.jQuery(selector).append($wnd.jQuery(svg));
    }-*/;
}
