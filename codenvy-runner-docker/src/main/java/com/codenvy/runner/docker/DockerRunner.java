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

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.util.CustomPortService;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.dto.RunRequest;
import com.google.common.io.ByteStreams;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author andrew00x
 */
@Singleton
public class DockerRunner extends BaseDockerRunner {
    @Inject
    public DockerRunner(@Named(DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                        @Named(CLEANUP_DELAY_TIME) int cleanupDelay,
                        @Named("runner.docker.host_name") String hostName,
                        ResourceAllocators allocators,
                        CustomPortService portService,
                        EventService eventService) {
        super(deployDirectoryRoot, cleanupDelay, hostName, allocators, portService, eventService);
    }

    @Override
    public String getName() {
        return "docker";
    }

    @Override
    public String getDescription() {
        return "The linux container runtime";
    }

    @Override
    protected DockerfileTemplate getDockerfileTemplate(RunRequest request) throws IOException {
        final String scriptUrl = request.getRunnerScriptUrl();
        if (scriptUrl == null) {
            return null;
        }
        // TODO: use HTTP client, when it become available
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InputStream in = new URL(scriptUrl).openStream()) {
            ByteStreams.copy(in, output);
        }
        return DockerfileTemplate.from("DockerfileTemplate", output.toString());
    }
}
