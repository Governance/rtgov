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
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.karaf.shell.console.OsgiCommandSupport;

/**
 * Abstract class general for all the karaf commands.
 * 
 * @author David Virgil Naranjo
 */
public abstract class AbstractFuseCommand extends OsgiCommandSupport {
    protected String karafHome = System.getProperty("karaf.home"); //$NON-NLS-1$

    protected String karafConfigDir = "etc"; //$NON-NLS-1$

    protected String karafConfigPath;

    /**
     * Instantiates a new abstract fuse command.
     */
    public AbstractFuseCommand() {
        StringBuilder sb = new StringBuilder(karafHome);
        if (!karafHome.endsWith(File.separator)) {
            sb.append(File.separator);
        }
        sb.append(karafConfigDir).append(File.separator);
        karafConfigPath = sb.toString();
    }

    /**
     * Copy the given filename from this bundle to Fuse. 2 assumptions are made:
     *
     * 1.) The filename is available in this bundle's /src/main/resources. 2.)
     * The target is simply FUSE_HOME/etc.
     *
     * @param filename
     *            the filename
     * @throws Exception
     *             the exception
     */
    protected void copyFile(String filename) throws Exception {
        File destFile = new File(karafConfigPath + filename);
        if (!destFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/" + filename); //$NON-NLS-1$
            FileUtils.copyInputStreamToFile(is, destFile);
        }
    }

    /**
     * Copy the given filename from this bundle to Fuse. 2 assumptions are made:
     *
     * 1.) The filename is available in this bundle's /src/main/resources. 2.)
     * The target is simply FUSE_HOME/etc.
     *
     * @param inputFile
     *            the input file
     * @param destFileName
     *            the dest file name
     * @throws Exception
     *             the exception
     */
    protected void copyFile(String inputFile, String destFileName) throws Exception {
        File destFile = new File(karafConfigPath + destFileName);
        if (!destFile.exists()) {
            InputStream is = this.getClass().getResourceAsStream("/" + inputFile); //$NON-NLS-1$
            FileUtils.copyInputStreamToFile(is, destFile);
        }
    }
}
