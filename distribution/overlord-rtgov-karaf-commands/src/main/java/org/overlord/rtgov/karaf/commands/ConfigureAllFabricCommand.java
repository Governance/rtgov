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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.felix.gogo.commands.Command;
import org.overlord.commons.karaf.commands.configure.AbstractConfigureFabricCommand;

/**
 * Karaf command that configures the fabric profile to allow the rtgov all
 * feature being executed correctly.
 *
 * @author David Virgil Naranjo
 */
@Command(scope = "overlord:fabric:rtgov:all", name = "configure")
public class ConfigureAllFabricCommand extends AbstractConfigureFabricCommand{

    private static String RTGOV_PROFILE_PATH;

    static {
        if (File.separator.equals("/")) { //$NON-NLS-1$
            RTGOV_PROFILE_PATH = "overlord/rtgov"; //$NON-NLS-1$
        } else {
            RTGOV_PROFILE_PATH = "overlord\\rtgov"; //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.karaf.shell.console.AbstractAction#doExecute()
     */
    @Override
    protected Object doExecute() throws Exception {
        super.doExecute();
        addHeaderProperties();
        return null;
    }



    /**
     * Adds the header properties.
     *
     * @throws Exception
     *             the exception
     */
    private void addHeaderProperties() throws Exception {

        String filePath = getOverlordPropertiesFilePath();

        Properties props = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(new File(filePath));
            props.load(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            props.setProperty(ConfigureConstants.RTGOV_HEADER_HREF, ConfigureConstants.RTGOV_HEADER_HREF_VALUE);
            props.setProperty(ConfigureConstants.RTGOV_HEADER_LABEL, ConfigureConstants.RTGOV_HEADER_LABEL_VALUE);
            props.setProperty(ConfigureConstants.RTGOV_HEADER_PRIMARY_BRAND, ConfigureConstants.RTGOV_HEADER_PRIMARY_BRAND_VALUE);
            props.setProperty(ConfigureConstants.RTGOV_HEADER_SECOND_BRAND, ConfigureConstants.RTGOV_HEADER_SECOND_BRAND_VALUE);
            props.store(out, null);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * Gets the fabric rtgov profile path.
     * 
     * @return the fuse config path
     */
    public String getRtgovFabricProfilePath() {
        StringBuilder fuse_config_path = new StringBuilder();
        fuse_config_path.append(getFabricProfilesPath()).append(RTGOV_PROFILE_PATH).append(File.separator)
                .append(ConfigureConstants.RTGOV_ALL_PROFILE).append(File.separator);
        return fuse_config_path.toString();
    }

    /**
     * Gets the rtgov properties file path.
     * 
     * @return the rtgov properties file path
     */
    private String getRtgovPropertiesFilePath() {
        StringBuilder fuse_config_path = new StringBuilder();
        fuse_config_path.append(getRtgovFabricProfilePath()).append(ConfigureConstants.RTGOV_PROPERTIES_FILE_NAME);
        return fuse_config_path.toString();
    }
}
