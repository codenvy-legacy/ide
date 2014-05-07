/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
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

import com.codenvy.api.project.server.AbstractVirtualFileEntry;
import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ValueProviderFactory;
import com.codenvy.api.project.shared.ValueProvider;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author andrew00x
 */
@Singleton
public class RunnerScriptValueProviderFactory implements ValueProviderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(RunnerScriptValueProviderFactory.class);

    @Override
    public String getName() {
        return com.codenvy.api.runner.internal.Constants.RUNNER_SCRIPT_FILES;
    }

    @Override
    public ValueProvider newInstance(final Project project) {
        return new ValueProvider() {
            @Override
            public List<String> getValues() {
                final FolderEntry projectFolder = project.getBaseFolder();
                final AbstractVirtualFileEntry envFile = projectFolder.getChild("dockerenv.c5y.json");
                final List<String> files = new LinkedList<>();
                String runDockerfileName = null;
                String debugDockerfileName = null;
                if (envFile != null && envFile.isFile()) {
                    files.add("dockerenv.c5y.json");
                    try {
                        try (InputStream in = ((FileEntry)envFile).getInputStream()) {
                            final DockerEnvironment env = JsonHelper.fromJson(in, DockerEnvironment.class, null);
                            runDockerfileName = env.getRunDockerfileName();
                            if (runDockerfileName == null) {
                                runDockerfileName = "run.dc5y";
                            }
                            debugDockerfileName = env.getDebugDockerfileName();
                            if (debugDockerfileName == null) {
                                debugDockerfileName = "debug.dc5y";
                            }
                        }
                    } catch (IOException | JsonParseException e) {
                        LOG.error(e.getMessage(), e);
                    }
                } else {
                    runDockerfileName = "run.dc5y";
                    debugDockerfileName = "debug.dc5y";
                }
                if (runDockerfileName != null && projectFolder.getChild(runDockerfileName) != null) {
                    files.add(runDockerfileName);
                }
                if (debugDockerfileName != null && projectFolder.getChild(debugDockerfileName) != null) {
                    files.add(debugDockerfileName);
                }
                return files;
            }

            @Override
            public void setValues(List<String> value) {
                // noop
            }
        };
    }
}
