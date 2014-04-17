package org.overlord.rtgov.ui.server.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.server.services.ISituationsServiceImpl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Used to export the message content of filtered Situations.
 */
public class SituationsExportServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    @Inject
    private ISituationsServiceImpl situationsServiceImpl;
    private MessageBus messageBus;
    private Cache<String, SituationsFilterBean> exportFilterCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).expireAfterAccess(1, TimeUnit.SECONDS)
            .<String, SituationsFilterBean> build();

    /**
     * @param messageBus
     *            the messageBus to set
     */
    @Inject
    public void setMessageBus(MessageBus messageBus) {
        this.messageBus = messageBus;
        this.messageBus.subscribe("situations/export", new MessageCallback() {
            @Override
            public void callback(Message message) {
                String exportKey = message.get(String.class, "exportKey");
                SituationsFilterBean exportFilter = message.get(SituationsFilterBean.class, "exportFilter");
                exportFilterCache.put(exportKey, exportFilter);
            }
        });
    }

    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws ServletException, IOException {
        httpServletResponse.setContentType("text/plain");
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=situations_export_"
                + new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date()) + ".txt");
        String exportKey = httpServletRequest.getParameter("_k");
        SituationsFilterBean situationsFilterBean = exportFilterCache.getIfPresent(exportKey);
        if (situationsFilterBean != null) {
            situationsServiceImpl.export(situationsFilterBean, httpServletResponse.getOutputStream());
        }
    }

}
