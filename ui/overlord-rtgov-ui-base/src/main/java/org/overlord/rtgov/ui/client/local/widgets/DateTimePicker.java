package org.overlord.rtgov.ui.client.local.widgets;

import java.util.Date;

import javax.annotation.PostConstruct;

import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.TextBox;

/**
 * GWT wrapper around http://tarruda.github.io/bootstrap-datetimepicker/
 * 
 */
public class DateTimePicker extends TextBox {

    private static final String DEFAULT_DATE_TIME_FORMAT = "dd/MM/yyyy hh:mm:ss"; //$NON-NLS-1$

    private static int cidCounter = 1;

    private static String generateUniqueCid() {
        return "cid-" + cidCounter++; //$NON-NLS-1$
    }

    private String cid;

    /**
     * Constructor.
     */
    public DateTimePicker() {
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
                    initPicker(cid,DEFAULT_DATE_TIME_FORMAT);
                } else {
                    removePicker(cid);
                }
            }
        });
    }

    /**
     * Initializes the bootstrap-datetime picker javascript.
     * @param cid 
     * @param dateTimeFormat 
     */
    protected native void initPicker(String cid, String dateTimeFormat) /*-{
		var selector = '.' + cid
		$wnd.jQuery(selector).closest('.input-append').datetimepicker({
			format: dateTimeFormat,
		});
		var dateTimePicker = this;
        $wnd.jQuery(selector).closest('.input-append').on('hide',function(){
			dateTimePicker.@org.overlord.rtgov.ui.client.local.widgets.DateTimePicker::setValue(Ljava/lang/String;)($wnd.jQuery(selector).val());
        });
    }-*/;

    @Override
    public void setValue(String value) {
        super.setValue(value);
        ValueChangeEvent.fire(this, value);
    }
    
    /**
     * Removes the bootstrap-datetimepicker from the DOM and cleans up all events.
     */
    protected native void removePicker(String cid) /*-{
		var selector = '.' + cid;
		$wnd.jQuery(selector).closest('.input-append').datetimepicker('destroy');
    }-*/;

    /**
     * @return the current value as a {@link Date} or null if empty
     */
    public Date getDateValue() {
        return parseDateTime(getValue());
    }

    /**
     * Parses the given value as a date using the configured date/time format.
     * 
     * @param value
     */
    private Date parseDateTime(String value) {
        if (value == null || "".equals(value)) //$NON-NLS-1$
            return null;
        DateTimeFormat format = getFormat();
        return format.parse(value);
    }

    /**
     * @param value
     *            the new {@link Date} value
     */
    public void setDateValue(Date value) {
        String v = formatDate(value);
        if (v == null)
            v = ""; //$NON-NLS-1$
        setValue(v);
    }
    
    /**
     * Formats the date using the configured date format.
     * 
     * @param value
     * @return the Date formatted as a string or null if the input is null
     */
    private String formatDate(Date value) {
        if (value == null)
            return null;
        DateTimeFormat format = getFormat();
        return format.format(value);
    }

    /**
     * Gets the format.
     */
    private DateTimeFormat getFormat() {
        String strFmt = DEFAULT_DATE_TIME_FORMAT;
        if (getElement().hasAttribute("data-format")) { //$NON-NLS-1$
            strFmt = getElement().getAttribute("data-format"); //$NON-NLS-1$
        }
        return DateTimeFormat.getFormat(strFmt);
    }

    /**
     * Sets the date format used by this instance and by bootstrap-datetimepicker
     * for this date box.
     * 
     * @param format
     */
    public void setDateFormat(String format) {
        getElement().setAttribute("data-format", format); //$NON-NLS-1$
    }

}
