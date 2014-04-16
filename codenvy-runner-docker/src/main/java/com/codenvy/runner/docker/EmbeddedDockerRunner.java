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
import com.codenvy.api.runner.internal.ResourceAllocators;

import java.util.ArrayList;
import java.util.List;

/**
 * Runner based on BaseDockerRunner that uses prepared set of dockerfiles.
 *
 * @author andrew00x
 */
public class EmbeddedDockerRunner extends BaseDockerRunner {
    private final String             name;
    private final List<java.io.File> dockerfiles;

    public EmbeddedDockerRunner(java.io.File deployDirectoryRoot,
                                int cleanupDelay,
                                String hostName,
                                ResourceAllocators allocators,
                                CustomPortService portService,
                                EventService eventService,
                                String name,
                                List<java.io.File> dockerfiles) {
        super(deployDirectoryRoot, cleanupDelay, hostName, allocators, portService, eventService);
        this.name = name;
        this.dockerfiles = new ArrayList<>(dockerfiles);
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
        String name = request.getRunner();
        if (request.getDebugMode() != null) {
            name += "_Debug";
        }
        for (java.io.File dockerfile : dockerfiles) {
            if (name.equals(dockerfile.getName())) {
                return DockerfileTemplate.from(dockerfile);
            }
        }
        return null;
    }
}
