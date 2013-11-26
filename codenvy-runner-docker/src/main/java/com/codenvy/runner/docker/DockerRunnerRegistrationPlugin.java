/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.runner.docker;

import com.codenvy.api.runner.internal.RunnerRegistrationPlugin;
import com.codenvy.api.runner.internal.RunnerRegistry;

import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Docker runner registration plugin.
 *
 * @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a>
 */
public class DockerRunnerRegistrationPlugin implements RunnerRegistrationPlugin {
    @Override
    public void registerTo(RunnerRegistry registry) {
        final URL dockerFilesUrl = Thread.currentThread().getContextClassLoader().getResource("conf/runner/docker");
        final java.io.File dockerFilesDir;
        final Map<String, java.io.File> dockerFileTemplates = new HashMap<>();
        if (dockerFilesUrl != null) {
            try {
                dockerFilesDir = new java.io.File(dockerFilesUrl.toURI());
            } catch (URISyntaxException e) {
                throw new IllegalStateException(e);
            }
            if (dockerFilesDir.isDirectory()) {
                final java.io.File[] dockerFiles = dockerFilesDir.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(java.io.File file) {
                        return file.isFile();
                    }
                });
                if (dockerFiles != null) {
                    for (java.io.File file : dockerFiles) {
                        final String fName = file.getName();
                        dockerFileTemplates.put(fName, file);
                    }
                }
            }
        }
        registry.add(new DockerRunner(dockerFileTemplates));
    }
}
