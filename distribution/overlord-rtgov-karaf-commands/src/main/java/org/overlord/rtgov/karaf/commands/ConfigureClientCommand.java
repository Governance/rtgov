/*
 * Copyright 2014 JBoss Inc
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
package org.overlord.rtgov.karaf.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.overlord.commons.codec.AesEncrypter;
import org.overlord.commons.karaf.commands.configure.AbstractConfigureCommand;
import org.overlord.rtgov.karaf.commands.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * Karaf command that configures the fuse instance to allow the rtgov client
 * feature being executed correctly. It copies the rtgov required files and also
 * call the overlord commons configure command.
 *
 * @author David Virgil Naranjo
 */
@Command(scope = "overlord:rtgov:client", name = "configure")
public class ConfigureClientCommand extends AbstractFuseCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureClientCommand.class);

    @Argument(index = 0, name = "user", required = true, multiValued = false)
    private final String _user = null;

    @Argument(index = 1, name = "password", required = true, multiValued = false)
    private final String _password = null;

    /*
     * (non-Javadoc)
     *
     * @see org.apache.karaf.shell.console.AbstractAction#doExecute()
     */
    @Override
    protected Object doExecute() throws Exception {
        LOGGER.info(Messages.getString("configure.command.executed")); //$NON-NLS-1$

        File srcFile = new File(karafConfigPath + "overlord-rtgov.properties"); //$NON-NLS-1$
        if (!srcFile.exists()) {
            // Calls the overlord commons configure commamd
            OverlordCommonsConfigureCommand overlordConfigure = new OverlordCommonsConfigureCommand();
            overlordConfigure.setBundleContext(bundleContext);
            overlordConfigure.setPassword(_password);
            overlordConfigure.execute(session);
            LOGGER.debug(Messages.getString("configure.command.copying.files")); //$NON-NLS-1$

            // copy the rtgov.properties file and add the properties related the
            // user and password
            Properties rtgovProps = new Properties();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("/cli/overlord-rtgov.properties");
            String encryptedPassword = "${crypt:" + AesEncrypter.encrypt(_password) + "}"; //$NON-NLS-1$ //$NON-NLS-2$
            OutputStream os = null;
            try {
                rtgovProps.load(is);
                rtgovProps.setProperty("RESTActivityServer.serverUsername", _user); //$NON-NLS-1$
                rtgovProps.setProperty("RESTActivityServer.serverPassword", encryptedPassword); //$NON-NLS-1$
                os = new FileOutputStream(srcFile);
                rtgovProps.store(os, ""); //$NON-NLS-1$
            } finally {
                is.close();
                if (os != null) {
                    os.close();
                }
            }



        } else {
            throw new RuntimeException(Messages.getString("configure.command.previous.installation"));
        }

        LOGGER.info(Messages.getString("configure.command.end.execution")); //$NON-NLS-1$
        return null;
    }

    private class OverlordCommonsConfigureCommand extends AbstractConfigureCommand {

        /**
         * Sets the password.
         *
         * @param password
         *            the new password
         */
        public void setPassword(String password) {
            super.password = password;
        }
    }
}
