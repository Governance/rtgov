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
package org.overlord.bam.tests.actmgmt.jbossas.actsvrrest;

import javax.inject.Inject;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

//@RunWith(Arquillian.class)
public class ActivityServerRESTTest {

    /*
    @Deployment
    public static WebArchive createDeployment() {
        String version=System.getProperty("bam.version");
        
        return ShrinkWrap.create(WebArchive.class)
            //.addClass(InventoryService.class)
            //.addAsResource("wsdl/OrderService.wsdl")
            .addAsResource(EmptyAsset.INSTANCE, "META-INF/beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsLibraries(
                    DependencyResolvers
                    .use(MavenDependencyResolver.class)
                    .artifacts("org.overlord.bam.activity-management:activity-server:"+version,
                            "org.overlord.bam.activity-management:activity-server-rests:"+version)
                    .resolveAsFiles());
    }
    */
    
    @Test
    @Ignore
    public void submitOrderDirectNoTxn() {

    }
}