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
import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.dto.server.DtoFactory;

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
    private final String                          name;
    private final Map<String, List<java.io.File>> dockerfiles;
    private final Map<String, RunnerEnvironment>  environments;

    EmbeddedDockerRunner(java.io.File deployDirectoryRoot,
                         int cleanupTime,
                         String hostName,
                         ResourceAllocators allocators,
                         CustomPortService portService,
                         EventService eventService,
                         String name,
                         Map<String, List<java.io.File>> dockerfiles) {
        super(deployDirectoryRoot, cleanupTime, hostName, allocators, portService, eventService);
        this.name = name;
        this.dockerfiles = dockerfiles;
        environments = new HashMap<>(dockerfiles.size());
        final DtoFactory dtoFactory = DtoFactory.getInstance();
        for (Map.Entry<String, List<File>> e : dockerfiles.entrySet()) {
            // TODO : environment description
            final RunnerEnvironment runnerEnvironment = dtoFactory.createDto(RunnerEnvironment.class)
                                                                  .withId(e.getKey())
                                                                  .withDescription(null)
                                                                  .withIsDefault("default".equals(e.getKey()));
            this.environments.put(runnerEnvironment.getId(), runnerEnvironment);
        }
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
    public Map<String, RunnerEnvironment> getEnvironments() {
        final Map<String, RunnerEnvironment> copy = new HashMap<>(environments.size());
        final DtoFactory dtoFactory = DtoFactory.getInstance();
        for (Map.Entry<String, RunnerEnvironment> entry : environments.entrySet()) {
            copy.put(entry.getKey(), dtoFactory.clone(entry.getValue()));
        }
        return copy;
    }

    @Override
    protected DockerfileTemplate getDockerfileTemplate(RunRequest request) {
        String environmentId = request.getEnvironmentId();
        if (environmentId == null) {
            environmentId = "default";
        }
        final List<java.io.File> list = dockerfiles.get(environmentId);
        final String name = request.getDebugMode() == null ? "run.dc5y" : "debug.dc5y";
        if (list != null) {
            for (java.io.File f : list) {
                if (name.equals(f.getName())) {
                    return DockerfileTemplate.from(f);
                }
            }
        }
        return null;
    }
}
