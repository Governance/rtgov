package org.overlord.rtgov.karaf.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.overlord.commons.codec.AesEncrypter;
import org.overlord.commons.karaf.commands.AbstractFabricCommand;
import org.overlord.commons.karaf.commands.configure.AbstractConfigureFabricCommand;
import org.overlord.rtgov.karaf.commands.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Karaf command that configures the fabric profile to allow the rtgov all
 * feature being executed correctly.
 *
 * @author David Virgil Naranjo
 */
@Command(scope = "overlord:fabric:rtgov:client  ", name = "configure")
public class ConfigureClientFabricCommand extends AbstractFabricCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigureClientCommand.class);

    private static String RTGOV_PROFILE_PATH;

    static {
        if (File.separator.equals("/")) { //$NON-NLS-1$
            RTGOV_PROFILE_PATH = "overlord/rtgov"; //$NON-NLS-1$
        } else {
            RTGOV_PROFILE_PATH = "overlord\\rtgov"; //$NON-NLS-1$
        }
    }

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

        // Calls the overlord commons configure commamd
        OverlordCommonsConfigureFabricCommand overlordConfigure = new OverlordCommonsConfigureFabricCommand();
        overlordConfigure.setBundleContext(bundleContext);
        overlordConfigure.setPassword(_password);
        overlordConfigure.execute(session);
        LOGGER.debug(Messages.getString("configure.command.copying.files")); //$NON-NLS-1$

        // copy the rtgov.properties file and add the properties related the
        // user and password
        Properties rtgovProps = new Properties();
        InputStream is = new FileInputStream(new File(getRtgovPropertiesFilePath()));
        String encryptedPassword = "$\\{crypt:" + AesEncrypter.encrypt(_password) + "\\}"; //$NON-NLS-1$ //$NON-NLS-2$
        OutputStream os = null;
        File srcFile = new File(getRtgovPropertiesFilePath());
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

        LOGGER.info(Messages.getString("configure.command.end.execution")); //$NON-NLS-1$
        return null;
    }

    protected class OverlordCommonsConfigureFabricCommand extends AbstractConfigureFabricCommand {

        /**
         * Sets the password.
         *
         * @param password
         *            the new password
         */
        @Override
        public void setPassword(String password) {
            super.password = password;
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
                .append(ConfigureConstants.RTGOV_CLIENT_PROFILE).append(File.separator);
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
