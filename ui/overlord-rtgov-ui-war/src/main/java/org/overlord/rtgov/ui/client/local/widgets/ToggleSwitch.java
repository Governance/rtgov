package org.overlord.rtgov.ui.client.local.widgets;

import javax.annotation.PostConstruct;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.user.client.ui.TextBox;

/**
 * GWT wrapper around http://www.bootstrap-switch.org
 * 
 */
public class ToggleSwitch extends TextBox {
    private static int cidCounter = 1;

    private static String generateUniqueCid() {
        return "tsid-" + cidCounter++; //$NON-NLS-1$
    }

    private String cid;

    /**
     * Constructor.
     */
    public ToggleSwitch() {
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
                    init(cid);
                } else {
                    remove(cid);
                }
            }
        });
    }

    /**
     * Initializes the bootstrap toggle-switch.
     * 
     * @param cid
     */
    protected native void init(String cid) /*-{
		var selector = '.' + cid
        $wnd.jQuery(selector).bootstrapSwitch();
        var toggleSwitch = this;
        $wnd.jQuery(selector).on('switch-change', function(event, state) {
            toggleSwitch.@org.overlord.rtgov.ui.client.local.widgets.ToggleSwitch::toggle(Ljava/lang/Boolean;)(@java.lang.Boolean::valueOf(Z)(state.value));
        });
    }-*/;

    /**
     * @param value
     *            The current value to toggle
     */
    public void toggle(Boolean value) {
        setValue(value.toString());
    }

    /**
     * Removes the bootstrap toggle-switch from the DOM and cleans up all
     * events.
     */
    protected native void remove(String cid) /*-{
		var selector = '.' + cid;
		$wnd.jQuery(selector).bootstrapSwitch('destroy');
    }-*/;

}
