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
import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.dto.server.DtoFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runner based on BaseDockerRunner that uses prepared set of dockerfiles.
 *
 * @author andrew00x
 */
public class EmbeddedDockerRunner extends BaseDockerRunner {
    private final String                         name;
    private final Map<String, DockerEnvironment> dockerEnvironments;
    private final java.io.File                   dockerfilesRepository;

    EmbeddedDockerRunner(java.io.File deployDirectoryRoot,
                         int cleanupTime,
                         String hostName,
                         ResourceAllocators allocators,
                         CustomPortService portService,
                         EventService eventService,
                         String name,
                         List<DockerEnvironment> dockerEnvironments,
                         java.io.File dockerfilesRepository) {
        super(deployDirectoryRoot, cleanupTime, hostName, allocators, portService, eventService);
        this.name = name;
        this.dockerEnvironments = new HashMap<>(dockerEnvironments.size());
        for (DockerEnvironment dockerEnvironment : dockerEnvironments) {
            this.dockerEnvironments.put(dockerEnvironment.getId(), dockerEnvironment);
        }
        this.dockerfilesRepository = dockerfilesRepository;
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
        final Map<String, RunnerEnvironment> runnerEnvironments = new HashMap<>(dockerEnvironments.size());
        final DtoFactory dtoFactory = DtoFactory.getInstance();
        for (DockerEnvironment dockerEnvironment : dockerEnvironments.values()) {
            runnerEnvironments.put(dockerEnvironment.getId(), dtoFactory.createDto(RunnerEnvironment.class)
                                                                        .withId(dockerEnvironment.getId())
                                                                        .withDescription(dockerEnvironment.getDescription())
                                                                        .withIsDefault("default".equals(dockerEnvironment.getId())));
        }
        return runnerEnvironments;
    }

    @Override
    protected DockerEnvironment getDockerEnvironment(RunRequest request) throws IOException, RunnerException {
        String environmentId = request.getEnvironmentId();
        if (environmentId == null) {
            environmentId = "default";
        }
        final DockerEnvironment environment = dockerEnvironments.get(environmentId);
        if (environment == null) {
            throw new RunnerException(String.format("Invalid environment id %s", request.getEnvironmentId()));
        }
        return environment;
    }

    @Override
    protected Dockerfile getDockerfile(DockerEnvironment dockerEnvironment, RunRequest request) throws IOException {
        final boolean debug = request.getDebugMode() != null;
        // DockerEnvironment should never be null.
        if (dockerEnvironment != null) {
            String dockerFileName = debug ? dockerEnvironment.getDebugDockerfileName() : dockerEnvironment.getRunDockerfileName();
            if (dockerFileName == null) {
                dockerFileName = debug ? "debug.dc5y" : "run.dc5y";
            }
            final String envDirPath = getName() + java.io.File.separatorChar + dockerEnvironment.getId() + java.io.File.separatorChar;
            java.io.File dockerFile = new java.io.File(dockerfilesRepository, envDirPath + dockerFileName);
            if (dockerFile.exists()) {
                return DockerfileParser.parse(dockerFile);
            }
            if (!debug) {
                // If there is no Dockerfile for simple run try to use Dockerfile for run under debug, if any.
                dockerFileName = dockerEnvironment.getDebugDockerfileName();
                if (dockerFileName == null) {
                    dockerFileName = "debug.dc5y";
                }
                dockerFile = new java.io.File(dockerfilesRepository, envDirPath + dockerFileName);
                if (dockerFile.exists()) {
                    return DockerfileParser.parse(dockerFile);
                }
            }
        }
        return null;
    }
}
