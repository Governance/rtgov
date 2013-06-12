/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008-12, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.overlord.rtgov.jbossas.tests.ep.mail;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.ep.mail.MailEventProcessor;


import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class EPMailTest {

    private static final String THE_EVENT = "THE EVENT";
    private static final String SOURCE_VALUE = "THE SOURCE";

    @Deployment
    public static WebArchive createDeployment() {
        String rtgovversion=System.getProperty("rtgov.version");
        String jacksonversion=System.getProperty("jackson.version");
        String mvelversion=System.getProperty("mvel.version");

        return ShrinkWrap.create(WebArchive.class)
            .addAsResource("script/Subject.mvel")
            .addAsResource("script/Content.mvel")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibraries(
                    DependencyResolvers
                    .use(MavenDependencyResolver.class)
                    .artifacts("org.overlord.rtgov.event-processor:ep-core:"+rtgovversion,
                            "org.overlord.rtgov.event-processor:ep-mail:"+rtgovversion,
                            "org.overlord.rtgov.common:rtgov-common:"+rtgovversion,
                            "org.mvel:mvel2:"+mvelversion,
                            "org.codehaus.jackson:jackson-core-asl:"+jacksonversion,
                            "org.codehaus.jackson:jackson-mapper-asl:"+jacksonversion)
                    .resolveAsFiles());
    }

    @Test
    public void testSendMail() {
        MailEventProcessor ep=new MailEventProcessor();
        
        ep.setFrom("overlord@redhat.com");
        ep.getTo().add("overlord@mailinator.com");
        ep.setSubjectScript("script/Subject.mvel");
        ep.setContentScript("script/Content.mvel");
        
        try {
            ep.init();
        } catch (Exception e) {
            fail("Failed to initialize: "+e);
        }
        
        try {
            ep.process(SOURCE_VALUE, THE_EVENT, 1);
        } catch (Exception e) {
            fail("Failed to send email: "+e);
        }
    }
}