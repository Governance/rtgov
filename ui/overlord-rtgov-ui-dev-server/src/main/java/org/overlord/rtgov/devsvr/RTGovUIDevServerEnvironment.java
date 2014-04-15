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

import java.io.File;
import java.io.FileWriter;
import java.util.Properties;

import org.overlord.commons.dev.server.DevServerEnvironment;

/**
 *
 * @author eric.wittmann@redhat.com
 */
public class RTGovUIDevServerEnvironment extends DevServerEnvironment {

    /**
     * Constructor.
     * @param args
     */
    public RTGovUIDevServerEnvironment(String[] args) {
        super(args);
    }

    /**
     * @see org.overlord.commons.dev.server.DevServerEnvironment#createAppConfigs()
     */
    @Override
    public void createAppConfigs() throws Exception {
        super.createAppConfigs();

        File dir = new File(getTargetDir(), "overlord-apps");

        File configFile1 = new File(dir, "rtgov-ui-overlordapp.properties");
        Properties props = new Properties();
        props.setProperty("overlordapp.app-id", "rtgov-ui");
        props.setProperty("overlordapp.href", "/rtgov-ui/index.html");// + (ide_srampUI ? "?gwt.codesvr=127.0.0.1:9997" : ""));
        props.setProperty("overlordapp.label", "RTGov");
        props.setProperty("overlordapp.primary-brand", "JBoss Overlord");
        props.setProperty("overlordapp.secondary-brand", "RTGov");
        props.store(new FileWriter(configFile1), "Overlord Runtime Governance UI application");
    }

}
