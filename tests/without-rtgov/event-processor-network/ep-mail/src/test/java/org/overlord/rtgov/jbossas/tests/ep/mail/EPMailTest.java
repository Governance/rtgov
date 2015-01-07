/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.jbossas.tests.ep.mail;

import static org.junit.Assert.fail;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.ep.mail.MailEventProcessor;

@RunWith(Arquillian.class)
@Ignore
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
                    Maven.resolver().resolve("org.overlord.rtgov.event-processor:ep-core:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.overlord.rtgov.event-processor:ep-mail:"+rtgovversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("org.mvel:mvel2:"+mvelversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("com.fasterxml.jackson.core:jackson-core:"+jacksonversion).withoutTransitivity().asSingleFile(),
                    Maven.resolver().resolve("com.fasterxml.jackson.core:jackson-databind:"+jacksonversion).withoutTransitivity().asSingleFile()
             )
             .addAsLibraries(Maven.resolver().resolve("org.overlord.rtgov.common:rtgov-common:"+rtgovversion).withTransitivity().asFile());
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