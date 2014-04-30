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
import com.codenvy.api.core.util.Pair;
import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.internal.ResourceAllocators;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runner based on BaseDockerRunner that uses prepared set of dockerfiles.
 *
 * @author andrew00x
 */
public class EmbeddedDockerRunner extends BaseDockerRunner {
    private final String                                        name;
    private final Map<String, List<Pair<String, java.io.File>>> dockerfiles;

    public EmbeddedDockerRunner(java.io.File deployDirectoryRoot,
                                int cleanupTime,
                                String hostName,
                                ResourceAllocators allocators,
                                CustomPortService portService,
                                EventService eventService,
                                String name,
                                Map<String, List<Pair<String, java.io.File>>> dockerfiles) {
        super(deployDirectoryRoot, cleanupTime, hostName, allocators, portService, eventService);
        this.name = name;
        this.dockerfiles = new HashMap<>(dockerfiles);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return "The linux container runtime";
    }

    @Override
    protected DockerfileTemplate getDockerfileTemplate(RunRequest request) {
        String environmentId = request.getEnvironmentId();
        if (environmentId == null) {
            environmentId = "default";
        }
        final List<Pair<String, File>> list = dockerfiles.get(environmentId);
        final String name = request.getDebugMode() != null ? "Dockerfile_Debug" : "Dockerfile";
        if (list != null) {
            for (Pair<String, File> pair : list) {
                if (name.equals(pair.first)) {
                    return DockerfileTemplate.from(pair.second);
                }
            }
        }
        return null;
    }
}
