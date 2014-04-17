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
package org.overlord.rtgov.devsvr;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.jboss.errai.bus.server.servlet.DefaultBlockingServlet;
import org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener;
import org.jboss.weld.environment.servlet.Listener;
import org.overlord.commons.dev.server.DevServerEnvironment;
import org.overlord.commons.dev.server.ErraiDevServer;
import org.overlord.commons.dev.server.MultiDefaultServlet;
import org.overlord.commons.dev.server.discovery.ErraiWebAppModuleFromMavenDiscoveryStrategy;
import org.overlord.commons.dev.server.discovery.JarModuleFromIDEDiscoveryStrategy;
import org.overlord.commons.dev.server.discovery.JarModuleFromMavenDiscoveryStrategy;
import org.overlord.commons.dev.server.discovery.WebAppModuleFromIDEDiscoveryStrategy;
import org.overlord.commons.gwt.server.filters.GWTCacheControlFilter;
import org.overlord.commons.gwt.server.filters.ResourceCacheControlFilter;
import org.overlord.commons.ui.header.OverlordHeaderDataJS;
import org.overlord.rtgov.ui.server.RtgovUI;
import org.overlord.rtgov.ui.server.servlet.SituationsExportServlet;

import com.google.common.collect.Lists;

/**
 * A dev server for DTGov.
 * @author eric.wittmann@redhat.com
 */
public class RTGovUIDevServer extends ErraiDevServer {

    /**
     * Main entry point.
     * @param args
     */
    public static void main(String [] args) throws Exception {
        RTGovUIDevServer devServer = new RTGovUIDevServer(args);
        devServer.go();
    }

    /**
     * Constructor.
     * @param args
     */
    public RTGovUIDevServer(String [] args) {
        super(args);
    }

    /**
     * @see org.overlord.commons.dev.server.DevServer#serverPort()
     */
    @Override
    protected int serverPort() {
        return 8080;
    }

    /**
     * @see org.overlord.commons.dev.server.DevServer#preConfig()
     */
    @Override
    protected void preConfig() {
        // Don't do any resource caching!
        System.setProperty("overlord.resource-caching.disabled", "true");
    }

    /**
     * @see org.overlord.commons.dev.server.ErraiDevServer#getErraiModuleId()
     */
    @Override
    protected String getErraiModuleId() {
        return "rtgov-ui";
    }

    /**
     * @see org.overlord.commons.dev.server.DevServer#createDevEnvironment()
     */
    @Override
    protected DevServerEnvironment createDevEnvironment() {
        return new RTGovUIDevServerEnvironment(args);
    }

    /**
     * @see org.overlord.commons.dev.server.DevServer#addModules(org.overlord.commons.dev.server.DevServerEnvironment)
     */
    @Override
    protected void addModules(DevServerEnvironment environment) {
        environment.addModule("rtgov-ui",
                new WebAppModuleFromIDEDiscoveryStrategy(RtgovUI.class),
                new ErraiWebAppModuleFromMavenDiscoveryStrategy(RtgovUI.class));
        environment.addModule("overlord-commons-uiheader",
                new JarModuleFromIDEDiscoveryStrategy(OverlordHeaderDataJS.class, "src/main/resources/META-INF/resources"),
                new JarModuleFromMavenDiscoveryStrategy(OverlordHeaderDataJS.class, "/META-INF/resources"));
    }

    /**
     * @see org.overlord.commons.dev.server.DevServer#addModulesToJetty(org.overlord.commons.dev.server.DevServerEnvironment, org.eclipse.jetty.server.handler.ContextHandlerCollection)
     */
    @Override
    protected void addModulesToJetty(DevServerEnvironment environment, ContextHandlerCollection handlers) throws Exception {
        super.addModulesToJetty(environment, handlers);
        /* *********
         * RTGov UI
         * ********* */
        ServletContextHandler rtgovUI = new ServletContextHandler(ServletContextHandler.SESSIONS);
        rtgovUI.setContextPath("/rtgov-ui");
        rtgovUI.setWelcomeFiles(new String[] { "index.html" });
        rtgovUI.setResourceBase(environment.getModuleDir("rtgov-ui").getCanonicalPath());
        rtgovUI.setInitParameter("errai.properties", "/WEB-INF/errai.properties");
        rtgovUI.setInitParameter("login.config", "/WEB-INF/login.config");
        rtgovUI.setInitParameter("users.properties", "/WEB-INF/users.properties");
        rtgovUI.addEventListener(new Listener());
        rtgovUI.addEventListener(new BeanManagerResourceBindingListener());
        rtgovUI.addFilter(GWTCacheControlFilter.class, "/app/*", EnumSet.of(DispatcherType.REQUEST));
        rtgovUI.addFilter(ResourceCacheControlFilter.class, "/css/*", EnumSet.of(DispatcherType.REQUEST));
        rtgovUI.addFilter(ResourceCacheControlFilter.class, "/images/*", EnumSet.of(DispatcherType.REQUEST));
        rtgovUI.addFilter(ResourceCacheControlFilter.class, "/js/*", EnumSet.of(DispatcherType.REQUEST));
        rtgovUI.addFilter(org.overlord.rtgov.ui.server.filters.LocaleFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
        // Servlets
        ServletHolder erraiServlet = new ServletHolder(DefaultBlockingServlet.class);
        erraiServlet.setInitOrder(1);
        rtgovUI.addServlet(erraiServlet, "*.erraiBus");
        ServletHolder headerDataServlet = new ServletHolder(OverlordHeaderDataJS.class);
        headerDataServlet.setInitParameter("app-id", "rtgov-ui");
        rtgovUI.addServlet(headerDataServlet, "/js/overlord-header-data.js");
        // File resources
        ServletHolder resources = new ServletHolder(new MultiDefaultServlet());
        resources.setInitParameter("resourceBase", "/");
        resources.setInitParameter("resourceBases", environment.getModuleDir("rtgov-ui").getCanonicalPath()
                + "|" + environment.getModuleDir("overlord-commons-uiheader").getCanonicalPath());
        resources.setInitParameter("dirAllowed", "true");
        resources.setInitParameter("pathInfoOnly", "false");
        String[] fileTypes = new String[] { "html", "js", "css", "png", "gif" };
        for (String fileType : fileTypes) {
            rtgovUI.addServlet(resources, "*." + fileType);
        }
        ServletHolder servletHolder = new ServletHolder(SituationsExportServlet.class);
        servletHolder.setInitOrder(2);
        rtgovUI.addServlet(servletHolder, "/situations/export");

        rtgovUI.setSecurityHandler(createSecurityHandler());
        handlers.addHandler(rtgovUI);
    }

	/**
	 * Creates a basic auth security handler.
	 */
	private SecurityHandler createSecurityHandler() {
		HashLoginService hashLoginService = new HashLoginService();
		for (String user : Lists.newArrayList("testuser_1","testuser_2","admin_1","admin_2")) {
			String[] roles = new String[] { "ROLE_USER" };
			if (user.startsWith("admin"))
				roles = new String[] { "ROLE_ADMIN" };
			hashLoginService.putUser(user, Credential.getCredential(user), roles);
		}
		hashLoginService.setName("RTGovRealm");
		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(new String[] { "ROLE_USER", "ROLE_ADMIN" });
		constraint.setAuthenticate(true);
		String[] protectedMethods = new String[] { "GET", "HEAD", "POST", "PUT", "DELETE", "TRACE", "CONNECT" };
		ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
		csh.setAuthenticator(new BasicAuthenticator());
		csh.setRealmName("RTGovRealm");
		for (String method : protectedMethods) {
			ConstraintMapping cm = new ConstraintMapping();
			cm.setConstraint(constraint);
			cm.setPathSpec("/*");
			cm.setMethod(method);
			csh.addConstraintMapping(cm);
		}
		csh.setLoginService(hashLoginService);
		return csh;
	}

	/**
     * @see org.overlord.commons.dev.server.DevServer#postStart(org.overlord.commons.dev.server.DevServerEnvironment)
     */
    @Override
    protected void postStart(DevServerEnvironment environment) throws Exception {
        System.out.println("----------  DONE  ---------------");
        System.out.println("Now try:  \n  http://localhost:"+serverPort()+"/rtgov-ui/index.html");
        System.out.println("---------------------------------");
    }

}
