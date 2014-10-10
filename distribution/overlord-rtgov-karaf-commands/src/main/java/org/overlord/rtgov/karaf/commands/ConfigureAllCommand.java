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

import org.apache.felix.gogo.commands.Command;
import org.overlord.commons.karaf.commands.configure.AbstractConfigureCommand;
import org.overlord.rtgov.karaf.commands.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Karaf command that configures the fuse instance to allow the rtgov all
 * feature being executed correctly. It copies the rtgov required files and also
 * call the overlord commons configure command.
 *
 * @author David Virgil Naranjo
 */
@Command(scope = "overlord:rtgov:all", name = "configure")
public class ConfigureAllCommand extends AbstractConfigureCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureAllCommand.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * org.overlord.commons.karaf.commands.configure.ConfigureCommand#doExecute
     * ()
     */
    @Override
    protected Object doExecute() throws Exception {
        LOGGER.info(Messages.getString("configure.all.command.executed")); //$NON-NLS-1$
        // Call the overlord Commons command
        super.doExecute();

        // Copy the required files
        LOGGER.debug(Messages.getString("configure.command.copying.files")); //$NON-NLS-1$
        copyFile("all/overlord-rtgov-elasticsearch.properties", "overlord-rtgov-elasticsearch.properties"); //$NON-NLS-1$ $NON-NLS-2$
        copyFile("all/" + ConfigureConstants.RTGOV_PROPERTIES_FILE_NAME, ConfigureConstants.RTGOV_PROPERTIES_FILE_NAME); //$NON-NLS-1$ $NON-NLS-2$
        File dir = new File(karafConfigPath + "overlord-apps"); //$NON-NLS-1$
        if (!dir.exists()) {
            dir.mkdir();
        }
        copyFile("all/rtgovui-overlordapp.properties", "overlord-apps/rtgovui-overlordapp.properties"); //$NON-NLS-1$ //$NON-NLS-2$



        LOGGER.info(Messages.getString("configure.command.end.execution")); //$NON-NLS-1$
        return null;
    }
}
