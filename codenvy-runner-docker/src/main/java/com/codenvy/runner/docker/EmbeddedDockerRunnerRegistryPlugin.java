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
import com.codenvy.api.runner.internal.Constants;
import com.codenvy.api.runner.internal.ResourceAllocators;
import com.codenvy.api.runner.internal.RunnerRegistry;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author andrew00x
 */
@Singleton
public class EmbeddedDockerRunnerRegistryPlugin {
    public static final String DOCKERFILES_REPO = "runner.docker.dockerfiles_repo";


    private final List<EmbeddedDockerRunner> myRunners;
    private final RunnerRegistry             registry;

    @Inject
    public EmbeddedDockerRunnerRegistryPlugin(RunnerRegistry registry,
                                              @Named(Constants.DEPLOY_DIRECTORY) java.io.File deployDirectoryRoot,
                                              @Named(Constants.APP_CLEANUP_TIME) int cleanupTime,
                                              @Named(BaseDockerRunner.HOST_NAME) String hostName,
                                              ResourceAllocators allocators,
                                              CustomPortService portService,
                                              EventService eventService,
                                              @Nullable @Named(DOCKERFILES_REPO) String dockerfilesRepository) {
        this.registry = registry;
        this.myRunners = new LinkedList<>();
        for (Map.Entry<String, Map<String, List<java.io.File>>> entry : findDockerfiles(dockerfilesRepository).entrySet()) {
            myRunners.add(new EmbeddedDockerRunner(deployDirectoryRoot,
                                                   cleanupTime,
                                                   hostName,
                                                   allocators,
                                                   portService,
                                                   eventService,
                                                   entry.getKey(),
                                                   entry.getValue()));
        }
    }

    @PostConstruct
    private void start() {
        for (EmbeddedDockerRunner runner : myRunners) {
            runner.start();
            registry.add(runner);
        }
    }

    @PreDestroy
    private void stop() {
        for (EmbeddedDockerRunner runner : myRunners) {
            registry.remove(runner.getName());
            runner.stop();
        }
    }

    /** Finds Dockerfiles. */
    private Map<String, Map<String, List<java.io.File>>> findDockerfiles(String dockerfilesRepository) {
        java.io.File dockerFilesDir = null;
        if (!(dockerfilesRepository == null || dockerfilesRepository.isEmpty())) {
            dockerFilesDir = new java.io.File(dockerfilesRepository);
        }
        if (dockerFilesDir == null) {
            final URL dockerFilesUrl = Thread.currentThread().getContextClassLoader().getResource("codenvy/runner/docker");
            if (dockerFilesUrl != null) {
                try {
                    dockerFilesDir = new java.io.File(dockerFilesUrl.toURI());
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
        if (dockerFilesDir != null && dockerFilesDir.isDirectory()) {
            final Map<String, Map<String, List<java.io.File>>> dockerFiles = new HashMap<>();
            final FileFilter dirFilter = new FileFilter() {
                @Override
                public boolean accept(java.io.File pathname) {
                    return pathname.isDirectory();
                }
            };
            final FileFilter dockerfileFilter = new FileFilter() {
                @Override
                public boolean accept(java.io.File pathname) {
                    String name;
                    return pathname.isFile() && ((name = pathname.getName()).equals("run.dc5y") || name.equals("debug.dc5y"));
                }
            };
            for (java.io.File f1 : dockerFilesDir.listFiles(dirFilter)) {
                final String runnerName = f1.getName();
                final Map<String, List<java.io.File>> runnerMap = new HashMap<>();
                dockerFiles.put(runnerName, runnerMap);
                for (java.io.File f2 : f1.listFiles(dirFilter)) {
                    final String envName = f2.getName();
                    final LinkedList<java.io.File> envList = new LinkedList<>();
                    Collections.addAll(envList, f2.listFiles(dockerfileFilter));
                    runnerMap.put(envName, envList);
                }
            }
            return dockerFiles;
        }
        return Collections.emptyMap();
    }
}
